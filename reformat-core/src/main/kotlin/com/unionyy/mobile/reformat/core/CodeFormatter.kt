package com.unionyy.mobile.reformat.core

import com.unionyy.mobile.reformat.core.reporter.WriterReporter
import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.environment.setIdeaIoUseFallback
import org.jetbrains.kotlin.cli.common.messages.MessageRenderer
import org.jetbrains.kotlin.cli.common.messages.PrintingMessageCollector
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.com.intellij.lang.FileASTNode
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
import org.jetbrains.kotlin.config.ApiVersion
import org.jetbrains.kotlin.config.CommonConfigurationKeys
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.JVMConfigurationKeys
import org.jetbrains.kotlin.config.JvmTarget
import org.jetbrains.kotlin.config.LanguageVersion
import org.jetbrains.kotlin.config.LanguageVersionSettingsImpl
import org.jetbrains.kotlin.idea.KotlinLanguage
import sun.reflect.ReflectionFactory

/**
 * Created by 张宇 on 2019/5/9.
 * E-mail: zhangyu4@yy.com
 * YY: 909017428
 */
@Suppress("MemberVisibilityCanBePrivate")
object CodeFormatter {

    private val psiFileFactory: PsiFileFactory

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
                    return constructor.newInstance() as T
                }
                return null
            }
        }
        val compilerConfiguration = CompilerConfiguration()
        setIdeaIoUseFallback()
        compilerConfiguration.put(
            CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY,
            PrintingMessageCollector(System.err, MessageRenderer.PLAIN_FULL_PATHS, false)
        )
        compilerConfiguration.put(CommonConfigurationKeys.MODULE_NAME, "CodeFormatter")
        compilerConfiguration.put(CommonConfigurationKeys.LANGUAGE_VERSION_SETTINGS,
            LanguageVersionSettingsImpl(
                LanguageVersion.LATEST_STABLE,
                ApiVersion.createByLanguageVersion(LanguageVersion.LATEST_STABLE)
            ))
        compilerConfiguration.put(JVMConfigurationKeys.JVM_TARGET, JvmTarget.JVM_1_8)
        val project = KotlinCoreEnvironment.createForProduction(
            Disposer.newDisposable(),
            compilerConfiguration,
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
        rules: Set<FormatRule>,
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
            .replace("\t", "    ")

        var repeat: Boolean
        var repeatTime = 1
        var psiFile: FileASTNode
        var context: FormatContext? = null
        var hasError = false
        do {
            psiFile = context?.fileContent
                ?: psiFileFactory.createFileFromText(fileName, lang, normalizedText).node
            context = FormatContext(
                fileName,
                psiFile,
                lang,
                repeatTime++,
                reporter,
                rules
            )
            rules.forEach {
                if (context.reportCnt > 0) {
                    context.notifyTextChange()
                }

                it.beforeVisit(context)
                psiFile.visit { node ->
                    it.visit(context, node)
                }
                it.afterVisit(context)
            }

            hasError = hasError || context.reportCnt > 0
            repeat = context.requestRepeat && repeatTime < 50
        } while (repeat)

        return psiFile.text.replace("\n", System.lineSeparator())
    }

    private fun ASTNode.visit(cb: (node: ASTNode) -> Unit) {
        cb(this)
        this.getChildren(null).forEach { it.visit(cb) }
    }
}