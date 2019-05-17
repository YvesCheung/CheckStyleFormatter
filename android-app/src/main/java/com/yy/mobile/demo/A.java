package com.yy.mobile.demo;

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

    public static class B {
        public B
                () {
            super
                    ();
        }
    }
}