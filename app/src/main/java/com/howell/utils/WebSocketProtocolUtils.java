package com.howell.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.howell.protocol.entity.AlarmPushConnectRes;
import com.howell.protocol.entity.EventNotify;
import com.howell.protocol.entity.EventNotifyRes;
import com.howell.protocol.entity.KeepAlive;
import com.howell.protocol.entity.KeepAliveRes;

/**
 * @author 霍之昊 
 *
 * 类说明
 */
public class WebSocketProtocolUtils {
	//报警推送连接协议
	public static JSONObject createAlarmPushConnectJSONObject(int cseq, String session,String username){
		JSONObject object = new JSONObject();
		try {
			object.put("Message", 0x0001);
			object.put("CSeq", cseq);
			JSONObject request = new JSONObject();
			request.put("Session", session);
			request.put("Username", username);
			object.put("Request", request);
			Log.d("报警推送连接协议 request", object.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return object;
	}
	
	//报警推送心跳
	public static JSONObject createKeepAliveJSONObject(int cseq, int systemUpTime){
		JSONObject object = new JSONObject();
		try {
			object.put("Message", 0x0002);
			object.put("CSeq", cseq);
			JSONObject request = new JSONObject();
			JSONObject keepAlive = new JSONObject();
			keepAlive.put("SystemUpTime", systemUpTime);
			request.put("Request", keepAlive);
			object.put("Request", request);
			
			Log.d("报警推送心跳 request", object.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return object;
	}
	
	//ADC事件递交回复
	public static JSONObject createADCResJSONObject(int cseq){
		JSONObject object = new JSONObject();
		try {
			object.put("Message", 0x8003);
			object.put("CSeq", cseq);
			JSONObject response = new JSONObject();
			response.put("Result", "0");
			object.put("Response", response);
			Log.d("ADC事件递交回复 request", object.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return object;
	}
	
	//解析json回复
	public static Object parseJSONString(String JSONString) throws JSONException {
//		Log.d("JSONString", JSONString);
		JSONObject object = new JSONObject(JSONString);
		int message = object.getInt("Message");
		int cseq = object.getInt("CSeq");
	
		if(message == 0x8001){//报警推送连接协议
			System.out.println("bb   报警推送连接协议");
//			int cseq = object.getInt("CSeq");
			JSONObject response = object.getJSONObject("Response");
			String result = response.getString("Result");
			return new AlarmPushConnectRes(message,cseq,result);
		}else if(message == 0x8002){//报警推送心跳协议
//			int cseq = object.getInt("CSeq");
			System.out.println("cc  报警推送心跳协议");
			JSONObject response = object.getJSONObject("Response");
			String result = response.getString("Result");
			String time;
			int heartbeatInterval;
			try{
				JSONObject kar = response.getJSONObject("KeepAlive");
				time = kar.getString("Time");
				heartbeatInterval = kar.getInt("HeartbeatInterval");
			}catch(JSONException e){
				Log.i("","KeepAlive == null");
				time = "";
				heartbeatInterval = 0;
			}
			return new KeepAliveRes(message,cseq,result,new KeepAlive(time,heartbeatInterval));
		}else if(message == 0x0003){//ADC事件递交
			System.out.println("dd   ADC事件递交");
			JSONObject request = object.getJSONObject("Request");
			JSONObject eventNotify = request.getJSONObject("EventNotify");
			String id = eventNotify.getString("Id");
			String name = eventNotify.getString("Name");
			String eventType = eventNotify.getString("EventType");
			String eventState = eventNotify.getString("EventState");
			String time = eventNotify.getString("Time");
			System.out.println("time_1:"+time.toString());
			time = Utils.utc2TimeZone(eventNotify.getString("Time").substring(0, 19));
//			System.out.println("time_2:"+time);
			String imageUrl = "";
			try{
				JSONArray imageUrls = eventNotify.getJSONArray("ImageUrl");
				Log.e("adc   ImageUrl size", imageUrls.length()+"");
				String[] imgs = new String[imageUrls.length()]; 
				for (int i = 0; i < imageUrls.length(); i++) {  
					imgs[i] = imageUrls.get(i).toString();
					Log.e("data", imgs[i]);
				}
				EventNotify e = new EventNotify();
				imageUrl = e.convertArrayToString(imgs);
//				System.out.println("ImageUrl:"+ imageUrl);
			}catch(JSONException e){
				Log.i("","imageUrl == null");
				imageUrl = "";
			}
			return new EventNotifyRes(message,cseq,new EventNotify(id,name,eventType,eventState,time,imageUrl));
		}
		return null;
	}
	
}
