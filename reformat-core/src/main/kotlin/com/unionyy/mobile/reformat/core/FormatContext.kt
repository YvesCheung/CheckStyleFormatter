package com.unionyy.mobile.reformat.core

import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.com.intellij.lang.Language
import java.util.ArrayList

@Suppress("MemberVisibilityCanBePrivate", "unused", "CanBeParameter")
class FormatContext(
    val fileName: String,
    val fileContent: ASTNode,
    val language: Language,
    private val reporter: Reporter,
    private val rules: Set<FormatRule>
) {

    private val fileText: String
        get() = fileContent.text

    private var locationGetter: ((offset: Int) -> Location)? = null

    fun getCodeFragment(node: ASTNode): CodeFragment {
        val range = node.textRange
        val getLocation = locationGetter
            ?: calculateLineColByOffset(fileText).also { locationGetter = it }
        val start = getLocation(range.startOffset)
        val end = getLocation(range.endOffset)
        return CodeFragment(fileName, start, end)
    }

    fun getCodeLocation(node: ASTNode): Location {
        val getLocation = locationGetter
            ?: calculateLineColByOffset(fileText).also { locationGetter = it }
        return getLocation(node.textRange.startOffset)
    }

    fun report(msg: String, code: CodeFragment, changeText: Boolean = false) {
        reporter.report(msg, code)
        if (changeText) {
            locationGetter = null
        }
    }

    private fun calculateLineColByOffset(text: String): (offset: Int) -> Location {
        var i = -1
        val e = text.length
        val arr = ArrayList<Int>()
        do {
            arr.add(i + 1)
            i = text.indexOf('\n', i + 1)
        } while (i != -1)
        arr.add(e + if (arr.last() == e) 1 else 0)
        val segmentTree = SegmentTree(arr.toTypedArray())
        return { offset ->
            val line = segmentTree.indexOf(offset)
            if (line != -1) {
                val col = offset - segmentTree.get(line).left
                Location(line + 1, col + 1, offset)
            } else {
                Location(1, 1, offset)
            }
        }
    }

    private class SegmentTree(sortedArray: Array<Int>) {

        private val segments: List<Segment>

        init {
            require(sortedArray.size > 1) { "At least two data points are required" }
            sortedArray.reduce { r, v -> require(r <= v) { "Data points are not sorted (ASC)" }; v }
            segments = sortedArray.take(sortedArray.size - 1)
                .mapIndexed { i: Int, v: Int -> Segment(v, sortedArray[i + 1] - 1) }
        }

        fun get(i: Int): Segment = segments[i]
        fun indexOf(v: Int): Int = binarySearch(v, 0, this.segments.size - 1)

        private fun binarySearch(v: Int, l: Int, r: Int): Int = when {
            l > r -> -1
            else -> {
                val i = l + (r - l) / 2
                val s = segments[i]
                if (v < s.left) binarySearch(v, l, i - 1) else (if (s.right < v) binarySearch(v, i + 1, r) else i)
            }
        }
    }

    private data class Segment(val left: Int, val right: Int)
}