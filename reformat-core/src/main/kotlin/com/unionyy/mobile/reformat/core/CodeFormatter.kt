package com.unionyy.mobile.reformat.core

import com.unionyy.mobile.reformat.core.reporter.WriterReporter
import com.unionyy.mobile.reformat.core.rule.DumpAST
import com.unionyy.mobile.reformat.core.rule.LineBreaker
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.com.intellij.lang.java.JavaLanguage
import org.jetbrains.kotlin.com.intellij.mock.MockProject
import org.jetbrains.kotlin.com.intellij.openapi.extensions.ExtensionPoint
import org.jetbrains.kotlin.com.intellij.openapi.extensions.Extensions
import org.jetbrains.kotlin.com.intellij.openapi.util.Disposer
import org.jetbrains.kotlin.com.intellij.openapi.util.UserDataHolderBase
import org.jetbrains.kotlin.com.intellij.pom.PomModel
import org.jetbrains.kotlin.com.intellij.pom.PomModelAspect
import org.jetbrains.kotlin.com.intellij.pom.PomTransaction
import org.jetbrains.kotlin.com.intellij.pom.impl.PomTransactionBase
import org.jetbrains.kotlin.com.intellij.pom.tree.TreeAspect
import org.jetbrains.kotlin.com.intellij.psi.PsiFileFactory
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.TreeCopyHandler
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.idea.KotlinLanguage
import sun.reflect.ReflectionFactory


/**
 * Created by 张宇 on 2019/5/9.
 * E-mail: zhangyu4@yy.com
 * YY: 909017428
 */
object CodeFormatter {

    private val psiFileFactory: PsiFileFactory

    @Suppress("RemoveExplicitTypeArguments")
    private val usingRules = mutableSetOf<FormatRule>(
        //DumpAST(),
        LineBreaker()
    )

    init {
        val pomModel: PomModel = object : UserDataHolderBase(), PomModel {
            override fun runTransaction(transaction: PomTransaction) {
                (transaction as PomTransactionBase).run()
            }

            @Suppress("UNCHECKED_CAST")
            override fun <T : PomModelAspect> getModelAspect(aspect: Class<T>): T? {
                if (aspect == TreeAspect::class.java) {
                    // using approach described in https://git.io/vKQTo due to the magical bytecode of TreeAspect
                    // (check constructor signature and compare it to the source)
                    // (org.jetbrains.kotlin:kotlin-compiler-embeddable:1.0.3)
                    val constructor = ReflectionFactory.getReflectionFactory()
                        .newConstructorForSerialization(
                            aspect, Any::class.java.getDeclaredConstructor(*arrayOfNulls<Class<*>>(0))
                        )
                    return constructor.newInstance(*emptyArray()) as T
                }
                return null
            }
        }
        val project = KotlinCoreEnvironment.createForProduction(
            Disposer.newDisposable(),
            CompilerConfiguration(),
            EnvironmentConfigFiles.JVM_CONFIG_FILES).project as MockProject
        val extensionPoint = "org.jetbrains.kotlin.com.intellij.treeCopyHandler"
        val extensionClassName = TreeCopyHandler::class.java.name
        for (area in arrayOf(Extensions.getArea(project), Extensions.getArea(null))) {
            if (!area.hasExtensionPoint(extensionPoint)) {
                area.registerExtensionPoint(
                    extensionPoint, extensionClassName, ExtensionPoint.Kind.INTERFACE)
            }
        }
        project.registerService(PomModel::class.java, pomModel)
        psiFileFactory = PsiFileFactory.getInstance(project)
    }

    @JvmStatic
    @JvmOverloads
    fun reformat(
        fileName: String,
        fileContent: String,
        rules: Set<FormatRule> = usingRules,
        reporter: Reporter = WriterReporter(System.out)
    ): String {
        val lang =
            if (fileName.endsWith(".kt") || fileName.endsWith(".kts")) {
                KotlinLanguage.INSTANCE
            } else if (fileName.endsWith(".java")) {
                JavaLanguage.INSTANCE
            } else {
                return fileContent
            }
        val normalizedText = fileContent
            .replace("\r\n", "\n")
            .replace("\r", "\n")

        val psiFile = psiFileFactory.createFileFromText(fileName, lang, normalizedText)
        val context = FormatContext(
            fileName,
            psiFile.node,
            lang,
            reporter,
            rules
        )

        rules.forEach { it.beforeVisit(context) }
        psiFile.node.visit { node ->
            rules.forEach { it.visit(context, node) }
        }
        rules.forEach { it.afterVisit(context) }

        return if (reporter.getReportCount(fileName) > 0) {
            psiFile.text.replace("\n", System.lineSeparator())
        } else {
            psiFile.text
        }
    }

    private fun ASTNode.visit(cb: (node: ASTNode) -> Unit) {
        cb(this)
        this.getChildren(null).forEach { it.visit(cb) }
    }
}