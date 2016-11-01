/*
 * talk_jni.cpp
 *
 *  Created on: 2016年5月24日
 *      Author: howell
 */


#include <jni.h>
#include <pthread.h>
#include <android/log.h>
#include <stdio.h>
#include <string.h>
#include "include/net_sdk.h"
#include "include/voice_test/voice_test.h"
#include "com_howell_formuseum_JNIManager.h"


#define LOGI(...) ((void)__android_log_print(ANDROID_LOG_INFO, "talk_jni", __VA_ARGS__))
#define LOGW(...) ((void)__android_log_print(ANDROID_LOG_WARN, "talk_jni", __VA_ARGS__))
#define LOGE(...) ((void)__android_log_print(ANDROID_LOG_ERROR, "talk_jni", __VA_ARGS__))



struct TalkStreamResource
{
	JavaVM * jvm;
	JNIEnv * env;
	jobject obj;
	jmethodID mid;
	int method_ready;
	USER_HANDLE user_handle;
	VOICE_STREAM_HANDLE voice_stream_handle;
};
static struct TalkStreamResource * talkRes = NULL;
extern void audio_play(const char* buf,int len);
int on_voice_fun(int voice_type,const char* buf,int len){
	__android_log_print(ANDROID_LOG_INFO, "jni", "on_voice_fun voice_type:%d len:%d",voice_type,len);
	audio_play(buf,len);
	return 0;
}

int registerService(const char* id,const char*local_phone,const char* name,const char* ip,short port){
	/**
	 * 注册到服务器
	 * id: 必须唯一
	 * local_phone: 电话号码
	 * name: 姓名(UTF-8格式)
	 * ip: 服务器IP
	 * port:服务器端口
	 * fun:回调函数
	 * return: 0-成功,-1-失败
	 */

	return 0;

//	return voice_register2svr(id,local_phone,name,ip,port,on_voice_fun);
}

static int create_resource(JNIEnv *env, jobject obj,jstring j_id, jstring j_local_phone,jstring j_name,jstring j_ip ,short port)
{
  talkRes = (struct TalkStreamResource *)calloc(1,sizeof(*talkRes));
  if (talkRes == NULL) return -1;

  env->GetJavaVM(&talkRes->jvm);

  talkRes->obj = env->NewGlobalRef(obj);
  talkRes->method_ready = 0;
  const char* id = env-> GetStringUTFChars(j_id,NULL);
  const char* ip = env-> GetStringUTFChars(j_ip,NULL);
  const char* local_phone = env-> GetStringUTFChars(j_local_phone,NULL);
  const char* name = env-> GetStringUTFChars(j_name,NULL);
  int ret = registerService(id,local_phone,name,ip,port);
  __android_log_print(ANDROID_LOG_INFO, "registerService", "ret %d,ip %s,port:%d",ret,ip,port);
  env->ReleaseStringUTFChars(j_id,id);
  env->ReleaseStringUTFChars(j_ip,ip);
  env->ReleaseStringUTFChars(j_local_phone,local_phone);
  env->ReleaseStringUTFChars(j_name,name);
  return ret;
}



JNIEXPORT jint JNICALL Java_com_howell_formuseum_JNIManager_register2service
  (JNIEnv *env, jobject obj, jstring j_id, jstring j_local_phone, jstring j_name, jstring j_ip, jshort port){
	return create_resource(env,obj,j_id,j_local_phone,j_name,j_ip , port);
}


JNIEXPORT void JNICALL Java_com_howell_formuseum_JNIManager_unregister2service
(JNIEnv *env, jobject cls){
	if(talkRes!=NULL){
		if(talkRes->obj!=NULL){
			env->DeleteGlobalRef(talkRes->obj);
			talkRes->obj = NULL;
		}
		free(talkRes);
		talkRes = NULL;
	}
	voice_unregister2svr();
}

JNIEXPORT int JNICALL Java_com_howell_formuseum_JNIManager_getRegisterState
(JNIEnv *env, jobject cls){
	/*
	 * 获取注册状态
	 * state: 返回 0-异常  1-正常
	 * return:0-成功 -1-失败
	 */
	int state;
	int ret = voice_get_register_state(&state);
	return state;
}

JNIEXPORT int JNICALL Java_com_howell_formuseum_JNIManager_setHeartBeat
(JNIEnv *env, jobject cls){
	return voice_send_register_heartbeat();
}

/*
 * 请求与服务器说话
 * return: 0-成功(因为服务器不是立刻回复，所以还需要调用voice_get_talk_state) -1-失败
 */
JNIEXPORT int JNICALL Java_com_howell_formuseum_JNIManager_requestTalk
(JNIEnv *env, jobject cls){
	return voice_start_talk2svr();
}

JNIEXPORT int JNICALL Java_com_howell_formuseum_JNIManager_getTalkState
(JNIEnv *env, jobject cls){
	/*
	 * 获取与服务器的说话状态
	 * state: 返回 1-正在等待服务器应答  2-服务器允许通话  3-服务器拒绝通话
	 * return: 0-成功 -1-失败
	 */
	int state;
	int ret = voice_get_talk_state(&state);
	return state;
}




JNIEXPORT void JNICALL Java_com_howell_formuseum_JNIManager_stopTalk
(JNIEnv *env, jobject cls){
	voice_stop_talk2svr();
}

JNIEXPORT int JNICALL Java_com_howell_formuseum_JNIManager_setData
(JNIEnv *env, jobject cls,jbyteArray bytes ,jint len){
	int voice_type = 1;
	char *temp = (char *)env->GetByteArrayElements(bytes,NULL);
	if(temp == NULL){
		__android_log_print(ANDROID_LOG_INFO, "inputVoice", "temp == NULL");
		return 0;
	}
	int ret = voice_input_voice_data(voice_type,temp,len);
	env->ReleaseByteArrayElements(bytes,(jbyte*)temp,0);
	return ret;
}



