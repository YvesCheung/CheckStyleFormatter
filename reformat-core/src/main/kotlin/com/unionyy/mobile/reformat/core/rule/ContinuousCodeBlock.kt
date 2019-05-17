package com.unionyy.mobile.reformat.core.rule

import com.unionyy.mobile.reformat.core.FormatContext
import com.unionyy.mobile.reformat.core.FormatRule
import com.unionyy.mobile.reformat.core.utils.nextNode
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.com.intellij.psi.JavaTokenType
import org.jetbrains.kotlin.com.intellij.psi.PsiBlockStatement
import org.jetbrains.kotlin.com.intellij.psi.PsiCatchSection
import org.jetbrains.kotlin.com.intellij.psi.PsiCodeBlock
import org.jetbrains.kotlin.com.intellij.psi.PsiComment
import org.jetbrains.kotlin.com.intellij.psi.PsiIfStatement
import org.jetbrains.kotlin.com.intellij.psi.PsiWhiteSpace
import org.jetbrains.kotlin.psi.psiUtil.children
import java.util.*

/**
 * 移动连续代码块之间的注释
 *
 * if(){
 * }
 * //注释
 * else{
 * }
 *
 * if(){
 * }else{
 * //注释
 * }
 */
class ContinuousCodeBlock : FormatRule {

    private class Movement(
        val comments: List<Pair<ASTNode, Boolean>>,
        val anchor: ASTNode
    ) : Runnable {

        override fun run() {
            comments.forEach { (comment, valid) ->
                comment.treeParent.removeChild(comment)
                if (valid) {
                    anchor.treeParent.addChild(comment, anchor)
                }
            }
        }
    }

    private val handleList = mutableListOf<Movement>()

    override fun beforeVisit(context: FormatContext) {
        super.beforeVisit(context)
        handleList.clear()
    }

    override fun visit(context: FormatContext, node: ASTNode) {
        if (node is PsiCatchSection ||
            node.elementType == JavaTokenType.ELSE_KEYWORD) {
            handleElseOrCatch(context, node)
        }
    }

    override fun afterVisit(context: FormatContext) {
        handleList.forEach { it.run() }
        super.afterVisit(context)
    }

    private fun handleElseOrCatch(
        context: FormatContext,
        node: ASTNode //else catch..
    ) {

        val comments = node.getTargetComment()
        if (comments.isNotEmpty()) {
            val anchor = node.getTargetBlockAnchor() ?: return

            context.report("Move comment into the code block: ${node.text}.",
                context.getCodeFragment(node))

            handleList.add(Movement(comments, anchor))
        }
    }

    private fun ASTNode.findCodeBlock(): ASTNode? {
        var codeBlock = this.children().find { it is PsiCodeBlock }
        if (codeBlock == null) {
            codeBlock = this.nextNode { it is PsiBlockStatement }
                ?.getChildren(null)?.find { it is PsiCodeBlock }
        }
        if (codeBlock == null) {
            codeBlock = this.nextNode { it is PsiIfStatement }
                ?.getChildren(null)?.find { it is PsiBlockStatement }
                ?.getChildren(null)?.find { it is PsiCodeBlock }
        }
        return codeBlock
    }

    private fun ASTNode.getTargetBlockAnchor(): ASTNode? {
        val codeBlock = this.findCodeBlock()
        var anchor: ASTNode? = codeBlock?.getChildren(null)?.firstOrNull()
        if (anchor != null && anchor.elementType == JavaTokenType.LBRACE) {
            anchor = anchor.treeNext
        }
        return anchor
    }

    private fun ASTNode.getTargetComment(): List<Pair<ASTNode, Boolean>> {
        var p: ASTNode? = this.treePrev
        val result = LinkedList<Pair<ASTNode, Boolean>>()
        var valid = false
        while (p != null) {
            if (p is PsiWhiteSpace) {
                result.addFirst(p to valid)
            } else if (p is PsiComment) {
                valid = true
                result.addFirst(p to valid)
            } else if (p is PsiBlockStatement) {
                p = p.getChildren(null).lastOrNull()
                continue
            } else {
                break
            }
            p = p.treePrev
        }
        return if (valid) result else listOf()
    }
}