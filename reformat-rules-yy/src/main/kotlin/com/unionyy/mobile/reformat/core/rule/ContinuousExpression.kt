package com.unionyy.mobile.reformat.core.rule

import com.unionyy.mobile.reformat.core.FormatContext
import com.unionyy.mobile.reformat.core.FormatRule
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.com.intellij.psi.PsiExpressionList
import org.jetbrains.kotlin.com.intellij.psi.PsiMethodCallExpression
import org.jetbrains.kotlin.com.intellij.psi.PsiWhiteSpace
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.JavaElementType.ENUM_CONSTANT
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.JavaElementType.METHOD
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.JavaElementType.PARAMETER_LIST

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

        if (node.isParamList()) {
            if (node.treeParent.isMethod() && node.treePrev is PsiWhiteSpace) {
                context.report("Invalid whitespace before parameter: $node",
                    context.getCodeFragment(node))
                nodeToRemove.add(node.treePrev)
            }
        }
    }

    private fun ASTNode.isParamList(): Boolean =
        this is PsiExpressionList || this.elementType == PARAMETER_LIST

    private fun ASTNode.isMethod(): Boolean =
        this is PsiMethodCallExpression ||
            this.elementType == METHOD ||
            this.elementType == ENUM_CONSTANT

    override fun afterVisit(context: FormatContext) {
        nodeToRemove.forEach { node ->
            node.treeParent.removeChild(node)
        }
        super.afterVisit(context)
    }
}