package com.howell.protocol.entity;
/**
 * @author 霍之昊 
 *
 * 类说明:字体
 */
public class Font {
	Double fontSize;	//字体大小
	int fontColor;		//字体颜色 ARGB格式
	String fontFamily;	//字体
	boolean bold;		//粗体
	public Font(Double fontSize, int fontColor, String fontFamily, boolean bold) {
		super();
		this.fontSize = fontSize;
		this.fontColor = fontColor;
		this.fontFamily = fontFamily;
		this.bold = bold;
	}
	public Font() {
		super();
	}
	public Double getFontSize() {
		return fontSize;
	}
	public void setFontSize(Double fontSize) {
		this.fontSize = fontSize;
	}
	public int getFontColor() {
		return fontColor;
	}
	public void setFontColor(int fontColor) {
		this.fontColor = fontColor;
	}
	public String getFontFamily() {
		return fontFamily;
	}
	public void setFontFamily(String fontFamily) {
		this.fontFamily = fontFamily;
	}
	public boolean isBold() {
		return bold;
	}
	public void setBold(boolean bold) {
		this.bold = bold;
	}
	
	

}
