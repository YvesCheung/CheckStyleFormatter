package com.unionyy.mobile.reformat.core.rule

import com.unionyy.mobile.reformat.core.FormatContext
import com.unionyy.mobile.reformat.core.FormatRule
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.com.intellij.psi.PsiMethodCallExpression

/**
 * @author YvesCheung
 * 2020/11/14
 */
class HuyaExReplacement : FormatRule {

    override fun visit(context: FormatContext, node: ASTNode) {
        if (node is PsiMethodCallExpression) {
            val method = node.resolveMethod()
            println(method)
        }
    }
}