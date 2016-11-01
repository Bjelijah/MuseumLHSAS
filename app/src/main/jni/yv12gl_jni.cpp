/*
 * yv12gl_jni.cpp
 *
 *  Created on: 2016年5月24日
 *      Author: howell
 */

#include <stdio.h>
#include <stdlib.h>
#include <android/log.h>
#include <jni.h>
#include <EGL/egl.h>
#include <EGL/eglext.h>
#include <GLES2/gl2.h>
#include <GLES2/gl2ext.h>
#include <pthread.h>
#include <semaphore.h>

#include "hwplay/stream_type.h"
#include "hwplay/play_def.h"
#include "com_howell_formuseum_JNIManager.h"
#define LOGI(...) ((void)__android_log_print(ANDROID_LOG_INFO, "yv12", __VA_ARGS__))
#define LOGW(...) ((void)__android_log_print(ANDROID_LOG_WARN, "yv12", __VA_ARGS__))
#define LOGE(...) ((void)__android_log_print(ANDROID_LOG_ERROR, "yv12", __VA_ARGS__))


struct YV12glDisplay
{
	char * y;
	char * u;
	char * v;
	unsigned long long time;
	int width;
	int height;
	//int inited;
	int enable;
	//int is_catch_picture;
	//char path[50];

	/* multi thread */
	int method_ready;
	JavaVM * jvm;
	JNIEnv * env;
	jmethodID mid,mSetTime;
	jobject obj;
	pthread_mutex_t lock;
	unsigned long long first_time;
	//sem_t over_sem;
	//sem_t over_ret_sem;
};

static struct YV12glDisplay yuvSelf;

void yuv12gl_set_enable(int enable)
{
	yuvSelf.enable = enable;
	yuvSelf.method_ready = 0;
}

void yv12gl_display(const unsigned char * y, const unsigned char *u,const unsigned char *v, int width, int height, unsigned long long time)
{
	//LOGE("display");
	//__android_log_print(ANDROID_LOG_INFO, "yv12gl_display", "time: %llu",time);
	if (!yuvSelf.enable) return;
	if(yuvSelf.obj == NULL){
		LOGE("self.obj == null");
		return;
	}

	yuvSelf.time = time/1000;
	//LOGE("progress self.time :%llu",self.time);
	if(yuvSelf.jvm->AttachCurrentThread( &yuvSelf.env, NULL) != JNI_OK) {
		LOGE("%s: AttachCurrentThread() failed", __FUNCTION__);
		return;
	}

	/* get JAVA method first */
	if (!yuvSelf.method_ready) {
		//LOGE("111111111");

		jclass cls = yuvSelf.env->GetObjectClass(yuvSelf.obj);
		//self.clz = (*self.env)->FindClass(self.env, "com/howell/webcam/player/YV12Renderer");
		if (cls == NULL) {
			LOGE("FindClass() Error.....");
			goto error;
		}
		//�ٻ�����еķ���
		yuvSelf.mid = yuvSelf.env->GetMethodID( cls, "requestRender", "()V");
		//self.mSetTime = (*self.env)->GetMethodID(self.env, cls, "setTime", "(J)V");
		if (yuvSelf.mid == NULL) {
			LOGE("GetMethodID() Error.....");
			goto error;
		}
		yuvSelf.method_ready=1;
	}

	// LOGE("22222222");
	//(*self.env)->CallVoidMethod(self.env,self.obj,self.mSetTime,self.time);
	/*
  if (sem_trywait(&self.over_sem)==0) {
	  if (self.method_ready)
	  {

	  }
	  sem_post(&self.over_ret_sem);
	  self.enable=0;
	  return;
  }
	 */
	//LOGE("33333333");
	pthread_mutex_lock(&yuvSelf.lock);
	if (width!=yuvSelf.width || height!=yuvSelf.height) {
		yuvSelf.y = (char *)realloc(yuvSelf.y,width*height);
		yuvSelf.u = (char *)realloc(yuvSelf.u,width*height/4);
		yuvSelf.v = (char *)realloc(yuvSelf.v,width*height/4);
		yuvSelf.width = width;
		yuvSelf.height = height;
		if(yuvSelf.y==NULL || yuvSelf.u == NULL || yuvSelf.v==NULL){
			LOGE("yv12gl_display realloc error");
			pthread_mutex_unlock(&yuvSelf.lock);
			goto error;
		}

	}
	memcpy(yuvSelf.y,y,width*height);
	memcpy(yuvSelf.u,u,width*height/4);
	memcpy(yuvSelf.v,v,width*height/4);
	pthread_mutex_unlock(&yuvSelf.lock);

	//LOGE("4444444");
	/* notify the JAVA */
	if(yuvSelf.obj!=NULL && yuvSelf.mid!=NULL){
		yuvSelf.env->CallVoidMethod( yuvSelf.obj, yuvSelf.mid, NULL);
	}else{
		LOGE("yv12gl_display  obj=null  ||  mid = null");
	}
	//LOGE("555555555");

	if (yuvSelf.jvm->DetachCurrentThread() != JNI_OK) {
		LOGE("%s: DetachCurrentThread() failed", __FUNCTION__);
	}
	return;

	error:
	if (yuvSelf.jvm->DetachCurrentThread() != JNI_OK) {
		LOGE("%s: DetachCurrentThread() failed", __FUNCTION__);
	}
	return;
}

JNIEXPORT void JNICALL Java_com_howell_formuseum_JNIManager_nativeInit
(JNIEnv *env, jobject obj)
{
	//self = malloc(sizeof(YV12glDisplay));
	//memset(&self,0,sizeof(YV12glDisplay));
	env->GetJavaVM(&yuvSelf.jvm);
	//����ֱ�Ӹ�ֵ(g_obj = obj)
	yuvSelf.obj = env->NewGlobalRef(obj);
	pthread_mutex_init(&yuvSelf.lock,NULL);
	//sem_init(&self.over_sem,0,0);
	//sem_init(&self.over_ret_sem,0,0);
	yuvSelf.width = 352;
	yuvSelf.height = 288;
	yuvSelf.y = (char *)malloc(yuvSelf.width*yuvSelf.height);
	yuvSelf.u = (char *)malloc(yuvSelf.width*yuvSelf.height/4);
	yuvSelf.v = (char *)malloc(yuvSelf.width*yuvSelf.height/4);
	if(yuvSelf.y==NULL || yuvSelf.u==NULL || yuvSelf.v==NULL){
		LOGE("native init malloc error");
		return;
	}


	memset(yuvSelf.y,0,yuvSelf.width*yuvSelf.height);
	memset(yuvSelf.u,128,yuvSelf.width*yuvSelf.height/4);
	memset(yuvSelf.v,128,yuvSelf.width*yuvSelf.height/4);
	//self.time = 0;
	LOGI("nativeInit ok");
}

JNIEXPORT void JNICALL JNICALL Java_com_howell_formuseum_JNIManager_nativeOnSurfaceCreated
(JNIEnv *env, jobject obj)
{
	//self.inited=1;
	LOGI("nativeOnSurfaceCreated");
	yuvSelf.enable=1;
}

JNIEXPORT void JNICALL JNICALL Java_com_howell_formuseum_JNIManager_nativeRenderY
(JNIEnv *env, jobject obj)
{
	pthread_mutex_lock(&yuvSelf.lock);
	if (yuvSelf.y == NULL) {
		char value[4] = {0,0,0,0};
		glTexImage2D(GL_TEXTURE_2D,0,GL_LUMINANCE,2,2,0,GL_LUMINANCE,GL_UNSIGNED_BYTE,value);
	}
	else {
		//LOGI("render y");
		glTexImage2D(GL_TEXTURE_2D,0,GL_LUMINANCE,yuvSelf.width,yuvSelf.height,0,GL_LUMINANCE,GL_UNSIGNED_BYTE,yuvSelf.y);
	}
}

JNIEXPORT void JNICALL JNICALL Java_com_howell_formuseum_JNIManager_nativeRenderU
(JNIEnv *env, jobject obj)
{
	if (yuvSelf.u == NULL) {
		char value[] = {128};
		glTexImage2D(GL_TEXTURE_2D,0,GL_LUMINANCE,1,1,0,GL_LUMINANCE,GL_UNSIGNED_BYTE,value);
	}
	else {
		glTexImage2D(GL_TEXTURE_2D,0,GL_LUMINANCE,yuvSelf.width/2,yuvSelf.height/2,0,GL_LUMINANCE,GL_UNSIGNED_BYTE,yuvSelf.u);
	}
}

JNIEXPORT void JNICALL JNICALL Java_com_howell_formuseum_JNIManager_nativeRenderV
(JNIEnv *env, jobject obj)
{
	if (yuvSelf.v==NULL) {
		char value[] = {128};
		glTexImage2D(GL_TEXTURE_2D,0,GL_LUMINANCE,1,1,0,GL_LUMINANCE,GL_UNSIGNED_BYTE,value);
	}
	else {
		glTexImage2D(GL_TEXTURE_2D,0,GL_LUMINANCE,yuvSelf.width/2,yuvSelf.height/2,0,GL_LUMINANCE,GL_UNSIGNED_BYTE,yuvSelf.v);
	}
	pthread_mutex_unlock(&yuvSelf.lock);
}

JNIEXPORT void JNICALL Java_com_howell_formuseum_JNIManager_nativeDeinit
(JNIEnv *env, jobject obj)
{
	/* TODO */
	yuvSelf.method_ready = 0;

	if(yuvSelf.y!=NULL){
		free(yuvSelf.y);
		yuvSelf.y = NULL;
	}
	if(yuvSelf.u != NULL){
		free(yuvSelf.u);
		yuvSelf.u = NULL;
	}
	if(yuvSelf.v != NULL){
		free(yuvSelf.v);
		yuvSelf.v = NULL;
	}
}




