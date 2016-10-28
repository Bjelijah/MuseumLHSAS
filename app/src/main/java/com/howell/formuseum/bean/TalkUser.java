package com.howell.formuseum.bean;

public class TalkUser {
	String userId;
	String userName;
	String nickName = null;
	boolean isOnline;
	boolean isSilent;
	
	public TalkUser() {
		// TODO Auto-generated constructor stub
	}
	
	public TalkUser(String userId, String userName, String nickName, boolean isOnline, boolean isSilent) {
		super();
		this.userId = userId;
		this.userName = userName;
		this.nickName = nickName;
		this.isOnline = isOnline;
		this.isSilent = isSilent;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public boolean isOnline() {
		return isOnline;
	}
	public void setOnline(boolean isOnline) {
		this.isOnline = isOnline;
	}
	public boolean isSilent() {
		return isSilent;
	}
	public void setSilent(boolean isSilent) {
		this.isSilent = isSilent;
	}
	@Override
	public String toString() {
		return "TalkUser [userId=" + userId + ", userName=" + userName + ", nickName=" + nickName + ", isOnline="
				+ isOnline + ", isSilent=" + isSilent + "]";
	}
	
}
