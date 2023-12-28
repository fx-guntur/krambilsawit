package com.kelompokNizarBersaudara.krambilsawit.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kelompokNizarBersaudara.krambilsawit.databinding.FragmentArticleDetailBinding

class ArticleDetail : Fragment() {
    private var _binding: FragmentArticleDetailBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val title = arguments?.getString("title")
        val content = arguments?.getString("content")
        val tag = arguments?.getString("tag")
        val date = arguments?.getString("date")
        val thumbnail = arguments?.getString("thumbnail")


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