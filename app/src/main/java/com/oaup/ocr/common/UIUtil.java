package com.oaup.ocr.common;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.util.DisplayMetrics;

import java.io.File;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class UIUtil {

	public final static int D_WIDTH = 720;
	public final static int D_HEIGHT = 1080;
	public final static int D_LINE = 6;
	public static final int SIZE_TIP = 36;
	
	public static float width = D_WIDTH;
	public static float height  = D_HEIGHT;
	//Øßpublic static BitmapCache BitCache = new BitmapCache(20);
	
	public static void toInit(Activity context){
		
		DisplayMetrics metric = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(metric);
        width = metric.widthPixels; 
        height = metric.heightPixels;
        
	}
	public static int getDesignWidth(int D_WIDTH){
		int v = Math.round(D_WIDTH*width/ UIUtil.D_WIDTH);
		if( v < 0 )
			return 1;
		else
			return v;
	}
	
	public static int getDesignHeight(int D_HEIGHT){
		int v = Math.round(D_HEIGHT*height/ UIUtil.D_HEIGHT);
		if( v < 0 )
			return 1;
		else
			return v;
	}
//	public static int getTextHeight(int design_height){
//		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,getDesignHeight(design_height), FoneTv.mInstance.getResources().getDisplayMetrics());
//	}
	public static int getTextHeight(Resources resources,int px) {
	    float scaledDensity = resources.getDisplayMetrics().scaledDensity;
	    return (int)((float)px/scaledDensity);
	}
	public static int getTexDesigntHeight(Resources resources,int px) {
	    float scaledDensity = resources.getDisplayMetrics().scaledDensity;
	    return (int)((float)getDesignHeight(px)/scaledDensity);
	}

	public static int getLineSize() {
		// TODO Auto-generated method stub
		return getDesignHeight(D_LINE);
	}
	
	public static String getAboutContext(Context context){
		String aboutStr = null;
			try {
				InputStream in = context.getAssets().open("about.txt");
				byte[] datas = new byte[in.available()];
				in.read(datas);
				aboutStr = new String(datas,"utf-8");
				in.close();
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		return aboutStr==null?"":aboutStr;
	}
	
	public static void drawComFocus(Canvas canvas,NinePatchDrawable bitmap,Rect rect){
		bitmap.setBounds(rect);
		bitmap.draw(canvas);
	}
	
     public static int dip2px(Context context, float dpValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        int px=(int) (dpValue * scale + 0.5f);
 
        return px;  
    }  
  
    public static int px2dip(Context context, float pxValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        int dp =(int) (pxValue / scale + 0.5f);
 
        return dp;  
        
    }  
  
    public static String getFormatStr(String str_,int width,Paint paint,String def){
    	int w = (int)paint.measureText(str_);
		
		//int width = getWidth();
		if( w > width ){
			StringBuffer sb = new StringBuffer(str_);
			while(true){
				sb.deleteCharAt(sb.length()/2);
				sb.deleteCharAt(sb.length()/2);
				w = (int) paint.measureText(sb.toString());
				if( w < width )
					break;
			}
			sb.insert(sb.length()/2,def);
			return sb.toString();
			
		}else
			return str_;
    }
    public  static String getFormatStr(String str_,int width,Paint paint){
		

    	return getFormatStr(str_,width,paint,"...");
		
	}
    
  
    
    public static Bitmap toRoundBitmap(Bitmap bitmap) {

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float roundPx;
        float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
        if (width <= height) {
            roundPx = width / 2;
            top = 0;
            bottom = width;
            left = 0;
            right = width;
            height = width;
            dst_left = 0;
            dst_top = 0;
            dst_right = width;
            dst_bottom = width;
        } else {
            roundPx = height / 2;
            float clip = (width - height) / 2;
            left = clip;
            right = width - clip;
            top = 0;
            bottom = height;
            width = height;
            dst_left = 0;
            dst_top = 0;
            dst_right = height;
            dst_bottom = height;
        }
        Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect src = new Rect((int) left, (int) top, (int) right,
                (int) bottom);
        final Rect dst = new Rect((int) dst_left, (int) dst_top,
                (int) dst_right, (int) dst_bottom);
        final RectF rectF = new RectF(dst);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, src, dst, paint);
        return output;
    }
    public static float getCenterY(Paint paint,int y,int h){
    	
    	FontMetrics fm = paint.getFontMetrics();
    	float fm_height = fm.bottom - fm.top;
    	
    	return y+(h+fm_height)/2-fm.bottom;

    }
    
    
    public static Bitmap scaleBitmapForMax(String path ,int max)
    {
    	

    	BitmapFactory.Options options = new BitmapFactory.Options();
		// options.inPreferredConfig = Bitmap.Config.RGB_565;
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);
		
		if(  options.outWidth > options.outHeight )
			options.inSampleSize = (int) Math.ceil(options.outWidth/max);
		else
			options.inSampleSize = (int) Math.ceil(options.outHeight /max);

		options.inJustDecodeBounds = false;
		Bitmap bmp = BitmapFactory.decodeFile(path, options);
		
    	
    	
    	
    	return bmp;
    }

	//add by jkx 2015/07/29
	public static Bitmap drawableToBitmap(Drawable drawable) {
		int w = drawable.getIntrinsicWidth();
		int h = drawable.getIntrinsicHeight();

		Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Config.ARGB_8888
				: Config.RGB_565;

		Bitmap bitmap = Bitmap.createBitmap(w, h, config);

		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, w, h);

		drawable.draw(canvas);
		return bitmap;
	}

	//add by jkx 2015/07/30
	//time format 15-07-30 10:18
	public static String getFileModifyTime(String filePath){
		if (!filePath.isEmpty()){
			File file = new File(filePath);
			if (file.exists() && file.isFile()){
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Calendar date = Calendar.getInstance();
				long time = file.lastModified();
				date.setTimeInMillis(time);
				String strTime = df.format(date.getTime());
				return strTime.substring(2, strTime.length()-3);
			}
		}
		return null;
	}


    
}
