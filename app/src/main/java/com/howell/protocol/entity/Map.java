package com.howell.protocol.entity;

import java.io.Serializable;
import java.util.Arrays;

/**
 * @author 霍之昊 
 *
 * 类说明
 */
public class Map implements Serializable{
	private String id;
	private String name;
	private String comment;
	private String mapFormat;
	private String dataPath;
	
	private String MD5Code;
	private String lastModificationTime;
	
	private boolean hasAlarm;
	
	public Map(String id, String name, String comment, String mapFormat) {
		super();
		this.id = id;
		this.name = name;
		this.comment = comment;
		this.mapFormat = mapFormat;
		hasAlarm = false;
	}
	
	
	public Map(String id, String name, String comment, String mapFormat,
			String data) {
		super();
		this.id = id;
		this.name = name;
		this.comment = comment;
		this.mapFormat = mapFormat;
		this.dataPath = data;
		hasAlarm = false;
	}
	
	public Map(String id, String name, String comment, String mapFormat
		, String mD5Code, String lastModificationTime) {
		super();
		this.id = id;
		this.name = name;
		this.comment = comment;
		this.mapFormat = mapFormat;
		MD5Code = mD5Code;
		this.lastModificationTime = lastModificationTime;
		hasAlarm = false;
	}

	public Map(String id, String name, String comment, String mapFormat,
			String dataPath, String mD5Code, String lastModificationTime) {
		super();
		this.id = id;
		this.name = name;
		this.comment = comment;
		this.mapFormat = mapFormat;
		this.dataPath = dataPath;
		MD5Code = mD5Code;
		this.lastModificationTime = lastModificationTime;
		hasAlarm = false;
	}


	public Map() {
		super();
		hasAlarm = false;
	}
	
	public boolean isHasAlarm() {
		return hasAlarm;
	}

	public void setHasAlarm(boolean hasAlarm) {
		this.hasAlarm = hasAlarm;
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
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getMapFormat() {
		return mapFormat;
	}
	public void setMapFormat(String mapFormat) {
		this.mapFormat = mapFormat;
	}
	public String getDataPath() {
		return dataPath;
	}
	public void setDataPath(String data) {
		this.dataPath = data;
	}


	public String getMD5Code() {
		return MD5Code;
	}


	public void setMD5Code(String mD5Code) {
		MD5Code = mD5Code;
	}


	public String getLastModificationTime() {
		return lastModificationTime;
	}


	public void setLastModificationTime(String lastModificationTime) {
		this.lastModificationTime = lastModificationTime;
	}


	@Override
	public String toString() {
		return "Map [id=" + id + ", name=" + name + ", comment=" + comment
				+ ", mapFormat=" + mapFormat + ", dataPath=" + dataPath
				+ ", MD5Code=" + MD5Code + ", lastModificationTime="
				+ lastModificationTime + ", hasAlarm=" + hasAlarm + "]";
	}

}
