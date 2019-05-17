package com.unionyy.mobile.reformat.core

import org.junit.Assert
import org.junit.Test

class ContinuousCodeBlockTest {

    @Test
    fun testTryBlock() {
        val text = CodeFormatter.reformat("A.java", """
public class A {
    public void main() {
        try {
            return super.performRequest(request);
        }
        //        catch (Throwable t) {
        //            throw t;
        //        }
        catch (Exception e) {
            //            e.printStackTrace();
            HttpLog.e("DownloadContinueNetwork", e);
        } finally {
            try {
                mRandomAccessFile.close();
            } catch (IOException e) {
                HttpLog.e(e, "RandomAccessFile close error", e);
            }
        }
    }
}
""".trimIndent())

        Assert.assertEquals("""
public class A {
    public void main() {
        try {
            return super.performRequest(request);
        } catch (Exception e) {
        //catch (Throwable t) {
        //throw t;
        //}
            //e.printStackTrace();
            HttpLog.e("DownloadContinueNetwork", e);
        } finally {
            try {
                mRandomAccessFile.close();
            } catch (IOException e) {
                HttpLog.e(e, "RandomAccessFile close error", e);
            }
        }
    }
}
""".trimIndent(), text)
    }

    @Test
    fun testElseBlock() {
        val text = CodeFormatter.reformat("A.java", """
public class A {

    void function() {
        if(a && b || c) {
            //Do something.
        }
        /*
        some comment
        */
        else if (b) {
            //Do something difference.
        }
        /*
         * some comment
         */
        else {
            //Do something else.
        }
    }
}
""".trimIndent())

        Assert.assertEquals("""
public class A {

    void function() {
        if(a && b || c) {
            //Do something.
        } else if (b) {
        /*
        some comment
        */
            //Do something difference.
        } else {
        /*
         * some comment
         */
            //Do something else.
        }
    }
}
""".trimIndent(), text)
    }

    @Test
    fun testJavaIfBlock() {

        val text = CodeFormatter.reformat("A.java", """
class A {
    void main(){
        if (sRecyclerViewItemHeights.get(i) != null) // (this is a sanity check)
        {
            scrollY += sRecyclerViewItemHeights.get(i); //add all heights of the views that are gone
        }
    }
}
""".trimIndent())

        Assert.assertEquals("""""", text)
    }

    @Test
    fun testJavaIfBlock2() {

        val text = CodeFormatter.reformat("A.java", """
class A{
    void main(){
        if (i == R.id.iv_arrow) {
            String url = ((TextView) findViewById(R.id.tv_title)).getText().toString();
            if (url != null && url.length() > 0 && !url.startsWith("http://") && !url.startsWith("https://")) {
                url = "http://" + url;
            }
            if (URLUtil.isValidUrl(url)) {
                NavigationUtils.toJSSupportedWebView(EnvSettingActivity.this, url);
            } else {
                toast("invalid url!");
            }
        } else if (i == R.id.iv_push_channel_arrow) {
            Toast.makeText(EnvSettingActivity.this, "输入错误", Toast.LENGTH_SHORT).show();

        } /* else if (i == R.id.simple_title_left) {
            finish();

        } */ else if (i == R.id.btn_crash_test) {
            throw new NullPointerException("环境设置里“点我Java崩溃”测试");

        } else if (i == R.id.btn_native_crash_test) {
            //CrashReport.testNativeCrash();

        }
    }
}
        """.trimIndent())

        Assert.assertEquals("""""", text)
    }
}