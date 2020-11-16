import com.android.tools.lint.checks.infrastructure.TestFiles
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import com.huya.pitaya.HyExDetector
import org.junit.Test
import java.io.File

/**
 * @author YvesCheung
 * 2020/11/16
 */
@Suppress("UnstableApiUsage")
class HyExDetectorTest {

    @Test
    fun `Check Replace Java collections`() {
        lint().files(
            TestFiles.java("""
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
        
        boolean result = new LinkedList<String>().add("?");
        
        a.b();
    }
}
""".trimIndent()))
            .detector(HyExDetector())
            .sdkHome(File("/Users/yvescheung/Library/Android/sdk"))
            .run()
            .expect("""
src/a/A.java:13: Error: Replace Ex [HyExCollection]
            put("k0", "v0");
            ~~~~~~~~~~~~~~~
src/a/A.java:18: Error: Replace Ex [HyExCollection]
        field1.put("k1", "v1");
        ~~~~~~~~~~~~~~~~~~~~~~
src/a/A.java:23: Error: Replace Ex [HyExCollection]
        map.put("k2", "v2");
        ~~~~~~~~~~~~~~~~~~~
src/a/A.java:25: Error: Replace Ex [HyExCollection]
        boolean result = new LinkedList<String>().add("?");
                         ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
4 errors, 0 warnings
            """.trimIndent())
            .expectFixDiffs("""
Fix for src/a/A.java line 13: Replace with com.hyex.collections.MapEx.put(this, "k0", "v0"):
@@ -13 +13
-             put("k0", "v0");
+             com.hyex.collections.MapEx.put(this, "k0", "v0");
Fix for src/a/A.java line 18: Replace with com.hyex.collections.MapEx.put(field1, "k1", "v1"):
@@ -18 +18
-         field1.put("k1", "v1");
+         com.hyex.collections.MapEx.put(field1, "k1", "v1");
Fix for src/a/A.java line 23: Replace with com.hyex.collections.MapEx.put(map, "k2", "v2"):
@@ -23 +23
-         map.put("k2", "v2");
+         com.hyex.collections.MapEx.put(map, "k2", "v2");
Fix for src/a/A.java line 25: Replace with com.hyex.collections.ListEx.add(new LinkedList<String>(), "?"):
@@ -25 +25
-         boolean result = new LinkedList<String>().add("?");
+         boolean result = com.hyex.collections.ListEx.add(new LinkedList<String>(), "?");
            """.trimIndent())
    }
}