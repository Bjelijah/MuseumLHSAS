package com.howell.protocol.entity;

public class TalkDialog {
	String dialogId;
	String userName;
	String mobileId;
	int mobileType;
	public String getDialogId() {
		return dialogId;
	}
	public void setDialogId(String dialogId) {
		this.dialogId = dialogId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getMobileId() {
		return mobileId;
	}
	public void setMobileId(String mobileId) {
		this.mobileId = mobileId;
	}
	public int getMobileType() {
		return mobileType;
	}
	public void setMobileType(int mobileType) {
		this.mobileType = mobileType;
	}
	public TalkDialog(String dialogId, String userName, String mobileId, int mobileType) {
		super();
		this.dialogId = dialogId;
		this.userName = userName;
		this.mobileId = mobileId;
		this.mobileType = mobileType;
	}
	
}
