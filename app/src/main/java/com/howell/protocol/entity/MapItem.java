package com.howell.protocol.entity;
/**
 * @author 霍之昊 
 *
 * 类说明
 */
public class MapItem {
	private String id;
	private String itemType;
	private String componentId;
	private Coordinate coordinate;
	private Double angle;
	private String mapId;
	public MapItem(String id, String itemType, String componentId,
			Coordinate coordinate, Double angle) {
		super();
		this.id = id;
		this.itemType = itemType;
		this.componentId = componentId;
		this.coordinate = coordinate;
		this.angle = angle;
	}
	public MapItem() {
		super();
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getItemType() {
		return itemType;
	}
	public void setItemType(String itemType) {
		this.itemType = itemType;
	}
	public String getComponentId() {
		return componentId;
	}
	public void setComponentId(String componentId) {
		this.componentId = componentId;
	}
	public Coordinate getCoordinate() {
		return coordinate;
	}
	public void setCoordinate(Coordinate coordinate) {
		this.coordinate = coordinate;
	}
	public Double getAngle() {
		return angle;
	}
	public void setAngle(Double angle) {
		this.angle = angle;
	}
	public String getMapId() {
		return mapId;
	}
	public void setMapId(String mapId) {
		this.mapId = mapId;
	}
	@Override
	public String toString() {
		return "MapItem [id=" + id + ", itemType=" + itemType
				+ ", componentId=" + componentId + ", coordinate=" + coordinate
				+ ", angle=" + angle + ", mapId=" + mapId + "]";
	}

}
