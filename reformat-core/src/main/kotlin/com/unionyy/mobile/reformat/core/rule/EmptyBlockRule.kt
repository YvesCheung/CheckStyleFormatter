package com.unionyy.mobile.reformat.core.rule

import com.unionyy.mobile.reformat.core.FormatContext
import com.unionyy.mobile.reformat.core.FormatRule
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.com.intellij.psi.JavaTokenType.END_OF_LINE_COMMENT
import org.jetbrains.kotlin.com.intellij.psi.JavaTokenType.LBRACE
import org.jetbrains.kotlin.com.intellij.psi.JavaTokenType.RBRACE
import org.jetbrains.kotlin.com.intellij.psi.PsiCatchSection
import org.jetbrains.kotlin.com.intellij.psi.PsiWhiteSpace
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.JavaElementType.CODE_BLOCK
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.PsiCommentImpl
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.PsiWhiteSpaceImpl
import org.jetbrains.kotlin.psi.psiUtil.children

/**
 * Created BY PYF 2019/5/16
 * email: pengyangfan@yy.com
 * try-catch 空代码块
 */

class EmptyBlockRule : FormatRule {

    private val toBeDone = mutableListOf<TryCatchEmptyBlock>()

    override fun beforeVisit(context: FormatContext) {
        super.beforeVisit(context)
        toBeDone.clear()
    }

    override fun visit(context: FormatContext, node: ASTNode) {
        //优先级放在执行完删除空行之后执行
        //do nothing
        if (node is PsiCatchSection) {
            val codeBlock = node.findChildByType(CODE_BLOCK) ?: return
            //什么都没有才塞注释，有的话就不用塞
            val hasOtherElement = codeBlock.children().firstOrNull {
                it.elementType != LBRACE && it.elementType != RBRACE && it !is PsiWhiteSpace
            } != null
            if (!hasOtherElement) {
                toBeDone.add(TryCatchEmptyBlock(codeBlock, context))
            }
        }
    }

    override fun afterVisit(context: FormatContext) {
        super.afterVisit(context)
        toBeDone.forEach {
            it.report(context)
        }
        toBeDone.forEach {
            try {
                it.addUselessComment()
            } catch (e: Exception) {
                context.notifyTextChange()
                it.report(context)
                throw e
            }
        }
    }

    class TryCatchEmptyBlock(val node: ASTNode, val context: FormatContext) {
        fun addUselessComment() {
            val lastSpace = node.children().last {
                it is PsiWhiteSpace
            }
            val nothing = PsiCommentImpl(END_OF_LINE_COMMENT, "//Do nothing.")
            //现在问题要把catch这一行的缩进算出来，这个取缩进的方法有点取巧
            val location = context.getCodeLocation(node.treeParent)
            val indent = "    ".repeat(location.column / 4 + 1)
            node.addChild(PsiWhiteSpaceImpl("\n" + indent), lastSpace)
            node.addChild(nothing, lastSpace)
        }

        fun report(context: FormatContext) {
            context.report("add comment at catch ${node.text}",
                context.getCodeFragment(node))
        }
    }
}