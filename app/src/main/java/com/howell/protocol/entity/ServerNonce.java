package com.howell.protocol.entity;
/**
 * @author 霍之昊 
 *
 * 类说明
 */
public class ServerNonce {
	private String nonce;
	private String domain;
	public ServerNonce(String nonce, String domain) {
		super();
		this.nonce = nonce;
		this.domain = domain;
	}
	public ServerNonce() {
		super();
	}
	public String getNonce() {
		return nonce;
	}
	public void setNonce(String nonce) {
		this.nonce = nonce;
	}
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	

}
