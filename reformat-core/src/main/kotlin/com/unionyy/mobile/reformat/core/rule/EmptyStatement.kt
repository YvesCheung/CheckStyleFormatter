package com.unionyy.mobile.reformat.core.rule

import com.unionyy.mobile.reformat.core.FormatContext
import com.unionyy.mobile.reformat.core.FormatRule
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.com.intellij.psi.PsiEmptyStatement

class EmptyStatement : FormatRule {

    private val emptyStatement = mutableListOf<ASTNode>()

    override fun beforeVisit(context: FormatContext) {
        super.beforeVisit(context)
        emptyStatement.clear()
    }

    override fun visit(context: FormatContext, node: ASTNode) {
        if (node is PsiEmptyStatement) {
            context.report("remove empty statement: " +
                "${(node as PsiEmptyStatement).text}.",
                context.getCodeFragment(node))
            emptyStatement.add(node)
        }
    }

    override fun afterVisit(context: FormatContext) {
        emptyStatement.forEach { node ->
            node.treeParent.removeChild(node)
        }
        super.afterVisit(context)
    }
}