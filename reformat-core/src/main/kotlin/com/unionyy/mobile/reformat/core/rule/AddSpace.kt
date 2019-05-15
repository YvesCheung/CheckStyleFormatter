package com.unionyy.mobile.reformat.core.rule

import com.unionyy.mobile.reformat.core.FormatContext
import com.unionyy.mobile.reformat.core.FormatRule
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.com.intellij.lang.java.JavaLanguage
import org.jetbrains.kotlin.com.intellij.psi.JavaTokenType.END_OF_LINE_COMMENT
import org.jetbrains.kotlin.com.intellij.psi.PsiComment
import org.jetbrains.kotlin.com.intellij.psi.PsiWhiteSpace
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.PsiWhiteSpaceImpl

/**
 * Created BY PYF 2019/5/15
 * email: pengyangfan@yy.com
 */
class AddSpace : FormatRule {

    private val toBeAddSpace = mutableListOf<AddSpaceAction>()

    override fun beforeVisit(context: FormatContext) {
        super.beforeVisit(context)
        toBeAddSpace.clear()
    }

    override fun visit(context: FormatContext, node: ASTNode) {
        if (context.language != JavaLanguage.INSTANCE) {
            return
        }

        when (context.scanningTimes) {
            1 -> {
                if (node is PsiComment) {
                    addSpaceComment(context, node)
                }
            }
        }
    }

    override fun afterVisit(context: FormatContext) {
        super.afterVisit(context)
        toBeAddSpace.forEach {
            it.addSpace()
        }
    }

    private interface AddSpaceAction {
        fun addSpace()
    }

    private class AddSpaceSomewhere(
        val parent: ASTNode,
        val beforeNode: ASTNode
    ) : AddSpaceAction {
        override fun addSpace() {
            parent.addChild(PsiWhiteSpaceImpl(" "), beforeNode)
        }
    }

    private fun addSpaceComment(
        context: FormatContext,
        node: ASTNode
    ) {
        if (node.elementType == END_OF_LINE_COMMENT && node.treePrev !is PsiWhiteSpace) {
            toBeAddSpace.add(AddSpaceSomewhere(node.treeParent, node))
        }
    }
}