package com.howell.formuseum.bean;

import java.util.Arrays;

public class AudioComeData {
	String senderId;
	String sender;
	int type;
	byte [] g711data;
	public String getSenderId() {
		return senderId;
	}
	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}
	public String getSender() {
		return sender;
	}
	public void setSender(String sender) {
		this.sender = sender;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public byte[] getG711data() {
		return g711data;
	}
	public void setG711data(byte[] g711data) {
		this.g711data = g711data;
	}
	public AudioComeData(String senderId, String sender, int type, byte[] g711data) {
		super();
		this.senderId = senderId;
		this.sender = sender;
		this.type = type;
		this.g711data = g711data;
	}
	public AudioComeData() {
		super();
	}
	@Override
	public String toString() {
		return "AudioComeData [senderId=" + senderId + ", sender=" + sender + ", type=" + type + ", g711data="
				+ Arrays.toString(g711data) + "]";
	}
	
}
