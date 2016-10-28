package com.howell.protocol.entity;
/**
 * @author 霍之昊 
 *
 * 类说明
 */
public class KeepAlive {
	private String time;
	private int heartbeatInterval;
	public KeepAlive(String time, int heartbeatInterval) {
		super();
		this.time = time;
		this.heartbeatInterval = heartbeatInterval;
	}
	public KeepAlive() {
		super();
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public int getHeartbeatInterval() {
		return heartbeatInterval;
	}
	public void setHeartbeatInterval(int heartbeatInterval) {
		this.heartbeatInterval = heartbeatInterval;
	}
	@Override
	public String toString() {
		return "KeepAlive [time=" + time + ", heartbeatInterval="
				+ heartbeatInterval + "]";
	}
	
	

}
