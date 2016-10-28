package com.howell.formuseum;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;

import com.howell.formuseum.adapter.SamplePagerAdapter;
import com.howell.museumlhs.R;
import com.howell.utils.CacheUtils;

import java.io.File;
import java.util.ArrayList;

import uk.co.senab.photoview.HackyViewPager;

/**
 * @author 霍之昊 
 *
 * 类说明
 */
public class PictureActivity extends Activity implements OnPageChangeListener{
	
	private HackyViewPager viewPager;
	private SamplePagerAdapter adapter;
	private ArrayList<String> mList;
	private int position;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.picture_view);

		init();
	}
	
	public ArrayList<String> getFileName(File file){
		File[] fileArray = file.listFiles();
		mList = new ArrayList<String>();
		for (File f : fileArray) {
			if(f.isFile() && !mList.contains(f.getPath())){
				mList.add(f.getPath());
			}
		}
		return mList;
	}
	
	private void init(){
		File f = new File(CacheUtils.getPictureCachePath());
		mList = getFileName(f);
        Intent intent = getIntent();
        position = intent.getIntExtra("position", 0);
		viewPager = (HackyViewPager) findViewById(R.id.viewPager);
        try{
        	adapter = new SamplePagerAdapter(mList,PictureActivity.this);
        }catch(OutOfMemoryError e){
        	System.out.println("OutOfMemory");
        }
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(position);
        viewPager.setOnPageChangeListener(this);
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageSelected(int arg0) {
		// TODO Auto-generated method stub
		
	}
}
