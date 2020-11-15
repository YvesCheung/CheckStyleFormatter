package com.unionyy.mobile.reformat.core.rule

import com.unionyy.mobile.reformat.core.FormatContext
import com.unionyy.mobile.reformat.core.FormatRule
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.com.intellij.psi.PsiMethod
import org.jetbrains.kotlin.com.intellij.psi.PsiMethodCallExpression
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.java.PsiCodeBlockImpl

/**
 * @author YvesCheung
 * 2020/11/14
 */
class HuyaExReplacement : FormatRule {

    override fun visit(context: FormatContext, node: ASTNode) {
        if (node is PsiMethodCallExpression) {
            val method = node.resolveMethod()
            if (method != null) {
                if (method.isMethodOf(context, Map, "put")) {
                    val receiver =
                        node.methodExpression.qualifierExpression?.text ?: "this"
                    val params =
                        node.argumentList.expressions

                    val code = "$MapEx.put(" +
                        "$receiver," +
                        "${params.joinToString(separator = ",") { it.text }})"
                    node.treeParent.replaceChild(node, PsiCodeBlockImpl(code))
                    context.report("replace ${node.getText()} with $code",
                        context.getCodeFragment(node), true)
                }
            }
        }
    }

    private fun PsiMethod.isMethodOf(
        context: FormatContext,
        clsName: String,
        funcName: String
    ): Boolean {
        return this.name == funcName &&
            context.evaluator.isMemberInSubClassOf(this, clsName, false)
    }

    companion object {

        private const val Map = "java.util.Map"
        private const val MapEx = "com.hyex.collections.MapEx"
    }
}