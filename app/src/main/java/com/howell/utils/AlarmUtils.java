package com.howell.utils;


import android.content.Context;

import com.howell.museumlhs.R;

public class AlarmUtils {

	public static String getAlarmType(Context context,String alarmType){
		
		String alarmName = null;
		if (alarmType.equals("IO")) {
			alarmName = context.getResources().getString(R.string.alarmType_io);
		}else
		if (alarmType.equals("VMD")) {
			alarmName = context.getResources().getString(R.string.alarmType_vmd);
		}else 
		if (alarmType.equals("Videoloss")) {
			alarmName = context.getResources().getString(R.string.alarmType_videoloss);
		}else
		if (alarmType.equals("IRCut")) {
			alarmName = context.getResources().getString(R.string.alarmType_ircut);
		}else
		if (alarmType.equals("DayNight")) {
			alarmName = context.getResources().getString(R.string.alarmType_dayNight);
		}else
		if (alarmType.equals("RecordState")) {
			alarmName = context.getResources().getString(R.string.alarmType_recordState);
		}else{
			alarmName = "未知类型："+alarmType;
		}
		return alarmName;
	}
}
