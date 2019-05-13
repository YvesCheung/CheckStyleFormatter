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

    //asljdfashflishlhjfkasdddasdasdadasldhjksfakjhdgfkahsdhflakshdfkgsdhjgfhjasgjkhasjkfhjadsghjfasjdbhjabfghgsadklsjaflkjdskhfailhahekfdjshkjhfjkdhfjkdhskjfhksealsdhfiludsahfklhsaklhfkahfksgdhsgckyasdvfluahlshdfklhasjkdhfbkuagjfgsafhsjhfksdhfusgrakfhbksahdfkasgvakhdfkj
    public void doc(String arg1, int arg2) {//asljdfashflishlh
        System.out.println(arg1);
    }

    public void dontChange(String arg1, int argdd) { //asljdfashflishlh
        //asljdfashflishlh
        System.out.println(arg1);

        System.out.println(argdd);//asljdfashflishl

        int ddd = 12362713;//asljdfashflishlhjfkasdddasdasdadasldhjksfakjhdgfkahsdhflakshdfkgsdhjgfhjasgjkhasjkfhjadsghjfasjdbhjabfghgsadklsjaflkjdskhfailhahekfdjshkjhfjkdhfjkdhskjfhksealsdhfiludsahfklhsaklhfkahfksgdhsgckyasdvfluahlshdfklhasjkdhfbkuagjfgsafhsjhfksdhfusgrakfhbksahdfkasgvakhdfkj
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
}
        """.trimIndent(), setOf(DumpAST(), LineBreaker()))

        Assert.assertEquals(text, """""".trimIndent())
    }
}