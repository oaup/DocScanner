package com.oaup.ocr.docscanner.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jkx on 2015/10/13.
 * 拷贝asset文件夹下指定文件夹下内容到/sdcard/Android/data/app程序包名.
 */

public class AssetCopyer {
    private Context mContext;
    private AssetManager mAssetManager;
    private String mAssetFolder;
    private String mTargetFolder;
    private File mAppDirectory;
    private static final String ASSET_LIST_FILENAME = "assets.lst";
    public AssetCopyer(Context context){
        if (null == context){
            return;
        }
        this.mContext = context;
        mAssetManager = mContext.getAssets();
        Log.i("tag", "AssetCopyer instance successed!");
    }

    // srcFolder assets下的文件夹名称
    // 如果不指定,将assets下的文件按照目录结构拷贝到sdcard下
    public void setAssetFolder(String assetFolder){
        mAssetFolder = assetFolder;
    }


    // targetFolder 目标文件夹
    // 设置copy到的指定文件夹，默认情况下，将assets下当前指定的文件夹直接copy。
    public  void setTargetFolder(String targetFolder){
        this.mTargetFolder = targetFolder;
    }

    // 开始拷贝，注意要放在线程中调用。
    // return 是否成功，true 成功；false 失败。
    public boolean copy() throws IOException {

        List<String> srcFiles = new ArrayList<String>();

        mAppDirectory = mContext.getExternalFilesDir(null);
        if (null == mAppDirectory){
            return false;
        }

        if (null != mTargetFolder && !mTargetFolder.isEmpty()){
            File appDirSubfolder = new File(mAppDirectory,mTargetFolder);
            if (!appDirSubfolder.exists()){
                appDirSubfolder.mkdirs();
            }
            mAppDirectory = appDirSubfolder;
            Log.i("tag_app", mAppDirectory.getAbsolutePath());
        }

        //读取assets/$(subDirectory)目录下的assets.lst文件，得到需要copy的文件列表
        List<String> assets = getAssetsList();

        for (String asset : assets){
            if ( !new File(mAppDirectory,asset).exists()){
                srcFiles.add(asset);
            }
        }

        // 依次拷贝到app下
        for (String file : srcFiles){
            copy(file);
        }

        return true;
    }

    protected List<String> getAssetsList() throws IOException {
        List<String> files = new ArrayList<String>();
        InputStream listFile=null;
        if (null != mAssetFolder && !mAssetFolder.isEmpty()){
            listFile = mAssetManager.open(mAssetFolder);
        }else {
            String temp = new File(ASSET_LIST_FILENAME).getPath();
            listFile = mAssetManager.open(temp);
        }
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(listFile));
        String path;
        while (null != (path = bufferedReader.readLine())){
            files.add(path);
            Log.i("tag_src", path);
        }
        return files;
    }

    protected void copy(String asset) throws IOException {
        InputStream source = mAssetManager.open(new File(asset).getPath());
        File destFile = new File(mAppDirectory,asset);
        destFile.getParentFile().mkdirs();
        Log.i("tag_dest", destFile.getAbsolutePath());
        OutputStream destStream = new FileOutputStream(destFile);

        byte[] buffer = new byte[1024];
        int nRead;
        while ((nRead = source.read(buffer)) != -1){
            if (0 == nRead){
                nRead = source.read();
                if (nRead<0){
                    break;
                }
                destStream.write(nRead);
                continue;
            }

            destStream.write(buffer,0, nRead);
            Log.i("tag_read", "buffer write " + nRead);
        }
        source.close();
        destStream.close();
    }
}
