package com.oaup.ocr.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Vector;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;

public class BitmapCache {

	int mMax;

	HashMap<String, Bitmap> mCache;
	Vector<String> mKeys;
	Vector<String> mReqs;
	Vector<String> thubnils = new Vector<String>();

	
	
	static String CACHE_DIR;
	
	static{
		
		CACHE_DIR = Environment.getExternalStorageDirectory().getAbsolutePath()+"/northking/caches/";
		
		File file = new File(CACHE_DIR);
		if( !file.exists() )
			file.mkdirs();
		
	}
	public BitmapCache(int size) {
		mMax = size;
		mCache = new HashMap<String, Bitmap>();
		mKeys = new Vector<String>();
		mReqs = new Vector<String>();
	}

	public void remove(String url) {
		try {
			mKeys.remove(url);
			Bitmap bit_cache = mCache.get(url);
			if (bit_cache != null && !bit_cache.isRecycled())
				bit_cache.recycle();
			mCache.remove(url);
		} catch (Exception e) {

		}

	}

	public void add(String url, Bitmap bit) {
		
		
		NLog.i("cache  key.size:%d cache.size:%d", mKeys.size(),mCache.size() );
		
		synchronized (mCache) {
		
		if (mKeys.size() >= mMax) {
			String key = mKeys.remove(0);
			Bitmap bit_cache = mCache.remove(key);
				
			if (bit_cache != null && !bit_cache.isRecycled())
				bit_cache.recycle();
			key = null;
			bit_cache = null;
		} 
		mKeys.add(url);
		mCache.put(url, bit);
		
		mReqs.remove(url);
		
		MessageCenter.sendMessage(Event.REQ_BITMAP_CREATED,url);
		}
	}

	public Bitmap get(String url) {
		synchronized (mCache) {
		if (mCache.containsKey(url))
		{
			Bitmap bitmap = mCache.get(url);
			if( bitmap != null && !bitmap.isRecycled() )
			{
				mKeys.remove(url);
				mKeys.add(url);
		     	return bitmap;
			}
			else
			{
				
				remove(url);
				
			}
		}
		return null;
		}
	}
	
	public Bitmap getorAdd(String url,int w,int h){
		synchronized (mCache) {
		if (mCache.containsKey(url))
		{
			Bitmap bitmap = mCache.get(url);
			if( bitmap != null && !bitmap.isRecycled() )
			{
				mKeys.remove(url);
				mKeys.add(url);
				return bitmap;
			}
			else
			{
				remove(url);
				return getorAdd(url,w,h);
			}
		}
		else if( !isRequested(url)){
			if (mKeys.size() >= mMax-1) {
				
				remove(url);
				
			}
			if(  url.startsWith("http") && !new File( getUrlLocalPath(url) ).exists() )
			{
					createBitmapFromNet(url,w,h);
			}
			else
				createBitmapFromFileInThread(url,w,h);
		}
		return null;
		}
		
	}

	private void createBitmapFromNet(final String url, final  int w,final int h) {

		setRequired(url);
		
		new Thread(){
			
			public void run(){
				
				String localfile = getUrlLocalPath(url);
				
				try {
					download(url,localfile);
					createBitmapFromFileInThread(url,w,h);
					
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				
			}
			
		}.start();
		
		
	}
	
	private void download(String url,String path) throws IOException {
		URL realUrl = new URL(url);
		HttpURLConnection httpURLConnection = (HttpURLConnection) realUrl
				.openConnection();
		httpURLConnection.setRequestProperty("accept", "*/*");
		httpURLConnection.setRequestProperty("connection", "Keep-Alive");
		
		httpURLConnection.addRequestProperty("User-Agent", "Mozilla");
		httpURLConnection.setRequestMethod("GET");
		httpURLConnection.setInstanceFollowRedirects(false);
		httpURLConnection.setDoOutput(false);
		httpURLConnection.setDoInput(true);
		httpURLConnection.setConnectTimeout(5000);

		InputStream inputstream = httpURLConnection.getInputStream();
		FileOutputStream fout = new FileOutputStream(path);
		
		byte[] datas = new byte[1024];
		
		int len;
		while( (len=inputstream.read(datas))>0 )
		{
			
			fout.write(datas, 0,len);
		}
		
		inputstream.close();
		fout.close();
	
		
	}
	
	
	private String getUrlLocalPath(String url)
	{
		
		if( url.startsWith("http") )
			return CACHE_DIR+url.hashCode();
		else
			return url;
		
	}

	public void release() {

		while (mKeys.size() > 0) {
			String key = mKeys.remove(0);
			Bitmap bit_cache = mCache.get(key);
			if (bit_cache != null && !bit_cache.isRecycled())
				bit_cache.recycle();
			mCache.remove(key);
			key = null;
			bit_cache = null;
		}
		mReqs.clear();
		mKeys = null;
		mCache = null;
		mReqs = null;

	}

	public boolean isRequested(String url) {
		return mReqs.contains(url);
	}

	public void setRequired(String url) {
		if (!mReqs.contains(url))
			mReqs.add(url);
	}

	public void createBitmapFromFileInThread(final String path,final int t_w,final int t_h) {

		setRequired(path);
		new Thread() {
			public void run() {
				
				String localpath = getUrlLocalPath(path);
				File file = new File(localpath);
				if( !file.exists() )
				{
					NLog.i("BitmapCache load image:%s not exist",path);
					return;
				}
				BitmapFactory.Options options = new BitmapFactory.Options();
				// options.inPreferredConfig = Bitmap.Config.RGB_565;
				options.inJustDecodeBounds = true;
				BitmapFactory.decodeFile(localpath, options);

				float w = options.outWidth / t_w;
				float h = options.outHeight / t_h;
				if (w > 1 || h > 1) {
					options.inSampleSize = (int) Math.ceil(w > h ? w : h);
				}

				options.inJustDecodeBounds = false;
				Bitmap bmp = BitmapFactory.decodeFile(localpath, options);
				
				if (bmp != null)
					add(path, bmp);

			}
		}.start();

	}

	public static Bitmap decordBitmap(byte[] data,int width,int height)
	{
		BitmapFactory.Options options = new BitmapFactory.Options();
		
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(data, 0, data.length, options);
		float w = options.outWidth / width;
		float h = options.outHeight / height;
		
		if (w > 1 || h > 1) {
			options.inSampleSize = (int) Math.ceil(w > h ? w : h);
		}
		//options.inPreferredConfig = Config.ARGB_8888;
		options.inJustDecodeBounds = false;
		Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length, options);

		return bmp;
		
	}
	
	public static Bitmap decordBitmap(File f,int width,int height)
	{
		
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(f.getAbsolutePath(), options);

		if (width * height != 0){
			float w = options.outWidth / width;
			float h = options.outHeight / height;

			if (w > 1 || h > 1) {
				options.inSampleSize = (int) Math.ceil(w > h ? w : h);
			}
		}

		options.inJustDecodeBounds = false;
		Bitmap bmp = BitmapFactory.decodeFile(f.getAbsolutePath(), options);
		
		return bmp;
		
	}
	
	public static Bitmap ResizeBitmap(Bitmap bitmap, int newWidth) {
	     int width = bitmap.getWidth();
	     int height = bitmap.getHeight();
	     float temp = ((float) height) / ((float) width);
	     int newHeight = (int) ((newWidth) * temp);
	     float scaleWidth = ((float) newWidth) / width;
	     float scaleHeight = ((float) newHeight) / height;
	     Matrix matrix = new Matrix();
	     // resize the bit map
	     matrix.postScale(scaleWidth, scaleHeight);
	     // matrix.postRotate(45);
	     Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
	     bitmap.recycle();
	     return resizedBitmap;
	}
	
	public static Bitmap rotate(Bitmap b, int degrees){
		 if (degrees != 0 && b != null) {
	            Matrix m = new Matrix();
	            m.setRotate(degrees,
	                    (float) b.getWidth() / 2, (float) b.getHeight() / 2);
	            try {
	                Bitmap b2 = Bitmap.createBitmap(
	                        b, 0, 0, b.getWidth(), b.getHeight(), m, true);
	                if (b != b2) {
	                    b.recycle();  
	                    b = b2;
	                }
	                
	            } catch (OutOfMemoryError ex) {
	                
	            }
	        }
	        return b;
	}
	
	

}
