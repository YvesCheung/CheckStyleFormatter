package com.unionyy.mobile.reformat.core

import com.unionyy.mobile.reformat.core.rule.DumpAST
import org.junit.Test


class DumpASTTest {

    @Test
    fun testJavaDump() {
        CodeFormatter.reformat("a.java", """
            package com.yy.mobile;

            public class A {}
        """.trimIndent(), setOf(DumpAST()))
    }

    @Test
    fun testKotlinDump() {
        CodeFormatter.reformat("b.kt", """
            package com.yy.mobile;

            data class A {}
        """.trimIndent(), setOf(DumpAST()))
    }
}