package com.unionyy.mobile.plugin

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin
import com.android.build.gradle.internal.VariantManager
import com.android.build.gradle.internal.scope.VariantScope
import com.unionyy.mobile.reformat.core.CodeFormatter
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.JavaPluginConvention

class FormatPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.rootProject.subprojects { p ->
            p.tasks.create("srcDirs") {
                it.group = "checkstyle"

                it.doLast {
                    getSourceFiles(p) {
                        System.out.println(it)
                    }
                }
            }
            getSourceFiles(p) {
                createFormatTask(p, it)
            }
        }
    }

    private static Task createFormatTask(
            Project project,
            Collection<File> input) {
        if (project.tasks.findByName("JavaFormatting") == null) {
            project.tasks.create("JavaFormatting") {
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

    private static void getSourceFiles(Project project, Action<Collection<File>> callback) {
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
                } ?: variantManager.variantScopes.firstOrNull()

                if (variant != null) {
                    List<File> files = variant.variantData
                            .javaSources
                            .collect { it.dir }
                            .grep { it.path.endsWith(".java") }
                    callback.execute(files)
                }
            } else {
                project.convention.findPlugin(JavaPluginConvention)
                        ?.sourceSets?.forEach {
                    callback.execute(
                            it.java.getSourceDirectories().asFileTree.matching {
                                it.include("**/*.java")
                            }.files
                    )

                }
            }
        }
    }
}