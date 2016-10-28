package com.howell.formuseum;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.howell.db.DBManager;
import com.howell.formusemu.action.AlarmHistoryAction;
import com.howell.museumlhs.R;
import com.howell.protocol.HttpProtocol;
import com.howell.protocol.entity.EventNotify;
import com.howell.protocol.entity.Map;
import com.howell.protocol.entity.MapItem;
import com.howell.utils.CacheUtils;
import com.howell.utils.DialogUtils;
import com.howell.utils.MD5;
import com.howell.utils.PhoneConfigUtils;
import com.howell.utils.ScaleImageUtils;
import com.howell.utils.Utils;

import org.json.JSONException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

/**
 * @author 霍之昊 
 *
 * 类说明
 */
public class MapActivity extends Activity implements OnClickListener{

	private TextView mapName;
	private Map map;
	private FrameLayout layout;
	private ImageView imgAlarm;
	private LinearLayout mTalk;
	private LayoutParams wrapContentParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	
	private DBManager mgr;		//数据库操作类
	//协议相关
	private HttpProtocol hp;	//http协议类
	private String webserviceIp,session,cookieHalf,verify;
	
	private MsgReceiver msgReceiver; 			//广播接收器，接受后台service收到的报警信息
	
	private ArrayList<MapItem> mapItemList;		//子模块列表
	private ArrayList<EventNotify> alarmList;	//报警列表
	private HashMap<ImageView, AlarmThread> item;
	private int parentLayoutWidth = 0;				//地图长
	private int parentLayoutHeight = 0;			//地图高
	private static final int alarmPointWidth = 72;	//闪烁图标长
	private static final int alarmPointHeight = 72;	//闪烁图标高
	
	private static final int ADD_VIEW = 1;
	private static final int UPDATE_ALARM = 2;
	
	private Dialog waitDialog;
	private boolean isBroadcastReceiverRegister;
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		Log.i("123", "onConfigurationChanged      ");
		if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
			Log.i("123", "横屏");
			//setContentView(R.layout.map);
		
		}else{
			
			Log.i("123", "竖屏");
		
		//	setContentView(R.layout.logo);
			//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
		}		
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		Log.i("123", "map on pause");
		
		super.onPause();
	}
	
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		Log.i("123", "map restart");
		
		
		super.onRestart();
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		Log.i("123", "map onstart");
		super.onStart();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
//		try {
//			Thread.sleep(5000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		Log.i("123", "map on resume");
	//	setContentView(R.layout.map);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
		
		
		int rotatin = this.getWindowManager().getDefaultDisplay().getRotation();
		
		
		
		Log.i("123", "rotatin="+rotatin);
		
		
		
		super.onResume();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
			
		Log.i("123", "map on create");
		
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);

		
		init();
		setMapBackground();
		calculateLayout();
		//addMapItem();
		AddMapItem thread = new AddMapItem();
		thread.start();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(mgr != null){
			mgr.closeDB();
		}
		if(item != null){
			item.clear();
			item = null;
		}
		if(mapItemList != null){
			mapItemList.clear();
			mapItemList = null;
		}
		if(alarmList != null){
			alarmList.clear();
			alarmList = null;
		}
		if(isBroadcastReceiverRegister){
			unregisterReceiver(msgReceiver);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, Menu.NONE, "历史报警");
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			try {
				AlarmHistoryAction.getInstance()
				.setWebServiceIP(webserviceIp)
				.setCookie(cookieHalf+"verifysession="+MD5.getMD5("GET:"+"/howell/ver10/data_service/Business/Informations/Event/Records:"+verify))
				.setSession(session)
				.setCookieHalf(cookieHalf)
				.setVerify(verify);
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Intent intent = new Intent(this,AlarmHistoryListActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	
	private void init(){
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		Intent intent = getIntent();
		isBroadcastReceiverRegister = false;
		webserviceIp = intent.getStringExtra("webserviceIp");
		session = intent.getStringExtra("session");
		cookieHalf = intent.getStringExtra("cookieHalf");
		verify = intent.getStringExtra("verify");
		map = (Map) intent.getSerializableExtra("map");
		mapName = (TextView)findViewById(R.id.tv_map_name);
		mapName.setText(Utils.utf8Togb2312(new String(Base64.decode(map.getName(),0))));
//		mapName.setText(new String(Base64.decode(map.getName(),0)));
		imgAlarm = (ImageView)findViewById(R.id.map_activity_img_alarm);
		mTalk = (LinearLayout)findViewById(R.id.ll_map_talk);
		mTalk.setOnClickListener(this);
		mTalk.setVisibility(View.VISIBLE);//FIXME
		item = new HashMap<ImageView, AlarmThread>();
		hp = new HttpProtocol();
		layout = (FrameLayout)findViewById(R.id.fl_map);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		mgr = new DBManager(this);
		mapItemList = mgr.queryMapItem(map.getId());
	}
	
	private void calculateLayout(){
		//480,748
		//854,374
		//计算layout大小，用于设置地图子模块的确切位置
		ViewTreeObserver vto = layout.getViewTreeObserver(); 
		vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() { 
			public boolean onPreDraw() { 
				int width = layout.getMeasuredHeight(); 
				int height = layout.getMeasuredWidth(); 
//				Log.e("", "layout height:"+height+", layout width:"+width);
				if(width > height){
					parentLayoutWidth = height;
					parentLayoutHeight = width;
				}else{
					parentLayoutWidth = height;
					parentLayoutHeight = width;
				}
						
				return true; 
			} 
		}); 
	}
	
	@SuppressLint({ "ShowToast", "NewApi" })
	@SuppressWarnings("deprecation")
	private void setMapBackground(){
		//判断文件夹里是否存在地图文件，不存在则从平台获取
		if(CacheUtils.isBitmapExist(map.getId())){//文件夹下存在地图文件
			Drawable drawable = new BitmapDrawable(BitmapFactory.decodeFile(map.getDataPath()));
			layout.setBackground(drawable);
		}else{//文件夹下不存在地图文件
			waitDialog = DialogUtils.postWaitDialog(this);
			waitDialog.show();
			Toast.makeText(MapActivity.this, "第一次加载地图可能需要一些时间，请耐心等待", Toast.LENGTH_SHORT).show();
			new AsyncTask<Void, Integer, Void>() {
				Drawable drawable = null;
				@Override
				protected Void doInBackground(Void... arg0) {
					try {
						byte[] data = hp.mapsData(webserviceIp, map.getId(), cookieHalf+"verifysession="+MD5.getMD5("GET:"+"/howell/ver10/data_service/management/System/Maps/"+map.getId()+"/Data:"+verify));
						//存于文件中
						CacheUtils.saveBmpToSd(ScaleImageUtils.decodeByteArray(PhoneConfigUtils.getPhoneWidth(MapActivity.this), PhoneConfigUtils.getPhoneHeight(MapActivity.this), data), map.getId());
						drawable = new BitmapDrawable(BitmapFactory.decodeFile(map.getDataPath()));
					} catch (NoSuchAlgorithmException e) {
						e.printStackTrace();
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					} catch (JSONException e) {
						e.printStackTrace();
					}
					return null;
				}
						
				@Override
				protected void onPostExecute(Void result) {
					super.onPostExecute(result);
					waitDialog.dismiss();
					layout.setBackground(drawable);
				}
			}.execute();
		}
	}
	
	/*private void addMapItem(){
		new AsyncTask<Void, Integer, Void>(){

			@Override
			protected Void doInBackground(Void... arg0) {
				//等待计算出layout大小
				while(parentLayoutWidth == 0 && parentLayoutHeight ==0){
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				//设置地图子模块
				createChildren();
				return null;
			}
			protected void onPostExecute(Void result) {
				if(!MapActivity.this.isDestroyed()){
					registerReceiver();
					updateAlarm();
				}
				
			};
		}.execute();
	}*/
	
	class AddMapItem extends Thread{
		@Override
		public void run() {
			super.run();
			//等待计算出layout大小
			while(parentLayoutWidth == 0 && parentLayoutHeight ==0){
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			//设置地图子模块
			createChildren();
			handler.sendEmptyMessage(UPDATE_ALARM);
		}
	}
	
	private void registerReceiver(){
		//动态注册广播接收器  
		msgReceiver = new MsgReceiver();  
	    IntentFilter intentFilter = new IntentFilter();  
	    intentFilter.addAction("com.howell.formuseum.RECEIVER");  
	    registerReceiver(msgReceiver, intentFilter);
	    isBroadcastReceiverRegister = true;
	}
	
	public void updateAlarm(){
		alarmList = null;
		alarmList = mgr.queryAllAlarmListWithMapId(map.getId());
		for(Entry<ImageView, AlarmThread> entry : item.entrySet()){
			for(EventNotify e : alarmList){
				if(entry.getKey().getTag().equals(e.getId())){
					if(e.getIsAlarmed() == 0  && entry.getValue() == null){
						//设置闪烁图片为可点击
						entry.getKey().setClickable(true);
						//报警线程启动
						item.put(entry.getKey(), new AlarmThread(entry.getKey()));
						entry.getValue().start();
					}else if(e.getIsAlarmed() == 1  && entry.getValue() != null){
						try {
							//报警闪烁图片设置不可点击
							entry.getKey().setClickable(false);
							//闪烁线程标志位置true（关闭线程）
							entry.getValue().setStopAlarming(true);
							//回收线程资源
							entry.getValue().join();
							//闪烁线程置null
							item.put(entry.getKey(), null);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
					}
				}
			}
		}
	}
	
	//判断地图图片是横屏还是竖屏
	private boolean isLandscape(String f){
		BitmapFactory.Options o = new BitmapFactory.Options();  
        o.inJustDecodeBounds = true;  
        try {
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}  
        int width_tmp = o.outWidth, height_tmp = o.outHeight;  
        if(width_tmp > height_tmp){
        	return true;
        }else{
        	return false;
        }
	}

	private void createChildren(){
		if(mapItemList != null){
			for(MapItem item : mapItemList){
				createAlarmPoint(getResources().getDrawable(R.mipmap.ic_launcher_empty),item.getComponentId(),item.getCoordinate().getX(),item.getCoordinate().getY());
			}
		}
	}
	
	public void createAlarmPoint(Drawable point ,String imageId, Double x , Double y) {
		if(point == null){
			Log.e("", "point is null");
			return;
		}
		ImageView alarmPoint = new ImageView(this);
		alarmPoint.setLayoutParams(wrapContentParams);
		alarmPoint.setTag(imageId);
		alarmPoint.setImageDrawable(point);
		alarmPoint.setOnClickListener(this);
		alarmPoint.setClickable(false);
//		ViewTreeObserver vto = alarmPoint.getViewTreeObserver(); 
//		vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() { 
//		    public boolean onPreDraw() { 
//		        alarmPointHeight = alarmPoint.getMeasuredHeight(); 
//		        alarmPointWidth = alarmPoint.getMeasuredWidth(); 
//		        Log.e("", "height:"+alarmPointHeight+", width:"+alarmPointWidth);
//		        return true; 
//		    } 
//		}); 
//		Log.v("", "layout add item:"+parentLayoutWidth+","+parentLayoutHeight);
//		Log.v("", "layout add x:"+x+",y:"+y);
		alarmPoint.setX((float)(parentLayoutWidth * x  - alarmPointWidth / 2));
		alarmPoint.setY((float)(parentLayoutHeight * y - alarmPointHeight / 2));
		
		//把子模块添加到layout中
		//layout.addView(alarmPoint);
		Message msg = new Message();
		msg.what = ADD_VIEW;
		msg.obj = alarmPoint;
		handler.sendMessage(msg);
		
//		item.put(alarmPoint, new AlarmThread(alarmPoint));
	}
	
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler(){
		@SuppressLint("NewApi")
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case ADD_VIEW:
				ImageView iv = (ImageView)msg.obj;
				layout.addView(iv);
//				item.put(iv, new AlarmThread(iv));
				item.put(iv, null);
				break;
			case UPDATE_ALARM:
				if(!MapActivity.this.isDestroyed()){
					registerReceiver();
					updateAlarm();
				}
				break;
			default:
				break;
			}
		}
	};

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.ll_map_talk:
			Intent talkIntent = new Intent(this,TalkActivity.class);
			startActivity(talkIntent);
			break;

		default:
			//点击报警闪烁图标
			for(Entry<ImageView, AlarmThread> entry : item.entrySet()){
				if(view.getTag().equals(entry.getKey().getTag())){
					try {
						//报警闪烁图片设置不可点击
						entry.getKey().setClickable(false);
						//闪烁线程标志位置true（关闭线程）
						entry.getValue().setStopAlarming(true);
						//回收线程资源
						entry.getValue().join();
						//闪烁线程置null
						item.put(entry.getKey(), null);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					Intent intent = new Intent(this,AlarmDetailActivity.class);
					for(EventNotify e : alarmList){
						if(e.getId().equals(view.getTag())){
							intent.putExtra("eventNotify", e);
							//消除通知栏
							String ns = Context.NOTIFICATION_SERVICE;  
							NotificationManager mNotificationManager = (NotificationManager)this.getSystemService(ns); 
							mNotificationManager.cancel(mgr.selectEventNotifySqlKey(e));
							//设置数据库地图子模块的isAlarm标志位为1（已查看警报）
							e.setIsAlarmed(1);//已查看警报
							mgr.updateEventNotifyAlarmFlag(e);//更新数据库
							break;
						}
					}
					intent.putExtra("session", session);
					intent.putExtra("cookieHalf", cookieHalf);
					intent.putExtra("verify", verify);
					intent.putExtra("webserviceIp", webserviceIp);
					startActivity(intent);
					break;
				}
			}
			break;
		}
	}
	
	/** 
	   * 广播接收器 
	   * @author huo 
	   * 
	   */  
	public class MsgReceiver extends BroadcastReceiver{  
		@Override  
		public void onReceive(Context context, Intent intent) {  
			System.out.println("MapActivity收到广播！！！");
			//ret:0 登录失败 1 登录成功 2 有报警  -2 其它
			int ret = intent.getIntExtra("ret", -2); 
			System.out.println("login ret :"+ret);
			if(ret == 2){
				//获取数据库数据
				updateAlarm();
			}
		}  
	}

	//-----------------------------------------------------
	
	class AlarmThread extends Thread{
	
		private static final int SETIMGVISIBLE = 1;
		private static final int SETIMGGONE = 2;
		private ImageView imageView;
		private boolean isStopAlarming;
		
		public AlarmThread(ImageView imageView) {
			super();
			this.imageView = imageView;
			this.isStopAlarming = false;
		}
	
		public boolean isStopAlarming() {
			return isStopAlarming;
		}
	
		public void setStopAlarming(boolean isStopAlarming) {
			this.isStopAlarming = isStopAlarming;
		}
	
		@Override
		public void run() {
			super.run();
			while(!isStopAlarming){
				Message msg = new Message();
				msg.what = SETIMGVISIBLE;
				msg.obj = imageView;
				handler.sendMessage(msg);
				try {
					Thread.sleep(300);
					msg = new Message();
					msg.what = SETIMGGONE;
					msg.obj = imageView;
					handler.sendMessage(msg);
					Thread.sleep(300);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
		}
		
		@SuppressLint("HandlerLeak")
		Handler handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case SETIMGVISIBLE:
					ImageView img = (ImageView)msg.obj;
					img.setImageDrawable(getResources().getDrawable(R.mipmap.icon_map_alarm));
					break;
				case SETIMGGONE:
					img = (ImageView)msg.obj;
					img.setImageDrawable(getResources().getDrawable(R.mipmap.ic_launcher_empty));
					break;
				default:
					break;
				}
			}
		};
	}
}
