package com.howell.protocol.entity;

import java.io.Serializable;

/**
 * @author 霍之昊 
 *
 * 类说明
 */
public class EventNotifyRes {
	private int message;
	private int cSeq;
	private EventNotify eventNotify;

	public EventNotifyRes(EventNotify eventNotify) {
		super();
		this.eventNotify = eventNotify;
	}

	public EventNotifyRes(int message, int cSeq, EventNotify eventNotify) {
		super();
		this.message = message;
		this.cSeq = cSeq;
		this.eventNotify = eventNotify;
	}

	public EventNotifyRes() {
		super();
	}

	public int getMessage() {
		return message;
	}

	public void setMessage(int message) {
		this.message = message;
	}

	public int getcSeq() {
		return cSeq;
	}

	public void setcSeq(int cSeq) {
		this.cSeq = cSeq;
	}

	public EventNotify getEventNotify() {
		return eventNotify;
	}

	public void setEventNotify(EventNotify eventNotify) {
		this.eventNotify = eventNotify;
	}

	@Override
	public String toString() {
		return eventNotify.toString() ;
	}
}
