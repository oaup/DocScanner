package com.oaup.ocr.common;

import android.util.Log;

public class NLog {

	static String tag = "NLog";
	public static void i(String format,Object ...args)
	{
	   String str = String.format(format, args);
	   if( str.indexOf("PROGRESS") < 0 )
		   Log.i(tag,str);
	}
	public static void e(String format,Object ...args)
	{
	   Log.e(tag,String.format(format, args));
	}
	
}
