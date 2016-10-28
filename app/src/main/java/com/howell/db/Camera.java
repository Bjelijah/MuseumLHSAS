package com.howell.db;

import java.io.Serializable;

public class Camera implements Serializable{
	public int _id;
	public String deviceId;
	public String name;
	public String ip;
	public int slot;
	public int isShown;
	public Double xPosition;
	public Double yPosition;
	public String mapId;
	public String mapName;	
	public String pictureUrl;
	public String componentId;
	
	public String eventType;
	public String eventState;
	
	public int alarm_date_year;
	public int alarm_date_month;
	public int alarm_date_day;
	public int alarm_date_hour;
	public int alarm_date_minute;
	public int alarm_date_second;
	
	public Camera(String deviceId, Double xPosition,
			Double yPosition, String mapId, String mapName, String componentId) {
		super();
		this.deviceId = deviceId;
		this.isShown = 0;
		this.xPosition = xPosition;
		this.yPosition = yPosition;
		this.mapId = mapId;
		this.mapName = mapName;
		this.componentId = componentId;
	}
	
	public Camera(String id,String name, String pictureUrl, String eventType,
			String eventState, int alarm_date_year, int alarm_date_month,
			int alarm_date_day, int alarm_date_hour, int alarm_date_minute,
			int alarm_date_second) {
		super();
		this.componentId = id;
		this.name = name;
		this.pictureUrl = pictureUrl;
		this.eventType = eventType;
		this.eventState = eventState;
		this.alarm_date_year = alarm_date_year;
		this.alarm_date_month = alarm_date_month;
		this.alarm_date_day = alarm_date_day;
		this.alarm_date_hour = alarm_date_hour;
		this.alarm_date_minute = alarm_date_minute;
		this.alarm_date_second = alarm_date_second;
	}

	public Camera() {
		super();
	}

	@Override
	public String toString() {
		return "Camera [_id=" + _id + ", deviceId=" + deviceId + ", name="
				+ name + ", ip=" + ip + ", slot=" + slot + ", isShown="
				+ isShown + ", xPosition=" + xPosition + ", yPosition="
				+ yPosition + ", mapId=" + mapId + ", mapName=" + mapName
				+ ", pictureUrl=" + pictureUrl + ", componentId=" + componentId
				+ ", eventType=" + eventType + ", eventState=" + eventState
				+ ", alarm_date_year=" + alarm_date_year
				+ ", alarm_date_month=" + alarm_date_month
				+ ", alarm_date_day=" + alarm_date_day + ", alarm_date_hour="
				+ alarm_date_hour + ", alarm_date_minute=" + alarm_date_minute
				+ ", alarm_date_second=" + alarm_date_second + "]";
	}

}
