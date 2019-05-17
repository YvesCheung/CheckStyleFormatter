package com.unionyy.mobile.reformat.core.rule

import com.unionyy.mobile.reformat.core.FormatContext
import com.unionyy.mobile.reformat.core.FormatRule
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.com.intellij.psi.PsiExpressionList
import org.jetbrains.kotlin.com.intellij.psi.PsiWhiteSpace

/**
 * 连续的语句不应该被打断
 */
class ContinuousExpression : FormatRule {

    private val nodeToRemove = mutableListOf<ASTNode>()

    override fun beforeVisit(context: FormatContext) {
        super.beforeVisit(context)
        nodeToRemove.clear()
    }

    override fun visit(context: FormatContext, node: ASTNode) {
        if (node is PsiExpressionList) {
            if (node.treePrev is PsiWhiteSpace) {
                context.report("Invalid whitespace before ")
                nodeToRemove.add(node.treePrev)
            }
        }
    }
}