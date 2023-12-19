package com.kelompokNizarBersaudara.krambilsawit.dashboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.kelompokNizarBersaudara.krambilsawit.R
import com.kelompokNizarBersaudara.krambilsawit.databinding.FragmentArticleBinding
import com.kelompokNizarBersaudara.krambilsawit.databinding.FragmentHomeBinding

class ArticleFragment : Fragment() {
    private var _binding: FragmentArticleBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val floatingButton: ExtendedFloatingActionButton = binding.newArticle
        floatingButton.setOnClickListener {
            navigateToPostFragment()
        }
    }

    private fun navigateToPostFragment() {
        findNavController().navigate(R.id.action_nav_article_to_nav_post)
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
}