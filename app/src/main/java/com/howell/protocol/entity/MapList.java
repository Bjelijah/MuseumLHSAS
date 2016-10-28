package com.howell.protocol.entity;

import java.util.ArrayList;

import android.util.Log;

/**
 * @author 霍之昊 
 *
 * 类说明
 */
public class MapList {
	private Page page;
	private ArrayList<Map> map;
	public MapList(Page page, ArrayList<Map> map) {
		super();
		this.page = page;
		this.map = map;
	}
	public MapList() {
		super();
	}
	public Page getPage() {
		return page;
	}
	public void setPage(Page page) {
		this.page = page;
	}
	public ArrayList<Map> getMap() {
		return map;
	}
	public void setMap(ArrayList<Map> map) {
		this.map = map;
	}
	
	public void showMapList(){
		if(map == null){
			return;
		}
		for(Map m : map){
			Log.v("map", m.toString());
		}
	}

}
