/*
 * talk_protocol_jni.cpp
 *
 *  Created on: 2016年6月7日
 *      Author: howell
 */



#include <jni.h>
#include <android/log.h>
#include <stdlib.h>
#include "include/voice_test/voice_test.h"
#include "include/voice_test/hwvoice_msg.h"
#include "com_howell_utils_TalkJniUtil.h"


#define LOGI(...) ((void)__android_log_print(ANDROID_LOG_INFO, "talk_jni", __VA_ARGS__))
#define LOGW(...) ((void)__android_log_print(ANDROID_LOG_WARN, "talk_jni", __VA_ARGS__))
#define LOGE(...) ((void)__android_log_print(ANDROID_LOG_ERROR, "talk_jni", __VA_ARGS__))


using namespace hwvoice;

typedef struct{
	JavaVM * jvm;
	JNIEnv * env;
	jobject voice_callback_obj,res_callback_obj;

}T_TalkMgr;
static T_TalkMgr g_talkMgr;

int on_talk_voice_fun(int voice_type,const char* buf,int len){
	//__android_log_print(ANDROID_LOG_INFO, "jni", "on_voice_fun voice_type:%d len:%d",voice_type,len);
	//audio_play(buf,len);
	return 0;
}

int on_talk_res_fun(int msgCommand,void * res,int len){

	return 0;
}


static void register2svr(const char *userName,const char *password,const char *mobileId,int channel,
		const char *ip,int port){
	voice_register2svr(userName,password,mobileId,0,channel,0,0,ip,port,on_talk_voice_fun,on_talk_res_fun);
}








JNIEXPORT void JNICALL Java_com_howell_utils_TalkJniUtil_talkInit
  (JNIEnv *, jclass){
	LOGI("talk jni init");
}

JNIEXPORT void JNICALL Java_com_howell_utils_TalkJniUtil_talkDeInit
  (JNIEnv *env, jclass){
	if(g_talkMgr.voice_callback_obj!=NULL){
		env->DeleteGlobalRef(g_talkMgr.voice_callback_obj);
		g_talkMgr.voice_callback_obj = NULL;
	}
	if(g_talkMgr.res_callback_obj!=NULL){
		env->DeleteGlobalRef(g_talkMgr.res_callback_obj);
		g_talkMgr.res_callback_obj = NULL;
	}
}

JNIEXPORT void JNICALL Java_com_howell_utils_TalkJniUtil_setCallbackObj
  (JNIEnv *env, jclass, jobject obj, jint flag){
	switch(flag){
	case 0:
		g_talkMgr.voice_callback_obj = env->NewGlobalRef(obj);
		break;
	case 1:
		g_talkMgr.res_callback_obj = env->NewGlobalRef(obj);
		break;
	default:
		break;
	}
}

JNIEXPORT void JNICALL Java_com_howell_utils_TalkJniUtil_setCallbackMethodName
  (JNIEnv *, jclass, jstring, jint){

}


JNIEXPORT jboolean JNICALL Java_com_howell_utils_TalkJniUtil_register2svr
  (JNIEnv *env, jclass, jstring name, jstring pwd, jstring id, jint channel, jstring ip, jint port){
	char *_name = (char *)env->GetStringUTFChars(name,NULL);
	char *_pwd 	= (char *)env->GetStringUTFChars(pwd,NULL);
	char *_id	= (char *)env->GetStringUTFChars(id,NULL);
	char *_ip	= (char *)env->GetStringUTFChars(ip,NULL);
	int _channel= channel;
	int _port	= port;

	register2svr(_name,_pwd,_id,_channel,_ip,_port);

	env->ReleaseStringUTFChars(name,_name);
	env->ReleaseStringUTFChars(pwd,_pwd);
	env->ReleaseStringUTFChars(id,_id);
	env->ReleaseStringUTFChars(ip,_ip);
	return true;
}











