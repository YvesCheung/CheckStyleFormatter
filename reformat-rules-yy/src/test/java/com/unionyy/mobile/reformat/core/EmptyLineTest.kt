package com.unionyy.mobile.reformat.core

import com.unionyy.mobile.reformat.core.rule.YYRuleSet
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class EmptyLineTest {

    @Before
    fun before(){
        CodeFormatter.defaultRules.addAll(YYRuleSet)
    }

    @Test
    fun testJavaEmptyLine() {
        val text = CodeFormatter.reformat("A.java", """
public class A {
    public void addInterceptor(Interceptor interceptor) {
        List<Interceptor> tmp = new ArrayList<>();
        ;
        tmp.addAll(mInterceptors);
        tmp.add(interceptor);
        mInterceptors = tmp;
    }
}
""".trimIndent())

        Assert.assertEquals("""public class A {
    public void addInterceptor(Interceptor interceptor) {
        List<Interceptor> tmp = new ArrayList<>();
""" + "        \n" + """
        tmp.addAll(mInterceptors);
        tmp.add(interceptor);
        mInterceptors = tmp;
    }
}
""".trimIndent(), text)
    }
}