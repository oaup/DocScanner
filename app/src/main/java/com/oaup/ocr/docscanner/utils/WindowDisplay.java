package com.oaup.ocr.docscanner.utils;

import android.content.Context;
import android.view.WindowManager;

/**
 * Created by jkx on 2015/9/10.
 */
public class WindowDisplay {
    private Context mContext;
    private static int nWidth;
    private static int nHeight;
    private WindowDisplay(Context context){
        this.mContext = context;
    }

    private static WindowDisplay mWindowDisplay;
    public static WindowDisplay getWindowDisplay(Context context){
        if (null == mWindowDisplay){
            mWindowDisplay = new WindowDisplay(context);
            WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
            nWidth = wm.getDefaultDisplay().getWidth();
            nHeight = wm.getDefaultDisplay().getHeight();
        }
        return mWindowDisplay;
    }

    public  int getDisplayWidth(){
        return nWidth;
    }
    public int getDisplayHeight(){
        return nHeight;
    }
}
