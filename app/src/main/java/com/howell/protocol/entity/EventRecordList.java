package com.howell.protocol.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;



/**
 * 
 * @author cbj
 *
 */
public class EventRecordList implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	Page page;
	
	ArrayList<EventRecord> eventRecords; 
	
	public EventRecordList() {
		this.page = new Page();
		this.eventRecords = new ArrayList<EventRecord>();
	}
	
	public void addEventRecords(EventRecord e){
		eventRecords.add(e);
	}
	
	public Page getPage() {
		return page;
	}

	public void setPage(Page page) {
		this.page = page;
	}

	public ArrayList<EventRecord> getEventRecords() {
		return eventRecords;
	}

	public void setEventRecords(ArrayList<EventRecord> eventRecords) {
		this.eventRecords = eventRecords;
	}

	public static class Page{
		int PageIndex;
		int PageSize;
		int PageCount;
		int RecordCount;
		int TotalRecordCount;
		public int getPageIndex() {
			return PageIndex;
		}
		public Page setPageIndex(int pageIndex) {
			PageIndex = pageIndex;
			return this;
		}
		public int getPageSize() {
			return PageSize;
		}
		public Page setPageSize(int pageSize) {
			PageSize = pageSize;
			return this;
		}
		public int getPageCount() {
			return PageCount;
		}
		public Page setPageCount(int pageCount) {
			PageCount = pageCount;
			return this;
		}
		public int getRecordCount() {
			return RecordCount;
		}
		public Page setRecordCount(int recordCount) {
			RecordCount = recordCount;
			return this;
		}
		public int getTotalRecordCount() {
			return TotalRecordCount;
		}
		public Page setTotalRecordCount(int totalRecordCount) {
			TotalRecordCount = totalRecordCount;
			return this;
		}
		@Override
		public String toString() {
			return "Page [PageIndex=" + PageIndex + ", PageSize=" + PageSize + ", PageCount=" + PageCount
					+ ", RecordCount=" + RecordCount + ", TotalRecordCount=" + TotalRecordCount + "]";
		}
		
	}
	
	public static class EventRecord{
		String id;//事件唯一标识符（设备产生的）
		String componentId;
		String name;
		String eventType;
		String alarmTime;
		int severity;//重要级别
		String disalarmTime;
		String processTime;
		String proecssDescription;
		String description;
		int objectType;
		double triggerVale;//触发事件时的数值 
		String [] pictrueId = null;
		ArrayList<EventRecordedFile> eventRecordedFiles = null;

		public EventRecord() {
		
		}
		
		public void addEventRecordedFile(EventRecordedFile e){
			if (eventRecordedFiles == null) {
				eventRecordedFiles = new ArrayList<EventRecordedFile>();
			}
			eventRecordedFiles.add(e);
		}
		
		public String getId() {
			return id;
		}
		public EventRecord setId(String id) {
			this.id = id;
			return this;
		}
		public String getComponentId() {
			return componentId;
		}
		public EventRecord setComponentId(String componentId) {
			this.componentId = componentId;
			return this;
		}
		public String getName() {
			return name;
		}
		public EventRecord setName(String name) {
			this.name = name;
			return this;
		}
		public String getEventType() {
			return eventType;
		}
		public EventRecord setEventType(String eventType) {
			this.eventType = eventType;
			return this;
		}
		public String getAlarmTime() {
			return alarmTime;
		}
		public EventRecord setAlarmTime(String alarmTime) {
			this.alarmTime = alarmTime;
			return this;
		}
		public int getSeverity() {
			return severity;
		}
		public EventRecord setSeverity(int severity) {
			this.severity = severity;
			return this;
		}
		public String getDisalarmTime() {
			return disalarmTime;
		}
		public EventRecord setDisalarmTime(String disalarmTime) {
			this.disalarmTime = disalarmTime;
			return this;
		}
		public String getProcessTime() {
			return processTime;
		}
		public EventRecord setProcessTime(String processTime) {
			this.processTime = processTime;
			return this;
		}
		public String getProecssDescription() {
			return proecssDescription;
		}
		public EventRecord setProecssDescription(String proecssDescription) {
			this.proecssDescription = proecssDescription;
			return this;
		}
		public String getDescription() {
			return description;
		}
		public EventRecord setDescription(String description) {
			this.description = description;
			return this;
		}
		public int getObjectType() {
			return objectType;
		}
		public EventRecord setObjectType(int objectType) {
			this.objectType = objectType;
			return this;
		}
		public double getTriggerVale() {
			return triggerVale;
		}
		public EventRecord setTriggerVale(double triggerVale) {
			this.triggerVale = triggerVale;
			return this;
		}
		public String[] getPictrueId() {
			return pictrueId;
		}
		public EventRecord setPictrueId(String[] pictrueId) {
			this.pictrueId = pictrueId;
			return this;
		}
		public ArrayList<EventRecordedFile> getEventRecordedFiles() {
			return eventRecordedFiles;
		}
		public EventRecord setEventRecordedFiles(ArrayList<EventRecordedFile> eventRecordedFiles) {
			this.eventRecordedFiles = eventRecordedFiles;
			return this;
		}
		
		public static class EventRecordedFile{
			String recordeFileID;//录像文件唯一标识符
			long recordedFileTimestamp;
			public String getRecordeFileID() {
				return recordeFileID;
			}
			public EventRecordedFile setRecordeFileID(String recordeFileID) {
				this.recordeFileID = recordeFileID;
				return this;
			}
			public long getRecordedFileTimestamp() {
				return recordedFileTimestamp;
			}
			public EventRecordedFile setRecordedFileTimestamp(long recordedFileTimestamp) {
				this.recordedFileTimestamp = recordedFileTimestamp;
				return this;
			}
		}

		@Override
		public String toString() {
			return "EventRecord [id=" + id + ", componentId=" + componentId + ", name=" + name + ", eventType="
					+ eventType + ", alarmTime=" + alarmTime + ", severity=" + severity + ", disalarmTime="
					+ disalarmTime + ", processTime=" + processTime + ", proecssDescription=" + proecssDescription
					+ ", description=" + description + ", objectType=" + objectType + ", triggerVale=" + triggerVale
					+ ", pictrueId=" + Arrays.toString(pictrueId) + ", eventRecordedFiles=" + eventRecordedFiles + "]";
		}
		
	}
	@Override
	public String toString() {
		return "EventRecordList [page=" + page.toString() + ", eventRecords=" + eventRecords.toString() + "]";
	}	
}
