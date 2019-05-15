package com.unionyy.mobile.reformat.core

import com.unionyy.mobile.reformat.core.rule.DumpAST
import com.unionyy.mobile.reformat.core.rule.AddSpace
import com.unionyy.mobile.reformat.core.rule.LineBreaker
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

    @Test
    fun testJavaCommentTooClose() {
        val text = CodeFormatter.reformat("D.java", """
package com.yy.mobile.checkstyleformatter;

public class ChannelMediaVideoInfoView extends AbsFloatingView {

    public boolean isPluginLianMai() {//niubi
        int i = 0;//niubi
        int j = 1 ;
    }//niubi

}
        """.trimIndent(), setOf(DumpAST(), LineBreaker(), AddSpace()))

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
    void test(){
        if(true){ int a = b;}
    }
}
        """.trimIndent(), text)
    }
}