package com.unionyy.mobile.reformat.core.utils

import org.jetbrains.kotlin.com.intellij.lang.ASTNode

fun ASTNode.preNode(excludeSelf: Boolean = false, prediction: (node: ASTNode) -> Boolean): ASTNode? {
    var now: ASTNode? = if (excludeSelf) {
        this.treePrev
    } else {
        this
    }
    while (true) {
        if (now == null) {
            return null
        } else if (prediction(now)) {
            return now
        } else {
            now = now.treePrev
        }
    }
}