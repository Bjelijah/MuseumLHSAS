package com.howell.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.howell.protocol.entity.Coordinate;
import com.howell.protocol.entity.EventNotify;
import com.howell.protocol.entity.Map;
import com.howell.protocol.entity.MapItem;
import com.howell.utils.DebugUtil;

import java.util.ArrayList;

public class DBManager {
	private static final String TAG = "DBManager";
	private DBHelper helper;
	private SQLiteDatabase db;
	
	public DBManager(Context context) {
		helper = new DBHelper(context);
		//因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0, mFactory);
		//所以要确保context已初始化,我们可以把实例化DBManager的步骤放在Activity的onCreate里
		db = helper.getWritableDatabase();
	}
	
	/**
	 * add camera
	 * @param item
	 */
	public synchronized void addMapItem(MapItem item) {
		if (db == null) {
			DebugUtil.logE(TAG, "add map item error db = null");
			return;
		}
        db.beginTransaction();	//开始事务
        try {
        	db.execSQL("INSERT INTO map_item VALUES(NULL,?, ?, ?, ? , ? , ? , ?)", new Object[]{item.getId(),item.getComponentId(),item.getMapId(),item.getCoordinate().getX(),item.getCoordinate().getY()
        			,item.getItemType(),item.getAngle()});
        	db.setTransactionSuccessful();	//设置事务成功完成
        } finally {
        	db.endTransaction();	//结束事务
        }
	}
	
	public synchronized void addMap(Map map) {
		if (db == null) {
			DebugUtil.logE(TAG, "add map error db = null");
			return;
		}
        db.beginTransaction();	//开始事务
        try {
        	db.execSQL("INSERT INTO map VALUES(NULL,?, ?, ?, ? , ? , ? , ? )", new Object[]{map.getId(),map.getName(),map.getComment(),map.getMapFormat(),map.getDataPath(),map.getMD5Code(),map.getLastModificationTime()});
        	db.setTransactionSuccessful();	//设置事务成功完成
        } finally {
        	db.endTransaction();	//结束事务
        }
	}
	
	public synchronized void addAlarmList(EventNotify eventNotify) {
		if (db == null) {
			DebugUtil.logE(TAG, "add alarm list error db = null");
			return;
		}
        db.beginTransaction();	//开始事务
        try {
        	String mapId = "";
        	for(MapItem item : queryMapItem()){
        		if(item.getComponentId().equals(eventNotify.getId())){
        			mapId = item.getMapId();
        			Log.e("", "mapId:"+mapId);
        			break;
        		}
        	}
        	db.execSQL("INSERT INTO alarm_list VALUES(NULL,?, ?, ?, ? , ?,?,?,?,?,? )", new Object[]{eventNotify.getId(),eventNotify.getName(),mapId,eventNotify.getEventType(),eventNotify.getEventState()
        			,eventNotify.getTime(),eventNotify.getDescription(),eventNotify.getEventId(),eventNotify.getImageUrl(),0});
        	db.setTransactionSuccessful();	//设置事务成功完成
        } finally {
        	db.endTransaction();	//结束事务
        }
	}
	
	/**
	 * update camera's isshown flag
	 * @param camera
	 */
//	public synchronized void updateAlarmListFlag(EventNotify eventNotify) {
//		ContentValues cv = new ContentValues();
//		cv.put("isAlarmed", 1);
//		db.update("alarm_list", cv, "componentId = ?", new String[]{String.valueOf(eventNotify.getId())});
//	}
	
	/**
	 * delete old camera
	 * @param tableName
	 */
	public synchronized void deleteTable(String tableName) {
		if(db == null){
			DebugUtil.logE(TAG, "deleteTable error  db = null");
			return;
		}
		db.execSQL("delete from "+ tableName);
	}
	
	
//	public Camera queryCamera(Camera camera){
//		Camera cameraInTable = new Camera();
//		Cursor c = db.rawQuery("select * from camera where ip = ?",new String[]{camera.ip});
//		if(c.moveToFirst()) {
//		    String name = c.getString(c.getColumnIndex("name"));
//		    String ip = c.getString(c.getColumnIndex("ip"));
//		    int slot = c.getInt(c.getColumnIndex("slot"));
//		    int alarm_date_year = c.getInt(c.getColumnIndex("alarm_date_year"));
//		    int alarm_date_month = c.getInt(c.getColumnIndex("alarm_date_month"));
//		    int alarm_date_day = c.getInt(c.getColumnIndex("alarm_date_day"));
//		    int alarm_date_hour = c.getInt(c.getColumnIndex("alarm_date_hour"));
//		    int alarm_date_minute = c.getInt(c.getColumnIndex("alarm_date_minute"));
//		    int alarm_date_second = c.getInt(c.getColumnIndex("alarm_date_second"));
//		    cameraInTable.name = name;
//		    cameraInTable.ip = ip;
//		    cameraInTable.slot = slot;
//		    cameraInTable.alarm_date_year = alarm_date_year;
//		    cameraInTable.alarm_date_month = alarm_date_month;
//		    cameraInTable.alarm_date_day = alarm_date_day;
//		    cameraInTable.alarm_date_hour = alarm_date_hour;
//		    cameraInTable.alarm_date_minute = alarm_date_minute;
//		    cameraInTable.alarm_date_second = alarm_date_second;
//		    
//		}
//		return cameraInTable;
//	}
	
	public synchronized int selectEventNotifySqlKey(EventNotify eventNotify) {
		Cursor c = queryTheCursor("alarm_list");
        while (c.moveToNext()) {
        	if(c.getString(c.getColumnIndex("componentId")).equals(eventNotify.getId())){
        		return c.getInt(c.getColumnIndex("_id"));
        	}
        }
        c.close();
        return -1;
	}
	
	public synchronized boolean containsEventNotify(EventNotify eventNotify) {
	for(EventNotify e:queryAlarmList()){
		if(e.getId().equals(eventNotify.getId())){
			return true;
		}
	}
	return false;
	}
	
	/**
	 * query all camera, return list
	 * @return List<Camera>
	 */
	public synchronized ArrayList<Map> queryMap() {
		ArrayList<Map> list = new ArrayList<Map>();
		Cursor c = queryTheCursor("map");
        while (c.moveToNext()) {
        	Map map = new Map();
        	map.setId(c.getString(c.getColumnIndex("mapId")));
        	map.setName(c.getString(c.getColumnIndex("mapName")));
        	map.setComment(c.getString(c.getColumnIndex("comment")));
        	map.setMapFormat(c.getString(c.getColumnIndex("mapFormat")));
        	map.setDataPath(c.getString(c.getColumnIndex("mapDataPath")));
        	map.setMD5Code(c.getString(c.getColumnIndex("MD5Code")));
        	map.setLastModificationTime(c.getString(c.getColumnIndex("lastModificationTime")));
        	list.add(map);
        }
        c.close();
        return list;
	}
	
	public synchronized ArrayList<MapItem> queryMapItem() {
		ArrayList<MapItem> list = new ArrayList<MapItem>();
		Cursor c = queryTheCursor("map_item");
        while (c.moveToNext()) {
        	MapItem item = new MapItem();
        	item.setId(c.getString(c.getColumnIndex("deviceId")));
        	item.setComponentId(c.getString(c.getColumnIndex("componentId")));
        	item.setMapId(c.getString(c.getColumnIndex("mapId")));
        	Double x = c.getDouble(c.getColumnIndex("x_position"));
        	Double y = c.getDouble(c.getColumnIndex("y_position"));
        	item.setCoordinate(new Coordinate(x,y));
        	item.setItemType(c.getString(c.getColumnIndex("itemType")));
        	item.setAngle(c.getDouble(c.getColumnIndex("angle")));
        	list.add(item);
        }
        c.close();
        return list;
	}
	
	public synchronized ArrayList<MapItem> queryMapItem(String mapId) {
		ArrayList<MapItem> list = new ArrayList<MapItem>();
		Cursor c = queryTheCursor("map_item");
        while (c.moveToNext()) {
        	if(c.getString(c.getColumnIndex("mapId")).equals(mapId)){
        		MapItem item = new MapItem();
            	item.setId(c.getString(c.getColumnIndex("deviceId")));
            	item.setComponentId(c.getString(c.getColumnIndex("componentId")));
            	item.setMapId(c.getString(c.getColumnIndex("mapId")));
            	Double x = c.getDouble(c.getColumnIndex("x_position"));
            	Double y = c.getDouble(c.getColumnIndex("y_position"));
            	item.setCoordinate(new Coordinate(x,y));
            	item.setItemType(c.getString(c.getColumnIndex("itemType")));
            	item.setAngle(c.getDouble(c.getColumnIndex("angle")));
            	list.add(item);
        	}
        }
        c.close();
        return list;
	}
	
	public synchronized void updateEventNotifyAlarmFlag(EventNotify eventNotify){
		ContentValues cv = new ContentValues();
		cv.put("name", eventNotify.getName());
		cv.put("time", eventNotify.getTime());
		cv.put("isAlarmed", eventNotify.getIsAlarmed());
		cv.put("imageUrl", eventNotify.getImageUrl());
		db.update("alarm_list", cv, "componentId = ?", new String[]{String.valueOf(eventNotify.getId())});
	}
	
	public synchronized ArrayList<EventNotify> queryAlarmList() {
		ArrayList<EventNotify> list = new ArrayList<EventNotify>();
		Cursor c = queryTheCursor("alarm_list");
        while (c.moveToNext()) {
        	EventNotify item = new EventNotify();
        	item.setId(c.getString(c.getColumnIndex("componentId")));
        	item.setName(c.getString(c.getColumnIndex("name")));
        	item.setMapId(c.getString(c.getColumnIndex("mapId")));
        	item.setEventType(c.getString(c.getColumnIndex("eventType")));
        	item.setEventState(c.getString(c.getColumnIndex("eventState")));
        	item.setTime(c.getString(c.getColumnIndex("time")));
        	item.setImageUrl(c.getString(c.getColumnIndex("imageUrl")));
        	list.add(item);
        }
        c.close();
        return list;
	}
	
	//查询isAlarmed标记为为0（未查看报警）的报警列表
	public synchronized ArrayList<EventNotify> queryUnreadAlarmList() {
		ArrayList<EventNotify> list = new ArrayList<EventNotify>();
		Cursor c = queryTheCursor("alarm_list");
        while (c.moveToNext()) {
        	if(c.getInt(c.getColumnIndex("isAlarmed")) == 0){
	        	EventNotify item = new EventNotify();
	        	item.setId(c.getString(c.getColumnIndex("componentId")));
	        	item.setName(c.getString(c.getColumnIndex("name")));
	        	item.setMapId(c.getString(c.getColumnIndex("mapId")));
	        	item.setEventType(c.getString(c.getColumnIndex("eventType")));
	        	item.setEventState(c.getString(c.getColumnIndex("eventState")));
	        	item.setTime(c.getString(c.getColumnIndex("time")));
	        	item.setImageUrl(c.getString(c.getColumnIndex("imageUrl")));
	        	list.add(item);
        	}
        }
        c.close();
        return list;
	}
	
	//查询某个地图下的所有报警列表
	public synchronized ArrayList<EventNotify> queryAllAlarmListWithMapId(String mapId) {
		ArrayList<EventNotify> list = new ArrayList<EventNotify>();
		Cursor c = queryTheCursor("alarm_list");
        while (c.moveToNext()) {
        	if(c.getString(c.getColumnIndex("mapId")).equals(mapId)){
	        	EventNotify item = new EventNotify();
	        	item.setId(c.getString(c.getColumnIndex("componentId")));
	        	item.setName(c.getString(c.getColumnIndex("name")));
	        	item.setMapId(c.getString(c.getColumnIndex("mapId")));
	        	item.setEventType(c.getString(c.getColumnIndex("eventType")));
	        	item.setEventState(c.getString(c.getColumnIndex("eventState")));
	        	item.setTime(c.getString(c.getColumnIndex("time")));
	        	item.setImageUrl(c.getString(c.getColumnIndex("imageUrl")));
	        	item.setIsAlarmed(c.getInt(c.getColumnIndex("isAlarmed")));
	        	list.add(item);
        	}
        }
        c.close();
        return list;
	}
	
	//查询某个地图下的未看报警列表
	public synchronized ArrayList<EventNotify> queryAlarmListWithMapId(String mapId) {
		ArrayList<EventNotify> list = new ArrayList<EventNotify>();
		Cursor c = queryTheCursor("alarm_list");
        while (c.moveToNext()) {
        	if((c.getInt(c.getColumnIndex("isAlarmed")) == 0) && c.getString(c.getColumnIndex("mapId")).equals(mapId)){
	        	EventNotify item = new EventNotify();
	        	item.setId(c.getString(c.getColumnIndex("componentId")));
	        	item.setName(c.getString(c.getColumnIndex("name")));
	        	item.setMapId(c.getString(c.getColumnIndex("mapId")));
	        	item.setEventType(c.getString(c.getColumnIndex("eventType")));
	        	item.setEventState(c.getString(c.getColumnIndex("eventState")));
	        	item.setTime(c.getString(c.getColumnIndex("time")));
	        	item.setImageUrl(c.getString(c.getColumnIndex("imageUrl")));
	        	list.add(item);
        	}
        }
        c.close();
        return list;
	}
	
	public synchronized boolean hasAlarmWithMapId(String mapId){
		Cursor c = queryTheCursor("alarm_list");
		while (c.moveToNext()) {
			if((c.getInt(c.getColumnIndex("isAlarmed")) == 0) && c.getString(c.getColumnIndex("mapId")).equals(mapId)){
				c.close();
				Log.e("123", "has alarm with map id true");
				return true;
			}
		}
		c.close();
		Log.e("123","has alarm with map id false");
		return false;
	}
	
//	public synchronized boolean containsElement(Camera c) {
//		for(Camera camera:query()){
//			if(camera.componentId.equals(c.componentId)){
//				return true;
//			}
//		}
//		return false;
//	}
//	
//	public synchronized Camera selectElement(Camera c) {
//		for(Camera camera:query()){
//			if(camera.componentId.equals(c.componentId)){
//				return camera;
//			}
//		}
//		return null;
//	}
//	
//	public synchronized Camera selectElement(String componentId) {
//		for(Camera camera:query()){
//			if(camera.componentId.equals(componentId)){
//				return camera;
//			}
//		}
//		return null;
//	}
	
	/**
	 * query all cameras, return cursor
	 * @return	Cursor
	 */
	public synchronized Cursor queryTheCursor(String tableName) {
		if (db==null) {
			DebugUtil.logE(TAG, "queryTheCursor   db = null ");
			return null;
		}
        Cursor c = db.rawQuery("SELECT * FROM " + tableName, null);
        return c;
	}
	
	/**
	 * close database
	 */
	public synchronized void closeDB() {
		
		DebugUtil.logI(TAG, "close DB");
		db.close();
		db = null;
	}
}
