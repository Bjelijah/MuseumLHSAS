package com.howell.formusemu.action;

import com.howell.formuseum.LogoActivity;
import com.howell.formuseum.MapListActivity;
import com.howell.utils.MD5;
import com.howell.utils.SharedPreferencesUtils;

import android.content.Context;
import android.content.Intent;

public class LoginAction {
	private static LoginAction mInstance = null;
	public static LoginAction getInstance(){
		if (mInstance==null) {
			mInstance = new LoginAction();
		}
		return mInstance;
	}
	private LoginAction(){}
	private String name = null;//用户名
	private String pwd = null;//密码
	private String  session = null;//用户名
	private String cookieHalf = null;
	private String account = null;
	private String webserviceIp = null;
	private String verify = null;
	private String mac = null;
	private String uuid = null;
	
	public String getName() {
		return name;
	}
	public LoginAction setName(String name) {
		this.name = name;
		return this;
	}
	public String getPwd() {
		return pwd;
	}
	public LoginAction setPwd(String pwd) {
		this.pwd = pwd;
		return this;
	}
	public String getSession() {
		return session;
	}
	public LoginAction setSession(String session) {
		this.session = session;
		return this;
	}
	public String getCookieHalf() {
		return cookieHalf;
	}
	public LoginAction setCookieHalf(String cookieHalf) {
		this.cookieHalf = cookieHalf;
		return this;
	}
	public String getAccount() {
		return account;
	}
	public LoginAction setAccount(String account) {
		this.account = account;
		return this;
	}
	public String getWebserviceIp() {
		return webserviceIp;
	}
	public LoginAction setWebserviceIp(String webserviceIp) {
		this.webserviceIp = webserviceIp;
		return this;
	}
	public String getVerify() {
		return verify;
	}
	public void setVerify(String verify) {
		this.verify = verify;
	}
	public String getMac() {
		return mac;
	}
	public LoginAction setMac(String mac) {
		this.mac = mac;
		return this;
	}
	public String getUuid() {
		return uuid;
	}
	public LoginAction setUuid(String uuid) {
		this.uuid = uuid;
		return this;
	}
	
	public void saveTalkInfo(Context context){
		SharedPreferencesUtils.saveLoginTalkInfo(context, name, pwd, webserviceIp,uuid,mac);
	}
	
	public LoginAction loadTalkInfo(Context context){
		String _name = SharedPreferencesUtils.getTalkUserName(context);
		String _pwd = SharedPreferencesUtils.getTalkUserPwd(context);
		String _webserviceIp = SharedPreferencesUtils.getTalkIp(context);
		String _uuid = SharedPreferencesUtils.getTalkUuid(context);
		String _mac = SharedPreferencesUtils.getTalkMac(context);
		if(!_name.equals("")){
			this.name = _name;
		}
		if(!_pwd.equals("")){
			this.pwd = _pwd;
		}
		if(!_webserviceIp.equals("")){
			this.webserviceIp = _webserviceIp;
		}
		if(!_uuid.equals("")){
			this.uuid = _uuid;
		}
		if(!_mac.equals("")){
			this.mac = _mac;
		}
		return this;
	}
	
	public boolean login(Context context){
		if (session == null || cookieHalf == null || account==null || webserviceIp==null || verify == null) {
			return false;
		}
		Intent intent = new Intent(context,MapListActivity.class);
		intent.putExtra("session", session);
		intent.putExtra("cookieHalf", cookieHalf);
		intent.putExtra("webserviceIp", webserviceIp);
		intent.putExtra("account", account);
		intent.putExtra("verify",verify);
		
		context.startActivity(intent);
		return true;
	}
}
