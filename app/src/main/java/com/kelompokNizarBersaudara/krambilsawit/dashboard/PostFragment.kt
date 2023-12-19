package com.kelompokNizarBersaudara.krambilsawit.dashboard

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.android.material.imageview.ShapeableImageView
import com.kelompokNizarBersaudara.krambilsawit.MyOpenDocumentContract
import com.kelompokNizarBersaudara.krambilsawit.databinding.FragmentPostBinding

class PostFragment : Fragment() {
    private var _binding: FragmentPostBinding? = null

    private lateinit var imageUploadView: ShapeableImageView
    private lateinit var imageUpload: Uri
    private val binding get() = _binding!!

    private val openDocument = registerForActivityResult(MyOpenDocumentContract()) { uri ->
        uri?.let { onImageSelected(it) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageUploadView = binding.imageView

        val uploadFoto: Button = binding.uploadFoto
        uploadFoto.setOnClickListener {
            openDocument.launch(arrayOf("image/*"))
        }

        val submitButton: Button = binding.tvSubmit
        submitButton.setOnClickListener {
            TODO("Klik")
        }
    }

    private fun onImageSelected(uri: Uri) {
        Log.d(TAG, "Uri: $uri")
        imageUpload = uri
        imageUploadView.visibility = View.VISIBLE
        imageUploadView.setImageURI(uri)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPostBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private const val TAG = "PostArticleFragment"
    }
}