package com.howell.protocol.entity;

import java.util.ArrayList;

/**
 * @author 霍之昊 
 *
 * 类说明:事件联动列表
 */
public class EventLinkageList {
	private Page page;								//分页信息
	private ArrayList<EventLinkage> eventLinkage;	//事件联动列表
	public EventLinkageList(Page page, ArrayList<EventLinkage> eventLinkage) {
		super();
		this.page = page;
		this.eventLinkage = eventLinkage;
	}
	public EventLinkageList() {
		super();
	}
	public Page getPage() {
		return page;
	}
	public void setPage(Page page) {
		this.page = page;
	}
	public ArrayList<EventLinkage> getEventLinkage() {
		return eventLinkage;
	}
	public void setEventLinkage(ArrayList<EventLinkage> eventLinkage) {
		this.eventLinkage = eventLinkage;
	}
	
	
}
