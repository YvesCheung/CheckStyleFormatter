package com.unionyy.mobile.reformat.core.utils

import org.jetbrains.kotlin.com.intellij.lang.ASTNode

fun ASTNode.preNode(
    excludeSelf: Boolean = false,
    prediction: (node: ASTNode) -> Boolean
): ASTNode? =
    findNode(excludeSelf, prediction) { it.treePrev }

fun ASTNode.nextNode(
    excludeSelf: Boolean = false,
    prediction: (node: ASTNode) -> Boolean
): ASTNode? =
    findNode(excludeSelf, prediction) { it.treeNext }

private inline fun ASTNode.findNode(
    excludeSelf: Boolean = false,
    prediction: (node: ASTNode) -> Boolean,
    getNode: (ASTNode) -> ASTNode?
): ASTNode? {
    var now: ASTNode? = if (excludeSelf) {
        getNode(this)
    } else {
        this
    }
    while (true) {
        when {
            now == null -> return null
            prediction(now) -> return now
            else -> now = getNode(now)
        }
    }
}