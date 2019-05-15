package com.unionyy.mobile.plugin

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin
import com.android.build.gradle.internal.VariantManager
import com.android.build.gradle.internal.scope.VariantScope
import com.unionyy.mobile.reformat.core.CodeFormatter
import kotlin.Pair
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.ConfigurableFileTree
import org.gradle.api.plugins.JavaPluginConvention

class FormatPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.rootProject.allprojects { p ->
            println("FormatPlugin apply to ${p.name}")
            p.apply {
                it.plugin(FormatPlugin)
            }
        }

        if (project != project.rootProject) {
            getSourceFiles(project) {
                def files = it.first
                def sourceSetName = it.second
                createFormatTask(project, files, sourceSetName)
                createPrintFileTask(project, files)
            }
        }
    }

    private static Task createPrintFileTask(
            Project project,
            Collection<File> input) {
        def taskName = "printSrcDirs"
        if (project.tasks.findByName(taskName) == null) {
            project.tasks.create(taskName) {
                it.group = "checkstyle"
                it.doLast {
                    println("FileList:")
                    println(input.join("\n"))
                }
            }
        }
    }

    private static Task createFormatTask(
            Project project,
            Collection<File> input,
            String sourceSetName) {
        def taskName = sourceSetName == "main" ? "javaFormatting"
                : "${sourceSetName}JavaFormatting"
        if (project.tasks.findByName(taskName) == null) {
            project.tasks.create(taskName) {
                it.group = "checkstyle"

                it.doLast {
                    input.forEach { file ->
                        def newText = CodeFormatter.reformat(
                                file.absolutePath,
                                file.newReader().text)
                        file.write(newText)
                    }
                }
            }
        }
    }

    private static def getSourceFiles(
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
                    println("Variant: " + variant.fullVariantName +
                            "\nDir:\n" + fileTree.dir.join("\n"))
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

    private static <T> T firstOrNull(List<T> list) {
        if (list.isEmpty()) {
            return null
        } else {
            return list.first()
        }
    }
}