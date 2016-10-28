package com.howell.formuseum.bean;

import java.util.ArrayList;

public class HistoryAlarm {

	String deviceID;//事件唯一标识符（设备产生的）
	String componentID;//报警单元模块唯一标识符
	String name;//报警单元模块名称或描述信息
	String eventType;//事件类型
	String alarmTime;//报警/触发时间
	
	
	ArrayList<String> pictureIDList = new ArrayList<String>();
	ArrayList<String> recordFileIDList = new ArrayList<String>();
	
	public HistoryAlarm() {
		// TODO Auto-generated constructor stub
	}
	
	public String getDeviceID() {
		return deviceID;
	}
	public HistoryAlarm setDeviceID(String deviceID) {
		this.deviceID = deviceID;
		return this;
	}
	public String getComponentID() {
		return componentID;
	}
	public HistoryAlarm setComponentID(String componentID) {
		this.componentID = componentID;
		return this;
	}
	public String getName() {
		return name;
	}
	public HistoryAlarm setName(String name) {
		this.name = name;
		return this;
	}
	public String getEventType() {
		return eventType;
	}
	public HistoryAlarm setEventType(String eventType) {
		this.eventType = eventType;
		return this;
	}
	public String getAlarmTime() {
		return alarmTime;
	}
	public HistoryAlarm setAlarmTime(String alarmTime) {
		this.alarmTime = alarmTime;
		return this;
	}
	public ArrayList<String> getPictureIDList() {
		return pictureIDList;
	}
	public HistoryAlarm setPictureIDList(ArrayList<String> pictureIDList) {
		this.pictureIDList = pictureIDList;
		return this;
	}
	public ArrayList<String> getRecordFileIDList() {
		return recordFileIDList;
	}
	public HistoryAlarm setRecordFileIDList(ArrayList<String> recordFileIDList) {
		this.recordFileIDList = recordFileIDList;
		return this;
	}
	public void addPictureID2List(String picId){
		this.pictureIDList.add(picId);
	}
	public void addRecordFile2List(String recordFileId){
		this.recordFileIDList.add(recordFileId);
	}
}
