package com.unionyy.mobile.reformat.core.rule

import com.unionyy.mobile.reformat.core.FormatContext
import com.unionyy.mobile.reformat.core.FormatRule
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.com.intellij.psi.JavaTokenType.EQ
import org.jetbrains.kotlin.com.intellij.psi.JavaTokenType.LBRACKET
import org.jetbrains.kotlin.com.intellij.psi.JavaTokenType.RBRACKET
import org.jetbrains.kotlin.com.intellij.psi.PsiLocalVariable
import org.jetbrains.kotlin.com.intellij.psi.PsiTypeElement
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.JavaElementType.FIELD

/**
 * 调整数组的错误写法 int a[] -> int[] a
 */
class ArrayBracket : FormatRule {

    private val bracketList = mutableListOf<MoveBracket>()

    private class MoveBracket(
        val typeElement: ASTNode,
        val bracket: List<ASTNode>
    ) : Runnable {

        override fun run() {
            bracket.forEach { node ->
                node.treeParent.removeChild(node)
                typeElement.addChild(node, null)
            }
        }
    }

    override fun beforeVisit(context: FormatContext) {
        super.beforeVisit(context)
        bracketList.clear()
    }

    override fun visit(context: FormatContext, node: ASTNode) {
        if (node.elementType == FIELD ||
            node is PsiLocalVariable) {
            handleBracket(context, node)
        }
    }

    override fun afterVisit(context: FormatContext) {
        bracketList.forEach { it.run() }
        super.afterVisit(context)
    }

    private fun handleBracket(context: FormatContext, node: ASTNode) {
        val bracket = mutableListOf<ASTNode>()
        var readBracket = false
        var beforeEq = true
        var typeElement: ASTNode? = null

        node.getChildren(null).forEach { child ->
            when {
                child.elementType == EQ -> {
                    beforeEq = false
                }
                child is PsiTypeElement -> {
                    typeElement = child
                }
                beforeEq -> when {
                    child.elementType == LBRACKET -> {
                        readBracket = true
                        bracket.add(child)
                    }
                    child.elementType == RBRACKET -> {
                        bracket.add(child)
                        readBracket = false
                    }
                    readBracket -> {
                        bracket.add(child)
                    }
                }
            }
        }

        val type = typeElement
        if (type != null && bracket.isNotEmpty()) {
            context.report("Fix array bracket: ${node.text}.",
                context.getCodeFragment(node))
            bracketList.add(MoveBracket(type, bracket))
        }
    }
}