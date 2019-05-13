package com.unionyy.mobile.reformat.core

import com.unionyy.mobile.reformat.core.rule.DumpAST
import com.unionyy.mobile.reformat.core.rule.LineBreaker
import org.junit.Test


/**
 * Created by 张宇 on 2019/5/9.
 * E-mail: zhangyu4@yy.com
 * YY: 909017428
 */
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