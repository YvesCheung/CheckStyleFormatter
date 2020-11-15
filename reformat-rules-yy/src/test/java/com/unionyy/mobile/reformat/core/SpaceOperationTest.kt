package com.unionyy.mobile.reformat.core

import com.unionyy.mobile.reformat.core.rule.YYRuleSet
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class SpaceOperationTest {

    @Before
    fun before(){
        CodeFormatter.defaultRules.addAll(YYRuleSet)
    }

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

    public enum CacheType {
        PRIVATE,//登陆者私有数据
        PUBLIC//公共数据
    }
}
        """.trimIndent())

        Assert.assertEquals("""
class A {
    void test() {
        if(true) { int a = b; }
    }

    public enum CacheType {
        PRIVATE, //登陆者私有数据
        PUBLIC//公共数据
    }
}
""".trimIndent(), text)
    }

    @Test
    fun testJavaCommentTooClose() {
        val text = CodeFormatter.reformat("D.java", """
package com.yy.mobile.checkstyleformatter;

public class ChannelMediaVideoInfoView extends AbsFloatingView {

    public boolean isPluginLianMai() {//niubi
        int i = 0;//niubi
        int j = 1                        ;
    }//niubi

}
        """.trimIndent())

        Assert.assertEquals(text, """
package com.yy.mobile.checkstyleformatter;

public class ChannelMediaVideoInfoView extends AbsFloatingView {

    public boolean isPluginLianMai() { //niubi
        int i = 0; //niubi
        int j = 1;
    } //niubi

}
        """.trimIndent())
    }

    @Test
    fun testCommentOnFuncSpace() {

        val text = CodeFormatter.reformat("A.java", """
class A {
    //宇总威武
    void test(){
        if(true){int a = b;}
    }
}
        """.trimIndent())

        Assert.assertEquals("""
class A {
    //宇总威武
    void test() {
        if(true) { int a = b; }
    }
}
""".trimIndent(), text)
    }

    @Test
    fun testSpaceperationAfterComma() {
        val text = CodeFormatter.reformat("Haha.java", """
package com.yy.mobile.demo;

import java.io.File;

public class NormalJavaClass {

    private static void main(String a, String b, String c, String d, String e) {
    }
}
        """.trimIndent())

        Assert.assertEquals(text, """
package com.yy.mobile.demo;

import java.io.File;

public class NormalJavaClass {

    private static void main(
            String a,
            String b,
            String c,
            String d,
            String e
    ) {
    }
}
        """.trimIndent())
    }

    @Test
    fun testSpaceinForLoop() {
        val text = CodeFormatter.reformat("Haha.java", """
package com.yy.mobile.demo;

public class NormalJavaClass {

    private static void main() {
        for (; ; ) {

        }
    }
}
        """.trimIndent())

        Assert.assertEquals(text, """
package com.yy.mobile.demo;

public class NormalJavaClass {

    private static void main() {
        for (;; ) {

        }
    }
}""".trimIndent())
    }

    @Test
    fun testSpaceinForWithParamsLoop() {
        val text = CodeFormatter.reformat("Haha.java", """
package com.yy.mobile.demo;

public class NormalJavaClass {

    private static void main() {
        for (int i = 0; i < 10 ; i++) {

        }
    }
}
        """.trimIndent())

        Assert.assertEquals(text, """
package com.yy.mobile.demo;

public class NormalJavaClass {

    private static void main() {
        for (int i = 0; i < 10; i++) {

        }
    }
}""".trimIndent())
    }

    @Test
    fun testLiteral() {
        val text = CodeFormatter.reformat("J.java", """
class J{
    @SuppressLint({"NewApi"})
    @Target({ElementType.TYPE})
    public ViewHolder setAlpha(int viewId, float value) {
    }
}
""".trimIndent())

        Assert.assertEquals("""
class J{
    @SuppressLint({"NewApi"})
    @Target({ElementType.TYPE})
    public ViewHolder setAlpha(int viewId, float value) {
    }
}
        """.trimIndent(), text)
    }
}