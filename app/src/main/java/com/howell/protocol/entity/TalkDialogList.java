package com.howell.protocol.entity;

import java.util.List;

public class TalkDialogList {
	int result;
	List<TalkDialog> dialogs;
	public int getResult() {
		return result;
	}
	public void setResult(int result) {
		this.result = result;
	}
	public List<TalkDialog> getDialogs() {
		return dialogs;
	}
	public void setDialogs(List<TalkDialog> dialogs) {
		this.dialogs = dialogs;
	}
	public TalkDialogList(int result, List<TalkDialog> dialogs) {
		super();
		this.result = result;
		this.dialogs = dialogs;
	}
	
}
