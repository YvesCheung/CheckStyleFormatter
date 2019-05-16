package com.unionyy.mobile.reformat.core

import com.unionyy.mobile.reformat.core.rule.AddSwitchDefaultCase
import com.unionyy.mobile.reformat.core.rule.DumpAST
import com.unionyy.mobile.reformat.core.rule.LineBreaker
import com.unionyy.mobile.reformat.core.rule.SpaceOperation
import org.junit.Assert
import org.junit.Test

/**
 * Created BY PYF 2019/5/16
 * email: pengyangfan@yy.com
 */
class AddSwitchDefaultCaseTest {

    @Test
    fun testSwitchBlock() {
        val text = CodeFormatter.reformat("Haha.java", """
package com.yy.mobile.demo;

public class NormalJavaClass {

    private static void main() {
        int i = 0;
        switch (i) {
            case 1: {
                break;
            }
            case 2: {
                break;
            }
            case 3: {
                break;
            }
            case 4: {
                break;
            }
            case 5: {
                break;
            }
        }
    }
}
""".trimIndent())

        Assert.assertEquals(text, """
package com.yy.mobile.demo;

public class NormalJavaClass {

    private static void main() {
        int i = 0;
        switch (i) {
            case 1: {
                break;
            }
            case 2: {
                break;
            }
            case 3: {
                break;
            }
            case 4: {
                break;
            }
            case 5: {
                break;
            }
            default:
                break;
        }
    }
}
        """.trimIndent())
    }
}