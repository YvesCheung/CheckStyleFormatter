package com.unionyy.mobile.reformat.core.rule

import com.unionyy.mobile.reformat.core.FormatContext
import com.unionyy.mobile.reformat.core.FormatRule
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.com.intellij.psi.PsiQualifiedReferenceElement

class DumpAST : FormatRule {

    private var visitOnce = true

    override fun beforeVisit(context: FormatContext) {
        visitOnce = true
    }

    override fun visit(context: FormatContext, node: ASTNode) {
        if (visitOnce) {
            visitOnce = false

            visitInner(node, 0) { n, level ->
                context.report(
                    msg = "  ".repeat(level) + "($level)" +  "$n ${n::class.java.simpleName}",
                    code = context.getCodeFragment(n))
            }
        }
    }

    private fun visitInner(node: ASTNode, level: Int, cb: (node: ASTNode, level: Int) -> Unit) {
        cb(node, level)

        if (node is PsiQualifiedReferenceElement) {
            return
        }
        node.getChildren(null).forEach { visitInner(it, level + 1, cb) }
    }
}