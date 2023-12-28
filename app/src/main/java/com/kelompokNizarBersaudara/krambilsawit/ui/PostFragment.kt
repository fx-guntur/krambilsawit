package com.kelompokNizarBersaudara.krambilsawit.ui

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.StorageReference
import com.kelompokNizarBersaudara.krambilsawit.MyOpenDocumentContract
import com.kelompokNizarBersaudara.krambilsawit.R
import com.kelompokNizarBersaudara.krambilsawit.databinding.FragmentPostBinding
import com.kelompokNizarBersaudara.krambilsawit.extensions.Extensions.showNotification
import com.kelompokNizarBersaudara.krambilsawit.model.BlogPost
import com.kelompokNizarBersaudara.krambilsawit.utils.FirebaseUtils.firebaseDB
import java.text.SimpleDateFormat
import java.util.Date
import com.kelompokNizarBersaudara.krambilsawit.utils.FirebaseUtils.firebaseStorage
import com.kelompokNizarBersaudara.krambilsawit.utils.FirebaseUtils.firebaseUser
import com.kelompokNizarBersaudara.krambilsawit.extensions.Extensions.toast

class PostFragment : Fragment() {
    private var _binding: FragmentPostBinding? = null

    private lateinit var imageUploadView: ShapeableImageView
    private var imageUpload: Uri? = null
    private val binding get() = _binding!!

    /**
     * 1. Judul artikel
     * 2. Isi artikel
     */
    private lateinit var inputEditText: Array<TextInputEditText>
    private lateinit var inputComplete: AutoCompleteTextView

    private var postId: String? = null
    private var mode: String? = null
    private var articleOldData: BlogPost? = null

    private val openDocument = registerForActivityResult(MyOpenDocumentContract()) { uri ->
        uri?.let { onImageSelected(it) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageUploadView = binding.imageView

        mode = arguments?.getString("mode")

        if (mode == "UPDATE") {
            postId = arguments?.getString("postId")
            if (postId != null) {
                fetchPostData(postId!!)
            } else {
                Log.e(TAG, "Post ID is null.")
                requireActivity().toast("Gagal Update Artikel")
                backToArticle()
            }
        }

        val uploadFoto: Button = binding.uploadFoto
        uploadFoto.setOnClickListener {
            openDocument.launch(arrayOf("image/*"))
        }

        val submitButton: Button = binding.tvSubmit
        submitButton.setOnClickListener {
            onSubmit()
        }
    }

    private fun onSubmit() {
        inputEditText = arrayOf(binding.judulArtikel, binding.isiArtikel)
        inputComplete = binding.kategoriArtikel

        if (mode == "INSERT") {
            if (notEmpty()) {
                insertData()
            } else {
                inputEditText.forEach { input ->
                    if (input.text.toString().trim().isEmpty()) {
                        input.error = "${input.hint} is required"
                    }
                }
                inputComplete.text.toString().trim().takeIf { it.isEmpty() }
                    ?.let { inputComplete.error = "${inputComplete.hint} is required" }
                if (imageUpload == null || imageUpload.toString().trim().isEmpty()) {
                    requireActivity().toast("Thumbnail is required")
                }
            }
        } else if (mode == "UPDATE" && postId != null) {
            updateData()
        }
    }

    private fun notEmpty(): Boolean = binding.judulArtikel.text.toString().trim().isNotEmpty() &&
            binding.isiArtikel.text.toString().trim().isNotEmpty() &&
            binding.kategoriArtikel.text.toString().trim().isNotEmpty() &&
            imageUpload?.toString()?.trim()?.isNotEmpty() == true

    private fun insertData() {
        Log.i(TAG, "Mode Insert")
        val currentDate = Date()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val dateString = dateFormat.format(currentDate)

        val content = inputEditText[1].text.toString().trim()

        val tempArticleData = BlogPost(
            inputEditText[0].text.toString().trim(),
            getExcerpt(content, 100),
            dateString,
            content,
            "",
            inputComplete.text.toString()
        )

        val articleChild = firebaseDB.reference.child(ARTICLES_CHILD)
        val user = firebaseUser

        articleChild.push().setValue(
            tempArticleData,
            DatabaseReference.CompletionListener { databaseError, databaseReference ->
                if (databaseError != null) {
                    Log.w(
                        TAG, "Unable to write message to database.",
                        databaseError.toException()
                    )
                    requireActivity().toast("Gagal Post Artikel")
                    return@CompletionListener
                }

                val key = databaseReference.key
                val storageReference = firebaseStorage
                    .getReference(user!!.uid)
                    .child(key!!)
                    .child(imageUpload!!.lastPathSegment!!)

                putImageInStorage(storageReference, imageUpload!!, key, tempArticleData)
            }
        )
    }

    private fun updateData() {
        Log.i(TAG, "Mode Update")
        val content = inputEditText[1].text.toString().trim()

        val updatedArticleData = BlogPost(
            inputEditText[0].text.toString().trim(),
            getExcerpt(content, 100),
            articleOldData?.date,
            content,
            articleOldData?.thumbnail,
            inputComplete.text.toString()
        )

        val user = firebaseUser
        val articleRef = firebaseDB.reference.child(ARTICLES_CHILD).child(postId!!)
        articleRef.setValue(updatedArticleData)
            .addOnSuccessListener {
                if (imageUpload == null) {
                    notifSuccess()
                    clearInput()
                    backToArticle()
                    return@addOnSuccessListener
                }

                val storageReference = firebaseStorage
                    .getReference(user!!.uid)
                    .child(postId!!)
                    .child(imageUpload!!.lastPathSegment!!)

                putImageInStorage(storageReference, imageUpload!!, postId, updatedArticleData)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error updating article: $e")
                requireActivity().toast("Gagal Update Artikel")
            }
    }

    private fun putImageInStorage(storageReference: StorageReference, uri: Uri, key: String?, articleData: BlogPost) {
        Log.i(TAG, "Upload image")
        val uploadTask = storageReference.putFile(uri)

        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            storageReference.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                articleData.thumbnail = downloadUri.toString()
                saveArticleData(articleData, key)
            } else {
                Log.w(TAG, "Image upload task was unsuccessful.")
            }
        }
    }

    private fun saveArticleData(articleData: BlogPost, key: String?) {
        val articleChild = firebaseDB.reference.child(ARTICLES_CHILD)
        articleChild.child(key!!).setValue(articleData)
        notifSuccess()
        clearInput()
        backToArticle()
    }

    private fun notifSuccess() {
        if (mode == "UPDATE") {
            requireActivity().toast("Berhasil Update Post Artikel")
            requireActivity().showNotification(
                requireActivity(),
                "Update Post Artikel",
                "Kamu Berhasil update artikel")
        } else {
            requireActivity().toast("Berhasil Post Artikel")
            requireActivity().showNotification(
                requireActivity(),
                "Post Artikel",
                "Kamu Berhasil upload artikel baru")
        }
    }

    private fun onImageSelected(uri: Uri) {
        Log.d(TAG, "Uri: $uri")
        imageUpload = uri
        imageUploadView.visibility = View.VISIBLE
        imageUploadView.setImageURI(uri)
    }

    private fun fetchPostData(postId: String) {
        val postRef = firebaseDB.reference.child(ARTICLES_CHILD).child(postId)
        postRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val postData = dataSnapshot.getValue(BlogPost::class.java)
                    postData?.let { populateFormData(it) }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "Error fetching post data: ${databaseError.message}")
            }
        })
    }

    private fun populateFormData(postData: BlogPost) {
        articleOldData = postData
        binding.judulArtikel.setText(postData.title)
        binding.isiArtikel.setText(postData.content)
        binding.kategoriArtikel.setText(postData.tag)

        if (postData.thumbnail != null) {
            binding.imageView.visibility = View.VISIBLE
        }
        Glide.with(requireContext())
            .load(postData.thumbnail)
            .into(binding.imageView)
    }

    private fun clearInput() {
        binding.judulArtikel.setText("")
        binding.isiArtikel.setText("")
        binding.kategoriArtikel.setText("")
        imageUpload = null
    }

    private fun getExcerpt(input: String, length: Int): String {
        return if (input.length > length) {
            input.substring(0, length)
        } else {
            input
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPostBinding.inflate(inflater, container, false)

        return binding.root
    }

    private fun backToArticle() {
        findNavController().navigate(R.id.nav_article)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private const val TAG = "PostArticleFragment"
        const val ARTICLES_CHILD = "articles"
    }
}