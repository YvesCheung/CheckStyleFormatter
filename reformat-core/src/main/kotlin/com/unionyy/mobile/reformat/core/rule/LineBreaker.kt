package com.unionyy.mobile.reformat.core.rule

import com.unionyy.mobile.reformat.core.FormatContext
import com.unionyy.mobile.reformat.core.FormatRule
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.com.intellij.lang.java.JavaLanguage
import org.jetbrains.kotlin.com.intellij.psi.JavaTokenType.COMMA
import org.jetbrains.kotlin.com.intellij.psi.JavaTokenType.DOT
import org.jetbrains.kotlin.com.intellij.psi.JavaTokenType.END_OF_LINE_COMMENT
import org.jetbrains.kotlin.com.intellij.psi.JavaTokenType.LPARENTH
import org.jetbrains.kotlin.com.intellij.psi.JavaTokenType.RPARENTH
import org.jetbrains.kotlin.com.intellij.psi.PsiDeclarationStatement
import org.jetbrains.kotlin.com.intellij.psi.PsiExpressionStatement
import org.jetbrains.kotlin.com.intellij.psi.PsiReferenceExpression
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

        private fun lineBreak(
            context: FormatContext,
            lineStart: Int,
            moreIndent: String = ""
        ): ASTNode {
            val startNode = context.fileContent.psi.findElementAt(lineStart)?.node
                ?: return PsiWhiteSpaceImpl(lineBreak)
            return lineBreak(startNode, moreIndent)
        }

        private fun lineBreak(
            startNode: ASTNode,
            moreIndent: String = ""
        ): ASTNode {
            var lastIndent = ""
            if (startNode is PsiWhiteSpace) {
                lastIndent = getIndent((startNode as PsiWhiteSpace).text)
            } else if (startNode.treePrev is PsiWhiteSpace) {
                lastIndent = getIndent((startNode.treePrev as PsiWhiteSpace).text)
            }
            return PsiWhiteSpaceImpl(lineBreak + lastIndent + moreIndent)
        }

        private fun getIndent(txt: String): String {
            val idx = txt.lastIndexOf(lineBreak)
            return if (idx in 0..txt.length - 2) {
                txt.substring(idx + 1)
            } else {
                txt
            }
        }
    }

    private data class Line(
        val lineNum: Int,
        val start: Int,
        val end: Int,
        val exceed: Boolean
    )

    //map lineNum -> line(startOffset, endOffset)
    private val lines = mutableMapOf<Int, Line>()

    private val toBeLineBreak = mutableListOf<LineBreakAction>()

    private interface LineBreakAction {

        fun run(context: FormatContext)

        fun report(context: FormatContext)
    }

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
            visitWholeFile(node)
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
                    if (parent != null && (
                            parent.elementType == FIELD ||
                                parent is PsiDeclarationStatement ||
                                parent is PsiExpressionStatement
                            )
                    ) {
                        toBeLineBreak.add(
                            MoveCommentToStart(
                                node,
                                parent,
                                lineBreak(context, parent.startOffset - 1)
                            )
                        )
                    } else {
                        toBeLineBreak.add(
                            NormalLineBreak(
                                node,
                                lineBreak(context, line.start)
                            )
                        )
                    }

                    toBeLineBreak.add(
                        CutComment(node)
                    )
                }
            } else if (node.elementType == DOT) {
                //方法调用，断
                if (line.exceed) {
                    val parent = node.treeParent
                    if (parent != null && (parent is PsiReferenceExpression)) {
                        toBeLineBreak.add(
                            NormalLineBreak(
                                node,
                                lineBreak(
                                    context,
                                    line.start,
                                    indent + indent
                                )
                            )
                        )
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
    ) : LineBreakAction {

        override fun run(context: FormatContext) {
            when {
                toBeBreak is PsiWhiteSpace -> {
                    toBeBreak.treeParent.replaceChild(toBeBreak, lineBreak)
                }
                toBeBreak.treePrev is PsiWhiteSpace -> {
                    toBeBreak.treeParent.replaceChild(toBeBreak.treePrev, lineBreak)
                }
                else -> {
                    toBeBreak.treeParent.addChild(lineBreak, toBeBreak)
                }
            }
        }

        override fun report(context: FormatContext) {
            context.report(
                "Add a line break.",
                context.getCodeFragment(toBeBreak))
        }
    }

    /**
     * 把注释 [comment] 挪到 [parent] 的开头
     */
    private class MoveCommentToStart(
        val comment: ASTNode,
        val parent: ASTNode,
        val lineBreak: ASTNode
    ) : LineBreakAction {

        override fun run(context: FormatContext) {
            val whiteSpace = comment.treePrev
            if (whiteSpace is PsiWhiteSpace) {
                parent.removeChild(whiteSpace)
            }
            parent.removeChild(comment)
            val anchor = parent.children().firstOrNull()
            parent.addChild(comment, anchor)
            parent.addChild(lineBreak, anchor)
        }

        override fun report(context: FormatContext) {
            context.report(
                "Move comment to the start.",
                context.getCodeFragment(comment))
        }
    }

    /**
     * 如果注释 [comment] 过长，需要裁剪
     */
    private class CutComment(
        val comment: ASTNode
    ) : LineBreakAction {

        override fun run(context: FormatContext) {
            context.notifyTextChange()

            var startNode = comment
            var totalLength = comment.textLength
            val whiteSpace = comment.treePrev
            if (whiteSpace is PsiWhiteSpace) {
                startNode = whiteSpace
                totalLength += getIndent((whiteSpace as PsiWhiteSpace).text).length
            }

            if (totalLength >= maxLineLength) {
                val lineBreakNode = {
                    lineBreak(startNode, "")
                }
                val whiteSpaceLen = lineBreakNode().textLength

                fun checkAndCutComment(node: ASTNode) {
                    if (node.textLength + whiteSpaceLen >= maxLineLength) {
                        val half = node.textLength / 2
                        val halfComment = PsiCoreCommentImpl(
                            END_OF_LINE_COMMENT,
                            node.text.substring(0, half))
                        val otherComment = PsiCoreCommentImpl(
                            END_OF_LINE_COMMENT,
                            "//" + node.text.substring(half))
                        val parent = node.treeParent
                        var cutPoint = node.treeNext
                        if (cutPoint.treeParent !== parent) {
                            cutPoint = null
                        }
                        parent.replaceChild(node, halfComment)
                        parent.addChild(lineBreakNode(), cutPoint)
                        parent.addChild(otherComment, cutPoint)
                        checkAndCutComment(halfComment)
                        checkAndCutComment(otherComment)
                    }
                }

                checkAndCutComment(comment)
            }
        }

        override fun report(context: FormatContext) {
            context.report(
                "Cut the too long comment.",
                context.getCodeFragment(comment))
        }
    }

    override fun afterVisit(context: FormatContext) {
        super.afterVisit(context)
        toBeLineBreak.forEach { it.run(context) }
        context.notifyTextChange()
        toBeLineBreak.forEach { it.report(context) }
    }

    private fun visitWholeFile(file: FileElement) {
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
}