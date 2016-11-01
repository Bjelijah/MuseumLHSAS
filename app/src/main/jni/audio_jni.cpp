/*
 * audio_jni.cpp
 *
 *  Created on: 2016年5月24日
 *      Author: howell
 */

#include <jni.h>
#include <stdio.h>
#include <string.h>
#include <android/log.h>
#include <semaphore.h>
#include "com_howell_formuseum_JNIManager.h"
#define LOGI(...) ((void)__android_log_print(ANDROID_LOG_INFO, "audio_jni", __VA_ARGS__))
#define LOGW(...) ((void)__android_log_print(ANDROID_LOG_WARN, "audio_jni", __VA_ARGS__))
#define LOGE(...) ((void)__android_log_print(ANDROID_LOG_ERROR, "audio_jni", __VA_ARGS__))


typedef struct AudioPlay
{
	/* multi thread */
	int method_ready;
	JavaVM * jvm;
	JNIEnv * env;
	jmethodID mid;
	jobject obj;
	jfieldID data_length_id;
	jbyteArray data_array;
	int data_array_len;

	int stop;
	int test;
}TAudioPlay;
TAudioPlay* self = NULL;

void audio_stop()
{
	self->stop=1;
	//self.over = 1;
	//sem_post(&self.over_audio_sem);
	//sem_wait(&self.over_audio_ret_sem);
}


void audio_play(const char* buf,int len)
{
	if(self==NULL){
		LOGE("self==NULL");
		return;
	}
	if (self->stop) {
		LOGE("self.stop =1");
		return;
	}
	if(self->obj == NULL){
		LOGE("self.obj = null self.test=%d",self->test);
		return;
	}
	if (self->jvm->AttachCurrentThread( &self->env, NULL) != JNI_OK) {
		LOGE("%s: AttachCurrentThread() failed", __FUNCTION__);
		return;
	}
	/* get JAVA method first */
	if (!self->method_ready) {


		jclass cls;
		cls = self->env->GetObjectClass(self->obj);
		if (cls == NULL) {
			LOGE("FindClass() Error.....");
			goto error;
		}
		//�ٻ�����еķ���
		self->mid = self->env->GetMethodID(cls, "audioWrite", "()V");
		if (self->mid == NULL) {
			LOGE("GetMethodID() Error.....");
			goto error;
		}

		self->method_ready=1;
	}
	/* update length */
	self->env->SetIntField(self->obj,self->data_length_id,len);
	/* update data */
	if (len<=self->data_array_len) {
		self->env->SetByteArrayRegion(self->data_array,0,len,(const signed char *)buf);
		/* notify the JAVA */
		self->env->CallVoidMethod( self->obj, self->mid, NULL);
	}
	if (self->jvm->DetachCurrentThread() != JNI_OK) {
		LOGE("%s: DetachCurrentThread() failed", __FUNCTION__);
	}
	/* char* data = (*self.env)->GetByteArrayElements(self.env,self.data_array,0); */
	/* memcpy(data,buf,len); */

	return;

	error:
	if (self->jvm->DetachCurrentThread() != JNI_OK) {
		LOGE("%s: DetachCurrentThread() failed", __FUNCTION__);
	}
}

JNIEXPORT void JNICALL Java_com_howell_formuseum_JNIManager_nativeAudioInit
(JNIEnv *env, jobject obj)
{
	LOGI("native audio init");
	if(self == NULL){
		self = (TAudioPlay*)malloc(sizeof(TAudioPlay));
		memset(self,0,sizeof(TAudioPlay));
	}


	env->GetJavaVM(&self->jvm);
	//����ֱ�Ӹ�ֵ(g_obj = obj)
	self->obj = env->NewGlobalRef(obj);
	jclass clz = env->GetObjectClass( obj);
	self->data_length_id = env->GetFieldID(clz, "mAudioDataLength", "I");
	jfieldID id = env->GetFieldID(clz,"mAudioData","[B");

	jbyteArray data = (jbyteArray)env->GetObjectField(obj,id);
	self->data_array = (jbyteArray)env->NewGlobalRef(data);
	env->DeleteLocalRef(data);
	self->data_array_len =env->GetArrayLength(self->data_array);
	self->method_ready = 0;
	self->stop = 0;
	self->test = 234;
	LOGI("native audio init ok");
	if(self->obj==NULL){
		LOGE("obj=null");
	}else{
		LOGI("obj ok");
	}


}

JNIEXPORT void JNICALL Java_com_howell_formuseum_JNIManager_nativeAudioStop
(JNIEnv *env, jobject cls)
{
	audio_stop();
}

JNIEXPORT void JNICALL Java_com_howell_formuseum_JNIManager_nativeAudioDeinit
(JNIEnv *env, jobject obj)
{
	/* TODO */
	if(self==NULL)return;

	LOGI("native audio deinit");
	env->DeleteGlobalRef( self->obj);
	LOGI("obj free ok");
	env->DeleteGlobalRef( self->data_array);
	LOGI("data_array free ok");

	free(self);
	self = NULL;
}



