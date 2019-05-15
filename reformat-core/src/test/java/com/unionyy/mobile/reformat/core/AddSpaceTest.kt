package com.unionyy.mobile.reformat.core

import com.unionyy.mobile.reformat.core.rule.DumpAST
import org.junit.Assert
import org.junit.Test

class AddSpaceTest {

    @Test
    fun testCommentSpace() {

        val text = CodeFormatter.reformat("A.java", """
class A {
    int a = 123;/* i am comment. */
}
        """.trimIndent())

        Assert.assertEquals("""
class A {
    int a = 123; /* i am comment. */
}
        """.trimIndent(), text)
    }

    @Test
    fun testBraceSpace() {

        val text = CodeFormatter.reformat("A.java", """
class A {
    void test(){
        if(true){int a = b;}
    }
}
        """.trimIndent())

        Assert.assertEquals("""
class A {
    void test(){
        if(true){ int a = b;}
    }
}
        """.trimIndent(), text)
    }
}