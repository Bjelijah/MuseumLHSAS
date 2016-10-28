package com.howell.formuseum.bean;

public class TalkDialog {
	private String dialogId;
	private String userName;//用户名
	private String mobileId;
	private int mobileType;
	
	
	//
	private boolean isSelNextTarget = false;
	
	
	public TalkDialog(String id,String name,String mobile,int type) {
		this.dialogId = id;
		this.userName = name;
		this.mobileId = mobile;
		this.mobileType=type;
	}
	public TalkDialog() {
		// TODO Auto-generated constructor stub
	}
	
	public String getDialogId() {
		return dialogId;
	}
	public void setDialogId(String dialogId) {
		this.dialogId = dialogId;
	}
	public String getDialogName() {
		return userName;
	}
	public void setDialogName(String dialogName) {
		this.userName = dialogName;
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
	@Override
	public String toString() {
		return "TalkDialog [dialogId=" + dialogId + ", dialogName=" + userName + ", mobileId=" + mobileId
				+ ", mobileType=" + mobileType + "]";
	}
	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		if (o instanceof TalkDialog) {
			return this.userName.equals(((TalkDialog) o).userName);
		}
		return super.equals(o);
	}
	
	
	
}
