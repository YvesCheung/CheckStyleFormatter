package com.unionyy.mobile.reformat.core.reporter

import com.unionyy.mobile.reformat.core.CodeFragment
import com.unionyy.mobile.reformat.core.Reporter

class QuiteReporter : Reporter {

    private var totalCnt = 0

    override fun report(msg: String, code: CodeFragment) {
        totalCnt++
    }

    override fun getReportCount(): Int = totalCnt
}