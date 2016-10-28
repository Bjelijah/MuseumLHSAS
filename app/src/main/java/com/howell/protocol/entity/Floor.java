package com.howell.protocol.entity;

import java.util.ArrayList;

/**
 * @author 霍之昊 
 *
 * 类说明
 */
public class Floor {
	private String id;
	private String name;
	private byte[] mapBitmap;
	private ArrayList<MapItem> itemList;
	
	public Floor(String id, String name, byte[] mapBitmap,ArrayList<MapItem> itemList) {
		super();
		this.id = id;
		this.name = name;
		this.mapBitmap = mapBitmap;
		this.itemList = itemList;
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
	public byte[] getMapBitmap() {
		return mapBitmap;
	}
	public void setMapBitmap(byte[] mapBitmap) {
		this.mapBitmap = mapBitmap;
	}
	public ArrayList<MapItem> getItemList() {
		return itemList;
	}
	public void setItemList(ArrayList<MapItem> itemList) {
		this.itemList = itemList;
	}
	
	
}
