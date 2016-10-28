package com.howell.formuseum;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.howell.museumlhs.R;
import com.howell.protocol.HttpProtocol;
import com.howell.protocol.entity.EventNotify;
import com.howell.utils.CacheUtils;
import com.howell.utils.MD5;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;

/**
 * @author 霍之昊 
 *
 * 类说明
 */
public class AlarmDetailActivity extends Activity implements OnTouchListener{
	private TextView mEventName/*,mEventState*/,mEventType,mEventTime;
	private LinearLayout mPictures;
	private FrameLayout mPlayback,mPreview,mHandleAlarm;
	private EventNotify eventNotify;
	
	private LinearLayout mTalk;
	
	private String webserviceIp,session,cookieHalf,verify;
	private HttpProtocol hp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alarm_detail);

		init();
		
	}
	
	private void init(){
		mEventName = (TextView)findViewById(R.id.tv_alarm_detail_name);
		mEventType = (TextView)findViewById(R.id.tv_alarm_detail_event_type);
		mEventTime = (TextView)findViewById(R.id.tv_alarm_detail_event_time);
		mPictures = (LinearLayout)findViewById(R.id.ll_alarm_detail_pictures);
		mPlayback = (FrameLayout)findViewById(R.id.fl_alarm_detail_playback);
		mPlayback.setOnTouchListener(this);
		mTalk = (LinearLayout)findViewById(R.id.ll_alarm_detail_talk);
		mTalk.setOnTouchListener(this);
		mTalk.setVisibility(View.VISIBLE);//FIXME 
		mPreview = (FrameLayout)findViewById(R.id.fl_alarm_detail_preview);
		mPreview.setOnTouchListener(this);
		mHandleAlarm = (FrameLayout)findViewById(R.id.fl_alarm_detail_handle_alarm);
		mHandleAlarm.setOnTouchListener(this);
		
		hp = new HttpProtocol();
		
		Intent intent = getIntent();
		webserviceIp = intent.getStringExtra("webserviceIp");
		session = intent.getStringExtra("session");
		cookieHalf = intent.getStringExtra("cookieHalf");
		verify = intent.getStringExtra("verify");
		eventNotify = (EventNotify) intent.getSerializableExtra("eventNotify");
		Log.e("eventNotify","alarm detail oncreate"+ eventNotify.toString());
		if(eventNotify.getName() == null){
			mEventName.setText("");
		}else{
			mEventName.setText(eventNotify.getName().toString());
		}
		if(eventNotify.getEventType() == null){
			mEventType.setText("");
		}else{
			if(eventNotify.getEventType().equals("IO")){
				mEventType.setText("报警器报警");
				
			}else if(eventNotify.getEventType().equals("VMD")){
				mEventType.setText("移动侦测报警");
				mHandleAlarm.setVisibility(View.GONE);
			}
		}
		String time = eventNotify.getTime();
		mEventTime.setText(eventNotify.getDateYear(time)+"-"+eventNotify.getDateMonth(time)+"-"
				+eventNotify.getDateDay(time)+" "+eventNotify.getDateHour(time)+":"+eventNotify.getDateMin(time)
				+":"+eventNotify.getDateSec(time));
		
		if(eventNotify.getImageUrl() != null){
			String[] imgs = eventNotify.convertStringToArray(eventNotify.getImageUrl());
			for(String s : imgs){
				System.out.println("s:"+s);
			}
			addImgs(imgs);
		}
	}
	
	private void addImgs(String[] imgs){
		if(imgs == null){
			Log.e("addImgs", "addImgs is null");
			return;
		}
		CacheUtils.removeCache(CacheUtils.getPictureCachePath());
		//for(final String s : imgs){
		for(int i = 0 ; i < imgs.length ; i++){
			final ImageView imageView = new ImageView(this);
			imageView.setTag(i);
			imageView.setImageResource(R.mipmap.empty_bg);
			imageView.setScaleType(ImageView.ScaleType.FIT_XY);
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(128,128);
			layoutParams.setMargins(0, 0, 10, 0);
			imageView.setLayoutParams(layoutParams); 
			mPictures.addView(imageView);
			
			GetPictureTask task = new GetPictureTask(imgs[i],imageView,String.valueOf(i));
			task.execute();
			
			imageView.setOnTouchListener(new OnTouchListener() {
				
				@Override
				public boolean onTouch(View arg0, MotionEvent arg1) {
					Intent intent = new Intent(AlarmDetailActivity.this,PictureActivity.class);
					intent.putExtra("position", Integer.valueOf(imageView.getTag().toString()));
					startActivity(intent);
					return false;
				}
			});
			
			
			/*new AsyncTask<Void, Integer, Void>() {
				private InputStream is;
				private Bitmap bitmap;
				@Override
				protected Void doInBackground(Void... arg0) {
					URL url;
					try {
						url = new URL(imgs[i]);
						is = url.openStream();
						bitmap = BitmapFactory.decodeStream(is);  
				        is.close();
				        
				        CacheUtils.cachePictures(bitmap, "");
					} catch (MalformedURLException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}  
					return null;
				}
				
				protected void onPostExecute(Void result) {
					imageView.setImageBitmap(bitmap);  
				};
				
			}.execute();*/
		}
	}
	
	class GetPictureTask extends AsyncTask<Void, Integer, Void>{

		private InputStream is;
		private Bitmap bitmap;
		
		private String img;
		private	ImageView imageView;
		private String fileName;
		
		public GetPictureTask(String img,ImageView imageView,String fileName) {
			this.img = img;
			this.imageView = imageView;
			this.fileName = fileName;
		}
		
		@Override
		protected Void doInBackground(Void... arg0) {
			URL url;
			try {
				Log.e(fileName, img);
				url = new URL(img);
				is = url.openStream();
				bitmap = BitmapFactory.decodeStream(is);  
		        is.close();
		        
		        CacheUtils.cachePictures(bitmap, fileName);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}  
			return null;
		}
		
		protected void onPostExecute(Void result) {
			imageView.setImageBitmap(bitmap);  
		};
		
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (v.getId()) {
		case R.id.fl_alarm_detail_preview://预览
			Intent intent = new Intent(this,PlayerActivity.class);
			intent.putExtra("session", session);
			intent.putExtra("cookieHalf", cookieHalf);
			intent.putExtra("verify", verify);
			intent.putExtra("webserviceIp", webserviceIp);
		//	eventNotify.setEventState("Inactive");
			intent.putExtra("eventNotify", eventNotify);
			intent.putExtra("isPlayBack", false);
			startActivity(intent);
			break;
		case R.id.fl_alarm_detail_playback://回放
			System.out.println("playback");
			intent = new Intent(this,PlayerActivity.class);
			intent.putExtra("session", session);
			intent.putExtra("cookieHalf", cookieHalf);
			intent.putExtra("verify", verify);
			intent.putExtra("webserviceIp", webserviceIp);
			intent.putExtra("eventNotify", eventNotify);
			intent.putExtra("isPlayBack", true);
			startActivity(intent);
			break;
		case R.id.ll_alarm_detail_talk:	//语音对讲
			intent = new Intent(this,TalkActivity.class);
			startActivity(intent);
			break;
		case R.id.fl_alarm_detail_handle_alarm://处理警报
			LayoutInflater layoutInflater = LayoutInflater.from(this);
			final View view = layoutInflater.inflate(R.layout.process_dialog, null);
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			dialog.setTitle("请输入处理结果");
			dialog.setView(view);
			dialog.setPositiveButton("确定", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					final EditText et = (EditText) view.findViewById(R.id.process_dialog_edittext);
					Log.e("process", et.getText().toString());
					//发送报警处理协议
					new Thread(){
						public void run() {
							try {
								hp.process(webserviceIp, eventNotify.getId(), et.getText().toString(), cookieHalf+"verifysession="+MD5.getMD5("POST:"+"/howell/ver10/data_service/Business/Informations/IO/Inputs/Channels/"+eventNotify.getId()+"/Status/Process"+":"+verify));
							} catch (NoSuchAlgorithmException e) {
								e.printStackTrace();
							} catch (UnsupportedEncodingException e) {
								e.printStackTrace();
							} catch (JSONException e) {
								e.printStackTrace();
							}
						};
					}.start();
				}
			});
			dialog.setNegativeButton("取消", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					arg0.dismiss();
				}
			});
			dialog.show();
			break;
		default:
			break;
		}
		return false;
	}
}
