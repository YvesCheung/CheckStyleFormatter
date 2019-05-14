package com.unionyy.mobile.reformat.core.reporter

import com.unionyy.mobile.reformat.core.CodeFragment
import com.unionyy.mobile.reformat.core.Reporter
import java.io.File
import java.io.StringWriter
import java.lang.Appendable

class WriterReporter(private val writer: Appendable = StringWriter()) : Reporter {

    private var currentFile: String = ""

    private var currentShortName: String = ""

    private var index = 0

    private var totalCnt = 0

    override fun report(msg: String, code: CodeFragment) {
        val file = code.fileName
        if (currentFile == file) {
            index++
        } else {
            index = 0

            currentFile = file
            val sep = file.lastIndexOf(File.separatorChar)
            currentShortName =
                if (sep < 0) {
                    currentFile
                } else {
                    currentFile.substring(sep)
                }
            writer.append("$file:\n")
        }

        writer.append(" $index. " +
            "(line:${code.startPos.line}, column:${code.startPos.column}): $msg\n")
        totalCnt++
    }

    override fun getReportCount(): Int = totalCnt
}