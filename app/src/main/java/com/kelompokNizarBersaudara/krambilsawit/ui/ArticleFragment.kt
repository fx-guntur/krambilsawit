package com.kelompokNizarBersaudara.krambilsawit.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.kelompokNizarBersaudara.krambilsawit.BlogPostAdapter
import com.kelompokNizarBersaudara.krambilsawit.R
import com.kelompokNizarBersaudara.krambilsawit.databinding.FragmentArticleBinding
import com.kelompokNizarBersaudara.krambilsawit.model.BlogPost
import com.kelompokNizarBersaudara.krambilsawit.utils.bottomSheet.BottomSheetCallback
import com.kelompokNizarBersaudara.krambilsawit.utils.bottomSheet.BottomSheetFragment
import com.kelompokNizarBersaudara.krambilsawit.utils.FirebaseUtils.firebaseDB

class ArticleFragment : Fragment(), BottomSheetCallback {
    private var _binding: FragmentArticleBinding? = null
    private val binding get() = _binding!!

    private var adapter: BlogPostAdapter? = null
    private lateinit var manager: LinearLayoutManager
    private lateinit var articleRef: DatabaseReference

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val floatingButton: ExtendedFloatingActionButton = binding.newArticle
        floatingButton.setOnClickListener {
            navigateToPostFragment()
        }

        val tag = arguments?.getString("tag")
        Log.d(TAG, "Tag: $tag")
        manager = LinearLayoutManager(context)
        binding.articleRecyclerView.layoutManager = manager
//        binding.articleRecyclerView.addVeiledItems(15)

//        binding.articleRecyclerView.veil()
        articleRef = firebaseDB.reference.child(PostFragment.ARTICLES_CHILD)
        var query = articleRef.orderByChild("date")
        if (tag != "all" && tag != null) {
            query = articleRef.orderByChild("tag").equalTo(tag)
        }

        val options = FirebaseRecyclerOptions.Builder<BlogPost>()
            .setQuery(query, BlogPost::class.java)
            .build()
        adapter = BlogPostAdapter(
            options,
            this@ArticleFragment,
            ::navigateToArticleDetailFragment)
        binding.articleRecyclerView.adapter = adapter
//                binding.articleRecyclerView.unVeil()

        checkEmptyData(query, tag)

//        if (options.snapshots.size <= 0) {
//            binding.articleRecyclerView.visibility = View.INVISIBLE
//            binding.articleNotFound.visibility = View.VISIBLE
//            if (tag != "all" && tag != null) {
//                binding.articleNotFound.text = getString(R.string.artikel_kosong) + " Pada Kategori $tag"
//            }
//        } else {
//            binding.articleRecyclerView.visibility = View.VISIBLE
//            binding.articleNotFound.visibility = View.GONE
//        }
    }

    private fun checkEmptyData(query: Query, tag: String?) {
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    binding.articleRecyclerView.visibility = View.VISIBLE
                    binding.articleNotFound.visibility = View.GONE
                } else {
                    binding.articleRecyclerView.visibility = View.INVISIBLE
                    binding.articleNotFound.visibility = View.VISIBLE
                    if (tag != "all" && tag != null) {
                        val defaultText = getString(R.string.artikel_kosong)
                        binding.articleNotFound.text = "$defaultText Pada Kategori $tag"
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "onCancelled: ${error.message}")
            }
        })
    }

    private fun navigateToPostFragment() {
        val bundle = bundleOf("mode" to "INSERT")
        findNavController().navigate(R.id.action_nav_article_to_nav_post_insert, bundle)
    }

    private fun navigateToArticleDetailFragment(data: BlogPost) {
        val bundle = bundleOf(
            "title" to data.title,
            "content" to data.content,
            "thumbnail" to data.thumbnail,
            "tag" to data.tag,
            "date" to data.date)
        findNavController().navigate(R.id.nav_article_detail, bundle)
    }

    override fun onPause() {
        adapter?.stopListening()
//        binding.articleRecyclerView.unVeil()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        adapter?.startListening()
//        binding.articleRecyclerView.veil()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArticleBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private const val TAG = "ArticleFragment"
    }

    override fun onDeleteClicked(view: BottomSheetFragment, ref: String) {
        val articleRef = firebaseDB.reference.child(PostFragment.ARTICLES_CHILD).child(ref)
        articleRef.removeValue()
        view.dismiss()
    }

    override fun onEditClicked(view: BottomSheetFragment, ref: String) {
        val bundle = bundleOf("mode" to "UPDATE", "postId" to ref)
        findNavController().navigate(R.id.action_nav_article_to_nav_post_update, bundle)
        view.dismiss()
    }

    override fun onShareClicked(view: BottomSheetFragment, item: BlogPost) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, item.title + "\n\n" + item.desc)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
        view.dismiss()
    }
}