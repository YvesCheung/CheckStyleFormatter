package com.unionyy.mobile.reformat.core

import org.junit.Assert
import org.junit.Test

class AddSpaceTest {

    @Test
    fun testEndOfLineCommentSpace() {

        val text = CodeFormatter.reformat("A.java","""
class A {
    int a = 123;/* i am comment. */
}
        """.trimIndent())

        Assert.assertEquals("""
class A {
    int a = 123; /* i am comment. */
}
        """.trimIndent(),text)
    }
}