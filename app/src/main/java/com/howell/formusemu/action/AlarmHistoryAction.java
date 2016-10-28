package com.howell.formusemu.action;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.howell.formuseum.bean.HistoryAlarm;
import com.howell.protocol.Const;
import com.howell.protocol.HttpProtocol;
import com.howell.protocol.entity.EventRecordList;
import com.howell.protocol.entity.EventRecordList.EventRecord.EventRecordedFile;
import com.howell.utils.JsonUtils;
import com.howell.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

public class AlarmHistoryAction implements Const{

	private static AlarmHistoryAction mInstance = null;
	public static AlarmHistoryAction getInstance(){
		if(mInstance == null){
			mInstance = new AlarmHistoryAction();
		}
		return mInstance;
	}
	private Handler handler;
	private String webServiceIP = null;//服务器ip
	private String cookie = null;
	private String session = null;
	private String cookieHalf = null;
	private String verify = null;
	private HttpProtocol hp = new HttpProtocol();

	//
	private ArrayList<HistoryAlarm> hList = null;
	
	private AlarmHistoryAction(){}
	
	public String getWebServiceIP() {
		return webServiceIP;
	}
	public AlarmHistoryAction setWebServiceIP(String webServiceIP) {
		this.webServiceIP = webServiceIP;
		return this;
	}
	public String getCookie() {
		return cookie;
	}
	public AlarmHistoryAction setCookie(String cookie) {
		this.cookie = cookie;
		return this;
	}
	public void setHandle(Handler handler){
		this.handler = handler;
	}
	
	public String getSession() {
		return session;
	}

	public AlarmHistoryAction setSession(String session) {
		this.session = session;
		return this;
	}

	public String getCookieHalf() {
		return cookieHalf;
	}

	public AlarmHistoryAction setCookieHalf(String cookieHalf) {
		this.cookieHalf = cookieHalf;
		return this;
	}

	public String getVerify() {
		return verify;
	}

	public AlarmHistoryAction setVerify(String verify) {
		this.verify = verify;
		return this;
	}

	public ArrayList<HistoryAlarm> gethList() {
		return hList;
	}

	public void sethList(ArrayList<HistoryAlarm> hList) {
		this.hList = hList;
	}

	public String changePicID2PicURL(String webIP,String picID){
		return "http://"+webIP+":8800/howell/ver10/medium/Pictures/"+picID+"/Data";
	}
	
	
	public void getAlarmHistory(){
		
		
		new AsyncTask<Void, Void, Boolean>() {
			
			@Override
			protected Boolean doInBackground(Void... params) {
				Log.i("123", "webIp="+webServiceIP+ " cookie="+cookie);
				if(webServiceIP==null||cookie==null){
					return false;
				}
				String jsonStr = hp.historyAlarms(webServiceIP, cookie,Utils.Date2ISODate(new Date(0)),Utils.Date2ISODate(new Date()));
				EventRecordList eventRecordList = null;
				try {
					eventRecordList = JsonUtils.parseHistoryAlarmsJsonObject(new JSONObject(jsonStr));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				if (eventRecordList==null) {
					return false;
				}
				
				ArrayList<EventRecordList.EventRecord> arrayListEventRecord = eventRecordList.getEventRecords();
				
				for(int i=0;i<arrayListEventRecord.size();i++){
					HistoryAlarm historyAlarm = new HistoryAlarm();
					EventRecordList.EventRecord eventRecord = arrayListEventRecord.get(i);
					historyAlarm.setDeviceID(eventRecord.getId())
					.setComponentID(eventRecord.getComponentId())
					.setName(eventRecord.getName())
					.setEventType(eventRecord.getEventType())
					.setAlarmTime(eventRecord.getAlarmTime());
													
					if (null!=eventRecord.getPictrueId()) {
						String [] pictureIds = eventRecord.getPictrueId();
						for(int j=0;j<pictureIds.length;j++){
							historyAlarm.addPictureID2List(pictureIds[j]);
						}
					}
					
					if(null!=eventRecord.getEventRecordedFiles()){
						ArrayList<EventRecordedFile> arrayListRecordedFiles = eventRecord.getEventRecordedFiles();
						for(int k=0;k<arrayListRecordedFiles.size();k++){
							historyAlarm.addRecordFile2List(arrayListRecordedFiles.get(k).getRecordeFileID());	
						}
					}
					
					if (null!=hList) {
						hList.add(historyAlarm);
					}
					
				}		
				return true;
			}
			
			
			@Override
			protected void onPostExecute(Boolean result) {
				if (result) {
					Log.i("123", "get alarm history ok");
					handler.sendEmptyMessage(ALARM_HISTORY_GET_LIST_OK);
				}else{
					Log.e("123", "get alarm history error");
					handler.sendEmptyMessage(ALARM_HISTORY_GET_LIST_ERROR);
				}
				super.onPostExecute(result);
			}
		}.execute();
	
	}
}
