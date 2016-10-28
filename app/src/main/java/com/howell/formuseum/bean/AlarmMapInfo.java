package com.howell.formuseum.bean;

import com.howell.protocol.entity.Map;

public class AlarmMapInfo {

	private String session,cookieHalf,webServiceIP,verify;
	private Map alarmMap;
	
	public AlarmMapInfo() {
		// TODO Auto-generated constructor stub
	}
	
	public AlarmMapInfo(String session,String cookieHalf,String webServiceIP,String verify,Map alarmMap) {
		this.session = session;
		this.cookieHalf = cookieHalf;
		this.webServiceIP = webServiceIP;
		this.verify = verify;
		this.alarmMap = alarmMap;
	}
	
	public String getSession() {
		return session;
	}
	public void setSession(String session) {
		this.session = session;
	}
	public String getCookieHalf() {
		return cookieHalf;
	}
	public void setCookieHalf(String cookieHalf) {
		this.cookieHalf = cookieHalf;
	}
	public String getWebServiceIP() {
		return webServiceIP;
	}
	public void setWebServiceIP(String webServiceIP) {
		this.webServiceIP = webServiceIP;
	}
	public String getVerify() {
		return verify;
	}
	public void setVerify(String verify) {
		this.verify = verify;
	}
	public Map getAlarmMap() {
		return alarmMap;
	}
	public void setAlarmMap(Map alarmMap) {
		this.alarmMap = alarmMap;
	}
	
	
}
