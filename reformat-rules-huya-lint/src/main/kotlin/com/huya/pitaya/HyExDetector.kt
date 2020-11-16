package com.huya.pitaya

import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.LintFix
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import com.android.tools.lint.detector.api.getMethodName
import com.intellij.psi.PsiMethod
import org.jetbrains.uast.UCallExpression
import java.util.*

/**
 * @author YvesCheung
 * 2020/11/16
 */
@Suppress("UnstableApiUsage")
class HyExDetector : Detector(), Detector.UastScanner {

    override fun getApplicableMethodNames(): List<String> =
        (mapFunc + listFunc + queueFunc + arrayMapFunc + collectionFunc).toList()

    override fun visitMethodCall(context: JavaContext, node: UCallExpression, method: PsiMethod) {

        val methodName = getMethodName(node)

        fun isMethodOf(
            funcName: Iterable<String>,
            clsName: String
        ): Boolean {
            return funcName.contains(methodName) &&
                context.evaluator.isMemberInSubClassOf(method, clsName, false)
        }

        fun replaceTo(ex: String) {
            val receiver =
                node.receiver?.asSourceString() ?: "this"
            val params =
                node.valueArguments.map { it.asSourceString() }
            val codeSegment = mutableListOf(receiver) + params
            val code = "$ex.$methodName(" +
                "${codeSegment.joinToString(separator = ", ")})"

            val location = context.getLocation(node)
            val fix = LintFix.create()
                .replace()
                .range(location)
                .with(code)
                .shortenNames()
                .reformat(true)
                .build()
            context.report(ISSUE_COLLECTION_EX, location, "Replace Ex", fix)
        }

        when {
            isMethodOf(mapFunc, Map) -> {
                replaceTo(MapEx)
            }
            isMethodOf(listFunc, List) -> {
                replaceTo(ListEx)
            }
            isMethodOf(queueFunc, Queue) -> {
                replaceTo(QueueEx)
            }
            isMethodOf(arrayMapFunc, ArrayMap) -> {
                replaceTo(ArrayMapEx)
            }
            isMethodOf(collectionFunc, Collection) -> {
                replaceTo(CollectionEx)
            }
        }
    }


    companion object {

        val ISSUE_COLLECTION_EX: Issue = Issue.create(
            "HyExCollection",
            "Replace collection with ex",
            "Replace collection with ex",
            Category.USABILITY,
            1,
            Severity.ERROR,
            Implementation(
                HyExDetector::class.java,
                EnumSet.of(Scope.JAVA_FILE)
            ))

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