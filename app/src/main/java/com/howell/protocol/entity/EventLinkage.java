package com.howell.protocol.entity;

import java.util.ArrayList;

/**
 * @author 霍之昊 
 *
 * 类说明:事件联动
 */
public class EventLinkage {
	String componentId;											//组件唯一标识符
	String eventType;											//事件类型
	String eventState;											//事件状态
	ArrayList<VideoPreviewIdentifier> videoPreviewIdentifier;	//视频预览操作唯一标识符
	ArrayList<VideoPlaybackIdentifier> videoPlaybackIdentifier;	//视频回放操作唯一标识符
	TextIdentifier textIdentifier;								//联动显示信息
	AudioPlayerIdentifier audioPlayerIdentifier;				//联动音频
	ArrayList<VideoSnapIdentifier> videoSnapIdentifier;			//视频抓图操作唯一标识符
	ArrayList<String> executor;									//执行者
	public EventLinkage(String componentId, String eventType,
			String eventState,
			ArrayList<VideoPreviewIdentifier> videoPreviewIdentifier,
			ArrayList<VideoPlaybackIdentifier> videoPlaybackIdentifier,
			TextIdentifier textIdentifier,
			AudioPlayerIdentifier audioPlayerIdentifier,
			ArrayList<VideoSnapIdentifier> videoSnapIdentifier,
			ArrayList<String> executor) {
		super();
		this.componentId = componentId;
		this.eventType = eventType;
		this.eventState = eventState;
		this.videoPreviewIdentifier = videoPreviewIdentifier;
		this.videoPlaybackIdentifier = videoPlaybackIdentifier;
		this.textIdentifier = textIdentifier;
		this.audioPlayerIdentifier = audioPlayerIdentifier;
		this.videoSnapIdentifier = videoSnapIdentifier;
		this.executor = executor;
	}
	

	public EventLinkage(String componentId, String eventType,
			String eventState,
			ArrayList<VideoPreviewIdentifier> videoPreviewIdentifier) {
		super();
		this.componentId = componentId;
		this.eventType = eventType;
		this.eventState = eventState;
		this.videoPreviewIdentifier = videoPreviewIdentifier;
	}
	public EventLinkage(String componentId,String eventType,String eventState,
			ArrayList<VideoPreviewIdentifier> videoPreviewIdentifier,
			ArrayList<VideoPlaybackIdentifier> videoPlayBackIdentifier){
		super();
		this.componentId = componentId;
		this.eventType = eventType;
		this.eventState = eventState;
		this.videoPreviewIdentifier = videoPreviewIdentifier;
		this.videoPlaybackIdentifier = videoPlayBackIdentifier;
	}

	public EventLinkage() {
		super();
	}
	public String getComponentId() {
		return componentId;
	}
	public void setComponentId(String componentId) {
		this.componentId = componentId;
	}
	public String getEventType() {
		return eventType;
	}
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}
	public String getEventState() {
		return eventState;
	}
	public void setEventState(String eventState) {
		this.eventState = eventState;
	}
	public ArrayList<VideoPreviewIdentifier> getVideoPreviewIdentifier() {
		return videoPreviewIdentifier;
	}
	public void setVideoPreviewIdentifier(
			ArrayList<VideoPreviewIdentifier> videoPreviewIdentifier) {
		this.videoPreviewIdentifier = videoPreviewIdentifier;
	}
	public ArrayList<VideoPlaybackIdentifier> getVideoPlaybackIdentifier() {
		return videoPlaybackIdentifier;
	}
	public void setVideoPlaybackIdentifier(
			ArrayList<VideoPlaybackIdentifier> videoPlaybackIdentifier) {
		this.videoPlaybackIdentifier = videoPlaybackIdentifier;
	}
	public TextIdentifier getTextIdentifier() {
		return textIdentifier;
	}
	public void setTextIdentifier(TextIdentifier textIdentifier) {
		this.textIdentifier = textIdentifier;
	}
	public AudioPlayerIdentifier getAudioPlayerIdentifier() {
		return audioPlayerIdentifier;
	}
	public void setAudioPlayerIdentifier(AudioPlayerIdentifier audioPlayerIdentifier) {
		this.audioPlayerIdentifier = audioPlayerIdentifier;
	}
	public ArrayList<VideoSnapIdentifier> getVideoSnapIdentifier() {
		return videoSnapIdentifier;
	}
	public void setVideoSnapIdentifier(
			ArrayList<VideoSnapIdentifier> videoSnapIdentifier) {
		this.videoSnapIdentifier = videoSnapIdentifier;
	}
	public ArrayList<String> getExecutor() {
		return executor;
	}
	public void setExecutor(ArrayList<String> executor) {
		this.executor = executor;
	}
	

}
