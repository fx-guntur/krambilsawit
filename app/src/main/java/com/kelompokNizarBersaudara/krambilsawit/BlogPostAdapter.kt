package com.kelompokNizarBersaudara.krambilsawit

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.kelompokNizarBersaudara.krambilsawit.model.BlogPost
import com.kelompokNizarBersaudara.krambilsawit.databinding.ArticleItemRowBinding
import com.kelompokNizarBersaudara.krambilsawit.utils.BottomSheetCallback
import com.kelompokNizarBersaudara.krambilsawit.utils.BottomSheetFragment

class BlogPostAdapter(
    private val options: FirebaseRecyclerOptions<BlogPost>,
    private val callback: BottomSheetCallback,
    private val navigateToArticleDetailFragment: (BlogPost) -> Unit
) : FirebaseRecyclerAdapter<BlogPost, BlogPostAdapter.ArticleItemRowViewHolder>(options) {
    private var _binding: ArticleItemRowBinding? = null
    private val binding get() = _binding!!

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleItemRowViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.article_item_row, parent, false)
        _binding = ArticleItemRowBinding.bind(view)
        return ArticleItemRowViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ArticleItemRowViewHolder, position: Int, model: BlogPost) {
        holder.bind(model)

        holder.ivOption.setOnClickListener {
            Log.d(TAG, "onBindViewHolder: clicked $position")
            val ref = getRef(position)
            showBottomSheet(binding, options.snapshots[position], ref.key!!)
        }

        holder.container.setOnClickListener {
            navigateToArticleDetailFragment(options.snapshots[position])
        }
    }

    private fun showBottomSheet(binding: ArticleItemRowBinding, item: BlogPost, ref: String) {
        val bottomSheet = BottomSheetFragment()
        val args = Bundle().apply {
            putString("title", item.title)
        }
        bottomSheet.arguments = args
        bottomSheet.setCallback(callback)
        bottomSheet.setRef(ref)
        bottomSheet.setItem(item)
        bottomSheet.show(
            (binding.root.context as AppCompatActivity).supportFragmentManager,
            BottomSheetFragment.TAG)
    }

    inner class ArticleItemRowViewHolder(private val binding: ArticleItemRowBinding) :
        ViewHolder(binding.root) {

        val ivOption = binding.ivOption
        val container = binding.containerArticleItemRow

        fun bind(item: BlogPost) {
            if (item.thumbnail != "") {
                loadImageIntoView(binding.imgItemPhoto, item.thumbnail!!)
            } else {
                binding.imgItemPhoto.setImageResource(R.drawable.default_image_articles)
            }

            binding.tvJudul.text = item.title!!
            binding.tvDesc.text = item.desc!!
            binding.tvTag.text = item.tag!!
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
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun stopListening() {
        super.stopListening()
        notifyDataSetChanged()
    }

    companion object {
        private const val TAG = "BlogPostAdapter"
    }
}