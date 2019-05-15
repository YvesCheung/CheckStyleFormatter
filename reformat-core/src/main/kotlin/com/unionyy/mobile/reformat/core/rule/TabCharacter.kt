package com.unionyy.mobile.reformat.core.rule

import com.unionyy.mobile.reformat.core.FormatContext
import com.unionyy.mobile.reformat.core.FormatRule
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.com.intellij.psi.PsiWhiteSpace
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.PsiWhiteSpaceImpl

class TabCharacter : FormatRule {

    companion object {

        private const val TAB = "\t"

        private const val Indent = "    "
    }

    private val replaceTab = mutableListOf<ASTNode>()

    override fun beforeVisit(context: FormatContext) {
        super.beforeVisit(context)
        replaceTab.clear()
    }

    override fun visit(context: FormatContext, node: ASTNode) {
        if (node is PsiWhiteSpace) {
            val text = (node as PsiWhiteSpace).text
            if (text.contains(TAB)) {
                replaceTab.add(node)
                context.report(
                    "replace '\\t' with '$Indent'.",
                    context.getCodeFragment(node))
            }
        }
    }

    override fun afterVisit(context: FormatContext) {
        replaceTab.forEach { whiteSpace ->
            try {
                val newText = whiteSpace.text.replace(TAB, Indent)
                val newWhiteSpace = PsiWhiteSpaceImpl(newText)
                whiteSpace.treeParent.replaceChild(whiteSpace, newWhiteSpace)
            } finally {
                context.notifyTextChange()
                context.report("There is a error when replace Tab.",
                    context.getCodeFragment(whiteSpace))
            }
        }
        super.afterVisit(context)
    }
}