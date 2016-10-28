package com.howell.utils;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;

import com.howell.formusemu.action.AudioAction;
import com.howell.formusemu.action.ITalkOB;
import com.howell.formusemu.action.LoginAction;
import com.howell.formusemu.action.OnAudioComing;
import com.howell.formusemu.action.OnAudioStoping;
import com.howell.formuseum.JNIManager;
import com.howell.formuseum.bean.AudioComeData;
import com.howell.formuseum.bean.TalkDialog;
import com.howell.formuseum.bean.TalkGroup;
import com.howell.formuseum.bean.TalkUser;
import com.howell.formuseum.bean.TalkUserInfo;
import com.howell.protocol.Const;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * @author 霍之昊 
 *
 * 类说明
 * all method from activity to service should in a task
 * 
 * 
 */
public class TalkManager implements Const,OnAudioComing{
	private final static int MSG_NOT_LINK = 0x0;
	private final static int MSG_SEND_NOT_ACCESS = 0x1;
	private final static int MSG_RECEIVE_DATA	=0x2;
	private final static int MSG_RECEIVE_GROUP  = 0x3;
	private final static int MSG_GET_USER_LIST_AG = 0x4;
	
	
	private static TalkManager sInstance = new TalkManager();
	private Context context;
	String id;
	String name;
	String pwd;
	String mac;
	//private String talkIP = TALK_IP;//FIXME
	private String talkIP = LoginAction.getInstance().getWebserviceIp();//FIXME

	private final int talkPort = TALK_PORT;

	private boolean bRegisterOnce = false;
	private boolean bInTalk = false;//在talkActivity 界面中
	private boolean bHear = false;//on audio come;
	private JNIManager jni = JNIManager.getInstance();
	private Timer timer = null;
	private MyTimerTask myTimerTask = null;
	private Set<OnAudioStoping> obAudioStopSet = null;
	private Set<ITalkOB> obTalkSet = null;
	
	private Stack<AudioComeData> audioStack = new Stack<AudioComeData>(); 
	private Queue<AudioComeData> audioQueue = new ArrayBlockingQueue<AudioComeData>(20);
	
	//Dialog list 同频段下的 通话对象信息
	private List<TalkDialog> talkDialogs = null;
	private List<TalkDialog> nextSeleteTargets = new ArrayList<TalkDialog>();
	private boolean bLinked = false;
	private boolean bTalkable = true;//可通话  没被禁言
	private HeartBeatThread heartThread = null;
	
	
	//会话组
	private List<String> groupIds = null;
	
	//group 
	//所属的群组列表
	private List<TalkGroup> talkGroupsList = null; 
	private TalkGroup talkGroup = null; 
	
	//user
	private List<TalkUser> talkUList = null;
	private List<TalkDialog> talkDList = null;
	private List<TalkUserInfo> talkUserInfoList = null;
	boolean bTalkUOk = false;
	boolean bTalkDOk = false;
	
//	int testNUM = 0;
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_NOT_LINK:
//				testNUM++;//FIXME just for test
//				if(testNUM>20){
//					removeMessages(MSG_NOT_LINK);
//					break;
//				}
				
				//断连
				notifyTalkListener(bLinked);
				unregisterService();
				//注册
				bRegisterOnce = false;
				registerService();
		
				break;

			case MSG_SEND_NOT_ACCESS:
				
				
				break;
			case MSG_RECEIVE_DATA:
				sendReceiveRes();
				
				break;
			case MSG_RECEIVE_GROUP:
				sendReceiveGroupRes();
				break;
			case MSG_GET_USER_LIST_AG:
				getDilagList(0);
				break;
			default:
				break;
			}
		}
	};
	
	
	public String getId() {
		return id;
	}
	public TalkManager initSetPwd(String pwd){
		this.pwd = pwd;
		return this;
	}
	public TalkManager initSetId(String id) {
		this.id = id;
		return this;
	}
	public String getName() {
		return name;
	}
	public TalkManager initSetName(String name) {
		this.name = name;
		return this;
	}
	public String getTalkIP() {
		return talkIP;
	}
	public TalkManager initSetTalkIP(String talkIP) {
		this.talkIP = talkIP;
		return this;
	}
	private TalkManager() {}

	public static TalkManager getInstance() {
		return sInstance;
	}

	private boolean exit = false;//程序退出标志位

	public boolean isExit() {
		return exit;
	}
	public void setExit(boolean exit) {
		this.exit = exit;
	}

	public void test(){
		Log.e("", "jni:"+jni.toString());
	}

	public void start(Context context){//对外的封装接口
		this.context = context;
		exit = false;
		registerService();
		
		AudioAction.getInstance().registerOnAudioComing(this);
		startTimerTask();
	}

	public void stop(){//对外的分装接口
		exit = true;
		unregisterService();
		onAudioStopFun();
		AudioAction.getInstance().unregisterOnAudioComing(this);
		stopTimerTask();
	}

	public void getDilagList(int type){//interface open to TalkActivity only 
		if(talkDialogs!=null){
			talkDialogs.clear();
		}
		new AsyncTask<Integer, Void, Void>(){
			@Override
			protected Void doInBackground(Integer... params) {
				jni.talkGetDialogList(params[0]);
				return null;
			}
		}.execute(type);
	}
	
	public void setNextDialogTarget(List<Integer> idIdx,List<Integer>nameIdx){
		int idLen = 0,nameLen=0;
		String [] ids = null;
		String [] names = null;
		if (idIdx!=null) {
			idLen = idIdx.size();
			ids = new String[idLen];
			for(int i=0;i<idLen;i++){
				try {
					ids[i] = talkDialogs.get(idIdx.get(i)).getDialogId();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		if(nameIdx!=null){
			nameLen = nameIdx.size();
			names = new String[nameLen];
			for(int i=0;i<nameLen;i++){
				try {
					names[i] = talkDialogs.get(nameIdx.get(i)).getDialogName();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		new AsyncTask<String [], Void, Void>(){
			@Override
			protected Void doInBackground(String[]... params) {
				jni.talkSetNextTarget(params[0],params[1]);
				return null;
			}
		}.execute(ids,names);
	}
	
	
	public boolean sendVoiceData2Service(byte [] src,int len){
		String jsonStr = JsonUtils.audioData(nextSeleteTargets,src, len);
		if(jsonStr==null){
			
			notifyTalkStateListener(false);
			//重新获取用户列表
			handler.sendEmptyMessageDelayed(MSG_GET_USER_LIST_AG, 1000);
			return false;
		}
		Log.i("123", ""+jsonStr);
		jni.setAudioData(jsonStr, jsonStr.length());
		return true;
	}
	
	public List<TalkDialog> getDialogListRes(){
		return talkDialogs;
	}
	
	public List<TalkDialog> getNextSeleteTarget(){
		return nextSeleteTargets;
	}
	
	public void setNextDialogTarget(List<Integer> indexes){
		int len = indexes.size();
		String [] ids = new String[len];
		String [] names = new String[len];
		nextSeleteTargets.clear();
		int j=0;
		for(Integer i:indexes){
			if(talkDialogs.size()>i){
				ids[j] = talkDialogs.get(i).getDialogId();
				names[j] = talkDialogs.get(i).getDialogName();
				nextSeleteTargets.add(talkDialogs.get(i));
			}
			j++;
		}
	
		new AsyncTask<String [], Void, Void>(){
			@Override
			protected Void doInBackground(String[]... params) {
				jni.talkSetNextTarget(params[0],params[1]);
				return null;
			}
		}.execute(ids,names);
	}
	
	public void getGroups(String userName){
		
		if(talkGroupsList!=null){
			talkGroupsList.clear();
		}
		
		new AsyncTask<String, Void, Void>(){
			@Override
			protected Void doInBackground(String... params) {
				jni.talkGetGroups(params[0]);
				return null;
			}
		}.execute(userName);
	}
	
	public void getUsers(String userName,int isOnline,int isSilent){
		bTalkDOk = false;
		bTalkUOk = false;
		if(talkDList!=null){
			talkDList.clear();
		}
		if(talkUList!=null){
			talkUList.clear();
		}
		jni.talkGetUsers(userName, isOnline, isSilent);
	}
	
	public void getUser(String userName,String userId){
		bTalkDOk = false;
		bTalkUOk = false;
		if(talkDList!=null){
			talkDList.clear();
		}
		if(talkUList!=null){
			talkUList.clear();
		}
		jni.talkGetUser(userName, userId);
	}
	
	
	public void registerOnAudioStop(OnAudioStoping onAudioStop){
		if(obAudioStopSet == null){
			obAudioStopSet = new HashSet<OnAudioStoping>();
		}
		obAudioStopSet.add(onAudioStop);
	}

	public void unregisterOnAudioStop(OnAudioStoping onAudioStop){
		if (obAudioStopSet == null) {
			return;
		}
		obAudioStopSet.remove(onAudioStop);
	}

	public void registerOnTalk(ITalkOB o){
		if(obTalkSet==null){
			obTalkSet = new HashSet<ITalkOB>();
		}
		obTalkSet.add(o);
		o.onTalkLinked(bLinked);
	}
	
	public void unregisterOnTalk(ITalkOB o){
		if(obTalkSet==null){
			return;
		}
		obTalkSet.remove(o);
	}
	
	public boolean isBHear(){
		return bHear;
	}


	//is_voice_start 表示是否启动了对讲
	//    int is_voice_start = 0;
	//    
	//	public int getIs_voice_start() {
	//		return is_voice_start;
	//	}
	//	public void setIs_voice_start(int is_voice_start) {
	//		this.is_voice_start = is_voice_start;
	//	}

	public String getMac() {
		return mac;
	}
	public TalkManager initSetMac(String mac) {
		this.mac = mac;
		return this;
	}

	private void onAudioStopFun(){
		if (obAudioStopSet == null) {
			return;
		}
		for(OnAudioStoping o: obAudioStopSet){
			if (o!=null) {
				o.onAudioStop();
			}
		}
	}
	
	private void notifyTalkStateListener(Boolean bTalkable){
		if(obTalkSet==null)return;
		for(ITalkOB o:obTalkSet){
			if(null!=o){
				o.onTalkState(bTalkable);
			}
		}
	}
	
	private void notifyTalkListener(boolean bLink){
		if(obTalkSet==null){
			return;
		}
		for(ITalkOB o:obTalkSet){
			if(null!=o){
				o.onTalkLinked(bLink);
			}
		}
	}
	
	private void onDialogListReadyFun(){
		if(obTalkSet==null){
			return;
		}
		for(ITalkOB o:obTalkSet){
			if(null!=o){
				o.onDialogListReady();
			}
		}
	}

	private void startTimerTask(){
		timer = new Timer();
		myTimerTask = new MyTimerTask();
		timer.schedule(myTimerTask, 0,1000);
	}

	private void stopTimerTask(){
		if (timer!=null) {
			timer.cancel();
			timer.purge();
			timer = null;
		}
		if (myTimerTask!=null) {
			myTimerTask.cancel();
			myTimerTask = null;
		}
	}


	public synchronized boolean isbInTalk() {
		return bInTalk;
	}
	public synchronized void setbInTalk(boolean bInTalk) {
		this.bInTalk = bInTalk;
	}

	class MyTimerTask extends TimerTask{
		@Override
		public void run() {
			bHear = false;
		}
	}

	

	private void startCheckRegisterState(){
		
	}
	


	public boolean registerService(){
				
//		if (id==null||name==null||talkIP==null) {
//			Log.i("123", "id="+id+" name="+name+" talkIp="+talkIP);
//			DebugUtil.logE(null, "talkmanager registerService null ");
//			return false;
//		}
		if (bRegisterOnce) {//已经注册过了
			DebugUtil.logV(null, "register service already register");
			return true;
		}
		exit = false;
		//set register back
		new AsyncTask<Void, Void, Boolean>() {
			@Override
			protected Boolean doInBackground(Void... params) {
				jni.talkInit();
				jni.talkSetCallbackObject(TalkManager.this, 0);
				jni.talkSetCallbackMethodName("onRegisterRes", 0);
				jni.talkSetCallbackMethodName("onDialogListAdd", 1);
				jni.talkSetCallbackMethodName("onTalkSendResultRes", 2);
				jni.talkSetCallbackMethodName("onReceiveData", 3);
				jni.talkSetCallbackMethodName("onGroupCreat", 4);
				jni.talkSetCallbackMethodName("onRes", 5);
				jni.talkSetCallbackMethodName("onGetGroups", 6);
				jni.talkSetCallbackMethodName("onGetGroup", 7);
				jni.talkSetCallbackMethodName("onReceiveGroupData", 8);
				jni.talkSetCallbackMethodName("onGetUsers", 9);
				
				
				
			//	AudioAction.getInstance().initAudio();
				AudioAction.getInstance().audioPlay();
				bRegisterOnce = true;
				
				//int is_register = jni.register2service(id, mac, name, talkIP,(short)talkPort);//FIXME
				
//				String s = LoginAction.getInstance().getSession();
//				String a = LoginAction.getInstance().getAccount();
				
				LoginAction.getInstance().loadTalkInfo(context);
				String i = LoginAction.getInstance().getUuid();
				String p = LoginAction.getInstance().getWebserviceIp();
				String n = LoginAction.getInstance().getName();
				String k = LoginAction.getInstance().getPwd();
			
				Log.e("123","n:"+n+" k:"+k+" i: "+i+" p:"+p);
				return jni.talkRegister2service(n, k, i, 0, p, talkPort);
			}
			
			protected void onPostExecute(Boolean result) {
				if (result==false) {//连接失败
					//重连
					Log.i("123", "talk manage resgister task error");
					handler.sendEmptyMessageDelayed(MSG_NOT_LINK, 1000);
				}else{
					Log.i("123", "talk manage register task ok");
//					handler.sendEmptyMessageDelayed(MSG_NOT_LINK, 1000);//FIXME just for test
				}				
			};
			
		}.execute();
		return true;
	}


	public void unregisterService(){//注销服务			
		
//		jni.unregister2service();
		jni.talkUnregister();
		Log.e("123", "~~~~~~~~unregisterService   audio stop");
		AudioAction.getInstance().audioStop();
		
		jni.talkDeInit();
		
		bRegisterOnce = false;
	
	}



	public void stopTalk(){
		jni.stopTalk();
	}
	@Deprecated
	public void audioPlay(){
		//jni.audioPlay();
	}
	@Deprecated
	public void audioStop(){
		//jni.audioStop();
	}

	public int setData(byte[] buf,int len){
		return jni.setData(buf, len);
	}

	public int setAudioData(byte [] buf,int len){
		
		
		
		String string = new String(Base64.encode(buf, 0));
		Log.i("123", "len="+len+" strlen="+string.length());
		return jni.setAudioData(string, string.length());
		
	}
	
	
	public void startTalkActivity(Context context){
		if (isbInTalk()) {
			return;
		}
		setbInTalk(true);
		Intent talkIntent = new Intent();
		talkIntent.setAction("com.howell.formuseum.talkActivity");
		talkIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		talkIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(talkIntent);
		
		
	}

	@Override
	public void onAudioComing() {
		bHear = true;
	}

	public void onRegisterRes(int interval){
		Log.i("123", "talk manager interval="+interval);

		//连接成功
		handler.removeMessages(MSG_NOT_LINK);
		
		exit = false;
		bLinked = true;
		notifyTalkListener(bLinked);
		//TODO 开始心跳线程
		
		Log.i("123", "on register res interval:"+interval);
		if(heartThread==null){
			heartThread = new HeartBeatThread(interval, handler);
			heartThread.start();
		}	
	}	
	
	@Deprecated
	private boolean btest = false;
	@Deprecated
	public void testSetLinked(boolean bLink){
		btest = bLink;
	}
	
	private class HeartBeatThread extends Thread{
		private int interval;
		private Handler handler;
		public HeartBeatThread(int interval,Handler handler) {
			// TODO Auto-generated constructor stub
			this.interval = interval;
			this.handler = handler;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(bLinked &&!isExit()){
				Log.d("123", "set heart beat threadid:"+Thread.currentThread().getId());
				jni.talkSetHeartBeat();
				if(jni.talkGetRegisterState()!=1 ){
					Log.e("123", "心跳停止了");
					bLinked = false;
					break;
				}else{
					bLinked = true;
				}
				try {
					Thread.sleep(interval*1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if(!bLinked){
				handler.sendEmptyMessage(MSG_NOT_LINK);
			}
			heartThread = null;
			super.run();
		}
	} 
	
	//所有 应答 result
	public void onRes(int res){
		if(res==0){
			Log.i("123", "success");
		}else if(res == 202){
			Log.i("123", "no access");
		}
	}
	
	
	
	//收到 dialogList 
	public void onDialogListAdd(String dialogId,String dialogName,String mobileId,int mobileType,int len){
		if(talkDialogs==null){
			talkDialogs = new ArrayList<TalkDialog>();
		}
		talkDialogs.add(new TalkDialog(dialogId, dialogName, mobileId, mobileType));
		if(len == talkDialogs.size()){
			//获取完成
			onDialogListReadyFun();
		}
	}
	
	//收到 所有的发送数据的res   sending boardcastSending	groupSending
	public void onTalkSendResultRes(int res){
		Log.i("123", "res:"+res);
		if(res == 0){
			bTalkable = true;
		}else if(res == 201){
			bTalkable = false;
		}
		notifyTalkStateListener(bTalkable);
	}
	
	//受到音频数据 数据回调
	//@Link AudioAction.audioWrite()
	public void onReceiveData(String sendId,String sendName){
		handler.removeMessages(MSG_RECEIVE_DATA);
		handler.sendEmptyMessage(MSG_RECEIVE_DATA);
	}
	
	private void sendReceiveRes(){
		//jni.setReceiveRes(0);//FIXME   set in so
	}
	
	private void sendReceiveGroupRes(){
		//jni.setReceiveGroup(0);
	}
	
	//创建group 回受到的id
	public void onGroupCreat(String id,int res){
		if(res!=0){
			Log.e("123", "create group no access");
			return;
		}
		if(groupIds==null){
			groupIds = new ArrayList<String>();
		}
		groupIds.add(id);
	}
	
	
	//获取所属的群组列表
	public void onGetGroups(String [] groupInfo ,String [] members,boolean isSilent,int groupLen,int res){
		if(res!=0){
			Log.e("123", "get groups error");
			return;
		}
		if(talkGroupsList == null){
			talkGroupsList = new ArrayList<TalkGroup>();
		}
		talkGroupsList.add(new TalkGroup(groupInfo[0], groupInfo[1], members, groupInfo[2], groupInfo[3], isSilent));
		Log.i("123", "list size="+talkGroupsList.size());
		if(talkGroupsList.size()==groupLen){
			Log.i("123", "get groups over");
			for(TalkGroup t:talkGroupsList){
				Log.i("123", t.toString());
			}
		}
	}
	
	public void onGetGroup(String [] groupInfo ,String [] members,boolean isSilent,int groupLen,int res){
		talkGroup = new TalkGroup(groupInfo[0], groupInfo[1], members, groupInfo[2], groupInfo[3], isSilent);
	}
	
	public void onReceiveGroupData(String groupId,String sendId,String sender,int type){
		handler.removeMessages(MSG_RECEIVE_GROUP);
		handler.sendEmptyMessage(MSG_RECEIVE_GROUP);
	}
	
	private void makeTalkUserInfo(){
		//TODO
		if(talkUList==null)return;
		if(talkUList.isEmpty())return;
		talkUserInfoList = new ArrayList<TalkUserInfo>();
		new Runnable() {
			public void run() {
				for( TalkUser u:talkUList){
					TalkUserInfo info = new TalkUserInfo();
					info.setUser(u);
					for(TalkDialog d: talkDList){
						if(info.isBelong(d)){
							info.addUserDialog(d);
						}
					}
					talkUserInfoList.add(info);
				}
//				for(TalkUserInfo i:talkUserInfoList){
//					Log.i("123", i.toString());
//				}
			}
		}.run();
	}
	
	
	public void onGetUsers(String [] info,int infoLen,int res,boolean isOnline,boolean isSilent,int type,int size,int flag){
		
		if(res!=0){
			Log.e("123", "get user error");
			return;
		}
		
		if(flag==0){
			if(talkUList == null){
				talkUList = new ArrayList<TalkUser>();
			}
			String nickName = null;
			if(infoLen==3){
				nickName = info[2];
			}
			
			talkUList.add(new TalkUser(info[0], info[1], nickName, isOnline, isSilent));
			if(talkUList.size() == size){
				bTalkUOk = true;
			}

		}else if(flag == 1){
			if(talkDList == null){
				talkDList = new ArrayList<TalkDialog>();
			}
			//Log.i("123", "talkDlist add");
			talkDList.add(new TalkDialog(info[0], info[1], info[2], type));
			//Log.i("123", "talkDlist add    ok        size="+talkDList.size());
			if(talkDList.size() == size){
				bTalkDOk = true;
			}
		}
		
		if(bTalkDOk&&bTalkUOk){
			//Log.i("123", "onGetUsers ok");	
			makeTalkUserInfo();
		}
	}
	
	
	Context context2;
	public void setTestContext(Context context){
		this.context2 = context;
	}
	public void onAudioJson(String jsonStr){
//		Log.i("123", "on audio json  jsonstr="+jsonStr);
		Log.i("123", "on audio json len="+jsonStr.length());
		//String string = jsonStr.substring(jsonStr.lastIndexOf("000a\"}"));
		int offset = jsonStr.lastIndexOf("\"}");
		String string = null;
		if(offset>0){
			 string = jsonStr.subSequence(0, offset+"\"}".length()).toString();
		}else{
			string = jsonStr+"\"}";
		}
		//String string = jsonStr.subSequence(0, jsonStr.lastIndexOf("000a\"}")+"000a\"}".length()).toString();
		
//		Log.i("123", "string="+string);
		byte [] buf = null;
		AudioComeData audioComeData = null;
		try {
//			 buf = JsonUtils.audioReceive(new JSONObject(string));
			audioComeData = JsonUtils.parseAudioReceive(new JSONObject(string));
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		if(buf.length > 8912){
//			Toast.makeText(context2, ""+buf.length, Toast.LENGTH_SHORT).show();
//		}
		if(audioComeData==null)return;
		buf = audioComeData.getG711data();
		if(buf!=null){
			Log.i("123", "buf.length="+buf.length);
			//jni.g711AudioPlay(buf, buf.length);
			//tell audio coming
			AudioAction.getInstance().audioComing();
			
//			int maxSize = 8000*1 /  buf.length;
			int maxSize = 1;
			Log.i("123", "max size="+maxSize   + "       audioQueue.size ="+audioQueue.size());
			if(audioQueue.size()>=maxSize){
				Log.e("123","audio clear");
				audioQueue.clear();
			}
			
			audioQueue.offer(audioComeData);
			Log.i("123", "audio queue offer  size="+audioQueue.size());
			
			
			startPlayAudioFromQueue();
			
//			AudioAction.MyAudioPlay myAudioPlay = AudioAction.getInstance().buildMyAudioPlay(audioComeData.getSender());
//			if(myAudioPlay!=null){
//			//	myAudioPlay.audioPlayG711Data(buf, buf.length);
////				audioStack.push(audioComeData);
//				
//				
//				int maxSize = 8000*3 /  buf.length;
//				Log.i("123", "max size="+maxSize);
//				if(audioQueue.size()>=maxSize){
//					audioQueue.clear();
//				}
//				
//				audioQueue.offer(audioComeData);
//				Log.i("123", "audio queue offer  size="+audioQueue.size());
//			
//				
//			}
			
//			audioStack.push(audioComeData);
//			startPlayAudioFromStack();

		}
	}
	private boolean bAlreadyStartPlayFromStack = false;
	private boolean bAlreadyStartPlayFromQueue = false;
	public void startPlayAudioFromStack(){
		Log.i("123", "audio size:"+audioStack.size());
		if(bAlreadyStartPlayFromStack){
			Log.e("123", "alread start play from stack");
			return;
		}
		bAlreadyStartPlayFromStack = true;
		
		
		new Thread(){

			@Override
			public void run() {
				boolean ret = true;
				while (ret) {
					ret = playFromAudioStack();
				}

				super.run();
			}
			
		}.start();
		
		
		
//		new Runnable() {
//			public void run() {
//				boolean ret = true;
//				while (ret) {
//					ret = playFromAudioStack();
//				}
//
//			}
//		}.run();
		
		bAlreadyStartPlayFromStack = false;
	}
	
	public void stopPlayAudioFromQueue(){
		bPlayAudioQueue = false;
		if(playAudioQueueThread!=null){
			try {
				
				playAudioQueueThread.join();
				bAlreadyStartPlayFromQueue = false;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			playAudioQueueThread = null;
		}
	
	}
	
	
	public void startPlayAudioFromQueue(){
		Log.i("123", "audio size:"+audioQueue.size());
		if(bAlreadyStartPlayFromQueue){
			Log.e("123", "bAlreadyStartPlayFromQueue  = true return");
			return;
		}
		bAlreadyStartPlayFromQueue = true;
		
		if(playAudioQueueThread==null){
			bPlayAudioQueue = true;
			playAudioQueueThread = new PlayAudioQueueThread();
			playAudioQueueThread.start();
		}
		
		
//		new Thread(){
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				boolean ret = true;
//				while (ret) {
//					ret = playFromAudioQueue();
//				}
//
//			
//				super.run();
//			}
//		}.start();
		
//		new Runnable() {
//			public void run() {
//				boolean ret = true;
//				while (ret) {
//					ret = playFromAudioQueue();
//				}
//			}
//		}.run();
		
		Log.e("123", "bAlreadyStartPlayFromQueue = false");
		bAlreadyStartPlayFromQueue = false;
	}
	public PlayAudioQueueThread  playAudioQueueThread = null;
	private boolean bPlayAudioQueue = false;
	public class PlayAudioQueueThread extends Thread{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			while(bPlayAudioQueue){
				playFromAudioQueue();
			}
		}
	}
	
	
	private boolean playFromAudioStack(){
		
		if (!audioStack.empty()) {
			Log.i("123", "audio stack size="+audioStack.size());
			AudioComeData audioComeData = audioStack.pop();
			byte [] buf = audioComeData.getG711data();
			AudioAction.MyAudioPlay myAudioPlay = AudioAction.getInstance().buildMyAudioPlay(audioComeData.getSender());
			if(myAudioPlay!=null){
				myAudioPlay.audioPlayG711Data(buf, buf.length);
			}
			if(audioStack.size()!=0){
				audioStack.clear();
			}
			
			return true;
		}
		return true;
	}
	
	private boolean playFromAudioQueue(){
		if (audioQueue.size()!=0) {
			Log.i("123", "audio queue before size="+audioQueue.size());
//			AudioComeData audioComeData = audioStack.pop();
			AudioComeData audioComeData = audioQueue.poll();
			Log.i("123", "audio queue after size="+audioQueue.size());
			if(audioComeData == null){
				Log.i("123", "audioComeData == null");
				return false;
			}
			byte [] buf = audioComeData.getG711data();
			AudioAction.MyAudioPlay myAudioPlay = AudioAction.getInstance().buildMyAudioPlay(audioComeData.getSender());
			if(myAudioPlay!=null){
				myAudioPlay.audioPlayG711Data(buf, buf.length);
			}
		
			return true;
		}
//		Log.i("123", "playFromAudioQueue  auido queue siz = 0");
		return false;
	}
	
	
	//test
	public void showInfo(){
		if(talkDialogs==null)return;
		for(TalkDialog t:talkDialogs){
			Log.i("123", t.toString());
		}
	}
	
	
	public void audioTest(byte [] data,int len){//just for debug
		
		if(!DebugUtil.isDebug()){
			return;
		}
		
		byte [] g711buf=new byte[len/2 ];// = JNIManager.getInstance().pcm2G711u(data);
		
//		Log.i("123", "data len="+data.length+ "  len="+len);
		jni.pcm2G711u(data,len,g711buf);
		for(int i=0;i<100;i++){
			Log.i("123", g711buf[i]+"");
		}
		
		byte [] base64  = Base64.encode(g711buf, Base64.DEFAULT);
		byte [] stream = Base64.decode(base64, Base64.DEFAULT);
//		jni.g711AudioPlay(g711buf, len/2);
		jni.g711AudioPlay(stream, stream.length);
//		jni.pcmAudioPlay(data, len);
	}
	
	
	public void registTest(){
		
		new Runnable() {
			public void run() {
//				testNUM = 0;
				unregisterService();
				//注册
				bRegisterOnce = false;
				registerService();
			}
		}.run();
		
		
	}
	
	
}
