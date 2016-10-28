package com.howell.protocol.entity;

import java.util.ArrayList;

/**
 * @author 霍之昊 
 *
 * 类说明
 */
public class DevicePermissionList {
	private Page page;
	ArrayList<DevicePermission> devicePermission;
	public DevicePermissionList(Page page,
			ArrayList<DevicePermission> devicePermission) {
		super();
		this.page = page;
		this.devicePermission = devicePermission;
	}
	public DevicePermissionList() {
		super();
	}
	public Page getPage() {
		return page;
	}
	public void setPage(Page page) {
		this.page = page;
	}
	public ArrayList<DevicePermission> getDevicePermission() {
		return devicePermission;
	}
	public void setDevicePermission(ArrayList<DevicePermission> devicePermission) {
		this.devicePermission = devicePermission;
	}
	
	
}
