package com.unionyy.mobile.reformat.core

import com.unionyy.mobile.reformat.core.rule.DumpAST
import com.unionyy.mobile.reformat.core.rule.EmptyBlockRule
import org.junit.Assert
import org.junit.Test

class EmptyBlockTest {

    @Test
    fun testJavaEmptyTryCatch() {

        val text = CodeFormatter.reformat("A.java", """
package com.yy.mobile.demo;

public class NormalJavaClass {

    private static void main() {
        try {
            statTimestamp = mFile.lastModified();
            statSize = mFile.length();
            map = readFromXml(mFile);
        } catch (Exception e) {
        }

        try {
            map = readFromXml(mBackupFile);
        } catch (Exception e) {
            //do nothing
        }
    }
}
        """.trimIndent())

        Assert.assertEquals(text, """
package com.yy.mobile.demo;

public class NormalJavaClass {

    private static void main() {
        try {
            statTimestamp = mFile.lastModified();
            statSize = mFile.length();
            map = readFromXml(mFile);
        } catch (Exception e) {
            //Do nothing.
        }

        try {
            map = readFromXml(mBackupFile);
        } catch (Exception e) {
            //do nothing
        }
    }
}
        """.trimIndent())
    }
}