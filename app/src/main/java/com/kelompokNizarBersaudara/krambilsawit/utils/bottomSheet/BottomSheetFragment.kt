package com.kelompokNizarBersaudara.krambilsawit.utils.bottomSheet

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kelompokNizarBersaudara.krambilsawit.databinding.FragmentBottomSheetBinding
import com.kelompokNizarBersaudara.krambilsawit.model.BlogPost

class BottomSheetFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentBottomSheetBinding? = null
    private val binding get() = _binding!!

    private var callback: BottomSheetCallback? = null
    private var ref: String? = null
    private var item: BlogPost? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialog?.setOnShowListener {
            val bottomSheetDialog = dialog as BottomSheetDialog
            val bottomSheet = bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                BottomSheetBehavior.from(bottomSheet).state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val titleText = arguments?.getString("title") ?: "Article"
        binding.title.text = titleText

        binding.onDelete.setOnClickListener {
            callback?.onDeleteClicked(this, ref.orEmpty())
        }

        binding.onEdit.setOnClickListener {
            callback?.onEditClicked(this, ref.orEmpty())
        }

        binding.onShare.setOnClickListener {
            callback?.onShareClicked(this, item!!)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun setCallback(callback: BottomSheetCallback) {
        this.callback = callback
    }

    fun setRef(ref: String) {
        this.ref = ref
    }

    fun setItem(item: BlogPost) {
        this.item = item
    }

    companion object {
        const val TAG = "BottomSheetFragment"
    }
}