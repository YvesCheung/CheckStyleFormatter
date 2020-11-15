package com.unionyy.mobile.reformat.core

import com.unionyy.mobile.reformat.core.rule.AddSwitchDefaultCase
import com.unionyy.mobile.reformat.core.rule.ModifierRule
import com.unionyy.mobile.reformat.core.rule.DumpAST
import org.junit.Assert
import org.junit.Test

/**
 * Created BY PYF 2019/5/16
 * email: pengyangfan@yy.com
 */
class ModifierRuleTest {

    @Test
    fun testLackStatic() {
        val text = CodeFormatter.reformat("Haha.java", """
package com.yy.mobile.demo;

public class NormalJavaClass {
    private final String TAG = "NormalHJavaClass";
    private final String tag = "abc";
}
        """.trimIndent())

        Assert.assertEquals(text, """
package com.yy.mobile.demo;

public class NormalJavaClass {
    private static final String TAG = "NormalHJavaClass";
    private final String tag = "abc";
}""".trimIndent())
    }

    @Test
    fun testSwitchBlock() {
        val text = CodeFormatter.reformat("Haha.java", """
package com.yy.mobile.demo;

public class NormalJavaClass {
    private final String TAG = "NormalHJavaClass";

    public static String getQueryBalanceUrl(final long uid, final String url, final String appId) {
        String dataContent = String.format(BALANCE_DATA, uid(uid));
        String dataContentEncode = encode(dataContent);
        String data = String.format(DATA, dataContent);

        String dataEncode = String.format(DATA, dataContentEncode);

        String token = getToken(data);

        String encodedToken = encode(token);

        String signUrl = String.format(SIGN, encodedToken, dataEncode);
        return String.format(QUERY_BALANCE_URL, url, appId, signUrl);
    }
}
        """.trimIndent())

        Assert.assertEquals(text, """
package com.yy.mobile.demo;

public class NormalJavaClass {
    private static final String TAG = "NormalHJavaClass";

    public static String getQueryBalanceUrl(final long uid, final String url, final String appId) {
        String dataContent = String.format(BALANCE_DATA, uid(uid));
        String dataContentEncode = encode(dataContent);
        String data = String.format(DATA, dataContent);

        String dataEncode = String.format(DATA, dataContentEncode);

        String token = getToken(data);

        String encodedToken = encode(token);

        String signUrl = String.format(SIGN, encodedToken, dataEncode);
        return String.format(QUERY_BALANCE_URL, url, appId, signUrl);
    }
}""".trimIndent())
    }

}