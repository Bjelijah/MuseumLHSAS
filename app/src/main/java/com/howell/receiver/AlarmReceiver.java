package com.howell.receiver;

import java.util.List;

import org.codehaus.jackson.map.ser.StdSerializers.UtilDateSerializer;

import com.howell.formuseum.LogoActivity;
import com.howell.formuseum.MapActivity;
import com.howell.utils.SharedPreferencesUtils;
import com.howell.utils.Utils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {

	private Context context;

	private String session,webServiceIP,cookieHalf,verify;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.e("123", "on receive alarm receiver");
//		abortBroadcast();
		this.context = context;
		if (Utils.isThisApp(context)) {
			Log.e("123", "is this app");
			return;
		}
		Log.e("123", "not this app start activity");
		//start activity
//		Intent 
		
		SharedPreferencesUtils.setAlarm(context, true);
		session = intent.getStringExtra("session");
		webServiceIP = intent.getStringExtra("webServiceIP");
		cookieHalf = intent.getStringExtra("cookieHalf");
		verify = intent.getStringExtra("verify");
	
		Intent intentActivity = new Intent(context,LogoActivity.class);
		intentActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
		intentActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//		intent.putExtra("map", mapList.get((int)arg3));
//		intent.putExtra("session", session);
//		intent.putExtra("cookieHalf", cookieHalf);
//		intent.putExtra("verify", verify);
//		intent.putExtra("webServiceIP", webServiceIP);
		
//		Bundle bundle = new Bundle();
//		bundle.putBoolean("fromAlarmReceiver", true);
//		intent.putExtras(bundle);
		
		intentActivity.putExtra("fromAlarmReceiver", true);
		
		context.startActivity(intentActivity);
	}

	
	
	
	
	
	
	
	
	
	
	
	

	
}
