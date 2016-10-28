package com.howell.formuseum.bean;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

public class TalkUserInfo {
	TalkUser user;
	List<TalkDialog> userDialogList = new ArrayList<TalkDialog>();
	public TalkUserInfo() {
		// TODO Auto-generated constructor stub
	}
	public TalkUser getUser() {
		return user;
	}
	public void setUser(TalkUser user) {
		this.user = user;
	}
	public List<TalkDialog> getUserDialogList() {
		return userDialogList;
	}
	public void setUserDialogList(List<TalkDialog> userDialogList) {
		this.userDialogList = userDialogList;
	}
	public void addUserDialog(TalkDialog t){
		userDialogList.add(t);
	}
	public boolean isBelong(TalkDialog t){
		if(user==null){
			return false;
		}
		Log.i("123", "isbelong  usr name:"+user.getUserName()+" dialog name:"+t.getDialogName());
		
		if(user.getUserName().equals(t.getDialogName())){
			Log.e("123", "equals  "+user.getUserName());
			return true;
		}
		return false;
	}
	@Override
	public String toString() {
		return "TalkUserInfo [user=" + user + ", userDialogList=" + userDialogList + "]";
	}
	
	
}
