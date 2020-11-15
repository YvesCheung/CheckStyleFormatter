package com.unionyy.mobile.reformat.core

import com.unionyy.mobile.reformat.core.rule.DumpAST
import com.unionyy.mobile.reformat.core.rule.HuyaExReplacement
import org.junit.Assert
import org.junit.Test

/**
 * @author YvesCheung
 * 2020/11/14
 */
class HuyaExCaseTest {

    @Test
    fun mapPutCase() {
        val text = CodeFormatter.reformat("A.java", """
package a;

import java.util.HashMap;
import java.util.Map;
import java.util.LinkedList;

public class A {

    public com.java.B a = new com.java.B();
    
    Map<String, String> field1 = new HashMap<String, String>() {
        {
            put("k0", "v0");
        }
    };

    {
        field1.put("k1", "v1");
    }

    private static void main() {
        Map<String, String> map = new HashMap<>();
        map.put("k2", "v2");
        
        LinkedList result = new LinkedList<String>().add("?");
        
        a.b();
    }
}
""".trimIndent(), setOf(DumpAST(), HuyaExReplacement()))

        Assert.assertEquals("""
package a;

import java.util.HashMap;
import java.util.Map;
import java.util.LinkedList;
import com.hyex.collections.MapEx;
import com.hyex.collections.ListEx;

public class A {

    public com.java.B a = new com.java.B();
    
    Map<String, String> field1 = new HashMap<String, String>() {
        {
            MapEx.put(this, "k0", "v0");
        }
    };

    {
        MapEx.put(field1, "k1", "v1");
    }

    private static void main() {
        Map<String, String> map = new HashMap<>();
        MapEx.put(map, "k2", "v2");
        
        LinkedList result = ListEx.add(new LinkedList<String>(), "?");
        
        a.b();
    }
}
        """.trimIndent(), text)
    }

    @Test
    fun noImportCase() {
        val text = CodeFormatter.reformat("A.java", """
package a;

public class A {

    private static void main() {
        java.util.Map<java.lang.String, java.lang.String> map = new java.util.HashMap<>();
        map.put("k2", "v2");
        
        new java.util.LinkedList<java.lang.String>().add("?");
    }
}
""".trimIndent(), setOf(HuyaExReplacement()))

        Assert.assertEquals("""
package a;

import com.hyex.collections.MapEx;
import com.hyex.collections.ListEx;

public class A {

    private static void main() {
        java.util.Map<java.lang.String, java.lang.String> map = new java.util.HashMap<>();
        MapEx.put(map, "k2", "v2");
        
        ListEx.add(new java.util.LinkedList<java.lang.String>(), "?");
    }
}
""".trimIndent(), text)
    }
}