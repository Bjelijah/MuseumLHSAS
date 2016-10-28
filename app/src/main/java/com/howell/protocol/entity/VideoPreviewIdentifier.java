package com.howell.protocol.entity;
/**
 * @author 霍之昊 
 *
 * 类说明:视频预览操作唯一标识符
 */
public class VideoPreviewIdentifier {
	private String videoInputChannelId;//视频输入通道唯一标识符
	private int streamNo	;		//视频流编号 1-n
	private String protocol	;		//视频流连接协议
	public VideoPreviewIdentifier(String videoInputChannelId, int streamNo,
			String protocol) {
		super();
		this.videoInputChannelId = videoInputChannelId;
		this.streamNo = streamNo;
		this.protocol = protocol;
	}
	
	
	public VideoPreviewIdentifier(String videoInputChannelId, int streamNo) {
		super();
		this.videoInputChannelId = videoInputChannelId;
		this.streamNo = streamNo;
	}


	public VideoPreviewIdentifier() {
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
	
}
