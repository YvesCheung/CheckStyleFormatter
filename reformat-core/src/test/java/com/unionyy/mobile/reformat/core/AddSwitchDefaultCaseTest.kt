package com.unionyy.mobile.reformat.core

import com.unionyy.mobile.reformat.core.rule.AddSwitchDefaultCase
import com.unionyy.mobile.reformat.core.rule.DumpAST
import com.unionyy.mobile.reformat.core.rule.LineBreaker
import com.unionyy.mobile.reformat.core.rule.SpaceOperation
import org.junit.Assert
import org.junit.Test

/**
 * Created BY PYF 2019/5/16
 * email: pengyangfan@yy.com
 */
class AddSwitchDefaultCaseTest {

    @Test
    fun testSwitchBlock() {
        val text = CodeFormatter.reformat("Haha.java", """
package com.yy.mobile.demo;

public class NormalJavaClass {

    private static void main() {
        int i = 0;
        switch (i) {
            case 1: {
                break;
            }
            case 2: {
                break;
            }
            case 3: {
                break;
            }
            case 4: {
                break;
            }
            case 5: {
                break;
            }
        }
    }
}
        """.trimIndent(), setOf(DumpAST(), AddSwitchDefaultCase()))

        Assert.assertEquals(text, """
package com.yy.mobile.demo;

public class NormalJavaClass {

    private static void main() {
        int i = 0;
        switch (i) {
            case 1: {
                break;
            }
            case 2: {
                break;
            }
            case 3: {
                break;
            }
            case 4: {
                break;
            }
            case 5: {
                break;
            }
            default:
                break;
        }
    }
}
        """.trimIndent())
    }

    @Test
    fun testSwitchBlock2() {
        val text = CodeFormatter.reformat("Haha.java", """
package com.yy.mobile.demo;

public class NormalJavaClass {

    private static void main() {
        switch (mAutoAdjustType) {
            case AUTO_ADJUST_NONE: {
                // 不用做处理
                break;
            }
            case AUTO_ADJUST_WIDTH: {
                if (mRelativeWidth != 0 && mRelativeHeight != 0) {
                    customizeScale = (float) mRelativeWidth
                            / (float) mRelativeHeight;
                    viewWidth = (int) (viewHeight * customizeScale);
                    widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(viewWidth,
                            android.view.View.MeasureSpec.EXACTLY);
                }
                break;
            }
            case AUTO_ADJUST_HEIGHT: {
                if (mRelativeWidth != 0 && mRelativeHeight != 0) {
                    customizeScale = (float) mRelativeWidth
                            / (float) mRelativeHeight;
                    viewHeight = (int) (viewWidth / customizeScale);
                    heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(viewHeight,
                            android.view.View.MeasureSpec.EXACTLY);
                }
                break;
            }
            case AUTO_ADJUST_SCALE_WIDTH: {
                viewWidth = (int) (viewHeight * mScale);
                widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(viewWidth,
                        android.view.View.MeasureSpec.EXACTLY);
                break;
            }
            case AUTO_ADJUST_SCALE_HEIGHT: {
                viewHeight = (int) (viewWidth / mScale);
                heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(viewHeight,
                        android.view.View.MeasureSpec.EXACTLY);
                break;
            }
        }
    }
}
        """.trimIndent(), setOf(DumpAST(), AddSwitchDefaultCase()))

        Assert.assertEquals(text, """
package com.yy.mobile.demo;

public class NormalJavaClass {

    private static void main() {
        switch (mAutoAdjustType) {
            case AUTO_ADJUST_NONE: {
                // 不用做处理
                break;
            }
            case AUTO_ADJUST_WIDTH: {
                if (mRelativeWidth != 0 && mRelativeHeight != 0) {
                    customizeScale = (float) mRelativeWidth
                            / (float) mRelativeHeight;
                    viewWidth = (int) (viewHeight * customizeScale);
                    widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(viewWidth,
                            android.view.View.MeasureSpec.EXACTLY);
                }
                break;
            }
            case AUTO_ADJUST_HEIGHT: {
                if (mRelativeWidth != 0 && mRelativeHeight != 0) {
                    customizeScale = (float) mRelativeWidth
                            / (float) mRelativeHeight;
                    viewHeight = (int) (viewWidth / customizeScale);
                    heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(viewHeight,
                            android.view.View.MeasureSpec.EXACTLY);
                }
                break;
            }
            case AUTO_ADJUST_SCALE_WIDTH: {
                viewWidth = (int) (viewHeight * mScale);
                widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(viewWidth,
                        android.view.View.MeasureSpec.EXACTLY);
                break;
            }
            case AUTO_ADJUST_SCALE_HEIGHT: {
                viewHeight = (int) (viewWidth / mScale);
                heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(viewHeight,
                        android.view.View.MeasureSpec.EXACTLY);
                break;
            }
            default:
                break;
        }
    }
}""".trimIndent())
    }
}
