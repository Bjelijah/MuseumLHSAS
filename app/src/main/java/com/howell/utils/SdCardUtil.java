package com.howell.utils;

import java.io.File;

import android.os.Environment;
import android.util.Log;

public class SdCardUtil {
	private static String getSDCardPath(){
		return Environment.getExternalStorageDirectory().getAbsolutePath();
	}
	private static String getAlarmSoundDir(){
		return getSDCardPath()+ File.separator+"museum"+File.separator+"alarmSound"+File.separator;
	}
	
	public static void createAlarmSoundDir(){
		Log.e("123", "create alarm sounddir");
		File museumDir = new File(getSDCardPath() + "/museum");
		if (!museumDir.exists()) {
			museumDir.mkdirs();
		}
		File alarmSoundDir = new File(getSDCardPath() + "/museum/alarmSound");
		if (!alarmSoundDir.exists()) {
			alarmSoundDir.mkdirs();
		}
	}
	
	public static String getAlarmSoundFilePath(String fileName){
		if(fileName==null){
			return null;
		}
		File f = new File(getAlarmSoundDir() + fileName+".mp3");
		if(!f.exists()){
			return null;
		}
		return f.getAbsolutePath();
	}
}
