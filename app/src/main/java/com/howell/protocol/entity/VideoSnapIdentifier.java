package com.howell.protocol.entity;
/**
 * @author 霍之昊 
 *
 * 类说明:视频抓图操作唯一标识符
 */
public class VideoSnapIdentifier {
	String videoInputChannelId;
	int streamNo;
	String pictureFormat;
	public VideoSnapIdentifier(String videoInputChannelId, int streamNo,
			String pictureFormat) {
		super();
		this.videoInputChannelId = videoInputChannelId;
		this.streamNo = streamNo;
		this.pictureFormat = pictureFormat;
	}
	public VideoSnapIdentifier() {
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
	public String getPictureFormat() {
		return pictureFormat;
	}
	public void setPictureFormat(String pictureFormat) {
		this.pictureFormat = pictureFormat;
	}
	
	

}
