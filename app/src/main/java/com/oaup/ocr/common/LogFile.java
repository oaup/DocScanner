package com.oaup.ocr.common;

import android.util.Log;

import java.io.File;
import java.io.RandomAccessFile;

/**
 * Created by jkx on 2015/6/3.
 */
public class LogFile {
    private static LogFile instanceLog = null;
    public static LogFile getInstanceLog(){
        if (instanceLog == null){
            synchronized (LogFile.class){
                if (instanceLog == null){
                    instanceLog = new LogFile();
                }
            }
        }
        return instanceLog;
    }

    private LogFile(){}

    public void writeTxtToFile(String strLogContent,String strPath,String strLogFileName){
        makeFilePath(strPath,strLogFileName);
        String strFilePath = strPath+strLogFileName;
        String strContent = strLogContent + "\r\n";
        try{
            File file = new File(strFilePath);
            if (!file.exists()){
                Log.d("TestFile", "Create the file:" + strFilePath);
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            RandomAccessFile raf = new RandomAccessFile(file,"rwd");
            raf.seek(file.length());
            raf.write(strContent.getBytes());
            raf.close();
        }catch(Exception e){
            Log.i("Test File:", "Error on write file:" + e);
        }
    }

    public File makeFilePath(String strPath,String strLogFileName){
        File file = null;
        makeRootDir(strPath);
        try {
            file = new File(strPath+strLogFileName);
            if (!file.exists()){
                file.createNewFile();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return  file;
    }

    public static void makeRootDir(String strPath){
        File file = null;
        try{
            file = new File(strPath);
            if (!file.exists()){
                file.mkdir();
            }
        }catch (Exception e){
            Log.i("error:", e + "");
        }
    }
}
