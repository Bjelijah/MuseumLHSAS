package com.howell.protocol.entity;
/**
 * @author 霍之昊 
 *
 * 类说明:文本信息唯一标识符
 */
public class TextIdentifier {
	String text;		//文本信息
	int duration;		//持续时间(单位:秒)，0表示始终显示
	Font font;			//显示文本字体
	public TextIdentifier(String text, int duration, Font font) {
		super();
		this.text = text;
		this.duration = duration;
		this.font = font;
	}
	public TextIdentifier() {
		super();
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public int getDuration() {
		return duration;
	}
	public void setDuration(int duration) {
		this.duration = duration;
	}
	public Font getFont() {
		return font;
	}
	public void setFont(Font font) {
		this.font = font;
	}

}
