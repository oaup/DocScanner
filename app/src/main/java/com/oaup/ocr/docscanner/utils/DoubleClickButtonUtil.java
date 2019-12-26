package com.oaup.ocr.docscanner.utils;

/**
 * Created by jkx on 2015/8/20.
 */
public class DoubleClickButtonUtil {

    private static long mLastClickTime;
    public synchronized  static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - mLastClickTime;
        if ( 0 < timeD && timeD < 1000) {
            return true;
        }
        mLastClickTime = time;
        return false;
    }
}
