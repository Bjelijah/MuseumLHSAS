package com.howell.protocol.entity;
/**
 * @author 霍之昊 
 *
 * 类说明
 */
public class ClientCredential {
	private String userName;
	private String nonce;
	private String domain;
	private String clientNonce;
	private String verifySession;
	private String physicalAddress;
	public ClientCredential(String userName, String nonce, String domain,
			String clientNonce, String verifySession) {
		super();
		this.userName = userName;
		this.nonce = nonce;
		this.domain = domain;
		this.clientNonce = clientNonce;
		this.verifySession = verifySession;
	}
	public ClientCredential() {
		super();
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
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
	public String getClientNonce() {
		return clientNonce;
	}
	public void setClientNonce(String clientNonce) {
		this.clientNonce = clientNonce;
	}
	public String getVerifySession() {
		return verifySession;
	}
	public void setVerifySession(String verifySession) {
		this.verifySession = verifySession;
	}
	public String getPhysicalAddress() {
		return physicalAddress;
	}
	public void setPhysicalAddress(String physicalAddress) {
		this.physicalAddress = physicalAddress;
	}

}
