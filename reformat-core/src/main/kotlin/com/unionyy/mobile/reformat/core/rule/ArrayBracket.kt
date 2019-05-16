package com.unionyy.mobile.reformat.core.rule

import com.unionyy.mobile.reformat.core.FormatContext
import com.unionyy.mobile.reformat.core.FormatRule
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.com.intellij.psi.PsiLocalVariable
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.JavaElementType.FIELD

class ArrayBracket : FormatRule {

    override fun visit(context: FormatContext, node: ASTNode) {
        if (node.elementType == FIELD ||
            node is PsiLocalVariable) {
            handleBracket(node)
        }
    }

    private fun handleBracket(node: ASTNode) {

    }
}