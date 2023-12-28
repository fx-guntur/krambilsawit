package com.kelompokNizarBersaudara.krambilsawit.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.kelompokNizarBersaudara.krambilsawit.BlogPostAdapter
import com.kelompokNizarBersaudara.krambilsawit.databinding.FragmentArticleDetailBinding

class ArticleDetailFragment : Fragment() {
    private var _binding: FragmentArticleDetailBinding? = null
    private val binding get() = _binding!!

    private val args: ArticleDetailFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val title = args.title!!
        val content = args.content!!
        val tag = args.tag!!
        val date = args.date!!
        val thumbnail = args.thumbnail!!

        binding.tvJudulArtikel.text = title
        binding.tvTag.text = tag
        binding.tvDescArtikel.text = content
        binding.tvTanggal.text = date
        loadImageIntoView(binding.imageView, thumbnail)
    }

    private fun loadImageIntoView(view: ImageView, url: String, isCircular: Boolean = false) {
        if (url.startsWith("gs://")) {
            val storageReference = Firebase.storage.getReferenceFromUrl(url)
            storageReference.downloadUrl
                .addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()
                    loadWithGlide(view, downloadUrl, false)
                }
                .addOnFailureListener { e ->
                    Log.w(
                        TAG,
                        "Getting download url was not successful.",
                        e
                    )
                }
        } else {
            loadWithGlide(view, url, isCircular)
        }
    }

    private fun loadWithGlide(view: ImageView, url: String, isCircular: Boolean = true) {
        Glide.with(view.context).load(url).into(view)
        var requestBuilder = Glide.with(view.context).load(url)
        if (isCircular) {
            requestBuilder = requestBuilder.transform(CircleCrop())
        }
        requestBuilder.into(view)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentArticleDetailBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private const val TAG = "ArticleDetailFragment"
    }
}