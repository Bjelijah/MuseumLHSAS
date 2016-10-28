package com.howell.protocol.entity;
/**
 * @author 霍之昊 
 *
 * 类说明:视频回放操作唯一标识符
 */
public class VideoPlaybackIdentifier {
	String videoInputChannelId;	//视频输入通道唯一标识符
	int streamNo;				//视频流编号 1-n
	String protocol;			//视频流连接协议
	int beginTime;				//开始时间，正数表示触发时间往后多少单位时间，负数表示触发事件往前多少单位时间,(单位：秒) ，如果该值为null或0则表示报警时间开始回放
	int endTime;				//结束时间，正数表示触发时间往后多少单位时间，负数表示触发事件往前多少单位时间,(单位：秒) ，如果该值为null则表示回放到自动结束
	public VideoPlaybackIdentifier(String videoInputChannelId, int streamNo,
			String protocol, int beginTime, int endTime) {
		super();
		this.videoInputChannelId = videoInputChannelId;
		this.streamNo = streamNo;
		this.protocol = protocol;
		this.beginTime = beginTime;
		this.endTime = endTime;
	}
	public VideoPlaybackIdentifier() {
		super();
	}
	public String getVideoInputChannelId() {
		return videoInputChannelId;
	}
	public void setVideoInputChannelId(String videoInputChannelId) {
		this.videoInputChannelId = videoInputChannelId;
	}
	public int getStreamNo() {
		return streamNo;
	}
	public void setStreamNo(int streamNo) {
		this.streamNo = streamNo;
	}
	public String getProtocol() {
		return protocol;
	}
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	public int getBeginTime() {
		return beginTime;
	}
	public void setBeginTime(int beginTime) {
		this.beginTime = beginTime;
	}
	public int getEndTime() {
		return endTime;
	}
	public void setEndTime(int endTime) {
		this.endTime = endTime;
	}
	
}
