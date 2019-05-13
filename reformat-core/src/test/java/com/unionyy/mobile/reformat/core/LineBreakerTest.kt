package com.unionyy.mobile.reformat.core

import com.unionyy.mobile.reformat.core.rule.DumpAST
import com.unionyy.mobile.reformat.core.rule.LineBreaker
import org.junit.Assert
import org.junit.Test

class LineBreakerTest {

    @Test
    fun testJavaMethodParameters() {

        val text = CodeFormatter.reformat("A.java", """
package com.yy.mobile.checkstyleformatter;

public class A {

    public A(String arg1, int arg2, String veryLongArg3, float arg4, String arg5, byte[] arg6, int arg7, String veryLongArg8) {
        System.out.println(veryLongArg3);
    }

    public String makeAVeryLongMethod(String arg1, int arg2, String veryLongArg3, float arg4, String arg5, byte[] arg6, int arg7, String veryLongArg8) {
        System.out.println(veryLongArg3);
        return veryLongArg8;
    }

    public String tooMuchParam(String arg1, int arg2, String a, int b, int c) {
        return arg1;
    }

    public String dontChange(String arg1, int arg2, String veryLongArg3, float arg4) {
        System.out.println(veryLongArg3);
        return veryLongArg3;
    }
}
        """.trimIndent(), setOf(DumpAST(), LineBreaker()))
        Assert.assertEquals(text, """
package com.yy.mobile.checkstyleformatter;

public class A {

    public A(
        String arg1,
        int arg2,
        String veryLongArg3,
        float arg4,
        String arg5,
        byte[] arg6,
        int arg7,
        String veryLongArg8
    ) {
        System.out.println(veryLongArg3);
    }

    public String makeAVeryLongMethod(
        String arg1,
        int arg2,
        String veryLongArg3,
        float arg4,
        String arg5,
        byte[] arg6,
        int arg7,
        String veryLongArg8
    ) {
        System.out.println(veryLongArg3);
        return veryLongArg8;
    }

    public String tooMuchParam(
        String arg1,
        int arg2,
        String a,
        int b,
        int c
    ) {
        return arg1;
    }

    public String dontChange(String arg1, int arg2, String veryLongArg3, float arg4) {
        System.out.println(veryLongArg3);
        return veryLongArg3;
    }
}
        """.trimIndent())
    }

    @Test
    fun testJavaComment() {

        val text = CodeFormatter.reformat("A.java", """
package com.yy.mobile.checkstyleformatter;

public class A {

    public static final String TAG = "A"; //it' a tag for log. and this comment is too long, so cut it. asljdfashflishlhjfkasdddasdasdadasl.

    //it' a tag for log. and this comment is too long, so cut it. asljdfashflishlhjfkasdddasdasdadasl.
    public static final String TAGB = "B";

    public void docTooLong(String arg1, int arg2) {//asljdfashflishlhjfkasdddasdasdadasldhjksfakjhdgfkahsdhflakshdfkgsdhjgfhjasgjkhasjkfhjadsghjfasjdbhjabfghgsadklsjaflkjdskhfailhahekfdjshkjhfjkdhfjkdhskjfhksealsdhfiludsahfklhsaklhfkahfksgdhsgckyasdvfluahlshdfklhasjkdhfbkuagjfgsafhsjhfksdhfusgrakfhbksahdfkasgvakhdfkj
        System.out.println(arg1);
    }

    public void doc(String arg1, int arg2) {//asljdfashflishlh
        System.out.println(arg1);
    }

    public void dontChange(String arg1, int arg2) { //asljdfashflishlh
        //asljdfashflishlh
        System.out.println(arg1);

        //ajshfdkadh
        System.out.println(arg1);
    }
}
        """.trimIndent(), setOf(DumpAST(), LineBreaker()))

        Assert.assertEquals(text, """
package com.yy.mobile.checkstyleformatter;

public class A {

    //it' a tag for log. and this comment is too long, so cut it. asljdfashflishlhjfkasdddasdasdadasl.
    public static final String TAG = "A";

    public void docTooLong(String arg1, int arg2) {
        //asljdfashflishlhjfkasdddasdasdadasldhjksfakjhdgfkahsdhflakshdfkg
        //sdhjgfhjasgjkhasjkfhjadsghjfasjdbhjabfghgsadklsjaflkjdskhfailhahekf
        //djshkjhfjkdhfjkdhskjfhksealsdhfiludsahfklhsaklhfkahfksgdhsgckyasdv
        //fluahlshdfklhasjkdhfbkuagjfgsafhsjhfksdhfusgrakfhbksahdfkasgvakhdfkj
        System.out.println(arg1);
    }

    public void doc(String arg1, int arg2) { //asljdfashflishlh
        System.out.println(arg1);
    }

    public void dontChange(String arg1, int arg2) { //asljdfashflishlh
        //asljdfashflishlh
        System.out.println(arg1);
    }
}
        """.trimIndent())
    }

    @Test
    fun testJavaFunctionCallTooLong() {
        val text = CodeFormatter.reformat("C.java", """
package com.yy.mobile.checkstyleformatter;

public class A {

    public A(String arg1, int arg2, String veryLongArg3, float arg4, String arg5, byte[] arg6, int arg7, String veryLongArg8) {
        System.out.println(veryLongArg3);
    }

    public Observable<LoadPluginListener.Result> loadPlugin(final SinglePluginInfo pluginInfo, final boolean showDefaultLoading) {
        MLog.info("PluginCenterApiImpl", "loadPlugin plugin = %s, showDefaultLoading = %s", pluginInfo, showDefaultLoading);
        return PluginCenterController.INSTANCE.loadPlugin(pluginInfo, showDefaultLoading);
    }
}
        """.trimIndent(), setOf(DumpAST(), LineBreaker()))

        Assert.assertEquals(text, """""".trimIndent())
    }

    @Test
    fun testJavaStringTooLong() {
        val text = CodeFormatter.reformat("D.java", """
package com.yy.mobile.checkstyleformatter;

public class A {

    public A(String arg1, int arg2, String veryLongArg3, float arg4, String arg5, byte[] arg6, int arg7, String veryLongArg8) {
        System.out.println(veryLongArg3);
    }

    public Observable<LoadPluginListener.Result> loadPlugin(final SinglePluginInfo pluginInfo, final boolean showDefaultLoading) {
        MLog.info("zhangyu4 is a nice man, wangfeihang is a beautiful woman, pengkangjia is a well guider, pengyangfan is a good xiaodi");
    }
}
        """.trimIndent(), setOf(DumpAST(), LineBreaker()))

        Assert.assertEquals(text, """""".trimIndent())
    }

    @Test
    fun testJavaConditionTooLong() {
        val text = CodeFormatter.reformat("D.java", """
package com.yy.mobile.checkstyleformatter;

public class A {

    public BasePluginViewController getCurrentUsingController(boolean isHost) {
        for (Map.Entry<String, WeakReference<BasePluginViewController>> entry : isHost ? mHostControllers.entrySet() : mGuestControllers.entrySet()) {
            if (pluginsReadyState.containsKey(entry.getKey())) {
                return entry.getValue().get();
            }
        }

        return null;
    }

    public Observable<LoadPluginListener.Result> loadPlugin(final SinglePluginInfo pluginInfo, final boolean showDefaultLoading) {
        if (LiveModuleManagerProxy.getInstance().isBookMode(EarningRxEvent.StreamLight_Type) || LiveModuleManagerProxy.getInstance().getEarnigCommonEvent() != null) {
            return null;
        }
    }

    public boolean isPluginLianMai() {
        int lianMaiType = ICoreManagerBase.getCore(ITransChannelLianMaiCore.class).getLianmaiType().getZhangyu4().getWangfeihang();
        if (lianMaiType == TransChannelLianMaiType.HAPPY_BASKETBALL || lianMaiType == TransChannelLianMaiType.GREEDY_FACE || lianMaiType == TransChannelLianMaiType.FACE_LIMINATE
//                || lianMaiType == TransChannelLianMaiType.PVP
        ) {
            MLog.info(TAG, "ICoreManagerBase.getCore(ITransChannelLianMaiCore.class).isPluginLianMai : true");
            return true;
        }
        MLog.info(TAG, "ICoreManagerBase.getCore(ITransChannelLianMaiCore.class).isPluginLianMai : false");
        AudienceMetadataManager manager = AudienceMetadataManager.getInstance();
        return manager.isBasketBallLianMai() || manager.isGreedyFaceLianMai() || manager.isOppositeScoreLianMai();
    }
}
        """.trimIndent(), setOf(DumpAST(), LineBreaker()))

        Assert.assertEquals(text, """""".trimIndent())
    }
}