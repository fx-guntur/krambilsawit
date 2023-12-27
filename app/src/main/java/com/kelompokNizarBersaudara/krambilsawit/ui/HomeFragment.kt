package com.kelompokNizarBersaudara.krambilsawit.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.kelompokNizarBersaudara.krambilsawit.BlogPostAdapter
import com.kelompokNizarBersaudara.krambilsawit.R
import com.kelompokNizarBersaudara.krambilsawit.databinding.FragmentHomeBinding
import com.kelompokNizarBersaudara.krambilsawit.model.BlogPost
import com.kelompokNizarBersaudara.krambilsawit.utils.FirebaseUtils.firebaseDB

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: BlogPostAdapter
    private lateinit var manager: LinearLayoutManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val articleRef = firebaseDB.reference.child(PostFragment.ARTICLES_CHILD).limitToLast(3)
        val options = FirebaseRecyclerOptions.Builder<BlogPost>()
            .setQuery(articleRef, BlogPost::class.java)
            .build()
        adapter = BlogPostAdapter(options)
        manager = LinearLayoutManager(context)
        binding.articleRecyclerView.layoutManager = manager
        binding.articleRecyclerView.adapter = adapter

        val tagKuliner = binding.category.tagKuliner
        val tagBudaya = binding.category.tagBudaya
        val tagWisata = binding.category.tagWisata

        tagKuliner.setOnClickListener {
            goToArticle("Kuliner")
        }

        tagBudaya.setOnClickListener {
            goToArticle("Budaya")
        }

        tagWisata.setOnClickListener {
            goToArticle("Wisata")
        }
    }

    private fun goToArticle(tag: String) {
        val bundle = bundleOf("tag" to tag)
        findNavController().navigate(R.id.action_nav_home_to_nav_article, bundle)
    }

    override fun onPause() {
        adapter.stopListening()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        adapter.startListening()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}