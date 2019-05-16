package com.unionyy.mobile.reformat.core

import com.unionyy.mobile.reformat.core.rule.DumpAST
import org.junit.Assert
import org.junit.Test

class ArrayBraceTest {

    @Test
    fun testJavaBrace() {

        val text = CodeFormatter.reformat("A.java", """
public class A {
    int pos[] = new int[2];
}
        """.trimIndent(), setOf(DumpAST()))

        Assert.assertEquals("""

        """.trimIndent(), text)
    }
}