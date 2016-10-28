package com.howell.protocol.entity;

public class TalkAlive {
	String dialogId;

	public String getDialogId() {
		return dialogId;
	}

	public void setDialogId(String dialogId) {
		this.dialogId = dialogId;
	}

	public TalkAlive(String dialogId) {
		super();
		this.dialogId = dialogId;
	}
	
}
