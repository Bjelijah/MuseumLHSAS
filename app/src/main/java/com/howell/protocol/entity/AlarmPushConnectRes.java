package com.howell.protocol.entity;
/**
 * @author 霍之昊 
 *
 * 类说明
 */
public class AlarmPushConnectRes {
	private int message;
	private int cSeq;
	private String result;
	public AlarmPushConnectRes(int message, int cSeq, String result) {
		super();
		this.message = message;
		this.cSeq = cSeq;
		this.result = result;
	}
	public AlarmPushConnectRes() {
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
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	@Override
	public String toString() {
		return "AlarmPushConnectRes [message=" + message + ", cSeq=" + cSeq
				+ ", result=" + result + "]";
	}

}	
