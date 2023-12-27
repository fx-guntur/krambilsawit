package com.kelompokNizarBersaudara.krambilsawit.ui

import android.annotation.SuppressLint
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
import com.google.firebase.database.ValueEventListener
import com.kelompokNizarBersaudara.krambilsawit.BlogPostAdapter
import com.kelompokNizarBersaudara.krambilsawit.R
import com.kelompokNizarBersaudara.krambilsawit.databinding.FragmentArticleBinding
import com.kelompokNizarBersaudara.krambilsawit.model.BlogPost
import com.kelompokNizarBersaudara.krambilsawit.utils.FirebaseUtils.firebaseDB

class ArticleFragment : Fragment() {
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
        binding.articleRecyclerView.setLayoutManager(manager)
        binding.articleRecyclerView.addVeiledItems(15)

        binding.articleRecyclerView.veil()
        articleRef = firebaseDB.reference.child(PostFragment.ARTICLES_CHILD)
        var query = articleRef.orderByChild("date")

        if (tag != null) {
            query = articleRef.orderByChild("tag").equalTo(tag)
        }

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.d(TAG, "Number of items: ${dataSnapshot.childrenCount}")

                val options = FirebaseRecyclerOptions.Builder<BlogPost>()
                    .setQuery(query, BlogPost::class.java)
                    .build()
                adapter = BlogPostAdapter(options)
                binding.articleRecyclerView.setAdapter(adapter)
                binding.articleRecyclerView.unVeil()

                if (dataSnapshot.childrenCount.toInt() == 0) {
                    binding.articleRecyclerView.visibility = View.INVISIBLE
                    binding.articleNotFound.visibility = View.VISIBLE
                    if (tag != null) {
                        binding.articleNotFound.text = getString(R.string.artikel_kosong) + " Pada Kategori $tag"
                    }
                } else {
                    binding.articleRecyclerView.visibility = View.VISIBLE
                    binding.articleNotFound.visibility = View.GONE
                }
            }

            @SuppressLint("SetTextI18n")
            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "Error fetching articles: ${databaseError.message}")
                binding.articleRecyclerView.visibility = View.INVISIBLE
                binding.articleNotFound.visibility = View.VISIBLE
                if (tag != null) {
                    binding.articleNotFound.text = getString(R.string.artikel_kosong) + " Pada Kategori $tag"
                }
            }
        })
    }

    private fun navigateToPostFragment() {
        val bundle = bundleOf("mode" to "INSERT")
        findNavController().navigate(R.id.action_nav_article_to_nav_post, bundle)
    }

    override fun onPause() {
        adapter?.stopListening()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        adapter?.startListening()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
}