package com.howell.formusemu.action;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;

import com.howell.museumlhs.R;
import com.howell.utils.DebugUtil;

/**
 *
 * @author cbj
 * @deprecated
 *
 */
@Deprecated 
public class TalkAction {
	private static final int ID = 0;
	private static TalkAction mInstance = null;
	public static TalkAction getInstance(){
		if (mInstance == null) {
			mInstance = new TalkAction();
		}
		return mInstance;
	}
	private TalkAction(){}
	
	private NotificationManager nm = null;



	@SuppressLint("NewApi")
	public void postNotification(Context context){
		DebugUtil.logI(null, "talkaction notification start");
		nm = (NotificationManager) context.getSystemService(Service.NOTIFICATION_SERVICE);
		Notification.Builder nb = new Notification.Builder(context);
		nb.setContentTitle("对话服务正在运行中")
		.setTicker("对话服务")
		//.setContentText("aaaaa")
		.setSmallIcon(R.mipmap.ic_launcher)
		.setWhen(System.currentTimeMillis())
		.setOngoing(true);

		
		nm.notify(ID,nb.build());
		DebugUtil.logV(null, "talkaction notification start");
	}

	public void cancelNotification(){
		if (null==nm) {
			DebugUtil.logE(null,"talk action cancel noification nm=null");
			return;
		}
		nm.cancel(ID);
		
	}
	
}
