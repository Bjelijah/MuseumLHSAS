package com.howell.formuseum;

import android.util.Log;
	
/**
 * @author 霍之昊 
 *
 * 类说明
 */
@SuppressWarnings("JniMissingFunction")
public class JNIManager {
	
	private static JNIManager mInstance = null;
	public static JNIManager getInstance(){
		if (mInstance == null) {
			mInstance = new JNIManager();
		}
		return mInstance;
	}
	private JNIManager(){}

	static {
		System.loadLibrary("hwplay");
		System.loadLibrary("hw_jni");
		//System.loadLibrary("hwnet_jni");
       // System.loadLibrary("talk_jni");	
    }
	
	//语音对讲
	/*****new talk*******/
	public native void talkInit();
	public native void talkDeInit();
	public native boolean talkRegister2service(String name,String pwd,String id,int channel,String ip,int port);
	public native void talkUnregister();
	public native void talkSetCallbackObject(Object o,int flag);
	public native void talkSetCallbackMethodName(String str,int flag);
	public native void talkSetHeartBeat();
	public native int  talkGetRegisterState();
	public native void talkGetDialogList(int mobileType);
	public native void talkSetNextTarget(String [] dialogId,String [] userName);
	public native int setBoardcastData(byte[] buf,int len); 
	public native void talkSetSilent(String userName);
	public native void talkCreateGroup(String groupName,String [] memberNames,int memberLen);
	public native void talkDeleteGroup(String groupId);
	public native void talkUpdataGroup(String groupId,String groupName,String [] adds,String [] removes);
	public native void talkGetGroups(String userName);
	public native void talkGetGroup(String id);
	public native int setGroupData(String groupId,byte[] buf,int len);
	public native int setReceiveRes(int result,int seq);
	public native void talkSilentGroup(String groupId);
	public native void talkOnOfflineNotice(String dialogId,String userName,boolean isOnline);
	public native void talkGetUsers(String userName,int isOnline,int isSilent);//isOnline:-1 不启用
	public native void talkGetUser(String userName,String userId);
	public native int setReceiveGroup(int result,int seq);
	
	
	@Deprecated
	public native int register2service(String id,String local_phone,String name, String ip , short port);
	@Deprecated
	public native void unregister2service();
	@Deprecated
	public native int getRegisterState();	//0异常，1正常
	@Deprecated
	public native int setHeartBeat();
	@Deprecated
	public native int requestTalk();
	@Deprecated
	public native int getTalkState();		//1-正在等待服务器应答  2-服务器允许通话  3-服务器拒绝通话
	@Deprecated
	public native void stopTalk();
	public native int setData(byte[] buf, int len);
	public native int setAudioData(String data,int len);//整个json 字符串
	public native byte [] pcm2G711u(byte [] buf,int buflen,byte [] gBuf);
	public native byte [] g711u2Pcm(byte [] buf,int buflen,byte [] pcmBuf);
	public native void g711AudioPlay(byte [] buf,int len);
	public native void pcmAudioPlay(byte [] buf,int len);
	
	public void disConnect(){//FIXME
		Log.e("talk jni","talk disconnect with service");
	}
	
	//用于显示YUV数据
    public native void nativeInit();			//初始化
    public native void YUVSetCallbackObject(Object callbackObject,int flag);
    public native void nativeOnSurfaceCreated();//开始显示YUV数据
    public native void nativeDeinit();			//释放内存
    public native void nativeRenderY();			//渲染Y数据	
    public native void nativeRenderU();			//渲染U数据
    public native void nativeRenderV();			//渲染V数据
    
	
    //用于播放音频
    public native void nativeAudioInit();		//初始化
    public native void nativeAudioSetCallbackObject(Object o,int flag);
    public native void nativeAudioSetCallbackMethodName(String str,int flag);
    public native void nativeAudioBPlayable();
    public native void nativeAudioStop();		//停止
    public native void nativeAudioDeinit();		//释放内存
    
    
    
    
    //登录nvr 1:成功 0:失败
    public native int register(String ip);
    
    //登出nvr
    public native void unregister();
    
    //关闭码流
    public native void stopPlay(int is_playback);
    
    //播放回放
    public native int display(int isPlayBack,short begYear,short begMonth,short begDay,short begHour
    		,short begMinute,short begSecond,short endYear,short endMonth,short endDay,short endHour,short endMinute
    		,short endSecond,int slot);
    
}
