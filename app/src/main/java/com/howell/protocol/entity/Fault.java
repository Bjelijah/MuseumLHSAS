package com.howell.protocol.entity;
/**
 * @author 霍之昊 
 *
 * 类说明
 */
public class Fault {
	private int faultCode;
	private String faultReason;
	private ExceptionData exception;
	private String id;
	public Fault(int faultCode, String faultReason, ExceptionData exception,
			String id) {
		super();
		this.faultCode = faultCode;
		this.faultReason = faultReason;
		this.exception = exception;
		this.id = id;
	}
	public Fault() {
		super();
	}
	public int getFaultCode() {
		return faultCode;
	}
	public void setFaultCode(int faultCode) {
		this.faultCode = faultCode;
	}
	public String getFaultReason() {
		return faultReason;
	}
	public void setFaultReason(String faultReason) {
		this.faultReason = faultReason;
	}
	public ExceptionData getException() {
		return exception;
	}
	public void setException(ExceptionData exception) {
		this.exception = exception;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	@Override
	public String toString() {
		return "Fault [faultCode=" + faultCode + ", faultReason=" + faultReason
				+ ", exception=" + exception + ", id=" + id + "]";
	}
	

}
