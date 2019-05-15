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
        """.trimIndent())
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

    public static final String TAG = "A"; //it' a tag for log. and this comment is too long, so cut it. asljdfashflidsafghakjsdbhkjabfjhasdadsfsdfaddsfasdshlhjfkasdddasdasdadasl.

    //it' a tag for log. and this comment is too long, so cut it. asljdfashflishlhadsfjkghbajsdh,jsadfhaksdhkhfaskldhfkjhsdjvabskljhfklasfkasdddasdasdadasl.
    public static final String TAGB = "B";

    public void docTooLong(String arg1, int arg2) {//asljdfashasdsfasfaddfdsvfflishlhjfkasdddasdasdadasldhjksfakjhdgfkahsdhflakshdfkgsdhjgfhjasgjkhasjkfhjadsghjfasjdbhjabfghgsadklsjaflkjdskhfailhahekfdjshkjhfjkdhfjkdhskjfhksealsdhfiludsahfklhsaklhfkahfksgdhsgckyasdvfluahlshdfklhasjkdhfbkuagjfgsafhsjhfksdhfusgrakfhbksahdfkasgvakhdfkj
        System.out.println(arg1);
    }

    //asljdfashflishlhjfkasdddasdasdadasldhjksfakjhdgfkahsdhflakshdfkgsdhjgfhjasgjkhasjkfhjadsghjfasjdbhjabfghgsadklsjaflkjdskhfailhahekfdjshkjhfjkdhfjkdhskjfhksealsdhfiludsahfklhsaklhfkahfksgdhsgckyasdvfluahlshdfklhasjkdhfbkuagjfgsafhsjhfksdhfusgrakfhbksahdfkasgvakhdfkj
    public void doc(String arg1, int arg2) {//asljdfashflishlh
        System.out.println(arg1);
    }

    public void dontChange(String arg1, int argdd, int arggg) { //asljdfashflishlh
        //asljdfashflishlh
        System.out.println(arg1);

        System.out.println(argdd);//asljdfashflishl

        System.out.println(arggg);//asljdfashflishlasljdfashf苏打粉萨阿德沙发上的阿斯顿vlishlhjfkasdddasdasdadasldhjksfakjhdgasdsdfasdfad

/*            if (isLandScape) {
                //横屏 频道Combo 礼物名字统计
//                getCore(IHiidoStatisticCore.class).sendEventStatistic(getCore(IAuthCore.class).getUserId(), IHiidoStatisticCore.EVENT_ID_CHANNEL_COMBO, IHiidoStatisticCore.EVENT_LABEL_CHANNEL_COMBO_GIFT_NAME_LAND + itemBase.name);
                //横屏 频道Combo 礼物数量统计
//                getCore(IHiidoStatisticCore.class).sendEventStatistic(getCore(IAuthCore.class).getUserId(), IHiidoStatisticCore.EVENT_ID_CHANNEL_COMBO, IHiidoStatisticCore.EVENT_LABEL_CHANNEL_COMBO_GIFT_NUM_LAND + num);
                //横屏 频道Combo 礼物组数统计
                //CoreFactory.getCore(IHiidoStatisticCore.class).sendEventStatistic(LoginUtil.getUid(),IHiidoStatisticCore.EVENT_ID_CHANNEL_COMBO, IHiidoStatisticCore.EVENT_LABEL_CHANNEL_COMBO_GIFT_GROUP_LAND + (int)(nowCombo*1.0f/groupTotal));
                //横屏 频道Combo 连击次数统计
//                getCore(IHiidoStatisticCore.class).sendEventStatistic(getCore(IAuthCore.class).getUserId(), IHiidoStatisticCore.EVENT_ID_CHANNEL_COMBO, IHiidoStatisticCore.EVENT_LABEL_CHANNEL_COMBO_COMBO_NUM_LAND + nowCombo);

//                getCore(IHiidoStatisticCore.class).sendEventStatistic(getCore(IAuthCore.class).getUserId(), IHiidoStatisticCore.EVENT_ID_CHANNEL_COMBO, IHiidoStatisticCore.EVENT_LABEL_CHANNEL_COMBO_SUCCEED_LAND);
            } else {
                //竖屏 频道Combo 礼物名字统计
//                getCore(IHiidoStatisticCore.class).sendEventStatistic(getCore(IAuthCore.class).getUserId(), IHiidoStatisticCore.EVENT_ID_CHANNEL_COMBO, IHiidoStatisticCore.EVENT_LABEL_CHANNEL_COMBO_GIFT_NAME + itemBase.name);
                //竖屏 频道Combo 礼物数量统计
//                getCore(IHiidoStatisticCore.class).sendEventStatistic(getCore(IAuthCore.class).getUserId(), IHiidoStatisticCore.EVENT_ID_CHANNEL_COMBO, IHiidoStatisticCore.EVENT_LABEL_CHANNEL_COMBO_GIFT_NUM + num);
                //竖屏 频道Combo 礼物组数统计
                //CoreFactory.getCore(IHiidoStatisticCore.class).sendEventStatistic(LoginUtil.getUid(),IHiidoStatisticCore.EVENT_ID_CHANNEL_COMBO, IHiidoStatisticCore.EVENT_LABEL_CHANNEL_COMBO_GIFT_GROUP_LAND + (int)(nowCombo*1.0f/groupTotal));
                //竖屏 频道Combo 连击次数统计
//                getCore(IHiidoStatisticCore.class).sendEventStatistic(getCore(IAuthCore.class).getUserId(), IHiidoStatisticCore.EVENT_ID_CHANNEL_COMBO, IHiidoStatisticCore.EVENT_LABEL_CHANNEL_COMBO_COMBO_NUM + nowCombo);

//                getCore(IHiidoStatisticCore.class).sendEventStatistic(getCore(IAuthCore.class).getUserId(), IHiidoStatisticCore.EVENT_ID_CHANNEL_COMBO, IHiidoStatisticCore.EVENT_LABEL_CHANNEL_COMBO_SUCCEED);
            }*/
        int ddd = 12362713;//asljdfashflishlhjfkasdddasdasdadasldhjksfakjhdgfkahsdhflakshdfkgsdhjgfhjasgjkhasjkfhjadsghjfasjdbhjabfghgsadklsjaflkjdskhfailhahekfdjshkjhfjkdhfjkdhskjfhksealsdhfiludsahfklhsaklhfkahfksgdhsgckyasdvfluahlshdfklhasjkdhfbkuagjfgsafhsjhfksdhfusgrakfhbksahdfkasgvakhdfkj
    }
}
""".trimIndent(), setOf(DumpAST(), LineBreaker()))

        Assert.assertEquals("""
package com.yy.mobile.checkstyleformatter;

public class A {

    //it' a tag for log. and this comment is too long, so cut it. asljdf
    //ashflidsafghakjsdbhkjabfjhasdadsfsdfaddsfasdshlhjfkasdddasdasdadasl.
    public static final String TAG = "A";

    //it' a tag for log. and this comment is too long, so cut it. asljdfashflish
    //lhadsfjkghbajsdh,jsadfhaksdhkhfaskldhfkjhsdjvabskljhfklasfkasdddasdasdadasl.
    public static final String TAGB = "B";

    public void docTooLong(
        String arg1,
        int arg2
    ) {
    //asljdfashasdsfasfaddfdsvfflishlhjfkasdddasdasdadasldhjksfakjhdgfkahs
    //dhflakshdfkgsdhjgfhjasgjkhasjkfhjadsghjfasjdbhjabfghgsadklsjaflkjdskhfa
    //ilhahekfdjshkjhfjkdhfjkdhskjfhksealsdhfiludsahfklhsaklhfkahfksgdhsgckya
    //sdvfluahlshdfklhasjkdhfbkuagjfgsafhsjhfksdhfusgrakfhbksahdfkasgvakhdfkj
        System.out.println(arg1);
    }

    //asljdfashflishlhjfkasdddasdasdadasldhjksfakjhdgfkahsdhflakshdfkg
    //sdhjgfhjasgjkhasjkfhjadsghjfasjdbhjabfghgsadklsjaflkjdskhfailhahekf
    //djshkjhfjkdhfjkdhskjfhksealsdhfiludsahfklhsaklhfkahfksgdhsgckyasdvf
    //luahlshdfklhasjkdhfbkuagjfgsafhsjhfksdhfusgrakfhbksahdfkasgvakhdfkj
    public void doc(String arg1, int arg2) {//asljdfashflishlh
        System.out.println(arg1);
    }

    public void dontChange(String arg1, int argdd, int arggg) { //asljdfashflishlh
        //asljdfashflishlh
        System.out.println(arg1);

        System.out.println(argdd);//asljdfashflishl

        //asljdfashflishlasljdfashf苏打粉萨阿德沙发上的阿斯顿vlishlhjfkasdddasdasdadasldhjksfakjhdgasdsdfasdfad
        System.out.println(arggg);

/*            if (isLandScape) {                //横屏 频道Combo 礼物名字统计
//                getCore(IHiidoStatisticCore.class).send
    EventStatistic(getCore(IAuthCore.class).getUserId(), IHiido
    StatisticCore.EVENT_ID_CHANNEL_COMBO, IHiidoStatisticCo
    re.EVENT_LABEL_CHANNEL_COMBO_GIFT_NAME_LAND + itemBase.name);                //横屏 频道Combo 礼物数量统计
//                getCore(IHiidoStatisticCore.class).s
    endEventStatistic(getCore(IAuthCore.class).getUserId(),
    IHiidoStatisticCore.EVENT_ID_CHANNEL_COMBO, IHiidoSta
    tisticCore.EVENT_LABEL_CHANNEL_COMBO_GIFT_NUM_LAND + num);                //横屏 频道Combo 礼物组数统计
                //CoreFactory.getCore(IHiidoStatisticCore.cl
    ass).sendEventStatistic(LoginUtil.getUid(),IHiidoStatisticCore
    .EVENT_ID_CHANNEL_COMBO, IHiidoStatisticCore.EVENT_LABEL_C
    HANNEL_COMBO_GIFT_GROUP_LAND + (int)(nowCombo*1.0f/groupTotal));                //横屏 频道Combo 连击次数统计
//                getCore(IHiidoStatisticCore.class).sen
    dEventStatistic(getCore(IAuthCore.class).getUserId(), IHi
    idoStatisticCore.EVENT_ID_CHANNEL_COMBO, IHiidoStatist
    icCore.EVENT_LABEL_CHANNEL_COMBO_COMBO_NUM_LAND + nowCombo);
//                getCore(IHiidoStatisticCore.class).
    sendEventStatistic(getCore(IAuthCore.class).getUserId(
    ), IHiidoStatisticCore.EVENT_ID_CHANNEL_COMBO, IHii
    doStatisticCore.EVENT_LABEL_CHANNEL_COMBO_SUCCEED_LAND);            } else {                //竖屏 频道Combo 礼物名字统计
//                getCore(IHiidoStatisticCore.class).sen
    dEventStatistic(getCore(IAuthCore.class).getUserId(), IHi
    idoStatisticCore.EVENT_ID_CHANNEL_COMBO, IHiidoStatist
    icCore.EVENT_LABEL_CHANNEL_COMBO_GIFT_NAME + itemBase.name);                //竖屏 频道Combo 礼物数量统计
//                getCore(IHiidoStatisticCore.class).
    sendEventStatistic(getCore(IAuthCore.class).getUserId()
    , IHiidoStatisticCore.EVENT_ID_CHANNEL_COMBO, IHiid
    oStatisticCore.EVENT_LABEL_CHANNEL_COMBO_GIFT_NUM + num);                //竖屏 频道Combo 礼物组数统计
                //CoreFactory.getCore(IHiidoStatisticCore.cl
    ass).sendEventStatistic(LoginUtil.getUid(),IHiidoStatisticCore
    .EVENT_ID_CHANNEL_COMBO, IHiidoStatisticCore.EVENT_LABEL_C
    HANNEL_COMBO_GIFT_GROUP_LAND + (int)(nowCombo*1.0f/groupTotal));                //竖屏 频道Combo 连击次数统计
//                getCore(IHiidoStatisticCore.class).se
    ndEventStatistic(getCore(IAuthCore.class).getUserId(), I
    HiidoStatisticCore.EVENT_ID_CHANNEL_COMBO, IHiidoStat
    isticCore.EVENT_LABEL_CHANNEL_COMBO_COMBO_NUM + nowCombo);
//                getCore(IHiidoStatisticCore.class
    ).sendEventStatistic(getCore(IAuthCore.class).getUser
    Id(), IHiidoStatisticCore.EVENT_ID_CHANNEL_COMBO,
    IHiidoStatisticCore.EVENT_LABEL_CHANNEL_COMBO_SUCCEED);            }*/
        //asljdfashflishlhjfkasdddasdasdadasldhjksfakjhdgfkahsdhflakshdfkg
        //sdhjgfhjasgjkhasjkfhjadsghjfasjdbhjabfghgsadklsjaflkjdskhfailhahekf
        //djshkjhfjkdhfjkdhskjfhksealsdhfiludsahfklhsaklhfkahfksgdhsgckyasdvf
        //luahlshdfklhasjkdhfbkuagjfgsafhsjhfksdhfusgrakfhbksahdfkasgvakhdfkj
        int ddd = 12362713;
    }
}
""".trimIndent(), text)
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
    fun testJavaAddOperation() {
        val text = CodeFormatter.reformat("Haha.java", """
package com.yy.mobile.demo;

import java.io.File;

public class NormalJavaClass {

    private static void main(String a, String b, String c, String d, String e) {
        final File dir = new File(YYFileUtils.getRootDir() + File.separator + CommonFuncNoticeController.COMMON_ANIMATION_DIR);
        String aa = "asdsafahsdfhladh: " + dir.getPath() + dir.toString().length()+a.length()+ b.length() + " and the next is: " + dir;
        int bbb = a.length() + dir.toString().length()+a.length()+ b.length() + 123432 + b.length() + c.length() + d.length() + e.length();
    }

    private static class CommonFuncNoticeController {
        private static final String COMMON_ANIMATION_DIR = "ashdfduhishcahasdhlfhslhakHsfuiewhiknkdnajksdiufhgwgefukyawgekgakdhckgdfkaghfjkhe";
    }

    private static class YYFileUtils {

        private static String getRootDir() {
            return CommonFuncNoticeController.COMMON_ANIMATION_DIR;
        }
    }
}
        """.trimIndent())

        Assert.assertEquals(text, """
package com.yy.mobile.demo;

import java.io.File;

public class NormalJavaClass {

    private static void main(
        String a,
        String b,
        String c,
        String d,
        String e
    ) {
        final File dir = new File(YYFileUtils.getRootDir() +
            File.separator +
            CommonFuncNoticeController.COMMON_ANIMATION_DIR);
        String aa = "asdsafahsdfhladh: " + dir.getPath() + dir.toString().length()+a.length()+ b.length() +
            " and the next is: " + dir;
        int bbb = a.length() +
            dir.toString().length()+
            a.length()+
            b.length() + 123432 +
            b.length() +
            c.length() +
            d.length() +
            e.length();
    }

    private static class CommonFuncNoticeController {
        private static final String COMMON_ANIMATION_DIR = "ashdfduhishcahasdhlfhslhakHsfuiewhiknkdn" +
            "ajksdiufhgwgefukyawgekgakdhckgdfkaghfjkhe";
    }

    private static class YYFileUtils {

        private static String getRootDir() {
            return CommonFuncNoticeController.COMMON_ANIMATION_DIR;
        }
    }
}
        """.trimIndent())
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

    public Observable<LoadPluginListener.Result> loadPlugin2(final SinglePluginInfo pluginInfo, final boolean showDefaultLoading) {
        MLog.info("zhangyu4 is a nice man, " +
            "wangfeihang is a beautiful woman, " +
            "pengkangjia is a well guider, " +
            "pengyangfan is a good xiaodi");
    }
}
        """.trimIndent())

        Assert.assertEquals("""
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

    public Observable<LoadPluginListener.Result> loadPlugin(
        final SinglePluginInfo pluginInfo,
        final boolean showDefaultLoading
    ) {
        MLog.info("zhangyu4 is a nice man, wangfeihang is a beautiful woman, " +
            "pengkangjia is a well guider, pengyangfan is a good xiaodi");
    }

    public Observable<LoadPluginListener.Result> loadPlugin2(
        final SinglePluginInfo pluginInfo,
        final boolean showDefaultLoading
    ) {
        MLog.info("zhangyu4 is a nice man, " +
            "wangfeihang is a beautiful woman, " +
            "pengkangjia is a well guider, " +
            "pengyangfan is a good xiaodi");
    }
}
        """.trimIndent(), text)
    }

    @Test
    fun testJavaConditionTooLong() {
        val text = CodeFormatter.reformat("D.java", """
package com.yy.mobile.checkstyleformatter;

public class A {

    public Observable<LoadPluginListener.Result> loadPlugin(final SinglePluginInfo pluginInfo, final boolean showDefaultLoading) {
        if (LiveModuleManagerProxy.getInstance().isBookMode(EarningRxEvent.StreamLight_Type) && LiveModuleManagerProxy.getInstance().getEarnigCommonEvent() != null) {
            return null;
        } else if (LiveModuleManagerProxy.getInstance().isBookMode(EarningRxEvent.StreamLight_Type) || LiveModuleManagerProxy.getInstance().getEarnigCommonEvent() != null) {
            return null;
        }
    }
}
        """.trimIndent(), setOf(DumpAST(), LineBreaker()))

        Assert.assertEquals(text, """
package com.yy.mobile.checkstyleformatter;

public class A {

    public Observable<LoadPluginListener.Result> loadPlugin(
        final SinglePluginInfo pluginInfo,
        final boolean showDefaultLoading
    ) {
        if (LiveModuleManagerProxy.getInstance().isBookMode(EarningRxEvent.StreamLight_Type)
                && LiveModuleManagerProxy.getInstance().getEarnigCommonEvent() != null) {
            return null;
        } else if (LiveModuleManagerProxy.getInstance().isBookMode(EarningRxEvent.StreamLight_Type)
                || LiveModuleManagerProxy.getInstance().getEarnigCommonEvent() != null) {
            return null;
        }
    }
}
        """.trimIndent())
    }

    @Test
    fun testJavaWithEveryOddSituationICanImage() {
        val text = CodeFormatter.reformat("D.java", """
package com.yy.mobile.checkstyleformatter;

public class A {

    public Observable<LoadPluginListener.Result> loadPlugin(final SinglePluginInfo pluginInfo, final boolean showDefaultLoading) {
        MLog.info(TAG, "zhangyu4 is a nice man, wangfeihang is a beautiful woman, pengkangjia is a well guider,pengyangfan is a good xiaodi");
    }

    public Flowable<Scene> setChannelInfo(final IChannelBaseParam param) {
        final long sid = param.getSid();
        final long ssid = param.getSsid();
        final int liveType = param.getLiveType();
        final String templateId = param.getTemplateId();
        MLog.info(TAG, "requestChannelType setChannelInfo liveType = " + liveType + " templateId = " + templateId);
        if (TextUtils.isEmpty(templateId)
                || ((LinkChannelConstants.TEMPLATE_ENTERTAINMENT.equals(templateId)
                || LinkChannelConstants.TEMPLATE_MOBILE_LIVE.equals(templateId))
                && liveType == ILivingCoreConstant.LIVING_TYPE_UNKNOWN)) {
            return changeScene(Scene.PREPARE)
                    .concatMap(new Function<Scene, Publisher<DispenseChannelProtocol.ChannelSearchResp>>() {
                        @Override
                        public Publisher<DispenseChannelProtocol.ChannelSearchResp> apply(Scene scene) throws Exception {
                            applySceneToRoot(scene);
                            return requestChannelType(sid, ssid)
                                    .observeOn(AndroidSchedulers.mainThread());
                        }
                    })
                    .onErrorResumeNext(new Function<Throwable, Publisher<? extends DispenseChannelProtocol.ChannelSearchResp>>() {
                        @Override
                        public Publisher<? extends DispenseChannelProtocol.ChannelSearchResp> apply(Throwable throwable) throws Exception {
                            if (throwable instanceof EntNoConnectionError
                                    || throwable instanceof EntTimeoutError
                                    || throwable instanceof MaxRetryReachError) {
                                DispenseChannelProtocol.ChannelSearchResp errorResp = new DispenseChannelProtocol.ChannelSearchResp();
                                if (liveType == ILivingCoreConstant.LIVING_TYPE_MOBILE_LIVE) {
                                    errorResp.type = Uint32.toUInt(TYPE_MOBILE);
                                } else {
                                    errorResp.type = Uint32.toUInt(TYPE_DEFAULT);
                                }
                                MLog.info(TAG, "requestChannelType timeout or noconnection liveType =" + liveType);
                                return Flowable.just(errorResp);
                            }
                            return Flowable.error(throwable);
                        }
                    })
                    .concatMap(new Function<DispenseChannelProtocol.ChannelSearchResp, Publisher<? extends Scene>>() {
                        @Override
                        public Publisher<? extends Scene> apply(DispenseChannelProtocol.ChannelSearchResp channelSearchResp) throws Exception {
                            Map<String, String> extendInfo = channelSearchResp.mData;
                            int type = channelSearchResp.type.intValue();
                            //避免频道信息回来被覆盖
                            String channelTemplateId = ICoreManagerBase.getChannelLinkCore().getCurrentChannelInfo().templateid;
                            MLog.info(TAG, "requestChannelType receive start channelTemplateId = " + channelTemplateId
                                    + " templateId = " + templateId);
                            if (TextUtils.isEmpty(channelTemplateId)) {
                                channelTemplateId = templateId;
                                if (TextUtils.isEmpty(channelTemplateId)) {
                                    if (extendInfo != null && extendInfo.containsKey("template_id")) {
                                        channelTemplateId = extendInfo.get("template_id");
                                        ICoreManagerBase.getChannelLinkCore().setTemplateId(channelTemplateId);
                                        MLog.info(TAG, "requestChannelType receive over channelTemplateId = " + channelTemplateId);
                                    }
                                }
                            }
                            param.updateTemplateId(channelTemplateId);
                            //避免流信息回来被覆盖
                            int realLiveType = getStreamInfoType();
                            MLog.info(TAG, "requestChannelType receive type = " + type + " realLiveType = " + realLiveType);
                            if (realLiveType == 0) {
                                switch (type) {
                                    case TYPE_MOBILE:
                                        realLiveType = ILivingCoreConstant.LIVING_TYPE_MOBILE_LIVE;
                                        break;
                                    case TYPE_GAME:
                                        // 只有游戏类型的用网络传回来的sid ssid
                                        long uid = channelSearchResp.uid.longValue();
                                        long respSid = StringUtils.safeParseLong(channelSearchResp.reTopCid);
                                        long respSsid = StringUtils.safeParseLong(channelSearchResp.reSubCid);
                                        param.updateUid(uid);
                                        param.updateSidAndSSid(respSid, respSsid);
                                    case TYPE_NONE:
                                    case TYPE_DEFAULT:
                                    default:
                                        realLiveType = ILivingCoreConstant.LIVING_TYPE_SHOW_LIVE;
                                        break;
                                }
                            }
                            param.updateLiveType(realLiveType);
                            return changeInfoInner(param);
                        }
                    });
        } else {
            return changeInfoInner(param);
        }
    }
}""".trimIndent())

        Assert.assertEquals(text, """""".trimIndent())
    }

    @Test
    fun testJavaReferenceTooLong() {
        val text = CodeFormatter.reformat("D.java", """
package com.yy.mobile.checkstyleformatter;

public class A {

    public boolean isPluginLianMai() {
        int lianMaiType = ICoreManagerBase.getCore(ITransChannelLnMaiCore.class).getLianmaiType().getZhangyu4().getWangfeihang();
        ICoreManagerBase.getCore(ITransChannelLnMaiCore.class).getLianmaiType().getZhangyu4().getWangfeihang().testAsLongAs();
    }
}
        """.trimIndent(), setOf(DumpAST(), LineBreaker()))

        Assert.assertEquals(text, """
package com.yy.mobile.checkstyleformatter;

public class A {

    public boolean isPluginLianMai() {
        int lianMaiType = ICoreManagerBase.getCore(ITransChannelLnMaiCore.class)
                .getLianmaiType()
                .getZhangyu4()
                .getWangfeihang();
        ICoreManagerBase.getCore(ITransChannelLnMaiCore.class)
                .getLianmaiType()
                .getZhangyu4()
                .getWangfeihang()
                .testAsLongAs();
    }
}
        """.trimIndent())
    }

    @Test
    fun testJavaTrinocularTooLong() {
        val text = CodeFormatter.reformat("D.java", """
package com.yy.mobile.checkstyleformatter;

public class A {

    public boolean isPluginLianMai() {
        int lianMaiType = ICoreManagerBase.getCore(ITransChannelLnMaiCore.class).getLianmaiType() ? test.pluginA : test.pluginB;
    }
}
        """.trimIndent(), setOf(DumpAST(), LineBreaker()))

        Assert.assertEquals(text, """
package com.yy.mobile.checkstyleformatter;

public class A {

    public boolean isPluginLianMai() {
        int lianMaiType = ICoreManagerBase.getCore(ITransChannelLnMaiCore.class).getLianmaiType()
                ? test.pluginA
                : test.pluginB;
    }
}""".trimIndent())
    }

    @Test
    fun testJavaDeclarationTooLong() {
        val text = CodeFormatter.reformat("D.java", """
package com.yy.mobile.checkstyleformatter;

public class A {

    public boolean isPluginLianMai() {
        if (true) {
            ICoreManagerBase.getCore(IChatEmotionCore.class).addRichTextFilterFeature(RichTextManager.Feature.NOBLEMOTION);
        }

        String guideString = CommonPref.instance().get(String.valueOf(LoginUtil.getUid()) + "nobleChatEmotssssssionGuide");

        mTipsTextView.setCompoundDrawablesWithIntrinsicBounds(mXdown > getWidth() / 2 ? R.drawable.icon_voice : R.drawable.icon_brigh, 0, 0, 0);
        mTipsTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

        com.yy.mobile.http.RequestManager.instance().submitDownloadRequest(url, urlPathMap.get(url), new ResponseListener<String>());
    }
}
        """.trimIndent(), setOf(DumpAST(), LineBreaker()))

        Assert.assertEquals(text, """
package com.yy.mobile.checkstyleformatter;

public class A {

    public boolean isPluginLianMai() {
        if (true) {
            ICoreManagerBase.getCore(IChatEmotionCore.class)
                    .addRichTextFilterFeature(RichTextManager
                    .Feature
                    .NOBLEMOTION);
        }

        String guideString = CommonPref.instance().get(String.valueOf(LoginUtil.getUid()) +
            "nobleChatEmotssssssionGuide");

        mTipsTextView.setCompoundDrawablesWithIntrinsicBounds(
            mXdown > getWidth() / 2
                ? R.drawable.icon_voice
                : R.drawable.icon_brigh,
            0,
            0,
            0
        );
        mTipsTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

        com.yy.mobile.http.RequestManager.instance().submitDownloadRequest(
            url,
            urlPathMap.get(url),
            new ResponseListener<String>()
        );
    }
}
""".trimIndent())
    }

    @Test
    fun testJavaSpecialTooLong() {
        val text = CodeFormatter.reformat("D.java", """
package com.yy.mobile.checkstyleformatter;

public class A {

    public boolean isPluginLianMai() {
        com.yy.mobile.http.RequestManager.instance().submitDownloadRequest(url, urlPathMap.get(url), new ResponseListener<String>() {
                        @Override
                        public void onResponse(String response) {
                            MLog.info("BatchDownloadManager", url + " download success");
                            e.onNext(url);
                        }
                    }, new ResponseErrorListener() {
                        @Override
                        public void onErrorResponse(RequestError error) {
                            MLog.info("BatchDownloadManager", url + " download failed");
                            e.onNext(DOWNLOAD_FAILED);
                        }
                    }, new ProgressListener() {
                        @Override
                        public void onProgress(ProgressInfo info) {

                        }
                    }, false, true);
    }
}
        """.trimIndent(), setOf(DumpAST(), LineBreaker()))

        Assert.assertEquals(text, """
package com.yy.mobile.checkstyleformatter;

public class A {

    public boolean isPluginLianMai() {
        com.yy.mobile.http.RequestManager.instance().submitDownloadRequest(
            url,
            urlPathMap.get(url),
            new ResponseListener<String>() {
                        @Override
                        public void onResponse(String response) {
                            MLog.info("BatchDownloadManager", url + " download success");
                            e.onNext(url);
                        }
                    },
            new ResponseErrorListener() {
                        @Override
                        public void onErrorResponse(RequestError error) {
                            MLog.info("BatchDownloadManager", url + " download failed");
                            e.onNext(DOWNLOAD_FAILED);
                        }
                    },
            new ProgressListener() {
                        @Override
                        public void onProgress(ProgressInfo info) {

                        }
                    },
            false,
            true
        );
    }
}""".trimIndent())
    }

    @Test
    fun testJavaFuntionCallTooLong() {
        val text = CodeFormatter.reformat("D.java", """
package com.yy.mobile.checkstyleformatter;

public class A {

    public boolean isPluginLianMai() {
        mTipsTextView.setCompoundDrawablesWithIntrinsicBounds(info, urlPathMap.get(url, url, url, url), 0, 0, zhangyu4, wangfeihang, pengyangfan);
    }
}
        """.trimIndent(), setOf(DumpAST(), LineBreaker()))

        Assert.assertEquals(text, """
package com.yy.mobile.checkstyleformatter;

public class A {

    public boolean isPluginLianMai() {
        mTipsTextView.setCompoundDrawablesWithIntrinsicBounds(
            info,
            urlPathMap.get(
                url,
                url,
                url,
                url
            ),
            0,
            0,
            zhangyu4,
            wangfeihang,
            pengyangfan
        );
    }
}
        """.trimIndent())
    }

    @Test
    fun testJavaReference2TooLong() {
        val text = CodeFormatter.reformat("D.java", """
package com.yy.mobile.checkstyleformatter;

public class A {

    public boolean isPluginLianMai() {
        com.duowan.mobile.entlive.domain.pyf.FreeContainer container = new com.duowan.mobile.entlive.domain.pyf.FreeContainer();
        com.duowan.mobile.RequestManager.instance().submitDownloadRequest().test().test().test().test().test().test().test().test().test().test();
    }
}
        """.trimIndent(), setOf(DumpAST(), LineBreaker()))

        Assert.assertEquals(text, """
package com.yy.mobile.checkstyleformatter;

public class A {

    public boolean isPluginLianMai() {
        com.duowan.mobile.entlive.domain.pyf.FreeContainer container = new com.duowan.mobile.entlive.domain.pyf
                .FreeContainer();
        com.duowan.mobile.RequestManager
                .instance()
                .submitDownloadRequest()
                .test()
                .test()
                .test()
                .test()
                .test()
                .test()
                .test()
                .test()
                .test()
                .test();
    }
}
        """.trimIndent())
    }
}