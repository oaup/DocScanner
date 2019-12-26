package com.oaup.ocr.imgprocess.image;

import android.graphics.Bitmap;
import android.graphics.Point;

import java.util.List;

/**
 * Created by jkx on 2015/5/25.
 */
public class Preprocess {
   static {
       System.loadLibrary("cv_java");
       System.loadLibrary("imageprocess");
   }

    public native int detectCornersNative(Bitmap src, List<Point> points);
    public native Bitmap doPerspectiveNative(Bitmap src, List<Point> points);
    public native Bitmap cvt2GrayNative(Bitmap src);
    public native Bitmap cvt2BWNative(Bitmap src);
    public native Bitmap enhanceNative(Bitmap src);

    // detectline
    public native void setDetectLineParamNative(int minHLength,int minVLenght,int minRectPtDs,
                                          int megeVLineLenght,int megeHLineLength,int midLineDiffThresh,
                                          int maxGap);
    public native int detectFrameNative(Bitmap src);
    public native double detectLinesNative(Bitmap src, List<docFormLineInfo> horLines,List<docFormLineInfo> verLines);

    public int detectCorners(Bitmap src, List<Point> points){
        return  detectCornersNative(src, points);
    }
    public Bitmap doPerspective(Bitmap src, List<Point> points){
        return doPerspectiveNative(src, points);
    }

    public Bitmap cvt2Gray(Bitmap src){
        return cvt2GrayNative(src);
    }

    public Bitmap  cvt2BW(Bitmap src){
        return cvt2BWNative(src);
    }

    public Bitmap enhance(Bitmap src){
        return enhanceNative(src);
    }

    public void setDetectLineParam(int minHLength,int minVLenght,int minRectPtDs,
                                   int megeVLineLenght,int megeHLineLength,int midLineDiffThresh,
                                   int maxGap){
        setDetectLineParamNative(minHLength,minVLenght,minRectPtDs,megeVLineLenght,megeHLineLength
        ,midLineDiffThresh,maxGap);
    }

    public int detectFrame(Bitmap src){
        return detectFrameNative(src);
    }

    public double  detectLines(Bitmap src, List<docFormLineInfo> horLines,List<docFormLineInfo> verLines){
        return detectLinesNative(src,horLines,verLines);
    }
}

