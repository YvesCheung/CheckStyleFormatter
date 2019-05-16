package com.unionyy.mobile.reformat.core.rule

import com.unionyy.mobile.reformat.core.FormatContext
import com.unionyy.mobile.reformat.core.FormatRule
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.com.intellij.psi.JavaTokenType
import org.jetbrains.kotlin.com.intellij.psi.PsiCodeBlock
import org.jetbrains.kotlin.com.intellij.psi.PsiSwitchLabelStatement
import org.jetbrains.kotlin.com.intellij.psi.PsiSwitchStatement
import org.jetbrains.kotlin.com.intellij.psi.PsiWhiteSpace
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.PsiWhiteSpaceImpl
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.java.PsiBreakStatementImpl
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.java.PsiJavaTokenImpl
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.java.PsiKeywordImpl
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.java.PsiSwitchLabelStatementImpl
import org.jetbrains.kotlin.psi.psiUtil.children

/**
 * Created BY PYF 2019/5/16
 * email: pengyangfan@yy.com
 * 主要拿来添加switch case中遗漏的default块
 */
class AddSwitchDefaultCase : FormatRule {

    private val toBeAddDefault = mutableListOf<AddDefaultCaseAction>()

    override fun visit(context: FormatContext, node: ASTNode) {
        if (node is PsiSwitchStatement) {
            val codeBlock = node.lastChildNode
            if (codeBlock !is PsiCodeBlock) {
                return
            }
            var haveDefault = false
            codeBlock.children().forEach {
                if (it is PsiSwitchLabelStatement) {
                    if (it.firstChildNode.text == "default") {
                        haveDefault = true
                    }
                }
            }
            if (!haveDefault) {
                toBeAddDefault.add(AddDefaultCaseAction(codeBlock))
            }
        }
    }

    override fun beforeVisit(context: FormatContext) {
        super.beforeVisit(context)
        toBeAddDefault.clear()
    }

    override fun afterVisit(context: FormatContext) {
        super.afterVisit(context)

        toBeAddDefault.forEach {
            it.report(context)
        }
        toBeAddDefault.forEach {
            try {
                it.add()
            } catch (e: Exception) {
                context.notifyTextChange()
                it.report(context)
                throw e
            }
        }
    }

    private class AddDefaultCaseAction(
        val parent: ASTNode
    ) {
        fun add() {
            val spaceStandard = parent.children().first {
                it is PsiWhiteSpace && it.treeNext is PsiSwitchLabelStatement
            }

            val psiWhiteSpace2 = PsiWhiteSpaceImpl(spaceStandard.text)
            parent.replaceChild(parent.lastChildNode.treePrev, psiWhiteSpace2)

            val switchLabelStatement = PsiSwitchLabelStatementImpl()
            parent.addChild(switchLabelStatement, parent.lastChildNode)

            switchLabelStatement.addChild(PsiKeywordImpl(JavaTokenType.DEFAULT_KEYWORD, "default"))
            switchLabelStatement.addChild(PsiJavaTokenImpl(JavaTokenType.COLON, ":"))
            //还要算缩进
            val psiWhiteSpace1 = PsiWhiteSpaceImpl(spaceStandard.text + "    ")
            parent.addChild(psiWhiteSpace1, parent.lastChildNode)

            val breakStatement = PsiBreakStatementImpl()
            parent.addChild(breakStatement, parent.lastChildNode)

            breakStatement.addChild(PsiKeywordImpl(JavaTokenType.BREAK_KEYWORD, "break"))
            breakStatement.addChild(PsiJavaTokenImpl(JavaTokenType.SEMICOLON, ";"))

            val psiWhiteSpace3 = PsiWhiteSpaceImpl(spaceStandard.text.subSequence(0, spaceStandard.textLength - 4))
            parent.addChild(psiWhiteSpace3, parent.lastChildNode)
        }

        fun report(context: FormatContext) {
            context.report("add default case at last of switch case at ${context.getCodeLocation(parent)}",
                context.getCodeFragment(parent))
        }
    }
}