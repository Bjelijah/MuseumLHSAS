package com.howell.protocol.entity;
/**
 * @author 霍之昊 
 *
 * 类说明
 */
public class Device {
	private String id;
	private String authenticationCode;

	private String name;
	private String manufacturer;
	private String model;
	private String firmware;
	private String serialNumber;
	private String pointOfSale;
	private String information;
	private String username;
	private String password;
	private String uri;
	private String classification;
	private String protocolType;
	private String parentDeviceId;
	private int busAddress;
	private boolean hasSubDevice;

	private String abilities;
	private Double ratedVoltage;
	private int maximumUserConnectionsNumber;
	private int maximumVideoConnectionsNumber;
	private boolean existedInDatabase;
	public Device(String id, String authenticationCode, String name,
			String manufacturer, String model, String firmware,
			String serialNumber, String pointOfSale, String information,
			String username, String password, String uri,
			String classification, String protocolType, String parentDeviceId,
			int busAddress, boolean hasSubDevice, String abilities,
			Double ratedVoltage, int maximumUserConnectionsNumber,
			int maximumVideoConnectionsNumber, boolean existedInDatabase) {
		super();
		this.id = id;
		this.authenticationCode = authenticationCode;
		this.name = name;
		this.manufacturer = manufacturer;
		this.model = model;
		this.firmware = firmware;
		this.serialNumber = serialNumber;
		this.pointOfSale = pointOfSale;
		this.information = information;
		this.username = username;
		this.password = password;
		this.uri = uri;
		this.classification = classification;
		this.protocolType = protocolType;
		this.parentDeviceId = parentDeviceId;
		this.busAddress = busAddress;
		this.hasSubDevice = hasSubDevice;
		this.abilities = abilities;
		this.ratedVoltage = ratedVoltage;
		this.maximumUserConnectionsNumber = maximumUserConnectionsNumber;
		this.maximumVideoConnectionsNumber = maximumVideoConnectionsNumber;
		this.existedInDatabase = existedInDatabase;
	}
	
	public Device(String name, String uri) {
		super();
		this.name = name;
		this.uri = uri;
	}

	public Device() {
		super();
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getAuthenticationCode() {
		return authenticationCode;
	}
	public void setAuthenticationCode(String authenticationCode) {
		this.authenticationCode = authenticationCode;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getManufacturer() {
		return manufacturer;
	}
	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public String getFirmware() {
		return firmware;
	}
	public void setFirmware(String firmware) {
		this.firmware = firmware;
	}
	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	public String getPointOfSale() {
		return pointOfSale;
	}
	public void setPointOfSale(String pointOfSale) {
		this.pointOfSale = pointOfSale;
	}
	public String getInformation() {
		return information;
	}
	public void setInformation(String information) {
		this.information = information;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public String getClassification() {
		return classification;
	}
	public void setClassification(String classification) {
		this.classification = classification;
	}
	public String getProtocolType() {
		return protocolType;
	}
	public void setProtocolType(String protocolType) {
		this.protocolType = protocolType;
	}
	public String getParentDeviceId() {
		return parentDeviceId;
	}
	public void setParentDeviceId(String parentDeviceId) {
		this.parentDeviceId = parentDeviceId;
	}
	public int getBusAddress() {
		return busAddress;
	}
	public void setBusAddress(int busAddress) {
		this.busAddress = busAddress;
	}
	public boolean isHasSubDevice() {
		return hasSubDevice;
	}
	public void setHasSubDevice(boolean hasSubDevice) {
		this.hasSubDevice = hasSubDevice;
	}
	public String getAbilities() {
		return abilities;
	}
	public void setAbilities(String abilities) {
		this.abilities = abilities;
	}
	public Double getRatedVoltage() {
		return ratedVoltage;
	}
	public void setRatedVoltage(Double ratedVoltage) {
		this.ratedVoltage = ratedVoltage;
	}
	public int getMaximumUserConnectionsNumber() {
		return maximumUserConnectionsNumber;
	}
	public void setMaximumUserConnectionsNumber(int maximumUserConnectionsNumber) {
		this.maximumUserConnectionsNumber = maximumUserConnectionsNumber;
	}
	public int getMaximumVideoConnectionsNumber() {
		return maximumVideoConnectionsNumber;
	}
	public void setMaximumVideoConnectionsNumber(int maximumVideoConnectionsNumber) {
		this.maximumVideoConnectionsNumber = maximumVideoConnectionsNumber;
	}
	public boolean isExistedInDatabase() {
		return existedInDatabase;
	}
	public void setExistedInDatabase(boolean existedInDatabase) {
		this.existedInDatabase = existedInDatabase;
	}
	
	

}
