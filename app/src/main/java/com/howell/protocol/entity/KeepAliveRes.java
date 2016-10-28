package com.howell.protocol.entity;
/**
 * @author 霍之昊 
 *
 * 类说明
 */
public class KeepAliveRes {
	private int message;
	private int cSeq;
	private String result;
	private KeepAlive keepAlive;
	public KeepAliveRes(int message, int cSeq, String result,
			KeepAlive keepAlive) {
		super();
		this.message = message;
		this.cSeq = cSeq;
		this.result = result;
		this.keepAlive = keepAlive;
	}
	public KeepAliveRes() {
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
	public KeepAlive getKeepAlive() {
		return keepAlive;
	}
	public void setKeepAlive(KeepAlive keepAlive) {
		this.keepAlive = keepAlive;
	}
	@Override
	public String toString() {
		return "KeepAliveRes [message=" + message + ", cSeq=" + cSeq
				+ ", result=" + result + ", keepAlive=" + keepAlive.toString() + "]";
	}
	
	
}
