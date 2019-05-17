package com.yy.mobile.demo;

public class A {

    //private static final Pattern AIR_TICKET_WITH_SUB_CHANNEL_PATTERN = Pattern.compile(AIR_TICKET_WITH_SUB_CHANNEL_REG);
    private static final String NUMBER_REG = "[0-9]+";

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

    public boolean isPluginLianMai() {
        int lianMaiType = ICoreManagerBase.getCore(ITransChannelLnMaiCore.class).getLianmaiType().getZhangyu4().getWangfeihang().test().test();
        ICoreManagerBase.getCore(ITransChannelLnMaiCore.class).getLianmaiType().getZhangyu4().getWangfeihang().testAsLongAs();
    }

    public static class B {
        public B
                () {
            super
                    ();
        }
    }
}