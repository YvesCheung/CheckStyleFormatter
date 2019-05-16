package com.unionyy.mobile.reformat.core.rule

import com.unionyy.mobile.reformat.core.FormatContext
import com.unionyy.mobile.reformat.core.FormatRule
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.com.intellij.psi.JavaTokenType.STATIC_KEYWORD
import org.jetbrains.kotlin.com.intellij.psi.PsiKeyword
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.JavaElementType.MODIFIER_LIST
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.PsiWhiteSpaceImpl
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.java.FieldElement
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.java.PsiKeywordImpl
import org.jetbrains.kotlin.psi.psiUtil.children

/**
 * Created BY PYF 2019/5/16
 * email: pengyangfan@yy.com
 */
class ModifierRule : FormatRule {

    private val toBeDeal = mutableListOf<ModifierOperation>()

    override fun beforeVisit(context: FormatContext) {
        super.beforeVisit(context)
        toBeDeal.clear()
    }

    override fun visit(context: FormatContext, node: ASTNode) {
        //do nothing
        if (node is FieldElement) {
            val modifierList = node.findChildByType(MODIFIER_LIST)
            val hasStatic = modifierList?.findChildByType(STATIC_KEYWORD) != null
            val finalModifier = modifierList?.children()?.firstOrNull {
                //只有final，没有static
                it is PsiKeyword && (it as PsiKeyword).text == "final" && !hasStatic
            }
            finalModifier?.let {
                toBeDeal.add(StaticModifierAdd(finalModifier))
            }
        }
    }

    override fun afterVisit(context: FormatContext) {
        super.afterVisit(context)
        toBeDeal.forEach {
            it.report(context)
        }
        toBeDeal.forEach {
            try {
                it.operate()
            } finally {
                context.notifyTextChange()
                it.report(context)
            }
        }
    }

    interface ModifierOperation {
        fun operate()

        fun report(context: FormatContext)
    }

    class StaticModifierAdd(val node: ASTNode) : ModifierOperation {
        override fun operate() {
            val staticModifer = PsiKeywordImpl(STATIC_KEYWORD, "static")
            node.treeParent.addChild(staticModifer, node)
            node.treeParent.addChild(PsiWhiteSpaceImpl(" "), node)
        }

        override fun report(context: FormatContext) {
            context.report("Add a static modifier before ${context.getCodeLocation(node)}",
                context.getCodeFragment(node))
        }
    }
}