package com.howell.utils;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.map.ser.std.StdArraySerializers.StringArraySerializer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.LightingColorFilter;
import android.util.Base64;
import android.util.Log;

import com.howell.formuseum.JNIManager;
import com.howell.formuseum.bean.AudioComeData;
import com.howell.formuseum.bean.TalkDialog;
import com.howell.protocol.entity.Coordinate;
import com.howell.protocol.entity.Device;
import com.howell.protocol.entity.EventLinkage;
import com.howell.protocol.entity.EventLinkageList;
import com.howell.protocol.entity.EventRecordList;
import com.howell.protocol.entity.ExceptionData;
import com.howell.protocol.entity.Fault;
import com.howell.protocol.entity.Map;
import com.howell.protocol.entity.MapItem;
import com.howell.protocol.entity.MapItemList;
import com.howell.protocol.entity.MapList;
import com.howell.protocol.entity.Page;
import com.howell.protocol.entity.PlaybackTask;
import com.howell.protocol.entity.ServerNonce;
import com.howell.protocol.entity.TalkAlive;
import com.howell.protocol.entity.TalkDialogList;
import com.howell.protocol.entity.TalkRegister;
import com.howell.protocol.entity.TalkRes;
import com.howell.protocol.entity.VideoPlaybackIdentifier;
import com.howell.protocol.entity.VideoPreviewIdentifier;

/**
 * @author 霍之昊 
 *
 * 类说明
 */
public class JsonUtils {
	//Nonce
	public static ServerNonce parseNonceJsonObject(JSONObject param) throws JSONException{
		if(param == null){
			return null;
		}
		String nonce = param.getString("Nonce");
		String domain = param.getString("Domain");
		System.out.println("nonce:"+nonce+",domian:"+domain);
		return new ServerNonce(nonce,domain);
	}
	
	//Authenticate
	public static JSONObject createAuthenticateJsonObject(String userName,String nonce,String domain,String clientNonce,String verifySession) throws JSONException{
		JSONObject param = new JSONObject();  
		param.put("UserName", userName);  
		param.put("Nonce", nonce);  
		param.put("Domain", domain);
		param.put("ClientNonce", clientNonce); 
		param.put("VerifySession", verifySession); 
		return param;
	}
	
	public static Fault parseAuthenticateJsonObject(JSONObject param) throws JSONException{
		if(param == null){
			return null;
		}
		int faultCode = param.getInt("FaultCode");
		String faultReason = param.getString("FaultReason");
		String message = null,exceptionType = null;
		try{
			JSONObject exception = param.getJSONObject("Exception");
			message = exception.getString("Message");
			exceptionType = exception.getString("ExceptionType");
		}catch(Exception e){
			System.out.println("exception is null");
		}
		String id = null;
		try{
		id = param.getString("Id");
		}catch(Exception e){
			System.out.println("id is null");
		}
		return new Fault(faultCode,faultReason,new ExceptionData(message,exceptionType),id);
	}
	
	public static MapList parseMapsJsonObject(JSONObject param) throws JSONException{
		if(param == null){
			return null;
		}
		JSONObject page = param.getJSONObject("Page");
		int pageIndex = page.getInt("PageIndex");
		int pageSize = page.getInt("PageSize");
		int pageCount = page.getInt("PageCount");
		int recordCount = page.getInt("RecordCount");
		int totalRecordCount = page.getInt("TotalRecordCount");
		
		ArrayList<Map> maps = new ArrayList<Map>();
		JSONArray map = param.getJSONArray("Map");
		for (int i = 0; i < map.length(); i++) {  
			JSONObject item = map.getJSONObject(i); // 得到每个对象  
			String id = item.getString("Id"); 
			String name = item.getString("Name"); 
			String comment = "";
			try{
				comment = item.getString("Comment"); 
			}catch(Exception e){
				System.out.println("comment is null");
			}
			String mapFormat = item.getString("MapFormat"); 
			String mD5Code = "";
			try{
				mD5Code = item.getString("MD5Code");
			}catch(Exception e){
				System.out.println("mD5Code is null");
			}
			String lastModificationTime = "";
			try{
				lastModificationTime = item.getString("LastModificationTime");
			}catch(Exception e){
				System.out.println("lastModificationTime is null");
			}
			maps.add(new Map(id,name,comment,mapFormat,mD5Code,lastModificationTime));
		}
		return new MapList(new Page(pageIndex, pageSize,pageCount,recordCount,totalRecordCount),maps);
	}
	
	public static MapItemList parseMapsItemJsonObject(JSONObject param) throws JSONException{
		if(param == null){
			return null;
		}
		JSONObject page = param.getJSONObject("Page");
		int pageIndex = page.getInt("PageIndex");
		int pageSize = page.getInt("PageSize");
		int pageCount = page.getInt("PageCount");
		int recordCount = page.getInt("RecordCount");
		int totalRecordCount = page.getInt("TotalRecordCount");
		
		ArrayList<MapItem> maps = new ArrayList<MapItem>();
		JSONArray map = param.getJSONArray("MapItem");
		for (int i = 0; i < map.length(); i++) {  
			JSONObject item = map.getJSONObject(i); // 得到每个对象  
			String id = item.getString("Id"); 
			String itemType = item.getString("ItemType"); 
			String componentId = item.getString("ComponentId"); 
			JSONObject coordinate = item.getJSONObject("Coordinate");
			
			Double x = coordinate.getDouble("X");
			Double y = coordinate.getDouble("Y");
			Double angle = (double) 0;
			try{
				angle = item.getDouble("Angle");
			}catch(Exception e){
				System.out.println("angle is null");
			}
			maps.add(new MapItem(id,itemType,componentId,new Coordinate(x, y),angle));
		}
		return new MapItemList(new Page(pageIndex, pageSize,pageCount,recordCount,totalRecordCount),maps);
	}
	
	public static EventLinkageList parseEventLinkageListJsonObject(JSONObject param) throws JSONException{
		if(param == null){
			return null;
		}
		JSONObject page = param.getJSONObject("Page");
		int pageIndex = page.getInt("PageIndex");
		int pageSize = page.getInt("PageSize");
		int pageCount = page.getInt("PageCount");
		int recordCount = page.getInt("RecordCount");
		int totalRecordCount = page.getInt("TotalRecordCount");
		
		ArrayList<EventLinkage> eventLinkages = new ArrayList<EventLinkage>();
		JSONArray eventLinkage = param.getJSONArray("EventLinkage");
		for (int i = 0; i < eventLinkage.length(); i++) {  
			JSONObject item = eventLinkage.getJSONObject(i); // 得到每个对象  
			String componentId = item.getString("ComponentId"); 
			String eventType = item.getString("EventType"); 
			String eventState = item.getString("EventState"); 
			
			ArrayList<VideoPreviewIdentifier> videoPreviewIdentifiers = new ArrayList<VideoPreviewIdentifier>();
			try{
				JSONArray videoPreviewIdentifier = param.getJSONArray("VideoPreviewIdentifier");
				for (int j = 0; j < videoPreviewIdentifier.length(); j++) {  
					JSONObject item_videoPreviewIdentifier = videoPreviewIdentifier.getJSONObject(j); // 得到每个对象  
					String videoInputChannelId = item_videoPreviewIdentifier.getString("VideoInputChannelId");
					int streamNo = item_videoPreviewIdentifier.getInt("StreamNo");
					//String protocol = item_videoPreviewIdentifier.getString("Protocol");
					videoPreviewIdentifiers.add(new VideoPreviewIdentifier(videoInputChannelId,streamNo));
				}
			}catch(Exception e){
				Log.e("", "videoPreviewIdentifier is null");
			}
			ArrayList<VideoPlaybackIdentifier> videoPlaybackIdentifiers = new ArrayList<VideoPlaybackIdentifier>();
			try{
				JSONArray videoPlaybackIdentifier = param.getJSONArray("VideoPlaybackIdentifier");
				for (int j = 0; j < videoPlaybackIdentifier.length(); j++) {  
					JSONObject item_videoPlaybackIdentifier = videoPlaybackIdentifier.getJSONObject(j); // 得到每个对象  
					String videoInputChannelId = item_videoPlaybackIdentifier.getString("VideoInputChannelId");
					int streamNo = item_videoPlaybackIdentifier.getInt("StreamNo");
					String protocol = item_videoPlaybackIdentifier.getString("Protocol");
					int beginTime = item_videoPlaybackIdentifier.getInt("BeginTime");
					int endTime = item_videoPlaybackIdentifier.getInt("EndTime");
					videoPlaybackIdentifiers.add(new VideoPlaybackIdentifier(videoInputChannelId,streamNo,protocol,beginTime,endTime));
				}
			}catch(Exception e){
				Log.e("", "videoPlaybackIdentifier is null");
			}
			
			eventLinkages.add(new EventLinkage(componentId,eventType,eventState,videoPreviewIdentifiers));
		}
		
		return new EventLinkageList(new Page(pageIndex, pageSize,pageCount,recordCount,totalRecordCount),eventLinkages);
	}
	
	public static EventLinkage parseEventLinkageJsonObject(JSONObject param) throws JSONException{
		if(param == null){
			return null;
		}
		String componentId = param.getString("ComponentId"); 
		String eventType = param.getString("EventType"); 
		String eventState = param.getString("EventState"); 
		
		ArrayList<VideoPreviewIdentifier> videoPreviewIdentifiers = new ArrayList<VideoPreviewIdentifier>();
		try{
			JSONArray videoPreviewIdentifier = param.getJSONArray("VideoPreviewIdentifier");
			for (int j = 0; j < videoPreviewIdentifier.length(); j++) {  
				JSONObject item_videoPreviewIdentifier = videoPreviewIdentifier.getJSONObject(j); // 得到每个对象  
				String videoInputChannelId = item_videoPreviewIdentifier.getString("VideoInputChannelId");
				int streamNo = item_videoPreviewIdentifier.getInt("StreamNo");
				//String protocol = item_videoPreviewIdentifier.getString("Protocol");
				videoPreviewIdentifiers.add(new VideoPreviewIdentifier(videoInputChannelId,streamNo));
			}
		}catch(Exception e){
			Log.e("123", "videoPreviewIdentifier is null");
		}
		boolean ret = false;	
		ArrayList<VideoPlaybackIdentifier> videoPlaybackIdentifiers = new ArrayList<VideoPlaybackIdentifier>();
		try {
			
			JSONArray videoPlaybackIdentifier = param.getJSONArray("VideoPlaybackIdentifier");
			for (int j = 0; j < videoPlaybackIdentifier.length(); j++) {  
				JSONObject item_videoPlaybackIdentifier = videoPlaybackIdentifier.getJSONObject(j); // 得到每个对象  
				String videoInputChannelId = item_videoPlaybackIdentifier.getString("VideoInputChannelId");
				int streamNo = item_videoPlaybackIdentifier.getInt("StreamNo");
				String protocol = item_videoPlaybackIdentifier.getString("Protocol");
				int beginTime = 0,endTime = 0;
				try {
					beginTime = item_videoPlaybackIdentifier.getInt("BeginTime");
				} catch (Exception e) {
					// TODO: handle exception
				}
				try {
					endTime = item_videoPlaybackIdentifier.getInt("EndTime");
				} catch (Exception e) {
					// TODO: handle exception
				}
				if (beginTime == 0) {
					beginTime = 5;
				}
				if(endTime == 0){
					endTime = 30;
				}	
				videoPlaybackIdentifiers.add(new VideoPlaybackIdentifier(videoInputChannelId,streamNo,protocol,beginTime,endTime));
			}
			ret = true;
		} catch (Exception e) {
			// TODO: handle exception
			ret = false;
		}
		if (!ret) {
			return new EventLinkage(componentId,eventType,eventState,videoPreviewIdentifiers);
		}else{
			return new EventLinkage(componentId, eventType, eventState, videoPreviewIdentifiers, videoPlaybackIdentifiers);
		}
			
		
	}
	
	public static PlaybackTask parsePlaybackTaskJsonObject(JSONObject param) throws JSONException{
		if(param == null){
			return null;
		}
		
		String taskId = param.getString("TaskId"); 
		String videoInputChannelId = param.getString("VideoInputChannelId"); 
		String url = param.getString("Url"); 
		String protocol = param.getString("Protocol"); 
		String sDP = "";
		try{
			sDP = param.getString("SDP"); 
		}catch(Exception e){
			Log.e("PlaybackTask", "SDP is null");
		}
		
		return new PlaybackTask(taskId,videoInputChannelId,url,protocol,sDP);
	}
	
	public static Device parseDeviceJsonObject(JSONObject param) throws JSONException{
		if(param == null){
			return null;
		}
		
		String name = param.getString("Name"); 
		String url = param.getString("Uri"); 
		
		return new Device(name,url);
	}
	
	public static JSONObject createProcessJsonObject(String process) throws JSONException{
		JSONObject param = new JSONObject();  
		param.put("Description", process); 
		return param;
	}
	
	
	public static EventRecordList parseHistoryAlarmsJsonObject(JSONObject param) throws JSONException{
		EventRecordList eventRecordList = new EventRecordList();
		JSONObject page = param.getJSONObject("Page");
		int pageIndex = page.getInt("PageIndex");
		int pageSize = page.getInt("PageSize");
		int pageCount = page.getInt("PageCount");
		int recordCount = page.getInt("RecordCount");
		int totalRecordCount = page.getInt("TotalRecordCount");
	
		eventRecordList.getPage().setPageIndex(pageIndex).setPageSize(pageSize).setPageCount(pageCount)
		.setRecordCount(recordCount).setTotalRecordCount(totalRecordCount);
		JSONArray jsonArrayEventRecords = param.getJSONArray("EventRecord");
		for(int i=0;i<jsonArrayEventRecords.length();i++){
			EventRecordList.EventRecord record = new EventRecordList.EventRecord();
			JSONObject eventRecord = jsonArrayEventRecords.getJSONObject(i);
			String id 					= eventRecord.getString("Id");
			String componentId 			= eventRecord.getString("ComponentId");
			String name 				= eventRecord.getString("Name");
			String eventType			= eventRecord.getString("EventType");
			String alarmTime			= eventRecord.getString("AlarmTime");
			int    severity				= eventRecord.getInt("Severity");
			String disAlarmTime         = "";
			String processTime			= "";
			String processDescription   = "";
			String description			= "";
			int    objectType			= -1;
			double triggerValue			= 0.0;
			String [] pictureID			= null;
					
			try {
				disAlarmTime = eventRecord.getString("DisalarmTime");
			} catch (Exception e) {
//				e.printStackTrace();
			}
			
			try {
				processTime = eventRecord.getString("ProcessTime");
			} catch (Exception e) {
//				e.printStackTrace();
			}
			
			try {
				processDescription = eventRecord.getString("ProcessDescription");
			} catch (Exception e) {
//				e.printStackTrace();
			}
			
			try {
				description = eventRecord.getString("Description");
			} catch (Exception e) {
//				e.printStackTrace();
			}
			
			try {
				objectType = eventRecord.getInt("ObjectType");
			} catch (Exception e) {
//				e.printStackTrace();
			}
			
			
			try {
				triggerValue = eventRecord.getDouble("TriggerValue");
			} catch (Exception e) {
//				e.printStackTrace();
			}
			
			try {
				JSONArray jsonArrayPicId = eventRecord.getJSONArray("PictureId");
				pictureID = new String[jsonArrayPicId.length()];
				for(int j=0;j<jsonArrayPicId.length();j++){
					String picId = (String)jsonArrayPicId.get(j);
					Log.i("123", "picID "+ j+" "+picId);
					pictureID[j] = picId;
				}
			} catch (Exception e) {
//				e.printStackTrace();
			}
			
			
			try {
				JSONArray jsonArrayRecrdeFiles = eventRecord.getJSONArray("RecordedFile");
				for(int j=0;j<jsonArrayRecrdeFiles.length();j++){
					JSONObject jsonObjectRecordedFile = jsonArrayRecrdeFiles.getJSONObject(j);
					String recordedFileId = jsonObjectRecordedFile.getString("RecordedFileId");
					long   recordedFileTimestamp = -1;
					try {
						recordedFileTimestamp =	jsonObjectRecordedFile.getLong("RecordedFileTimestamp");
					} catch (Exception e) {
//						e.printStackTrace();
					}
					
					EventRecordList.EventRecord.EventRecordedFile erf = new EventRecordList.EventRecord.EventRecordedFile();
					erf.setRecordeFileID(recordedFileId).setRecordedFileTimestamp(recordedFileTimestamp);
					
					record.addEventRecordedFile(erf);
				}
			} catch (Exception e) {
//				e.printStackTrace();
			}
			
			record.setId(id).setComponentId(componentId).setName(name).setEventType(eventType).setAlarmTime(alarmTime)
			.setSeverity(severity).setDisalarmTime(disAlarmTime).setProcessTime(processTime).setProecssDescription(processDescription)
			.setDescription(description).setObjectType(objectType).setTriggerVale(triggerValue).setPictrueId(pictureID);
			eventRecordList.addEventRecords(record);
			
		}
		
//		Log.i("123", ""+eventRecordList.toString());
		
		return eventRecordList;
	}



	public static TalkRegister.Res parseTalkRegist(JSONObject param) throws JSONException{
		if(param==null)return null;
		int res 		= param.getInt("Result");
		int interval 	= param.getInt("Interval");
		String id		= param.getString("Id");
		return new TalkRegister.Res(res, interval, id);
	}
	
	public static TalkAlive parseTalkAlive(JSONObject param) throws JSONException{
		if(param == null)return null;
		String id = param.getString("DialogId");
		return new TalkAlive(id);
	}

	public static TalkRes paraseTalkRes(JSONObject param) throws JSONException{
		if(param == null)return null;
		int res = param.getInt("Result");
		return new TalkRes(res);
	}

	
	@Deprecated
	public static TalkDialogList paraseTalkDialogList(JSONObject param) throws JSONException{
		if(param == null)return null;
		int res = param.getInt("Result");
		int len = 0;
		try {
			JSONArray jsonArray = param.getJSONArray("DialogId");
			len = jsonArray.length();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if(len == 0){
			return new TalkDialogList(res, null);
		}
//		List<Talkd>
		
		for(int i=0;i<len;i++){
			
			
		}
		
		
		return null;
		
		
	}
	
	public static String audioData(List<TalkDialog> targets,byte [] data,int len){
		if(targets==null||targets.isEmpty())return null;
		if(len==0)return null;
//		Log.i("123", "len="+len);
//		for(int i=0;i<100;i++){
//			Log.i("123", ""+data[i]);
//		}
//		
		
		
		byte [] g711buf=new byte[len/2];// = JNIManager.getInstance().pcm2G711u(data);
		
		JNIManager.getInstance().pcm2G711u(data,len,g711buf);
		
		boolean bar = false;
		JSONObject object = null;
		boolean bUsingDialogId = false;//用dialogid true；  用   userName  false；  
		try {
			object = new JSONObject();
			if(targets.get(0).getDialogId()!=null && bUsingDialogId){
				JSONArray idArray = new JSONArray(); 
				for(int i=0;i<targets.size();i++){
					if(targets.get(i).getDialogId()!=null){
						idArray.put(i, targets.get(i).getDialogId());
					}
				}
				object.put("DialogId", idArray);
			}else{
				JSONArray nameArray = new JSONArray();
				for(int i=0;i<targets.size();i++){
					if(targets.get(i).getDialogName()!=null){
						nameArray.put(i,targets.get(i).getDialogName());
					}
				}
				object.put("UserName", nameArray);
			}
			object.put("ContentType", 0);
//			for(int i=0;i<100;i++){
//				Log.i("123", ""+g711buf[i]);
//			}
			
			
			String string = new String(Base64.encode(g711buf, Base64.DEFAULT));
//			Log.i("123", "string="+string);
			object.put("Content", string);
			//object.put("Content", g711buf.toString());//FIXME
			//Log.i("123", object.toString());
			
		//	Log.i("123",object.toString()+"strlen="+data.toString().length()+" data len="+data.length+" len="+len);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			bar = true;
			e.printStackTrace();
		}
		g711buf = null;
		return bar==false?object.toString():null;
	}
	
	public static AudioComeData parseAudioReceive(JSONObject param) throws JSONException{
		if(param==null)return null;
		String senderId = param.getString("SenderDialogId"); 
		String sender = param.getString("Sender");
		int type = param.getInt("ContentType");
		String content = param.getString("Content");
		byte [] gbuf = Base64.decode(content.getBytes(), Base64.DEFAULT);
		return new AudioComeData(senderId, sender, type, gbuf);
	}
	
	public static byte [] audioReceive(JSONObject param) throws JSONException {
		if(param==null)return null;

			String senderId = param.getString("SenderDialogId"); 
			String sender = param.getString("Sender");
			int type = param.getInt("ContentType");
			String content = param.getString("Content");
			
			//Log.i("123", "content="+content+" cont size="+content.length());
			
			byte [] gbuf = Base64.decode(content.getBytes(), Base64.DEFAULT);
			
			//byte [] gbuf  = content.getBytes();
			//Log.e("123", "size="+gbuf.length);
			
			
		return gbuf;	
			
	}
}
