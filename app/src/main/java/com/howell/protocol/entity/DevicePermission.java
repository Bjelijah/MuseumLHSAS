package com.howell.protocol.entity;
/**
 * @author 霍之昊 
 *
 * 类说明
 */
public class DevicePermission {
	private String id;
	private String name;
	private String permission;
	private Device device;
	private boolean isFromDepartment;
	public DevicePermission(String id, String name, String permission,
			Device device, boolean isFromDepartment) {
		super();
		this.id = id;
		this.name = name;
		this.permission = permission;
		this.device = device;
		this.isFromDepartment = isFromDepartment;
	}
	public DevicePermission() {
		super();
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPermission() {
		return permission;
	}
	public void setPermission(String permission) {
		this.permission = permission;
	}
	public Device getDevice() {
		return device;
	}
	public void setDevice(Device device) {
		this.device = device;
	}
	public boolean isFromDepartment() {
		return isFromDepartment;
	}
	public void setFromDepartment(boolean isFromDepartment) {
		this.isFromDepartment = isFromDepartment;
	}
	
	

}
