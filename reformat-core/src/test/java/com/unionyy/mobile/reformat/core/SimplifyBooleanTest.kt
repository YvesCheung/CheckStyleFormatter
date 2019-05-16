package com.unionyy.mobile.reformat.core

import org.junit.Test

class SimplifyBooleanTest {

    @Test
    fun testJavaBoolean() {

        val text = CodeFormatter.reformat("A.java", """
            private void initCropWindow(Rect bitmapRect) {

        // Tells the attribute functions the crop window has already been
        // initialized
        if (initializedCropWindow == false) {
            initializedCropWindow = true;
        }
        }

        public static boolean showGuidelines() {
        if ((Math.abs(Edge.LEFT.getCoordinate() - Edge.RIGHT.getCoordinate()) < DEFAULT_SHOW_GUIDELINES_LIMIT)
                || (Math.abs(Edge.TOP.getCoordinate() - Edge.BOTTOM.getCoordinate()) < DEFAULT_SHOW_GUIDELINES_LIMIT)) {
            return false;
        } else {
            return true;
        }
    }
        """.trimIndent())
    }
}