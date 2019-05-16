package com.unionyy.mobile.reformat.core

import com.unionyy.mobile.reformat.core.rule.DumpAST
import org.junit.Assert
import org.junit.Test

class ArrayBracketTest {

    @Test
    fun testJavaBracket() {

        val text = CodeFormatter.reformat("A.java", """
public class A {
    int pos[] = new int[2];

    int[] pos2 = new int[2];
}
        """.trimIndent(), setOf(DumpAST()))

        Assert.assertEquals("""

        """.trimIndent(), text)
    }

    @Test
    fun testJavaComplexBracket() {

        val text = CodeFormatter.reformat("A.java", """
import com.yy.mobile.ASD;

public class A {
    ASD pos[] = new ASD[2];

    ASD[] pos2 = new ASD[2];
}
        """.trimIndent(), setOf(DumpAST()))

        Assert.assertEquals("""

        """.trimIndent(), text)
    }

    @Test
    fun testJavaVariableBracket() {

        val text = CodeFormatter.reformat("A.java", """
public class A {

    public void main(){
        int pos[] = new int[2];

        ASD pos[] = new ASD[2];
    }
}
        """.trimIndent(), setOf(DumpAST()))

        Assert.assertEquals("""

        """.trimIndent(), text)
    }
}