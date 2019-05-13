package com.unionyy.mobile.reformat.core

data class CodeFragment(
    val fileName: String,
    val startPos: Location,
    val endPos: Location
)