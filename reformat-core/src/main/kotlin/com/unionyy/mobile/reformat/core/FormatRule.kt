package com.unionyy.mobile.reformat.core

import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.com.intellij.lang.Language
import org.jetbrains.kotlin.com.intellij.lang.java.JavaLanguage
import org.jetbrains.kotlin.idea.KotlinLanguage

/**
 * Created by 张宇 on 2019/5/9.
 * E-mail: zhangyu4@yy.com
 * YY: 909017428
 */
interface FormatRule {

    val targetLanguage: Set<Language>
        get() = setOf(KotlinLanguage.INSTANCE, JavaLanguage.INSTANCE)

    fun beforeVisit(context: FormatContext) {}

    fun visit(context: FormatContext, node: ASTNode)

    fun afterVisit(context: FormatContext) {}
}