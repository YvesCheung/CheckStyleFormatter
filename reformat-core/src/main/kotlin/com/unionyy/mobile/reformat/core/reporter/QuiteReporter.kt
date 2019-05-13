package com.unionyy.mobile.reformat.core.reporter

import com.unionyy.mobile.reformat.core.CodeFragment
import com.unionyy.mobile.reformat.core.Reporter

class QuiteReporter : Reporter {

    private var reportCnt = mutableMapOf<String, Int>()

    private var totalCnt = 0

    override fun report(msg: String, code: CodeFragment) {
        reportCnt.merge(code.fileName, 1) { i, j -> i + j }
        totalCnt++
    }

    override fun getReportCount(): Int = totalCnt

    override fun getReportCount(fileName: String): Int = reportCnt[fileName] ?: 0
}