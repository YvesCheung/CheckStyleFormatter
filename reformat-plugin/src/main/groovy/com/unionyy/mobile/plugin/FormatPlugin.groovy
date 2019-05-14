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
import org.gradle.api.plugins.JavaPluginConvention

class FormatPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.rootProject.subprojects { p ->
            def taskName = "printSrcDirs"
            if (p.tasks.findByName(taskName) == null) {
                p.tasks.create(taskName) {
                    it.group = "checkstyle"

                    it.doLast {
                        getSourceFiles(p) {
                            System.out.println("FileList:")
                            System.out.println(it.first.join("\n"))
                        }
                    }
                }
            }
            getSourceFiles(p) {
                def files = it.first
                def sourceSetName = it.second
                createFormatTask(p, files, sourceSetName)
            }
        }
    }

    private static Task createFormatTask(
            Project project,
            Collection<File> input,
            String sourceSetName) {
        def taskName = sourceSetName == "main" ? "JavaFormatting"
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
                    Collection<File> files = variant.variantData.javaSources.collect {
                        it.matching {
                            it.include("**/*.java")
                        }.files
                    }.flatten()
                    System.out.println("variant = " + variant + " files = " + files)
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