package com.unionyy.mobile.reformat.core

import com.unionyy.mobile.reformat.core.rule.YYRuleSet
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class ContinuousExpTest {

    @Before
    fun before(){
        CodeFormatter.defaultRules.addAll(YYRuleSet)
    }

    @Test
    fun testJavaMethodCall() {

        val text = CodeFormatter.reformat("A.java", """
class A {
    void main() {
        mActivity.getContentResolver().registerContentObserver(Settings.System.getUriFor
            (NAVIGATIONBAR_IS_MIN), true, mBarParams.navigationStatusObserver);
    }
}
""".trimIndent())

        Assert.assertEquals("""
class A {
    void main() {
        mActivity.getContentResolver().registerContentObserver(
                Settings.System.getUriFor(NAVIGATIONBAR_IS_MIN),
                true,
                mBarParams.navigationStatusObserver
        );
    }
}
""".trimIndent(), text)
    }

    @Test
    fun testJavaConstructor() {
        val text = CodeFormatter.reformat("A.java", """
class A {
    public A
            () {
        super
            ();
    }
}
""".trimIndent())

        Assert.assertEquals("""
class A {
    public A() {
        super();
    }
}
""".trimIndent(), text)
    }

    @Test
    fun testJavaNewLiteral(){
        val text = CodeFormatter.reformat("A.java", """
class A {
    public void B b = new B
        ();
}
""".trimIndent())

        Assert.assertEquals("""
class A {
    public void B b = new B();
}
""".trimIndent(), text)
    }

    @Test
    fun testJavaMethodDef(){
        val text = CodeFormatter.reformat("A.java", """
class A {
    public void main
        (String[] args){
    }
}
""".trimIndent())

        Assert.assertEquals("""
class A {
    public void main(String[] args) {
    }
}
""".trimIndent(), text)
    }

    @Test
    fun testJavaEnum(){
        val text = CodeFormatter.reformat("A.java", """
enum A {
    ONE
            (1),
    TWO
            (2);

    A(int a) {
    }
}
""".trimIndent())

        Assert.assertEquals("""
enum A {
    ONE(1),
    TWO(2);

    A(int a) {
    }
}
""".trimIndent(), text)
    }
}