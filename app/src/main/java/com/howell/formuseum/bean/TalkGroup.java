package com.howell.formuseum.bean;

import java.util.Arrays;

public class TalkGroup {
	String groupId;
	String groupName;
	String [] members;
	String owner;
	String creatTime;
	boolean isSilent;
	
	public TalkGroup() {
		// TODO Auto-generated constructor stub
	}
	
	public TalkGroup(String groupId, String groupName, String[] members, String owner, String creatTime,
			boolean isSilent) {
		super();
		this.groupId = groupId;
		this.groupName = groupName;
		this.members = members;
		this.owner = owner;
		this.creatTime = creatTime;
		this.isSilent = isSilent;
	}
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String[] getMembers() {
		return members;
	}
	public void setMembers(String[] members) {
		this.members = members;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public String getCreatTime() {
		return creatTime;
	}
	public void setCreatTime(String creatTime) {
		this.creatTime = creatTime;
	}
	public boolean isSilent() {
		return isSilent;
	}
	public void setSilent(boolean isSilent) {
		this.isSilent = isSilent;
	}
	@Override
	public String toString() {
		return "TalkGroup [groupId=" + groupId + ", groupName=" + groupName + ", members=" + Arrays.toString(members)
				+ ", owner=" + owner + ", creatTime=" + creatTime + ", isSilent=" + isSilent + "]";
	}
	
}
