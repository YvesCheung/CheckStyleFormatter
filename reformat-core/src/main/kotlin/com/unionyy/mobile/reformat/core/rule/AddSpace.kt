package com.unionyy.mobile.reformat.core.rule

import com.unionyy.mobile.reformat.core.FormatContext
import com.unionyy.mobile.reformat.core.FormatRule
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.com.intellij.psi.JavaTokenType.COMMA
import org.jetbrains.kotlin.com.intellij.psi.JavaTokenType.LBRACE
import org.jetbrains.kotlin.com.intellij.psi.JavaTokenType.RBRACE
import org.jetbrains.kotlin.com.intellij.psi.JavaTokenType.SEMICOLON
import org.jetbrains.kotlin.com.intellij.psi.PsiWhiteSpace
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.PsiWhiteSpaceImpl

/**
 * Created BY PYF 2019/5/15
 * email: pengyangfan@yy.com
 */
class AddSpace : FormatRule {

    private val toBeAddSpace = mutableListOf<AddSpaceAction>()
    private val expelChar = setOf(LBRACE, RBRACE, SEMICOLON, COMMA)

    override fun beforeVisit(context: FormatContext) {
        super.beforeVisit(context)
        toBeAddSpace.clear()
    }

    override fun visit(context: FormatContext, node: ASTNode) {
        if (expelChar.contains(node.elementType)) {
            addSpaceComment(context, node)
        }
    }

    override fun afterVisit(context: FormatContext) {
        super.afterVisit(context)
        toBeAddSpace.forEach {
            it.report(context)
        }
        toBeAddSpace.forEach {
            try {
                it.spaceOperation()
            } finally {
                context.notifyTextChange()
                it.report(context)
            }
        }
    }

    private interface AddSpaceAction {
        fun spaceOperation()

        fun report(context: FormatContext)
    }

    private class AddSpaceSomewhere(
        val parent: ASTNode,
        val beforeNode: ASTNode
    ) : AddSpaceAction {
        override fun spaceOperation() {
            parent.addChild(PsiWhiteSpaceImpl(" "), beforeNode)
        }

        override fun report(context: FormatContext) {
            context.report("Add a space before ${context.getCodeLocation(beforeNode)}",
                context.getCodeFragment(beforeNode))
        }
    }

    private class SubSpaceBeforeSemi(
        val replaceNode: ASTNode
    ) : AddSpaceAction {
        override fun spaceOperation() {
            replaceNode.treeParent.replaceChild(replaceNode, PsiWhiteSpaceImpl(""))
        }

        override fun report(context: FormatContext) {
            context.report("remove a space at ${context.getCodeLocation(replaceNode)}",
                context.getCodeFragment(replaceNode))
        }
    }

    private fun addSpaceComment(
        context: FormatContext,
        node: ASTNode
    ) {
        if (node.treeNext != null && node.treeNext !is PsiWhiteSpace) {
            toBeAddSpace.add(AddSpaceSomewhere(node.treeParent, node.treeNext))
        } else if (node.treeParent != null && node.treeParent.treeNext != null &&
            node.treeParent.treeNext !is PsiWhiteSpace) {
            if (node.treeNext == null) {
                toBeAddSpace.add(AddSpaceSomewhere(node.treeParent.treeParent, node.treeParent.treeNext))
            } else {
                toBeAddSpace.add(AddSpaceSomewhere(node.treeParent, node.treeNext))
            }
        } else if (node.elementType == SEMICOLON) {
            subSpaceBeforeSemiColon(context, node)
        }
    }

    private fun subSpaceBeforeSemiColon(
        context: FormatContext,
        node: ASTNode
    ) {
        val prev = node.treePrev
        if (prev != null && prev is PsiWhiteSpace) {
            // ; 前有空格
            toBeAddSpace.add(SubSpaceBeforeSemi(prev))
        }
    }
}