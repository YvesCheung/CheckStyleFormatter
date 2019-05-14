package com.unionyy.mobile.reformat.core

interface Reporter {

    fun report(msg: String, code: CodeFragment)

    fun getReportCount(): Int
}