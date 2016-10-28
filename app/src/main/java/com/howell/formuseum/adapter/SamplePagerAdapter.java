package com.howell.formuseum.adapter;

import java.io.File;
import java.util.ArrayList;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher.OnViewTapListener;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import com.howell.utils.DebugUtil;
import com.howell.utils.PhoneConfigUtils;
import com.howell.utils.ScaleImageUtils;

/**
 * @author 霍之昊 
 *
 * 类说明
 */
public class SamplePagerAdapter extends PagerAdapter implements OnViewTapListener{
	
	private ArrayList<String> mList;//缓存图片地址
	private Context context;
	
	public SamplePagerAdapter(ArrayList<String> mList,Context context) {
		// TODO Auto-generated constructor stub
		this.mList = mList;
		this.context = context;
	}
	
	@Override
	public int getItemPosition(Object object) {
		// TODO Auto-generated method stub
		return POSITION_NONE;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mList.size();
	}
	
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		// TODO Auto-generated method stub
		return view == object;
	}
	
	@Override
	public View instantiateItem(ViewGroup container, int position) {
		System.out.println("instatiateItem position:"+position);
		//获取手机屏幕宽度
		int requiredWidthSize = PhoneConfigUtils.getPhoneWidth(context);
		PhotoView photoView = new PhotoView(container.getContext());
		DebugUtil.logE("123", "instantiateItem   aaaaaaaa");
		photoView.setImageBitmap(ScaleImageUtils.decodeFile(requiredWidthSize,requiredWidthSize * 3 / 4
					,new File(mList.get(position))));
		DebugUtil.logE("123", "instantiateItem   bbbbbbb");
		//注册点击事件
		//photoView.setOnViewTapListener(this);
		// Now just add PhotoView to ViewPager and return it
		container.addView(photoView, LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		photoView.setTag(position);
	
		return photoView;
	}

	@Override
	public void onViewTap(View view, float x, float y) {
		// TODO Auto-generated method stub
		
	}

}
