package com.howell.utils;

import android.content.Context;
import android.view.WindowManager;

/**
 * @author 霍之昊 
 *
 * 类说明
 */
public class PhoneConfigUtils {
	private static WindowManager wm;
	
	public static int getPhoneWidth(Context context){
		wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		int width = wm.getDefaultDisplay().getWidth();//��Ļ���
		return width;
	}
	
	public static int getPhoneHeight(Context context){
		wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		int height = wm.getDefaultDisplay().getHeight();//��Ļ���
		return height;
	}
}
