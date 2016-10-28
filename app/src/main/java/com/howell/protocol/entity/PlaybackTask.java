package com.howell.protocol.entity;
/**
 * @author 霍之昊 
 *
 * 类说明
 */
public class PlaybackTask {
	String taskId;					//任务唯一标识符
	String videoInputChannelId;		//回放访问的视频输入通道唯一标识符
	String url;						//预览访问地址(NVR_IP)
	String protocol;				//协议类型 Howell,RTSP…
	String sDP;						//SDP描述信息
	public PlaybackTask(String taskId, String videoInputChannelId, String url,
			String protocol, String sDP) {
		super();
		this.taskId = taskId;
		this.videoInputChannelId = videoInputChannelId;
		this.url = url;
		this.protocol = protocol;
		this.sDP = sDP;
	}
	public PlaybackTask() {
		super();
	}
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	public String getVideoInputChannelId() {
		return videoInputChannelId;
	}
	public void setVideoInputChannelId(String videoInputChannelId) {
		this.videoInputChannelId = videoInputChannelId;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getProtocol() {
		return protocol;
	}
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	public String getsDP() {
		return sDP;
	}
	public void setsDP(String sDP) {
		this.sDP = sDP;
	}

}
