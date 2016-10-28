package com.howell.utils;

import android.view.View;

/**
 * @author 霍之昊 
 *
 * 类说明
 */
public class MeasureViewUtils {
	public static int mesureViewWidth(View layout){
		int w = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED); 
		int h = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED); 
		layout.measure(w, h); 
		//int height = btnLayout.getMeasuredHeight(); 
		int width = layout.getMeasuredWidth(); //获取控件宽度
		System.out.println("width:"+width);
//		MarginLayoutParams lp = (MarginLayoutParams) layout.getLayoutParams();
		//int leftMargin = lp.leftMargin;			//获取控件左边距
		//int rightMargin = lp.rightMargin;		//获取控件右边距
		//System.out.println("leftMargin:"+leftMargin+" rightMargin:"+rightMargin);
		return width ;//+ leftMargin + rightMargin;
	}
	
	public static int mesureViewHeight(View layout){
		int w = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED); 
		int h = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED); 
		layout.measure(w, h); 
		int height = layout.getMeasuredHeight(); 
//		int width = layout.getMeasuredWidth(); //获取控件宽度
		System.out.println("height:"+height);
//		MarginLayoutParams lp = (MarginLayoutParams) layout.getLayoutParams();
		//int leftMargin = lp.leftMargin;			//获取控件左边距
		//int rightMargin = lp.rightMargin;		//获取控件右边距
		//System.out.println("leftMargin:"+leftMargin+" rightMargin:"+rightMargin);
		return height ;//+ leftMargin + rightMargin;
	}
}
