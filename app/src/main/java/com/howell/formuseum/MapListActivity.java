package com.howell.formuseum;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.howell.db.DBManager;
import com.howell.ehlib.MyListView;
import com.howell.ehlib.MyListView.OnRefreshListener;
import com.howell.formusemu.action.AlarmHistoryAction;
import com.howell.formusemu.action.LoginAction;
import com.howell.museumlhs.R;
import com.howell.protocol.HttpProtocol;
import com.howell.protocol.entity.Map;
import com.howell.protocol.entity.MapItem;
import com.howell.protocol.entity.MapItemList;
import com.howell.protocol.entity.MapList;
import com.howell.service.TalkService;
import com.howell.utils.CacheUtils;
import com.howell.utils.JsonUtils;
import com.howell.utils.MD5;
import com.howell.utils.SharedPreferencesUtils;
import com.howell.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 霍之昊 
 *
 * 类说明
 */
public class MapListActivity extends Activity implements OnRefreshListener,OnItemClickListener,OnClickListener{
	
	private MyListView listview;
	private LinearLayout mTalk;
	private HttpProtocol hp;
	private DBManager mgr;
	
	private ArrayList<Map> mapList;
	private MapListAdapter adapter;
	
	//协议相关
	private String webserviceIp,session,cookieHalf,verify,account;
	
	private MsgReceiver msgReceiver; 
	
	private boolean bAlarm = false;//来自后台报警的调用 if true 将 跳转 map activity
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i("123", "maplistActivity  on create");
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
		setContentView(R.layout.map_list);

		init();
		registerReceiver();
		initService();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	
		
		int rotatin = this.getWindowManager().getDefaultDisplay().getRotation();
		Log.e("123", "on resume update alarm rot="+rotatin);
		
		updateAlarm();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(mgr != null){
			mgr.closeDB();
		}
		unregisterReceiver(msgReceiver);
//		Intent talkIntent = new Intent(this,TalkService.class);//FIXME stop service: app won't accept calling form webService
//		stopService(talkIntent);
	}
	
	private synchronized void checkIfFromAlarm(){
		bAlarm = SharedPreferencesUtils.getIsAlarm(this);
		if (bAlarm) {
			SharedPreferencesUtils.setAlarm(this, false);
		}
	}
	
	private void registerReceiver(){
		msgReceiver = new MsgReceiver();  
	    IntentFilter intentFilter = new IntentFilter();  
	    intentFilter.addAction("com.howell.formuseum.RECEIVER");  
	    registerReceiver(msgReceiver, intentFilter);
	}
	
	private void initService(){
		Intent intent = new Intent(this, MyService.class);
		if(isServiceRun(this)){
	      	Log.e("", "stop service");
	      	stopService(intent);
		}
		intent.putExtra("session", session);
		intent.putExtra("cookieHalf", cookieHalf);
		intent.putExtra("verify", verify);
		intent.putExtra("webserviceIp", webserviceIp);
		startService(intent); 
	}
	
	//判断Service是否在运行
	public boolean isServiceRun(Context context){
		int serviceCount = 100;
		int addCount = 100;
		@SuppressWarnings("static-access")
		ActivityManager am = (ActivityManager)context.getSystemService(context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> list = am.getRunningServices(serviceCount);
		while(list.size() == serviceCount){
			System.out.println("list.size():"+list.size());
			serviceCount += addCount;
			list = null;
			list = am.getRunningServices(serviceCount);
		}
		System.out.println("service count:"+list.size());
		for(RunningServiceInfo info : list){
			System.out.println(info.service.getClassName());
			if(info.service.getClassName().equals("com.howell.formuseum.MyService")){
				System.out.println("isServiceRun true");
			    return true;
			}
		}
		System.out.println("isServiceRun false");
		return false;
	}
	
	private void init(){
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		listview = (MyListView)findViewById(R.id.map_list_listview);
		listview.setonRefreshListener(this);
		listview.setOnItemClickListener(this);
		mTalk = (LinearLayout)findViewById(R.id.ll_map_list_talk);
		mTalk.setOnClickListener(this);
		mTalk.setVisibility(View.VISIBLE);//FIXME 
		hp = new HttpProtocol();
		
		Intent intent = getIntent();
		webserviceIp = intent.getStringExtra("webserviceIp");
		session = intent.getStringExtra("session");
		cookieHalf = intent.getStringExtra("cookieHalf");
		verify = intent.getStringExtra("verify");
		Log.i("123", "init verify="+verify);
		account = intent.getStringExtra("account");
		mapList = new ArrayList<Map>();
		adapter = new MapListAdapter(this,mapList);
		listview.setAdapter(adapter);
		//init talk   FIXME
//		TalkManager.getInstance().setId(session).setName(account);//FIXME
//		if (TalkManager.getInstance().get_register_state()!=1) {//未注册
//			TalkManager.getInstance().registerService();
//		}
		String mac = Utils.getPhoneMac(this);
		String uuid = Utils.getPhoneUid(this);
		LoginAction.getInstance().setMac(mac).setUuid(uuid).setSession(session).setAccount(account);
		
		
		Intent talkIntent = new Intent(this,TalkService.class);
		talkIntent.putExtra("session", session);
		talkIntent.putExtra("account", account);
		talkIntent.putExtra("mac", mac);
		talkIntent.putExtra("uuid", uuid);
		startService(talkIntent);
	}
	
	private boolean compareString(String s1,String s2){
		return s1.equals(s2);
	}
	
	private boolean isMapUpdate(ArrayList<Map> newMaps,ArrayList<Map> oldMaps){
		if(newMaps.size() != oldMaps.size()){
			//平台传入的地图列表个数与之前不一样，重新更新
			Log.e("","有更新");
			return true;
		}else{
			//平台传入的地图列表个数与之前一样，检查每个map的md5码和子模块更新时间
			for(int i = 0 ; i < newMaps.size() ; i ++){
				if(!compareString(newMaps.get(i).getMD5Code(),oldMaps.get(i).getMD5Code())
						|| !compareString(newMaps.get(i).getLastModificationTime(),oldMaps.get(i).getLastModificationTime())){
					Log.e("","有更新");
					return true;
				}
			}
		}
		Log.e("","没有更新");
		return false;
	}
	
	private void getMaps(){
//		int width = PhoneConfigUtils.getPhoneWidth(this);
//		int height = PhoneConfigUtils.getPhoneHeight(this);
		try {
			//获取地图
			MapList maps = JsonUtils.parseMapsJsonObject(new JSONObject(hp.maps(webserviceIp, 1, 10,cookieHalf+"verifysession="+MD5.getMD5("GET:"+"/howell/ver10/data_service/management/System/Maps:"+verify))));
			for(Map map : maps.getMap()){
//				byte[] data = hp.mapsData(webserviceIp, map.getId(), cookieHalf+"verifysession="+MD5.getMD5("GET:"+"/howell/ver10/data_service/management/System/Maps/"+map.getId()+"/Data:"+verify));
				//存于文件中
//				CacheUtils.saveBmpToSd(ScaleImageUtils.decodeByteArray(width, height, data), map.getId());
				Map m = new Map(map.getId(),map.getName(),map.getComment(),map.getMapFormat(),CacheUtils.getBitmapCachePath()+map.getId(),map.getMD5Code(),map.getLastModificationTime());
				mapList.add(m);
				//存于数据库中
				mgr.addMap(m);
				
				//获取地图子模块
				MapItemList mapItems = JsonUtils.parseMapsItemJsonObject(new JSONObject(hp.items(webserviceIp, map.getId(), 1,10,cookieHalf+"verifysession="+MD5.getMD5("GET:"+"/howell/ver10/data_service/management/System/Maps/"+map.getId()+"/Items:"+verify))));
				for(MapItem item : mapItems.getMapItem()){
					item.setMapId(map.getId());
//					Log.e("debug", "debug:"+item.toString());
					mgr.addMapItem(item);
				}
			}
			
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private void updateMaps(){
		Log.i("123", "updateMaps");
		mgr.deleteTable("map");
		mgr.deleteTable("map_item");
		mgr.deleteTable("alarm_list");
		mapList.clear();
		CacheUtils.removeCache(CacheUtils.getBitmapCachePath());
		getMaps();
	}


	@Override
	public void onRefresh() {
		Log.i("123", "map list activity onrefresh");
		
		new AsyncTask<Void, Integer, Void>() {

			@Override
			protected Void doInBackground(Void... arg0) {
				updateMaps();
				return null;
			}
			
			protected void onPostExecute(Void result) {
				//adapter.notifyDataSetChanged();
				adapter.setMapList(mapList);
				Log.e("123", "onrefresh update alarm");
				updateAlarm();
				listview.onRefreshComplete();
			};
			
		}.execute();
	}

	@Override
	public void onFirstRefresh() {
		Log.i("123", "on first refresh");
		mgr = new DBManager(this);
		mapList = mgr.queryMap();
		MapList maps;
		try {
			maps = JsonUtils.parseMapsJsonObject(new JSONObject(hp.maps(webserviceIp, 1, 10,cookieHalf+"verifysession="+MD5.getMD5("GET:"+"/howell/ver10/data_service/management/System/Maps:"+verify))));
			//判断是否有更新
			if(isMapUpdate(maps.getMap(), mapList)){
				updateMaps();
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		
		//if(mapList.size() == 0){
			//Toast.makeText(MapListActivity.this, "第一次加载可能需要几分钟时间，请耐心等待", 1000).show();
			//从平台获取地图信息
		//	getMaps();
		//}
	}

	@Override
	public void onFirstRefreshDown() {
		//adapter.notifyDataSetChanged();
		adapter.setMapList(mapList);
		Log.e("123", "on first refresh update alarm");
		updateAlarm();
		listview.onRefreshComplete();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, Menu.NONE, "历史报警");
		menu.add(1,1,Menu.NONE,"退出登录");
//		menu.add("退出登录");
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:{
			Log.i("123", "历史记录");
			//
			try {
				
//				AlarmHistoryAction.getInstance().setWebServiceIP(webserviceIp).setCookie(cookieHalf+"verifysession="+MD5.getMD5("GET:"+"/howell/ver10/data_service/Business/Informations/Event/Linkages:"+verify));
//				AlarmHistoryAction.getInstance().setWebServiceIP(webserviceIp).setCookie(cookieHalf+"verifysession="+MD5.getMD5("GET:"+"/howell/ver10/data_service/business/informations/Business/Informations/Event/Records:"+verify));
				AlarmHistoryAction.getInstance()
				.setWebServiceIP(webserviceIp)
				.setCookie(cookieHalf+"verifysession="+MD5.getMD5("GET:"+"/howell/ver10/data_service/Business/Informations/Event/Records:"+verify))
				.setSession(session)
				.setCookieHalf(cookieHalf)
				.setVerify(verify);
				Log.i("123", "verify:"+verify);
//				AlarmHistoryAction.getInstance().setWebServiceIP(webserviceIp).setCookie(cookieHalf+"verifysession="+MD5.getMD5("GET:"+"/howell/ver10/data_service/business/informations/Business/Informations/Devices:"+verify));
//				AlarmHistoryAction.getInstance().setWebServiceIP(webserviceIp).setCookie(cookieHalf+"verifysession="+MD5.getMD5("GET:"+"/howell/ver10/data_service/Business/Informations/Devices:"+verify));
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			Intent intent = new Intent(this,AlarmHistoryListActivity.class);
			startActivity(intent);
		}
			break;
		case 1:{
			//登出
			//停止服务1 talkservice  2 myservice
			
			
			Intent talkIntent = new Intent(this,TalkService.class);
			stopService(talkIntent);
			
			Intent myIntent = new Intent(this,MyService.class);
			stopService(myIntent);
			
			
			Intent intent = new Intent(this,LoginActivity.class);
			startActivity(intent);
			finish();
		}
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	class MapListAdapter extends BaseAdapter {

		private Context mContext;
		private ArrayList<Map> mapList;
		
		public MapListAdapter(Context context,ArrayList<Map> mapList) {
	        this.mContext = context;
	        this.mapList = mapList;
	    }
		
		public void setMapList(ArrayList<Map> mapList) {
			this.mapList = mapList;
		}

		@Override
	    public int getCount() {
	        return mapList.size() ;
	    }

	    @Override
	    public Object getItem(int position) {
	        return mapList.get(position) ;
	    }

	    @Override
	    public long getItemId(int position) {
	        return position;
	    }

	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	    	System.out.println("getView:"+position);
	    	ViewHolder holder = null;
	    	if (convertView == null) {
	    		LayoutInflater layoutInflater = LayoutInflater.from(mContext);
	    		convertView = layoutInflater.inflate(R.layout.map_list_item, null);
				holder = new ViewHolder();
					
				holder.mapName = (TextView)convertView.findViewById(R.id.tv_map_list_item);
				holder.alarmIcon = (ImageView)convertView.findViewById(R.id.map_list_item_alarm_icon);
	            convertView.setTag(holder);
	    	}else{
	         	holder = (ViewHolder)convertView.getTag();
	        }
	    	holder.mapName.setText(Utils.utf8Togb2312(new String(Base64.decode(mapList.get(position).getName(),0))));
//	    	holder.mapName.setText(new String(Base64.decode(mapList.get(position).getName(),0)));
	    	if(mapList.get(position).isHasAlarm()){
	    		holder.alarmIcon.setVisibility(View.VISIBLE);
	    	}else{
	    		holder.alarmIcon.setVisibility(View.GONE);
	    	}
			return convertView;
	    }
	    
	    class ViewHolder {
	        public TextView mapName;
	        public ImageView alarmIcon;
	    }
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3) {
		mapList.get((int)arg3).setHasAlarm(false);
		adapter.notifyDataSetChanged();
		Intent intent = new Intent(this,MapActivity.class);
		intent.putExtra("map", mapList.get((int)arg3));
		intent.putExtra("session", session);
		intent.putExtra("cookieHalf", cookieHalf);
		intent.putExtra("verify", verify);
		intent.putExtra("webserviceIp", webserviceIp);
		startActivity(intent);
	}

	private void updateAlarm(){
		if (mapList.size()==0) {
			return;
		}
		Log.i("123", "map list size="+mapList.size());
		for(Map map : mapList){
			if(mgr.hasAlarmWithMapId(map.getId())){
				map.setHasAlarm(true);
			}else{
				map.setHasAlarm(false);
			}
		}
		
		//来自 后台报警 自动 跳转
		checkIfFromAlarm();
		Map alarmMap=null;
		Log.i("123", " update Alarm  bAlarm="+bAlarm);
		boolean bHasAlarm = false;
		
		if (bAlarm) {
			for(Map map:mapList){
				if (map.isHasAlarm()) {
					map.setHasAlarm(false);
					alarmMap = map;
					Log.i("123", "has alarm");
					bHasAlarm = true;
					break;
				}
			}
			Log.i("123", "bHasAlarm="+bHasAlarm);
			if (bHasAlarm) {
				//跳转 到 map
				Intent intent = new Intent(this,MapActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				intent.putExtra("map", alarmMap);
				intent.putExtra("session", session);
				intent.putExtra("cookieHalf", cookieHalf);
				intent.putExtra("verify", verify);
				intent.putExtra("webserviceIp", webserviceIp);
				startActivity(intent);
			//	this.finish();
			}
			
		}
		adapter.notifyDataSetChanged();
	}
	
	
	
	
	/** 
	   * 广播接收器 
	   * @author huo 
	   * 
	   */  
	public class MsgReceiver extends BroadcastReceiver{  
		@Override  
		public void onReceive(Context context, Intent intent) {  
//			System.out.println("MapListActivity收到广播！！！");
			//ret:0 登录失败 1 登录成功 2 有报警  -2 其它
			int ret = intent.getIntExtra("ret", -2); 
			System.out.println("msgReceiver ret :"+ret);
			if(ret == 2){
				//获取数据库数据
				Log.e("123", "msgreceiver updata alarm");
				updateAlarm();
			}
		}  
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_map_list_talk:
			Intent intent = new Intent(this,TalkActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(intent);
			break;

		default:
			break;
		}
	}
}
