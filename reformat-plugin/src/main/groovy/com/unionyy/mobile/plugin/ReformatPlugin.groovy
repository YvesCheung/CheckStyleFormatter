package com.unionyy.mobile.plugin

import com.intellij.psi.PsiFileFactory
import org.gradle.api.Plugin
import org.gradle.api.Project

class ReformatPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        PsiFileFactory.getInstance(project)
    }
}