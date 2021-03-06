package com.unionyy.mobile.reformat.core

import com.unionyy.mobile.reformat.core.rule.DumpAST
import com.unionyy.mobile.reformat.core.rule.YYRuleSet
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class SimplifyBooleanTest {

    @Before
    fun before(){
        CodeFormatter.defaultRules.addAll(YYRuleSet)
    }

    @Test
    fun testSimplifyBooleanReturn() {

        val text = CodeFormatter.reformat("A.java", """
package com.yy.mobile.demo;

public class NormalJavaClass {

    private boolean main() {
        if ((Math.abs(Edge.LEFT.getCoordinate() - Edge.RIGHT.getCoordinate()) < DEFAULT_SHOW_GUIDELINES_LIMIT)
                || (Math.abs(Edge.TOP.getCoordinate() - Edge.BOTTOM.getCoordinate()) < DEFAULT_SHOW_GUIDELINES_LIMIT)) {
            return false;
        } else {
            return true;
        }
    }
}""".trimIndent(), setOf(DumpAST()))

        Assert.assertEquals("""""", text)
    }

    @Test
    fun testSimplifyBooleanExpression() {

        val text = CodeFormatter.reformat("A.java", """
package com.yy.mobile.demo;

public class NormalJavaClass {

    private void main() {
        if (initializedCropWindow == true) {
            initializedCropWindow = true;
        }
    }
}""".trimIndent(), setOf(DumpAST()))

        Assert.assertEquals("""""", text)
    }

    @Test
    fun testSimplifyPoyBooleanExpression() {

        val text = CodeFormatter.reformat("A.java", """
package com.yy.mobile.demo;

public class NormalJavaClass {

    private void main() {

        if (a.equals(b) || a != false || false) {

        }
    }
}""".trimIndent(), setOf(DumpAST()))

        Assert.assertEquals("""""", text)
    }
}