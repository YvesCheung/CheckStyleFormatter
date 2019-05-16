package com.unionyy.mobile.reformat.core

import org.junit.Test

class EmptyBlockTest {

    @Test
    fun testJavaEmptyTryCatch() {

        val text = CodeFormatter.reformat("", """
            try {
            statTimestamp = mFile.lastModified();
            statSize = mFile.length();
            map = readFromXml(mFile);
        } catch (Exception e) {
        }

        if ((map == null || map.size() == 0) && mTempFile.exists()) {
            map = readFromXml(mTempFile);
        }

        if ((mMap == null || mMap.size() == 0) && (map == null || map.size() == 0)) {
            try {
                map = readFromXml(mBackupFile);
            } catch (Exception e) {
            }
        }
        """.trimIndent())
    }
}