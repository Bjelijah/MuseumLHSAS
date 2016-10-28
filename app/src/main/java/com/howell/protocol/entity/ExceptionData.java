package com.howell.protocol.entity;
/**
 * @author 霍之昊 
 *
 * 类说明
 */
public class ExceptionData {
	private String message;
	private String exceptionType;
	public ExceptionData(String message, String exceptionType) {
		super();
		this.message = message;
		this.exceptionType = exceptionType;
	}
	public ExceptionData() {
		super();
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getExceptionType() {
		return exceptionType;
	}
	public void setExceptionType(String exceptionType) {
		this.exceptionType = exceptionType;
	}

}
