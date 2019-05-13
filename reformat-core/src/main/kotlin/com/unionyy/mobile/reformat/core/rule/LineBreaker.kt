package com.unionyy.mobile.reformat.core.rule

import com.unionyy.mobile.reformat.core.FormatContext
import com.unionyy.mobile.reformat.core.FormatRule
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.com.intellij.lang.java.JavaLanguage
import org.jetbrains.kotlin.com.intellij.psi.JavaTokenType.COMMA
import org.jetbrains.kotlin.com.intellij.psi.JavaTokenType.END_OF_LINE_COMMENT
import org.jetbrains.kotlin.com.intellij.psi.JavaTokenType.LPARENTH
import org.jetbrains.kotlin.com.intellij.psi.JavaTokenType.RPARENTH
import org.jetbrains.kotlin.com.intellij.psi.PsiComment
import org.jetbrains.kotlin.com.intellij.psi.PsiWhiteSpace
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.FileElement
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.JavaElementType.FIELD
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.JavaElementType.PARAMETER
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.PsiCoreCommentImpl
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.PsiWhiteSpaceImpl
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.java.ParameterListElement
import org.jetbrains.kotlin.psi.psiUtil.children

class LineBreaker : FormatRule {

    companion object {

        private const val maxLineLength = 120

        private const val lineBreak = "\n"

        private const val indent = "    "
    }

    private data class Line(
        val lineNum: Int,
        val start: Int,
        val end: Int,
        val exceed: Boolean
    )

    //map lineNum -> line(startOffset, endOffset)
    private val lines = mutableMapOf<Int, Line>()

    private val toBeLineBreak = mutableListOf<Runnable>()

    override fun beforeVisit(context: FormatContext) {
        super.beforeVisit(context)
        lines.clear()
        toBeLineBreak.clear()
    }

    override fun visit(context: FormatContext, node: ASTNode) {
        if (context.language != JavaLanguage.INSTANCE) {
            return
        }
        if (node is FileElement) {
            visitWholeFile(context, node)
        } else {

            if (lines.isEmpty()) {
                return
            }

            val location = context.getCodeLocation(node)
            val line = lines.getValue(location.line)

            if (node is ParameterListElement) {
                val paramNum = node.children().count {
                    it.elementType == PARAMETER
                }
                if (paramNum > 4 || line.exceed) {
                    node.getChildren(null).forEach { child ->
                        if (child.elementType == COMMA ||
                            child.elementType == LPARENTH) {
                            val whiteSpaceExpect = child.treeNext
                            toBeLineBreak.add(
                                NormalLineBreak(
                                    whiteSpaceExpect,
                                    lineBreak(context, line.start, indent)
                                )
                            )
                        } else if (child.elementType == RPARENTH) {
                            toBeLineBreak.add(
                                NormalLineBreak(
                                    child,
                                    lineBreak(context, line.start)
                                )
                            )
                        }
                    }
                }
            } else if (node.elementType == END_OF_LINE_COMMENT) {
                if (line.exceed) {
                    val parent = node.treeParent
                    if (parent != null &&
                        parent.elementType == FIELD) {

                    }
                }
            }
        }
    }

    /**
     * 在 [toBeBreak] 的位置插入一个换行的 [lineBreak]
     */
    private class NormalLineBreak(
        val toBeBreak: ASTNode,
        val lineBreak: ASTNode
    ) : Runnable {
        override fun run() {
            if (toBeBreak is PsiWhiteSpace) {
                toBeBreak.treeParent.replaceChild(toBeBreak, lineBreak)
            } else {
                toBeBreak.treeParent.addChild(lineBreak, toBeBreak)
            }
        }
    }

    /**
     * 把注释挪到 field 的开头
     */
    private class moveCommentToFieldStart() : Runnable {
        override fun run() {

        }
    }

    private fun lineBreak(
        context: FormatContext,
        lineStart: Int,
        moreIndent: String = ""
    ): ASTNode {
        val startNode = context.fileContent.psi.findElementAt(lineStart)?.node
            ?: return PsiWhiteSpaceImpl("\n")
        var lastIndent = ""
        if (startNode is PsiWhiteSpace) {
            lastIndent = (startNode as PsiWhiteSpace).text.replace("\n", "")
        }
        return PsiWhiteSpaceImpl("\n" + lastIndent + moreIndent)
    }

    override fun afterVisit(context: FormatContext) {
        super.afterVisit(context)
        toBeLineBreak.forEach { it.run() }
    }

    private fun visitWholeFile(context: FormatContext, file: FileElement) {
//        var changeText: Boolean
//        do {
//            var lineStart = 0
//            var lineEnd = 0
//            changeText = false
//            lines.clear()
//
//            val lines = file.text.split("\n")
//            for ((index, line) in lines.withIndex()) {
//                lineEnd += line.length + 1
//
//                this.lines[index + 1] = Line(
//                    index + 1, lineStart, lineEnd, line.length > maxLineLength)
//
//                if (line.length > maxLineLength) {
//                    val element = file.psi.findElementAt(lineEnd)?.node
//                    if (element != null) {
//                        val lastNode = element.preNode {
//                            it !is PsiWhiteSpace && it.elementType.toString() != "SEMICOLON"
//                        } //最后一个有效元素
//
//                        changeText = context.handleComment(lastNode) {
//                            lineBreak(context, lineStart, it)
//                        }
//                    }
//                }
//
//                if (changeText) {
//                    break
//                }
//
//                lineStart = lineEnd + 1
//            }
//        } while (changeText)

        var lineStart = 0
        var lineEnd = 0
        val textLines = file.text.split("\n")
        for ((index, line) in textLines.withIndex()) {
            val lineNum = index + 1
            lineEnd += line.length + 1
            lines[lineNum] = Line(
                lineNum, lineStart, lineEnd, line.length > maxLineLength)
            lineStart = lineEnd + 1
        }
    }

    private fun FormatContext.handleComment(
        lastNode: ASTNode?,
        doLineBreak: (String) -> ASTNode
    ): Boolean {
        var changeText = false
        if (lastNode is PsiComment) { //以注释结尾
            val comment = lastNode as PsiComment
            val preComment: ASTNode? = lastNode.treePrev
            if (preComment is PsiWhiteSpace) {
                if ((preComment as PsiWhiteSpace).text.contains("\n")) {
                    val half = comment.text.length / 2
                    val halfComment = PsiCoreCommentImpl(
                        END_OF_LINE_COMMENT,
                        comment.text.substring(0, half))
                    val otherComment = PsiCoreCommentImpl(
                        END_OF_LINE_COMMENT,
                        "//" + comment.text.substring(half))
                    val cutPoint = lastNode.treeNext
                    lastNode.treeParent.replaceChild(lastNode, halfComment)
                    cutPoint.treeParent.addChild(doLineBreak(""), cutPoint)
                    cutPoint.treeParent.addChild(otherComment, cutPoint)
                    this.report("cut the long comment.",
                        this.getCodeFragment(cutPoint), true)
                    changeText = true
                }
            }
            if (!changeText && preComment != null) {
                this.report("add line break before comment.",
                    this.getCodeFragment(preComment), true)
                lastNode.treeParent.addChild(doLineBreak(indent), lastNode)
                changeText = true
            }
        }
        return changeText
    }
}