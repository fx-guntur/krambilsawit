package com.kelompokNizarBersaudara.krambilsawit.model

data class BlogPost(
    val title: String? = null,
    val desc: String? = null,
    val date: String? = null,
    val content: String? = null,
    var thumbnail: String? = null,
    val tag: String? = null
)
