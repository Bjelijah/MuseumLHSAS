package com.howell.service;

import com.howell.formusemu.action.LoginAction;
import com.howell.formusemu.action.TalkAction;
import com.howell.utils.DebugUtil;
import com.howell.utils.TalkManager;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class TalkService extends Service {
	
	private final String TAG = "TalkService";
	private TalkManager talkMgr = TalkManager.getInstance();
	
	@Override
	public IBinder onBind(Intent intent) {
		DebugUtil.logI(TAG, "onBind");
		return null;
	}
	
	@Override
	public void onCreate() {
		DebugUtil.logI(TAG, "on create");
		
		super.onCreate();
	}
	
	@Override
	public void onDestroy() {
		DebugUtil.logE(TAG, "ondestroy");
		//TODO stopTalkManager
		talkMgr.stop();
		
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		DebugUtil.logI(TAG, "on start command");
		if(intent == null){
			DebugUtil.logE("TalkService", "start command intent = null");
		}
	
		String session = null;
		String account = null;
		String mac = null;
		String uuid = null;
		try {
			session = intent.getStringExtra("session");
			account = intent.getStringExtra("account");
			mac = intent.getStringExtra("mac");
			uuid = intent.getStringExtra("uuid");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			
			session = LoginAction.getInstance().getSession();
			account = LoginAction.getInstance().getAccount();
			mac = LoginAction.getInstance().getMac();
			uuid = LoginAction.getInstance().getUuid();
			
			e.printStackTrace();
		}
		
		
		
		
		
		DebugUtil.logI(TAG, "session:"+session+" account="+account+" mac="+mac);	
		talkMgr.initSetId(uuid).initSetName(session).initSetMac(mac).initSetPwd(account);
		talkMgr.start(this);
		
		
//		TalkAction.getInstance().postNotification(this);
		
		return super.onStartCommand(intent, flags, startId);
	}
}
