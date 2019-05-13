package com.unionyy.mobile.reformat.core

import org.jetbrains.kotlin.com.intellij.lang.ASTNode

/**
 * Created by 张宇 on 2019/5/9.
 * E-mail: zhangyu4@yy.com
 * YY: 909017428
 */
interface FormatRule {

    fun beforeVisit(context: FormatContext) {}

    fun visit(context: FormatContext, node: ASTNode)

    fun afterVisit(context: FormatContext) {}
}