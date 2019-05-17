package com.yy.mobile.demo;

public class RichTextManager {

    public static enum Feature {
        EMOTICON(0), CHANNELAIRTICKET(1), GROUPTICKET(2), IMAGE(3), VOICE(4), VIPEMOTICON(5), NUMBER(6), NOBLEEMOTION(7), NOBLEGIFEMOTION(8);

        Feature(int value) {
            this.value = value;
        }

        private int value;
    }


    public void main() {

        MLog.info(TAG, "type=" + type + "child=" + child + "style=" + style + "anchoruid=" + reportAnchorUid + "content=" + content + "extParUrlEncoder=" + extParUrlEncoder + "extProductParam" + extProductParam + "title=" + title);

        switch (mAutoAdjustType) {
            case AUTO_ADJUST_NONE: {
                // 不用做处理
                break;
            }
            case AUTO_ADJUST_SCALE_WIDTH: {
                viewWidth = (int) (viewHeight * mScale);
                widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(viewWidth, android.view.View.MeasureSpec.EXACTLY);
                break;
            }
            case AUTO_ADJUST_SCALE_HEIGHT: {
                viewHeight = (int) (viewWidth / mScale);
                heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(viewHeight, android.view.View.MeasureSpec.EXACTLY);
                break;
            }
        }
    }

}