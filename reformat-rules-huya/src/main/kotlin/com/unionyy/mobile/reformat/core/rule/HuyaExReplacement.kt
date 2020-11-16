package com.unionyy.mobile.reformat.core.rule

import com.unionyy.mobile.reformat.core.FormatContext
import com.unionyy.mobile.reformat.core.FormatRule
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.com.intellij.lang.Language
import org.jetbrains.kotlin.com.intellij.lang.java.JavaLanguage
import org.jetbrains.kotlin.com.intellij.psi.PsiJavaCodeReferenceElement
import org.jetbrains.kotlin.com.intellij.psi.PsiMethod
import org.jetbrains.kotlin.com.intellij.psi.PsiMethodCallExpression
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.PsiWhiteSpaceImpl
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.java.ImportListElement
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.java.ImportStatementElement
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.java.PsiCodeBlockImpl
import org.jetbrains.kotlin.psi.psiUtil.children

/**
 * @author YvesCheung
 * 2020/11/14
 */
class HuyaExReplacement : FormatRule {

    override val targetLanguage: Set<Language> = setOf(JavaLanguage.INSTANCE) //Only for Java

    private var imports: ImportListElement? = null

    private val needImport = mutableSetOf<String>()

    override fun beforeVisit(context: FormatContext) {
        super.beforeVisit(context)
        imports = null
        needImport.clear()
    }

    override fun visit(context: FormatContext, node: ASTNode) {
        if (node is ImportListElement) {
            imports = node
        } else if (node is PsiMethodCallExpression) {
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

                    context.report("replace ${node.getText()} with $code",
                        context.getCodeFragment(node), true)

                    node.treeParent.replaceChild(node, PsiCodeBlockImpl(code))
                }

                when {
                    method.isMethodOf(context, mapFunc, Map) -> {
                        replaceTo(MapEx)
                        needImport.add(MapExQualifier)
                    }
                    method.isMethodOf(context, listFunc, List) -> {
                        replaceTo(ListEx)
                        needImport.add(ListExQualifier)
                    }
                    method.isMethodOf(context, queueFunc, Queue) -> {
                        replaceTo(QueueEx)
                        needImport.add(QueueExQualifier)
                    }
                    method.isMethodOf(context, arrayMapFunc, ArrayMap) -> {
                        replaceTo(ArrayMapEx)
                        needImport.add(ArrayMapExQualifier)
                    }
                    method.isMethodOf(context, collectionFunc, Collection) -> {
                        replaceTo(CollectionEx)
                        needImport.add(CollectionExQualifier)
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

    override fun afterVisit(context: FormatContext) {
        super.afterVisit(context)
        val importList = imports ?: return //impossible
        if (needImport.isNotEmpty()) {
            var noImportCase = true //mark if none of imports exist

            //remove imports already exist
            importList.children().forEach { child ->
                if (child is ImportStatementElement) {
                    val importCode =
                        child.children().find { it is PsiJavaCodeReferenceElement }
                    if (importCode != null) {
                        needImport.remove(importCode.text)
                    }

                    noImportCase = false
                }
            }

            needImport.forEachIndexed { index, importStatement ->
                if (index > 0 || !noImportCase) { //if no import, don't add line break at first line
                    importList.rawAddChildren(PsiWhiteSpaceImpl("\n"))
                }
                importList.rawAddChildren(PsiCodeBlockImpl("import $importStatement;"))
            }

            if (noImportCase) { //if no import, add a line break
                importList.rawAddChildren(PsiWhiteSpaceImpl("\n\n"))
            }
        }
    }

    companion object {
        private const val Map = "java.util.Map"
        private const val MapEx = "MapEx"
        private const val MapExQualifier = "com.hyex.collections.MapEx"
        private val mapFunc = setOf("put", "remove", "containsValue")

        private const val List = "java.util.List"
        private const val ListEx = "ListEx"
        private const val ListExQualifier = "com.hyex.collections.ListEx"
        private val listFunc = setOf("add", "remove", "contains", "clear")

        private const val Queue = "java.util.Queue"
        private const val QueueEx = "QueueEx"
        private const val QueueExQualifier = "com.hyex.collections.QueueEx"
        private val queueFunc = setOf("add", "remove", "contains")

        private const val ArrayMap = "androidx.collection.SimpleArrayMap"
        private const val ArrayMapEx = "ArrayMapEx"
        private const val ArrayMapExQualifier = "com.hyex.collections.ArrayMapEx"
        private val arrayMapFunc = setOf("valueAt")

        private const val Collection = "java.util.Collection"
        private const val CollectionEx = "CollectionEx"
        private const val CollectionExQualifier = "com.hyex.collections.CollectionEx"
        private val collectionFunc = setOf("add", "remove", "contains", "clear")
    }
}