package com.unionyy.mobile.reformat.core.rule

import com.unionyy.mobile.reformat.core.FormatContext
import com.unionyy.mobile.reformat.core.FormatRule
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.com.intellij.psi.PsiBinaryExpression

/**
 * Created BY PYF 2019/5/17
 * email: pengyangfan@yy.com
 * 简化条件表达式
 */
class SimplifyBooleanRule : FormatRule {

    private val toBeSimplify = mutableListOf<SimplifyBinaryExpression>()

    override fun beforeVisit(context: FormatContext) {
        super.beforeVisit(context)
        toBeSimplify.clear()
    }

    override fun visit(context: FormatContext, node: ASTNode) {
        if (node is PsiBinaryExpression) {

        }
    }

    override fun afterVisit(context: FormatContext) {
        super.afterVisit(context)
        toBeSimplify.forEach {
            it.report(context)
        }
        toBeSimplify.forEach {
            try {
                it.operate()
            } catch (e: Exception) {
                context.notifyTextChange()
                it.report(context)
                throw e
            }
        }
    }

    class SimplifyBinaryExpression {
        fun operate() {
            //
        }

        fun report(context: FormatContext) {
//            context.report("simplify", context.getCodeFragment(null))
        }
    }
}