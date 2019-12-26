package com.oaup.ocr.docscanner.utils;

import android.app.ProgressDialog;
import android.content.Context;

import java.io.File;
import java.io.IOException;

/**
 * Created by jkx on 2015/10/15.
 */
public class InstallTessData {
    private Context mContext;
    public InstallTessData(Context context){
        mContext = context;
    }

    private File mAppDir;
    public void initTessData(){
        mAppDir = mContext.getExternalFilesDir(null);
        if (null != mAppDir){
            File[] files = new File(mAppDir,"tesseract/tessdata").listFiles();
            if (null != files && files.length>0){
                tessInitFinishCallback.onTessInitFinish();
            }else {
                assetCopy();
            }

        }
    }

    private void assetCopy(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    AssetCopyer assetCopyer = new AssetCopyer(mContext);
                    assetCopyer.setTargetFolder("tesseract");
                    assetCopyer.copy();
                    if (tessInitFinishCallback != null){
                        tessInitFinishCallback.onTessInitFinish();
                    }

                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private TessInitFinishCallback tessInitFinishCallback;
    public void setTessInitFinishCallback(TessInitFinishCallback tessInitFinishCallback){
        this.tessInitFinishCallback  = tessInitFinishCallback;
    }

    public interface TessInitFinishCallback{
        void onTessInitFinish();
    }

}
