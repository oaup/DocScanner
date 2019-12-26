package com.oaup.ocr.common;

import java.net.MalformedURLException;
import java.net.URL;

public class StringUtil {

	
	public static String[] getIpFromUrl(String url) throws MalformedURLException{
		
		NLog.i("url:%s",url);
		URL url_=new URL(url);
		
		
		
		return new String[]{url_.getHost(),String.valueOf(url_.getPort())};
		
		
		
	}

	
	
}
