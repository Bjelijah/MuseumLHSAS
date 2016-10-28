package com.howell.protocol.entity;

import java.io.Serializable;

import com.howell.utils.DebugUtil;

/**
 * @author 霍之昊 
 *
 * 类说明
 */
public class EventNotify implements Serializable{
	private String id;
	private String name;
	private String mapId;
	private String eventType;
	private String eventState;
	private String time;
	private String path;
	private String description;
	private String eventId;
	private String extendInformation;
	private String imageUrl;
	private int isAlarmed;
	
	public EventNotify(String id, String name, String eventType,
			String eventState, String time, String path, String description,
			String extendInformation) {
		super();
		this.id = id;
		this.name = name;
		this.eventType = eventType;
		this.eventState = eventState;
		this.time = time;
		this.path = path;
		this.description = description;
		this.extendInformation = extendInformation;
	}
	public EventNotify(String id, String name, String eventType,
			String eventState, String time,String imageUrl) {
		super();
		this.id = id;
		this.name = name;
		this.eventType = eventType;
		this.eventState = eventState;
		this.time = time;
		this.imageUrl = imageUrl;
	}
	
	public int getDateYear(String time){
		return time == null ? 0 : Integer.valueOf(time.substring(0, 4));
	}
	
	public int getDateMonth(String time){
		return time == null ? 0 : Integer.valueOf(time.substring(5, 7));
	}
	
	public int getDateDay(String time){
		return time == null ? 0 : Integer.valueOf(time.substring(8, 10));
	}
	
	public int getDateHour(String time){
		return time == null ? 0 : Integer.valueOf(time.substring(11, 13));
	}
	
	public int getDateMin(String time){
		return time == null ? 0 : Integer.valueOf(time.substring(14, 16));
	}
	
	public int getDateSec(String time){
		return time == null ? 0 : Integer.valueOf(time.substring(17, 19));
	}
	
	public EventNotify() {
		super();
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEventType() {
		return eventType;
	}
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}
	public String getEventState() {
		return eventState;
	}
	public void setEventState(String eventState) {
		this.eventState = eventState;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		DebugUtil.logI("eventnotfy", "time="+time);
		this.time = time;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getExtendInformation() {
		return extendInformation;
	}
	public void setExtendInformation(String extendInformation) {
		this.extendInformation = extendInformation;
	}
	
	public String getEventId() {
		return eventId;
	}
	public void setEventId(String eventId) {
		this.eventId = eventId;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	
	public String getMapId() {
		return mapId;
	}
	public void setMapId(String mapId) {
		this.mapId = mapId;
	}

	public String strSeparator = ";";
	public String convertArrayToString(String[] array){
	    String str = "";
	    for (int i = 0;i<array.length; i++) {
	        str = str+array[i];
	        // Do not append comma at the end of last element
	        if(i<array.length-1){
	            str = str+strSeparator;
	        }
	    }
	    return str;
	}
	public String[] convertStringToArray(String str){
	    String[] arr = str.split(strSeparator);
	    return arr;
	}
	
	public int getIsAlarmed() {
		return isAlarmed;
	}
	public void setIsAlarmed(int isAlarmed) {
		this.isAlarmed = isAlarmed;
	}
	@Override
	public String toString() {
		return "EventNotify [id=" + id + ", name=" + name + ", eventType="
				+ eventType + ", eventState=" + eventState + ", time=" + time
				+ ", path=" + path + ", description=" + description
				+ ", extendInformation=" + extendInformation + "imageUrl=" + imageUrl 
				+ ",isAlarmed = " + isAlarmed +"]";
	}
	
	

}
