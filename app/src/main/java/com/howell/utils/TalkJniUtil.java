package com.howell.utils;



@SuppressWarnings("JniMissingFunction")
public class TalkJniUtil {
	public static native void talkInit();
	public static native void talkDeInit();
	public static native void setCallbackObj(Object o,int flag);
	public static native void setCallbackMethodName(String methodStr,int flag);
	
	public static native boolean register2svr(String name,String pwd,String id,int channel,String ip,int port);
	
	
}
