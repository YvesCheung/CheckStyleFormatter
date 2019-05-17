package com.yy.mobile.demo;

import java.io.File;

public class NormalJavaClass {

    public NormalJavaClass() {
    }

    private static void main(
            String a,
            String b,
            String c,
            String d,
            String e
    ) {
        final File dir =
                new File(YYFileUtils.getRootDir() + File.separator + CommonFuncNoticeController.COMMON_ANIMATION_DIR);
        String aa = "asdsafahsdfhladh: " + dir.getPath() + " and the next is: " + dir;
        int bbb = a.length() + dir.toString().length() + 123432 + b.length() + c.length() + d.length() + e.length();
    }

    private static class CommonFuncNoticeController {
        private static final String COMMON_ANIMATION_DIR = "ashdfduhishcahdiufhgwgefukyawgekgakdhckgdfkaghfjkhe";
    }

    private static class YYFileUtils {

        private static String getRootDir() {
            return CommonFuncNoticeController.COMMON_ANIMATION_DIR;
        }
    }

    private static class DispenseChannelProtocol {

        private static class ALongClassName {


        }
    }

    private static NormalJavaClass.DispenseChannelProtocol.ALongClassName clsName =
            new NormalJavaClass.DispenseChannelProtocol.ALongClassName();
}
