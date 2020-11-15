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
                val methodName = method.name

                fun replaceTo(ex: String) {
                    val receiver =
                        node.methodExpression.qualifierExpression?.text ?: "this"
                    val params =
                        node.argumentList.expressions
                    val code = "$ex.$methodName(" +
                        "$receiver, " +
                        "${params.joinToString(separator = ", ") { it.text }})"

                    node.treeParent.replaceChild(node, PsiCodeBlockImpl(code))

                    context.report("replace ${node.getText()} with $code",
                        context.getCodeFragment(node), true)
                }

                when {
                    method.isMethodOf(context, mapFunc, Map) -> {
                        replaceTo(MapEx)
                    }
                    method.isMethodOf(context, listFunc, List) -> {
                        replaceTo(ListEx)
                    }
                    method.isMethodOf(context, queueFunc, Queue) -> {
                        replaceTo(QueueEx)
                    }
                    method.isMethodOf(context, arrayMapFunc, ArrayMap) -> {
                        replaceTo(ArrayMapEx)
                    }
                    method.isMethodOf(context, collectionFunc, Collection) -> {
                        replaceTo(CollectionEx)
                    }
                }
            }
        }
    }

    private fun PsiMethod.isMethodOf(
        context: FormatContext,
        funcName: Collection<String>,
        clsName: String
    ): Boolean {
        return funcName.contains(this.name) &&
            context.evaluator.isMemberInSubClassOf(this, clsName, false)
    }

    companion object {
        private const val Map = "java.util.Map"
        private const val MapEx = "com.hyex.collections.MapEx"
        private val mapFunc = setOf("put", "remove", "containsValue")

        private const val List = "java.util.List"
        private const val ListEx = "com.hyex.collections.ListEx"
        private val listFunc = setOf("add", "remove", "contains", "clear")

        private const val Queue = "java.util.Queue"
        private const val QueueEx = "com.hyex.collections.QueueEx"
        private val queueFunc = setOf("add", "remove", "contains")

        private const val ArrayMap = "androidx.collection.SimpleArrayMap"
        private const val ArrayMapEx = "com.hyex.collections.ArrayMapEx"
        private val arrayMapFunc = setOf("valueAt")

        private const val Collection = "java.util.Collection"
        private const val CollectionEx = "com.hyex.collections.CollectionEx"
        private val collectionFunc = setOf("add", "remove", "contains", "clear")
    }
}