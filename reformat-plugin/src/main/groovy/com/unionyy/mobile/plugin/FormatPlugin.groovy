package com.unionyy.mobile.plugin

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin
import com.android.build.gradle.internal.VariantManager
import com.android.build.gradle.internal.scope.VariantScope
import com.unionyy.mobile.reformat.core.CodeFormatter
import com.unionyy.mobile.reformat.core.FormatRule
import kotlin.Pair
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileTree
import org.gradle.api.plugins.JavaPluginConvention

@SuppressWarnings("GrMethodMayBeStatic")
abstract class FormatPlugin implements Plugin<Project> {

    private static final String PLUGIN_NAME = "FormatPlugin"

    protected abstract Set<FormatRule> rules()

    @Override
    void apply(Project project) {
        applyInner(project)

        project.subprojects { subProject ->
            applyInner(subProject)
        }
    }

    protected def applyInner(Project project) {
        println("$PLUGIN_NAME apply to ${project.name}")
        getSourceFiles(project) {
            def files = it.first
            def sourceSetName = it.second
            createFormatTask(project, files, sourceSetName)
            createPrintFileTask(project, files)
        }
    }

    protected def createPrintFileTask(
            Project project,
            Collection<File> input) {
        def taskName = "printSrcDirs"
        if (project.tasks.findByName(taskName) == null) {
            project.tasks.create(taskName) {
                it.group = "checkstyle"
                it.description = "输出会被格式化的文件列表"
                it.doLast {
                    println("$PLUGIN_NAME FileList:")
                    println(input.join("\n"))
                }
            }
        }
    }

    protected def createFormatTask(
            Project project,
            Collection<File> input,
            String sourceSetName) {
        def taskName = sourceSetName == "main" ? "javaFormatting"
                : "${sourceSetName}JavaFormatting"
        if (project.tasks.findByName(taskName) == null) {
            project.tasks.create(taskName) {
                it.group = "checkstyle"
                it.description = "格式化Java文件"
                it.doLast {
                    input.forEach { file ->
                        def newText = CodeFormatter.reformat(
                                file.absolutePath,
                                file.newReader().text,
                                rules())
                        file.write(newText)
                    }
                }
            }
        }
    }

    protected def getSourceFiles(
            Project project,
            Action<Pair<Collection<File>, String>> callback) {
        project.afterEvaluate {
            VariantManager variantManager = null
            if (project.plugins.hasPlugin(AppPlugin)) {
                variantManager = project.plugins.findPlugin(AppPlugin).variantManager
            } else if (project.plugins.hasPlugin(LibraryPlugin)) {
                variantManager = project.plugins.findPlugin(LibraryPlugin).variantManager
            }

            if (variantManager != null) {
                VariantScope variant = variantManager.variantScopes.find {
                    it.fullVariantName?.toLowerCase() == "debug"
                } ?: firstOrNull(variantManager.variantScopes)
                if (variant != null) {
                    List<ConfigurableFileTree> fileTree =
                            variant.variantData.javaSources.grep {
                                def buildPath = project.buildDir.absolutePath
                                return !it.dir.absolutePath.contains(buildPath)
                            }
                    Collection<File> files = fileTree.collect {
                        it.matching {
                            it.include("**/*.java")
                        }.files
                    }.flatten()
                    println("$PLUGIN_NAME Variant: " + variant.fullVariantName +
                            "\n$PLUGIN_NAME Dir:\n" + fileTree.dir.join("\n"))
                    callback.execute(new Pair<>(files, "main"))
                }
            } else {
                project.convention.findPlugin(JavaPluginConvention)
                        ?.sourceSets?.forEach {
                    callback.execute(
                            new Pair<>(
                                    it.java.getSourceDirectories().asFileTree.matching {
                                        it.include("**/*.java")
                                    }.files,
                                    it.name
                            )
                    )
                }
            }
        }
    }

    static <T> T firstOrNull(List<T> list) {
        if (list.isEmpty()) {
            return null
        } else {
            return list.first()
        }
    }
}