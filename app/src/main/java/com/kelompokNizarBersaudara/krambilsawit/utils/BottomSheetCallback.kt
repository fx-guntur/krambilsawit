package com.kelompokNizarBersaudara.krambilsawit.utils

import com.kelompokNizarBersaudara.krambilsawit.model.BlogPost

interface BottomSheetCallback {
    fun onDeleteClicked(view: BottomSheetFragment, ref: String)
    fun onEditClicked(view: BottomSheetFragment, ref: String)
    fun onShareClicked(view: BottomSheetFragment, item: BlogPost)
}
