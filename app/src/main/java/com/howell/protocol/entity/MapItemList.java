package com.howell.protocol.entity;

import java.util.ArrayList;

/**
 * @author 霍之昊 
 *
 * 类说明
 */
public class MapItemList {
	private Page page;
	private ArrayList<MapItem> mapItem;
	public MapItemList(Page page, ArrayList<MapItem> mapItem) {
		super();
		this.page = page;
		this.mapItem = mapItem;
	}
	
	public MapItemList() {
		super();
	}

	public Page getPage() {
		return page;
	}
	public void setPage(Page page) {
		this.page = page;
	}
	public ArrayList<MapItem> getMapItem() {
		return mapItem;
	}
	public void setMapItem(ArrayList<MapItem> mapItem) {
		this.mapItem = mapItem;
	}

}
