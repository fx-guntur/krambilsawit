package com.kelompokNizarBersaudara.krambilsawit

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.kelompokNizarBersaudara.krambilsawit.model.BlogPost
import com.kelompokNizarBersaudara.krambilsawit.databinding.ArticleItemRowBinding

class BlogPostAdapter(
    private val options: FirebaseRecyclerOptions<BlogPost>
) : FirebaseRecyclerAdapter<BlogPost, ViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.article_item_row, parent, false)
        val binding = ArticleItemRowBinding.bind(view)
        return ArticleItemRowViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: BlogPost) {
        (holder as ArticleItemRowViewHolder).bind(model)
    }

    inner class ArticleItemRowViewHolder(private val binding: ArticleItemRowBinding) :
        ViewHolder(binding.root) {
        fun bind(item: BlogPost) {
            loadImageIntoView(binding.imgItemPhoto, item.thumbnail!!)

            binding.tvJudul.text = item.title!!
            binding.tvDesc.text = item.desc!!
        }
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

    @SuppressLint("NotifyDataSetChanged")
    override fun stopListening() {
        super.stopListening()
        notifyDataSetChanged()
    }

    companion object {
        const val TAG = "BlogPostAdapter"
    }
}