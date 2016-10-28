package com.howell.protocol.entity;
/**
 * @author 霍之昊 
 *
 * 类说明:联动音频操作唯一标识符
 */
public class AudioPlayerIdentifier {
	String audioUrl;	//音频文件路径
	int repeatTimes;	//音频文件重复播放次数
	int duration;		//音频文件播放持续时间，单位：秒
	public AudioPlayerIdentifier(String audioUrl, int repeatTimes, int duration) {
		super();
		this.audioUrl = audioUrl;
		this.repeatTimes = repeatTimes;
		this.duration = duration;
	}
	public AudioPlayerIdentifier() {
		super();
	}
	public String getAudioUrl() {
		return audioUrl;
	}
	public void setAudioUrl(String audioUrl) {
		this.audioUrl = audioUrl;
	}
	public int getRepeatTimes() {
		return repeatTimes;
	}
	public void setRepeatTimes(int repeatTimes) {
		this.repeatTimes = repeatTimes;
	}
	public int getDuration() {
		return duration;
	}
	public void setDuration(int duration) {
		this.duration = duration;
	}

}
