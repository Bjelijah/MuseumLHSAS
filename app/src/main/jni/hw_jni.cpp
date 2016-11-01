/*
 * hw_jni.cpp
 *
 *  Created on: 2016年5月25日
 *      Author: howell
 */


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
#include <pthread.h>
#include "hwplay/stream_type.h"
#include "hwplay/play_def.h"
#include <unistd.h>
#include "net_sdk.h"
#include "include/voice_test/voice_test.h"
#include <stdlib.h>
#include <EGL/egl.h>
#include <EGL/eglext.h>
#include <GLES2/gl2.h>
#include <GLES2/gl2ext.h>
#include <semaphore.h>
#include "include/voice_test/hwvoice_msg.h"


using namespace hwvoice;

#define LOGI(...) ((void)__android_log_print(ANDROID_LOG_INFO, "hw_jni", __VA_ARGS__))
#define LOGW(...) ((void)__android_log_print(ANDROID_LOG_WARN, "hw_jni", __VA_ARGS__))
#define LOGE(...) ((void)__android_log_print(ANDROID_LOG_ERROR, "hw_jni", __VA_ARGS__))


typedef struct AudioPlay
{
	/* multi thread */
	int method_ready,jsonMethod_ready;
	JavaVM * jvm;
	JNIEnv * env;
	jmethodID mid,jsonMid;
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
	if(self==NULL)return;
	self->stop=1;
	//self.over = 1;
	//sem_post(&self.over_audio_sem);
	//sem_wait(&self.over_audio_ret_sem);
}

void talk_audio_play(const char *buf,int len){
	if(self==NULL){
		LOGE("self null");
		return ;
	}
	if(self->stop ){
		LOGE("self stop");
		return;
	}
	if(self->obj == NULL){
		LOGE("obj = null");
		return;
	}
	JNIEnv *env = NULL;


	if(self->jvm->GetEnv((void **)&env,JNI_VERSION_1_4)!=JNI_OK){
		LOGE("get env error");
		return;
	}
	if(!self->method_ready){
		jclass cls = env->GetObjectClass(self->obj);
		self->mid = env->GetMethodID(cls, "audioWrite", "()V");

		self->method_ready = 1;
	}

	env->SetIntField(self->obj,self->data_length_id,len);
	/* update data */
	if (len<=self->data_array_len) {
		env->SetByteArrayRegion(self->data_array,0,len,(const signed char *)buf);
		/* notify the JAVA */
		env->CallVoidMethod( self->obj, self->mid, NULL);
	}
	LOGE("talk audio play ok");
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

	LOGE("start to detach audio play thread");

	if (self->jvm->DetachCurrentThread() != JNI_OK) {
		LOGE("%s: DetachCurrentThread() failed", __FUNCTION__);
	}else{
		LOGE("audio_play detach ok");
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
	/*
	self->obj = env->NewGlobalRef(obj);
	jclass clz = env->GetObjectClass( obj);
	self->data_length_id = env->GetFieldID(clz, "mAudioDataLength", "I");
	jfieldID id = env->GetFieldID(clz,"mAudioData","[B");

	jbyteArray data = (jbyteArray)env->GetObjectField(obj,id);
	self->data_array = (jbyteArray)env->NewGlobalRef(data);
	env->DeleteLocalRef(data);
	self->data_array_len =env->GetArrayLength(self->data_array);
	 */
	self->jsonMethod_ready=0;
	self->method_ready = 0;
	self->stop = 0;
	self->test = 234;
	LOGI("native audio init ok");

}

JNIEXPORT void JNICALL Java_com_howell_formuseum_JNIManager_nativeAudioSetCallbackObject
(JNIEnv *env, jobject, jobject obj, jint flag){
	if(self==NULL)return;
	switch(flag){
	case 0:
		self->obj = env->NewGlobalRef(obj);
		break;
	default:
		break;
	}
}

JNIEXPORT void JNICALL Java_com_howell_formuseum_JNIManager_nativeAudioSetCallbackMethodName
(JNIEnv *env, jobject, jstring str, jint flag){
	if(self==NULL)return;
	if(self->obj==NULL)return;
	jclass clz = env->GetObjectClass(self->obj);
	char *_str = (char *)env->GetStringUTFChars(str,NULL);
	switch(flag){
	case 0:{
		self->data_length_id = env->GetFieldID(clz, _str, "I");
	}
	break;
	case 1:{
		jfieldID id = env->GetFieldID(clz,_str,"[B");
		jbyteArray data = (jbyteArray)env->GetObjectField(self->obj,id);
		self->data_array = (jbyteArray)env->NewGlobalRef(data);
		env->DeleteLocalRef(data);
		self->data_array_len =env->GetArrayLength(self->data_array);
	}
	break;
	default:
		break;
	}
	env->ReleaseStringUTFChars(str,_str);
}

JNIEXPORT void JNICALL Java_com_howell_formuseum_JNIManager_nativeAudioStop
(JNIEnv *env, jobject cls)
{
	audio_stop();
}


JNIEXPORT void JNICALL Java_com_howell_formuseum_JNIManager_nativeAudioBPlayable
  (JNIEnv *, jobject){
	if(self==NULL)return;
		self->stop=0;
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


/*
 * decode_jni.cpp
 *
 *  Created on: 2016年5月24日
 *      Author: howell
 */





struct StreamResource
{
	JavaVM * jvm;
	JNIEnv * env;
	jobject obj;
	jmethodID mid;
	USER_HANDLE user_handle;
	ALARM_STREAM_HANDLE alarm_stream_handle;
	PLAY_HANDLE play_handle;
	LIVE_STREAM_HANDLE live_stream_handle;
	FILE_STREAM_HANDLE file_stream_handle;
	int media_head_len;
	int is_exit;	//退出标记位
};
static struct StreamResource * res = NULL;

struct alarm_buf
{
	int slot;
	int sec;//1970到现在的sec
	unsigned msec;//毫秒
	char data[128 * 128 / 8];//报警区域
	int reserve[32];
};

extern void yv12gl_display(const unsigned char * y, const unsigned char *u,const unsigned char *v, int width, int height, unsigned long long time);
extern void audio_play(const char* buf,int len);
void on_audio_callback(PLAY_HANDLE handle,
		const char* buf,//数据缓存,如果是视频，则为YV12数据，如果是音频则为pcm数据
		int len,//数据长度,如果为视频则应该等于w * h * 3 / 2
		unsigned long timestamp,//时标,单位为毫秒
		long user){
	//__android_log_print(ANDROID_LOG_INFO, "audio", "on_audio_callback timestamp: %lu ",timestamp);

	//if(res[user]->is_exit == 1) return;
	//audio_play(buf,len,0,0,0);

}

void on_yuv_callback_ex(PLAY_HANDLE handle,
		const unsigned char* y,
		const unsigned char* u,
		const unsigned char* v,
		int y_stride,
		int uv_stride,
		int width,
		int height,
		unsigned long long time,
		long user)
{
	//__android_log_print(ANDROID_LOG_INFO, "jni", "on_yuv_callback_ex ");
	yv12gl_display(y,u,v,width,height,time);
}

static void on_source_callback(PLAY_HANDLE handle, int type, const char* buf, int len, unsigned long timestamp, long sys_tm, int w, int h, int framerate, int au_sample, int au_channel, int au_bits, long user){

	if(type == 0){//音频
		//		audio_play(buf,len,au_sample,au_channel,au_bits);
		//	audio_play(buf, len);//add cbj
	}else if(type == 1){//视频
		unsigned char* y = (unsigned char *)buf;
		unsigned char* u = y+w*h;
		unsigned char* v = u+w*h/4;
		yv12gl_display(y,u,v,w,h,timestamp);
	}
}




void on_live_stream_fun(LIVE_STREAM_HANDLE handle,int stream_type,const char* buf,int len,long userdata){
	//__android_log_print(ANDROID_LOG_INFO, "jni", "-------------stream_type %d-len %d",stream_type,len);
	//res->stream_len += len;
	if(res == NULL){
		return;
	}

	int ret = hwplay_input_data(res->play_handle, buf ,len);
}

void on_file_stream_fun(FILE_STREAM_HANDLE handle,const char* buf,int len,long userdata){
	//res->stream_len += len;
	//__android_log_print(ANDROID_LOG_INFO, "jni", "on_file_stream_fun res->play_handle:%d len:%d",res->play_handle,len);
	if(res==NULL){
		return;
	}

	int ret = hwplay_input_data(res->play_handle, buf ,len);
	while(ret <= 0 && res->is_exit == 0){
		usleep(10000);
		ret = hwplay_input_data(res->play_handle, buf ,len);
	}

	//__android_log_print(ANDROID_LOG_INFO, "jni", "on_file_stream_fun ret:%d",ret);
}

void on_alarm_stream_fun(ALARM_STREAM_HANDLE handle,int alarm_type,const char* buf,int len,long userdata){
	//if(alarm_type == HW_ALARM_IN){
	__android_log_print(ANDROID_LOG_INFO, "jni", "alarm_type11 %d",alarm_type);
	//}
	//if(alarm_type == HW_ALARM_MOTIONEX){
	//LOGE("test1111111111");
	if(res==NULL)return;
	if(res->obj == NULL) return;
	if(res->jvm->AttachCurrentThread( &res->env, NULL) != JNI_OK) {
		LOGE("%s: AttachCurrentThread() failed", __FUNCTION__);
		return;
	}
	//LOGE("test222222222");
	jclass cls = res->env->GetObjectClass(res->obj);
	if (cls == NULL) {
		LOGE("FindClass() Error.....");
		goto error;
	}
	if(alarm_type == HW_ALARM_MOTIONEX || alarm_type == HW_ALARM_IN){
		LOGE("test33333333333");
		//获取摄像机的通道号
		struct alarm_buf my_buf;
		memset(&my_buf,0,sizeof(my_buf));
		memcpy(&my_buf,buf,len);
		int nvr_slot = my_buf.slot;
		LOGE("test44444444444");
		//通过通道号获取摄像机ip
		slot_cfg_t slot_cfg;
		memset(&slot_cfg,0,sizeof(slot_cfg));
		slot_cfg.slot = nvr_slot;
		int ret = hwnet_get_slot_cfg(res->user_handle,&slot_cfg);
		if(ret == 0 ){
			__android_log_print(ANDROID_LOG_INFO, "jni", "hwnet_get_slot_cfg fail");
			goto error;
		}
		char *ip = slot_cfg.ip;
		jstring jstring_ip =res->env->NewStringUTF(ip) ;
		LOGE("test555555555");
		//通过通道号获取摄像机名称
		osd_name_t osd_name;
		memset(&osd_name,0,sizeof(osd_name));
		osd_name.slot = nvr_slot;
		ret = hwnet_get_osd_name(res->user_handle,&osd_name);
		if(ret == 0 ){
			__android_log_print(ANDROID_LOG_INFO, "jni", "hwnet_get_osd_name fail");
			goto error;
		}
		char *name = osd_name.name;
		LOGE("test6666666666");
		//把中文字符串转换成GB2312-------------------------------------------------
		//定义java String类 strClass
		jclass strClass = res->env->FindClass("java/lang/String");
		LOGE("test aaaaaaa");
		//获取String(byte[],String)的构造器,用于将本地byte[]数组转换为一个新String
		jmethodID ctorID = res->env->GetMethodID(strClass, "<init>", "([BLjava/lang/String;)V");
		//建立byte数组
		LOGE("test bbbbbbb");
		jbyteArray bytes = res->env->NewByteArray(strlen(name));
		//将char* 转换为byte数组
		LOGE("test ccccccc");
		res->env->SetByteArrayRegion(bytes, 0, strlen(name), (jbyte*)name);
		// 设置String, 保存语言类型,用于byte数组转换至String时的参数
		LOGE("test ddddddd");
		jstring encoding = res->env->NewStringUTF("GB2312");
		//将byte数组转换为java String,并输出
		LOGE("test eeeeeee");
		jstring jstring_name = (jstring)(res->env->NewObject(strClass, ctorID, bytes, encoding));
		//---------------------------------------------------------------------
		//jstring jstring_name = str2jstring(res->env,name) ;
		//__android_log_print(ANDROID_LOG_INFO, "jni", "osd_name.name: %s",jstring_name);
		//jstring jstring_name =(*res->env)->NewStringUTF(res->env,name) ;
		LOGE("test777777777");

		res->mid = res->env->GetMethodID( cls, "alarmStreamFun", "(ILjava/lang/String;Ljava/lang/String;I)V");
		if (res->mid == NULL ) {
			LOGE("GetMethodID() Error.....");
			goto error;
		}
		LOGE("test88888888");
		res->env->CallVoidMethod( res->obj, res->mid, nvr_slot,jstring_ip,jstring_name,alarm_type);
	}else{
		res->mid = res->env->GetMethodID( cls, "alarmStreamFun", "(ILjava/lang/String;Ljava/lang/String;I)V");
		if (res->mid == NULL ) {
			LOGE("GetMethodID() Error.....");
			goto error;
		}
		//LOGE("test10");
		char * ip = "";
		jstring jstring_ip = res->env->NewStringUTF(ip);
		res->env->CallVoidMethod( res->obj, res->mid, 0,jstring_ip,jstring_ip,alarm_type);
		res->env->ReleaseStringUTFChars(jstring_ip,ip);
	}
	//(*res->env)->ReleaseStringUTFChars(res->env,jstring_ip,ip);
	//(*res->env)->ReleaseStringUTFChars(res->env,jstring_name,name);
	if (res->jvm->DetachCurrentThread() != JNI_OK) {
		LOGE("%s: DetachCurrentThread() failed", __FUNCTION__);
	}
	//LOGE("test999999999");
	return;

	error:
	if (res->jvm->DetachCurrentThread() != JNI_OK) {
		LOGE("%s: DetachCurrentThread() failed", __FUNCTION__);
	}
	return;
	//}
}

static PLAY_HANDLE init_play_handle(int is_playback ,SYSTEMTIME beg,SYSTEMTIME end,int slot){
	if(res == NULL)return -1;
	hwplay_init(1,0,0);
	RECT area ;
	HW_MEDIAINFO media_head;
	memset(&media_head,0,sizeof(media_head));
	if(!is_playback){//预览
		res->live_stream_handle = hwnet_get_live_stream(res->user_handle,slot,1,0,on_live_stream_fun,0);

		LOGI("live_stream_handle: %d",res->live_stream_handle);
		LOGI("1");

		if(res->live_stream_handle == -1){
			LOGE("[init_play_handle]  live_stream_heandle=-1 return");
			return -1;
		}



		//int media_head_len = 0;
		int ret2 = hwnet_get_live_stream_head(res->live_stream_handle,(char*)&media_head,1024,&res->media_head_len);

		LOGI("ret2 :%d adec_code %x",ret2,media_head.adec_code);
		LOGI("is_playback :%d",is_playback);
	}else{//回放
		__android_log_print(ANDROID_LOG_INFO, "jni", "is_playback :%d ,user_hanle%d",is_playback,res->user_handle);
		file_stream_t file_info;
		LOGI("beg year=%d month=%d day=%d hour=%d minute=%d second=%d",beg.wYear,beg.wMonth,beg.wDay,beg.wHour,beg.wMinute,beg.wSecond );
		LOGI("end year=%d month=%d day=%d hour=%d minute=%d second=%d",end.wYear,end.wMonth,end.wDay,end.wHour,end.wMinute,end.wSecond );
		LOGI("slot=%d",slot);
		res->file_stream_handle = hwnet_get_file_stream_ex2(res->user_handle,slot,1,beg,end,0,0,on_file_stream_fun,0,&file_info);
		//res->file_stream_handle = hwnet_get_file_stream(res->user_handle,slot,beg,end,on_file_stream_fun,0,&file_info);
		__android_log_print(ANDROID_LOG_INFO, "jni", "file_stream_handle: %d",res->file_stream_handle);
		int b = hwnet_get_file_stream_head(res->file_stream_handle,(char*)&media_head,1024,&res->media_head_len);
		//media_head.adec_code = 0xa;
		__android_log_print(ANDROID_LOG_INFO, "jni", "hwnet_get_file_stream_head ret:%d",b);
	}
	PLAY_HANDLE  ph = hwplay_open_stream((char*)&media_head,sizeof(media_head),1024*1024,is_playback,area);
	hwplay_open_sound(ph);
	//hwplay_set_max_framenum_in_buf(ph,is_playback?25:5);
	__android_log_print(ANDROID_LOG_INFO, "JNI", "ph is:%d",ph);
	//	int b= hwplay_register_yuv_callback_ex(ph,on_yuv_callback_ex,0);

	int b = hwplay_register_source_data_callback(ph,on_source_callback,0);

	__android_log_print(ANDROID_LOG_INFO, "JNI", "hwplay_register_yuv_callback_ex :%d",b);
	//	hwplay_register_audio_callback(ph,on_audio_callback,0);
	b = hwplay_play(ph);
	__android_log_print(ANDROID_LOG_INFO, "JNI", "b:%d",b);
	return ph;
}

static int register_nvr(const char* ip){
	//__android_log_print(ANDROID_LOG_INFO, "jni", "start init ph palyback: %d",is_playback);
	//hwplay_init(1,352,288);
	__android_log_print(ANDROID_LOG_INFO, "jni", "0");

	int ret = hwnet_init(5888);

	/* 192.168.128.49 */
	res->user_handle = hwnet_login(ip,5198,"admin","12345");
	if(res->user_handle == -1){
		LOGE("hwnet_login fail");
		return 0;
	}


	//res->alarm_stream_handle = hwnet_get_alarm_stream(res->user_handle,on_alarm_stream_fun,0);
	//__android_log_print(ANDROID_LOG_INFO, "jni", "alarm_stream_handle :%d",res->alarm_stream_handle);

	return 1;
}

static int create_resource(JNIEnv *env, jobject obj, jstring j_ip)
{
	/* make sure init once */
	//__android_log_print(ANDROID_LOG_INFO, "!!!", "create_resource %d",is_playback);

	if(res!=NULL){
		if(res->obj!=NULL){
			env->DeleteGlobalRef(res->obj);
			res->obj = NULL;
		}
		free(res);
		res = NULL;
	}


	res = (struct StreamResource *)calloc(1,sizeof(*res));
	if (res == NULL) return 0;
	//res->is_playback = is_playback;
	res->is_exit = 0;
	env->GetJavaVM(&res->jvm);
	res->obj = env->NewGlobalRef(obj);
	const char* ip = env-> GetStringUTFChars(j_ip,NULL);
	int ret = register_nvr(ip);
	env->ReleaseStringUTFChars(j_ip,ip);

	return ret;
}

JNIEXPORT jint JNICALL Java_com_howell_formuseum_JNIManager_display
(JNIEnv *env, jobject obj,int is_playback,jshort begYear,jshort begMonth,jshort begDay,jshort begHour
		,jshort begMinute,jshort begSecond,jshort endYear,jshort endMonth,jshort endDay,jshort endHour,jshort endMinute
		,jshort endSecond,int slot){
	SYSTEMTIME beg;
	SYSTEMTIME end;
	__android_log_print(ANDROID_LOG_INFO, "decod_jni", "slot：%d",slot);
	if(is_playback == 0){
		res->play_handle = init_play_handle(is_playback,beg,end,slot);
	}else{
		beg.wYear = begYear;
		beg.wMonth = begMonth;
		beg.wDay = begDay;
		beg.wHour = begHour;
		beg.wMinute = begMinute;
		beg.wSecond = begSecond;

		end.wYear = endYear;
		end.wMonth = endMonth;
		end.wDay = endDay;
		end.wHour = endHour;
		end.wMinute = endMinute;
		end.wSecond = endSecond;
		__android_log_print(ANDROID_LOG_INFO, "decod_jni", "test beg:%d-%d-%d %d:%d:%d\n"
				,beg.wYear, beg.wMonth,beg.wDay,beg.wHour,beg.wMinute,beg.wSecond);
		__android_log_print(ANDROID_LOG_INFO, "decod_jni", "test end:%d-%d-%d %d:%d:%d\n"
				,end.wYear, end.wMonth,end.wDay,end.wHour,end.wMinute,end.wSecond);
		res->play_handle = init_play_handle(is_playback,beg,end,slot);
	}
	if(res->play_handle==-1){
		return 0;
	}
	return 1;
}

jobject Java_com_howell_formuseum_JNIManager_getSystime
(JNIEnv *env, jobject obj, jobject classobj){
	SYSTEMTIME systm;
	/*获取系统时间
	 * handle:					hwnet_login()返回的句柄
	 * systm:					返回系统时间
	 * return:					1:成功  0:返回
	 */
	int ret = hwnet_get_systime(res->user_handle,&systm);
	__android_log_print(ANDROID_LOG_INFO, "decod_jni", "ret:%d beg:%d-%d-%d %d:%d:%d\n",ret
			,systm.wYear, systm.wMonth,systm.wDay,systm.wHour,systm.wMinute,systm.wSecond);
	jclass objectClass = env->FindClass("com/example/formuseum2/SystimeJniObj");

	jfieldID wYear = env->GetFieldID(objectClass, "wYear", "S");
	jfieldID wMonth = env->GetFieldID(objectClass, "wMonth", "S");
	jfieldID wDay = env->GetFieldID(objectClass, "wDay", "S");
	jfieldID wHour = env->GetFieldID(objectClass, "wHour", "S");
	jfieldID wMinute = env->GetFieldID(objectClass, "wMinute", "S");
	jfieldID wSecond = env->GetFieldID(objectClass, "wSecond", "S");

	env->SetShortField(classobj, wYear,systm.wYear);
	env->SetShortField(classobj, wMonth,systm.wMonth);
	env->SetShortField(classobj, wDay,systm.wDay);
	env->SetShortField(classobj, wHour,systm.wHour);
	env->SetShortField(classobj, wMinute,systm.wMinute);
	env->SetShortField(classobj, wSecond,systm.wSecond);
	return classobj;
}
JNIEXPORT void JNICALL Java_com_howell_formuseum_JNIManager_stopPlay
(JNIEnv *env, jobject obj,int is_playback){
	int ret;
	res->is_exit = 1;
	if(!is_playback){
		ret = hwnet_close_live_stream(res->live_stream_handle);
	}else{
		ret = hwnet_close_file_stream(res-> file_stream_handle);
	}

	LOGI("stop ret=%d",ret);
	if(ret == 0){
		LOGE("close stream fail");
		return;
	}
	hwplay_stop(res->play_handle);
}

JNIEXPORT jint JNICALL Java_com_howell_formuseum_JNIManager_register
(JNIEnv *env, jobject obj, jstring j_ip){
	return create_resource(env,obj,j_ip);
}

JNIEXPORT void JNICALL Java_com_howell_formuseum_JNIManager_unregister
(JNIEnv *env, jobject obj){
	//int ret = hwnet_close_alarm_stream(res->alarm_stream_handle);
	int ret;
	//if(ret == 0){
	//	__android_log_print(ANDROID_LOG_INFO, "quit", "close alarm stream fail");
	//return;
	//}
	//__android_log_print(ANDROID_LOG_INFO, "quit", "3");
	ret = hwnet_logout(res->user_handle);

	LOGI("unregister  1111");
	if(ret == 0){
		__android_log_print(ANDROID_LOG_INFO, "quit", "logout fail");
		//return;
	}
	//__android_log_print(ANDROID_LOG_INFO, "quit", "5");
	//hwplay_stop(res->play_handle);
	//__android_log_print(ANDROID_LOG_INFO, "quit", "6");
	hwnet_release();

	LOGI("unregister  2222");
	if(res!=NULL){
		if(res->obj!=NULL){
			env->DeleteGlobalRef(res->obj);
			res->obj=NULL;
		}
		free(res);
		res = NULL;
	}
}

int Java_com_howell_formuseum_JNIManager_takePhoto
(JNIEnv *env, jobject obj,jint slot ,jstring path){
	const char* c_path = env-> GetStringUTFChars(path,NULL);
	__android_log_print(ANDROID_LOG_INFO, "", "%s",c_path);
	int ret = hwnet_save_to_jpg(res->user_handle,slot,1,75,c_path);
	env->ReleaseStringUTFChars(path,c_path);
	return ret;
}

/*
 * talk_jni.cpp
 *
 *  Created on: 2016年5月24日
 *      Author: howell
 */






struct TalkStreamResource
{
	JavaVM * jvm;
	JNIEnv * env;
	jobject obj,callbackObj;
	jmethodID mid,registMid,dialogListMid,resMid,receiveMid,groupCreatMid,allResMid,GroupsListMid,groupMid,groupReceiveMid,usersMid;
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
	//return voice_register2svr(id,local_phone,name,ip,port,on_voice_fun);
}

static int create_resource(JNIEnv *env, jobject obj,jstring j_id, jstring j_local_phone,jstring j_name,jstring j_ip ,short port)
{
	if(talkRes!=NULL){//之前没有释放
		if(talkRes->obj!=NULL){
			env->DeleteGlobalRef(talkRes->obj);
			talkRes->obj = NULL;
		}
		free(talkRes);
		talkRes = NULL;
	}

	const char* id = env-> GetStringUTFChars(j_id,NULL);
	const char* ip = env-> GetStringUTFChars(j_ip,NULL);
	const char* local_phone = env-> GetStringUTFChars(j_local_phone,NULL);
	const char* name = env-> GetStringUTFChars(j_name,NULL);
	int ret = registerService(id,local_phone,name,ip,port);
	env->ReleaseStringUTFChars(j_id,id);
	env->ReleaseStringUTFChars(j_ip,ip);
	env->ReleaseStringUTFChars(j_local_phone,local_phone);
	env->ReleaseStringUTFChars(j_name,name);

	if(ret!=0){
		LOGE("registerService failed");
		return -1;
	}


	talkRes = (struct TalkStreamResource *)calloc(1,sizeof(*talkRes));
	if (talkRes == NULL) return -1;

	env->GetJavaVM(&talkRes->jvm);

	talkRes->obj = env->NewGlobalRef(obj);
	talkRes->method_ready = 0;
	return ret;
}

int on_talk_voice_fun(int voice_type,const char* buf,int len){
	__android_log_print(ANDROID_LOG_INFO, "jni", "on_voice_fun voice_type:%d len:%d",voice_type,len);
	talk_audio_play(buf,len);
	return 0;
}

static void callbackRegisterRes(msg_register_res_t* r){
	LOGE("callbackRegisterRes");

	if(r->result!=0){
		return;
	}
	JNIEnv *env = NULL;
	JavaVM * _jvm = talkRes->jvm;
	if(_jvm->AttachCurrentThread( &env, NULL) != JNI_OK) {
		LOGE("%s: AttachCurrentThread() failed", __FUNCTION__);
		return;
	}



	//	if(talkRes->jvm->GetEnv((void **)&env,JNI_VERSION_1_6)!=JNI_OK){
	//		LOGE("get env error");
	//		return;
	//	}

	env->CallVoidMethod(talkRes->callbackObj,talkRes->registMid,r->interval);
	LOGE("call regist mid ok");

	if (_jvm->DetachCurrentThread() != JNI_OK) {
		LOGE("%s: DetachCurrentThread() failed", __FUNCTION__);
	}

}

static void callbackDialogList(msg_get_dialogList_res_t* r){
	if(r->result!=0){
		return;
	}
	JNIEnv *env = NULL;
	//	if(talkRes->jvm->GetEnv((void **)&env,JNI_VERSION_1_4)!=JNI_OK){
	//		LOGE("get env error");
	//		return;
	//	}

	if(talkRes->jvm->AttachCurrentThread( &env, NULL) != JNI_OK) {
		LOGE("%s: AttachCurrentThread() failed", __FUNCTION__);
		return;
	}


	for(int i=0;i<r->len;i++){
		char * id = r->dialog[i].dialogId;
		char * name = r->dialog[i].userName;
		char * mobile = r->dialog[i].mobileId;
		int type = r->dialog[i].mobileType;

		LOGI("id=%s name=%s  mobile=%s type=%d",id,name,mobile,type);

		//TODO call
		jstring idStr = env->NewStringUTF(id);
		jstring nameStr = env->NewStringUTF(name);
		jstring mobileStr = env->NewStringUTF(mobile);

		env->CallVoidMethod(talkRes->callbackObj,talkRes->dialogListMid,idStr,nameStr,mobileStr,type,r->len);

		env->DeleteLocalRef(idStr);
		env->DeleteLocalRef(nameStr);
		env->DeleteLocalRef(mobileStr);

	}

	if (talkRes->jvm->DetachCurrentThread() != JNI_OK) {
		LOGE("%s: DetachCurrentThread() failed", __FUNCTION__);
	}

}

static void callbackRes(msg_result_t* r){
	JNIEnv *env = NULL;
	//	if(talkRes->jvm->GetEnv((void **)&env,JNI_VERSION_1_4)!=JNI_OK){
	//		LOGE("get env error");
	//		return;
	//	}

	if(talkRes->jvm->AttachCurrentThread( &env, NULL) != JNI_OK) {
		LOGE("%s: AttachCurrentThread() failed", __FUNCTION__);
		return;
	}



	env->CallVoidMethod(talkRes->callbackObj,talkRes->resMid,r->result);
	if (talkRes->jvm->DetachCurrentThread() != JNI_OK) {
		LOGE("%s: DetachCurrentThread() failed", __FUNCTION__);
	}


}

static void callbackReceive(msg_receive_data_t* r){
	JNIEnv *env = NULL;
	if(talkRes==NULL)return;
	if(talkRes->callbackObj==NULL || talkRes->receiveMid == NULL)return;

	//	if(talkRes->jvm->GetEnv((void **)&env,JNI_VERSION_1_4)!=JNI_OK){
	//		LOGE("get env error");
	//		return;
	//	}


	if(talkRes->jvm->AttachCurrentThread( &env, NULL) != JNI_OK) {
		LOGE("%s: AttachCurrentThread() failed", __FUNCTION__);
		return;
	}

	jstring id = env->NewStringUTF(r->senderDialogId);
	jstring name = env->NewStringUTF(r->Sender);
	env->CallVoidMethod(talkRes->callbackObj,talkRes->receiveMid,id,name);
	env->DeleteLocalRef(id);
	env->DeleteLocalRef(name);

	if (talkRes->jvm->DetachCurrentThread() != JNI_OK) {
		LOGE("%s: DetachCurrentThread() failed", __FUNCTION__);
	}
}

static void callbackCreatGroup(msg_create_group_res_t* r){
	if(talkRes==NULL)return;
	if(talkRes->callbackObj==NULL || talkRes->groupCreatMid == NULL)return;
	JNIEnv *env = NULL;
	//	if(talkRes->jvm->GetEnv((void **)&env,JNI_VERSION_1_4)!=JNI_OK){
	//		LOGE("get env error");
	//		return;
	//	}

	if(talkRes->jvm->AttachCurrentThread( &env, NULL) != JNI_OK) {
		LOGE("%s: AttachCurrentThread() failed", __FUNCTION__);
		return;
	}


	jstring id = NULL;
	if(r->result==0){
		id = env->NewStringUTF(r->id);
	}
	env->CallVoidMethod(talkRes->callbackObj,talkRes->groupCreatMid,id,r->id);
	if(id!=NULL){
		env->DeleteLocalRef(id);
	}


	if (talkRes->jvm->DetachCurrentThread() != JNI_OK) {
		LOGE("%s: DetachCurrentThread() failed", __FUNCTION__);
	}
}

static void callbackAllRes(msg_result_t*r){
	if(talkRes==NULL)return;
	if(talkRes->callbackObj == NULL||talkRes->allResMid==NULL)return;
	JNIEnv *env = NULL;
	//	if(talkRes->jvm->GetEnv((void **)&env,JNI_VERSION_1_4)!=JNI_OK){
	//		LOGE("get env error");
	//		return;
	//	}


	if(talkRes->jvm->AttachCurrentThread( &env, NULL) != JNI_OK) {
		LOGE("%s: AttachCurrentThread() failed", __FUNCTION__);
		return;
	}

	env->CallVoidMethod(talkRes->callbackObj,talkRes->allResMid,r->result);


	if (talkRes->jvm->DetachCurrentThread() != JNI_OK) {
		LOGE("%s: DetachCurrentThread() failed", __FUNCTION__);
	}

}

static void callbackGetGroupList(msg_get_group_res_t *r){
	if(talkRes==NULL)return;
	if(talkRes->callbackObj==NULL || talkRes->GroupsListMid == NULL)return;
	JNIEnv *env = NULL;
	//	if(talkRes->jvm->GetEnv((void **)&env,JNI_VERSION_1_4)!=JNI_OK){
	//		LOGE("get env error");
	//		return;
	//	}


	if(talkRes->jvm->AttachCurrentThread( &env, NULL) != JNI_OK) {
		LOGE("%s: AttachCurrentThread() failed", __FUNCTION__);
		return;
	}


	int res = r->result;
	if(res !=0){
		env->CallVoidMethod(talkRes->callbackObj,talkRes->GroupsListMid,NULL,NULL,false,0,res);
		return;
	}
	jobjectArray stringArray = NULL;
	jobjectArray memberArray = NULL;
	for(int i=0;i<r->groupLen;i++){
		jstring id	 = env->NewStringUTF(r->group[i].groupId);
		jstring name = env->NewStringUTF(r->group[i].groupName);
		jstring own  = env->NewStringUTF(r->group[i].owner);
		jstring time = env->NewStringUTF(r->group[i].creatTime);

		stringArray = env->NewObjectArray(4, env->FindClass("java/lang/String"), 0);

		env->SetObjectArrayElement(stringArray, 0, id);
		env->SetObjectArrayElement(stringArray, 1, name);
		env->SetObjectArrayElement(stringArray, 2, own);
		env->SetObjectArrayElement(stringArray, 3, time);
		int len = r->group[i].membersLen;

		//		int len = env->GetArrayLength((jarray)r->group[i].members);
		memberArray = env->NewObjectArray(len, env->FindClass("java/lang/String"), 0);
		for(int j=0;j<len;j++){
			env->SetObjectArrayElement(memberArray,j,env->NewStringUTF(r->group[i].members[j]));
		}
		env->CallVoidMethod(talkRes->callbackObj,talkRes->GroupsListMid,stringArray,memberArray,r->group[i].isSilent,r->groupLen,0);
	}
	if(stringArray!=NULL){
		env->DeleteLocalRef(stringArray);
	}
	if(memberArray!=NULL){
		env->DeleteLocalRef(memberArray);
	}



	if (talkRes->jvm->DetachCurrentThread() != JNI_OK) {
		LOGE("%s: DetachCurrentThread() failed", __FUNCTION__);
	}

}

static void callbackGetGroup(msg_get_group_res_t *r){
	if(talkRes==NULL)return;
	if(talkRes->callbackObj==NULL || talkRes->groupMid == NULL)return;
	JNIEnv *env = NULL;
	//	if(talkRes->jvm->GetEnv((void **)&env,JNI_VERSION_1_4)!=JNI_OK){
	//		LOGE("get env error");
	//		return;
	//	}



	if(talkRes->jvm->AttachCurrentThread( &env, NULL) != JNI_OK) {
		LOGE("%s: AttachCurrentThread() failed", __FUNCTION__);
		return;
	}

	int res = r->result;
	if(res !=0){
		env->CallVoidMethod(talkRes->callbackObj,talkRes->groupMid,NULL,NULL,false,0,res);
		return;
	}
	jobjectArray stringArray = NULL;
	jobjectArray memberArray = NULL;
	for(int i=0;i<r->groupLen;i++){
		jstring id	 = env->NewStringUTF(r->group[i].groupId);
		jstring name = env->NewStringUTF(r->group[i].groupName);
		jstring own  = env->NewStringUTF(r->group[i].owner);
		jstring time = env->NewStringUTF(r->group[i].creatTime);

		stringArray = env->NewObjectArray(4, env->FindClass("java/lang/String"), 0);

		env->SetObjectArrayElement(stringArray, 0, id);
		env->SetObjectArrayElement(stringArray, 1, name);
		env->SetObjectArrayElement(stringArray, 2, own);
		env->SetObjectArrayElement(stringArray, 3, time);
		int len = r->group[i].membersLen;

		//		int len = env->GetArrayLength((jarray)r->group[i].members);
		memberArray = env->NewObjectArray(len, env->FindClass("java/lang/String"), 0);
		for(int j=0;j<len;j++){
			env->SetObjectArrayElement(memberArray,j,env->NewStringUTF(r->group[i].members[j]));
		}
		env->CallVoidMethod(talkRes->callbackObj,talkRes->groupMid,stringArray,memberArray,r->group[i].isSilent,r->groupLen,0);
	}
	if(stringArray!=NULL){
		env->DeleteLocalRef(stringArray);
	}
	if(memberArray!=NULL){
		env->DeleteLocalRef(memberArray);
	}


	if (talkRes->jvm->DetachCurrentThread() != JNI_OK) {
		LOGE("%s: DetachCurrentThread() failed", __FUNCTION__);
	}
}

static void callbackGroupReceive(msg_group_receive_data_t* r){
	if(talkRes==NULL)return;
	if(talkRes->callbackObj==NULL || talkRes->groupReceiveMid == NULL)return;
	JNIEnv *env = NULL;
	//	if(talkRes->jvm->GetEnv((void **)&env,JNI_VERSION_1_4)!=JNI_OK){
	//		LOGE("get env error");
	//		return;
	//	}

	if(talkRes->jvm->AttachCurrentThread( &env, NULL) != JNI_OK) {
		LOGE("%s: AttachCurrentThread() failed", __FUNCTION__);
		return;
	}

	jstring groupId 	= env->NewStringUTF(r->groupId);
	jstring senderId    = env->NewStringUTF(r->senderId);
	jstring sender		= env->NewStringUTF(r->sender);
	env->CallVoidMethod(talkRes->callbackObj,talkRes->groupReceiveMid,groupId,senderId,sender,r->contentType);
	env->DeleteLocalRef(groupId);
	env->DeleteLocalRef(senderId);
	env->DeleteLocalRef(sender);

	if (talkRes->jvm->DetachCurrentThread() != JNI_OK) {
		LOGE("%s: DetachCurrentThread() failed", __FUNCTION__);
	}
}

static void callbackGetUsers(msg_get_users_res_t* r){
	if(talkRes==NULL)return;
	if(talkRes->callbackObj==NULL || talkRes->usersMid == NULL)return;
	JNIEnv *env = NULL;
	//	if(talkRes->jvm->GetEnv((void **)&env,JNI_VERSION_1_4)!=JNI_OK){
	//		LOGE("get env error");
	//		return;
	//	}

	if(talkRes->jvm->AttachCurrentThread( &env, NULL) != JNI_OK) {
		LOGE("%s: AttachCurrentThread() failed", __FUNCTION__);
		return;
	}

	if(r->result!=0){
		env->CallVoidMethod(talkRes->callbackObj,talkRes->usersMid,NULL,0,r->result,false,false,0,0,0);
		return;
	}

	int userLen = r->user_len;
	for(int i=0;i<userLen;i++){
		int strlen = 0;
		jstring nickName = NULL;
		jstring userId 	 = env->NewStringUTF(r->user[i].userId);
		jstring userName = env->NewStringUTF(r->user[i].userName);

		if(strcmp(r->user[i].nickName,"")==0){
			strlen = 2;
		}else{
			strlen = 3;
			nickName = env->NewStringUTF(r->user[i].nickName);
		}
		jobjectArray stringArray = env->NewObjectArray(strlen, env->FindClass("java/lang/String"), 0);
		env->SetObjectArrayElement(stringArray, 0, userId);
		env->SetObjectArrayElement(stringArray, 1, userName);
		if(strlen==3){
			env->SetObjectArrayElement(stringArray, 2, nickName);
		}
		env->CallVoidMethod(talkRes->callbackObj,talkRes->usersMid,
				stringArray,strlen,0,r->user[i].isOnline,r->user[i].isSilent,0,userLen,0);

		if(nickName!=NULL){
			env->DeleteLocalRef(nickName);
		}
		env->DeleteLocalRef(userId);
		env->DeleteLocalRef(userName);
		env->DeleteLocalRef(stringArray);

	}

	//LOGE("set user ok");


	int dialogLen = r->dialog_len;
	//	LOGE("dialogLen=%d",dialogLen);
	for(int i=0;i<dialogLen;i++){
		//LOGE("dialogID=%s",r->dialog[i].dialogId);
		jstring dialogId = env->NewStringUTF(r->dialog[i].dialogId);
		jstring userName = env->NewStringUTF(r->dialog[i].userName);
		jstring mobileId = env->NewStringUTF(r->dialog[i].mobileId);

		jobjectArray stringArray = env->NewObjectArray(3, env->FindClass("java/lang/String"), 0);

		env->SetObjectArrayElement(stringArray, 0, dialogId);
		env->SetObjectArrayElement(stringArray, 1, userName);
		env->SetObjectArrayElement(stringArray, 2, mobileId);
		//	LOGE("aaaaaaaaaa");
		env->CallVoidMethod(talkRes->callbackObj,talkRes->usersMid,
				stringArray,3,0,false,false,r->dialog[i].mobileType,dialogLen,1);

		env->DeleteLocalRef(dialogId);
		env->DeleteLocalRef(userName);
		env->DeleteLocalRef(mobileId);
		env->DeleteLocalRef(stringArray);
	}


	if (talkRes->jvm->DetachCurrentThread() != JNI_OK) {
		LOGE("%s: DetachCurrentThread() failed", __FUNCTION__);
	}
}

int on_talk_res_fun(int msgCommand,void * res,int len){
	LOGE("get call back   command=0x%x",msgCommand);
	switch(msgCommand){
	case HWVOICE_PROTOCOL_ACK+HWVOICE_PROTOCOL_REGISTER:{
		callbackRegisterRes((msg_register_res_t*)res);
		break;
	}
	case HWVOICE_PROTOCOL_ACK+HWVOICE_PROTOCOL_DIALOG_LIST:
	{
		callbackDialogList((msg_get_dialogList_res_t*)res);
		break;
	}
	case HWVOICE_PROTOCOL_ACK+HWVOICE_PROTOCOL_SENDING:
	case HWVOICE_PROTOCOL_ACK+HWVOICE_PROTOCOL_BOARDCAST_SENDING:
	case HWVOICE_PROTOCOL_ACK+HWVOICE_PROTOCOL_GROUP_SENDING:
	{
		callbackRes((msg_result_t*)res);
		break;
	}
	case HWVOICE_PROTOCOL_RECEINING:
	{
		LOGE("callback receive");
		callbackReceive((msg_receive_data_t*)res);
		break;
	}
	case HWVOICE_PROTOCOL_ACK+HWVOICE_PROTOCOL_CREATE_GROUP:
	{
		callbackCreatGroup((msg_create_group_res_t*)res);
		break;
	}
	case HWVOICE_PROTOCOL_ACK+HWVOICE_PROTOCOL_DELETE_GROUP:
	case HWVOICE_PROTOCOL_ACK+HWVOICE_PROTOCOL_SILENT:
	case HWVOICE_PROTOCOL_ACK+HWVOICE_PROTOCOL_SET_GROUP:
	case HWVOICE_PROTOCOL_ACK+HWVOICE_PROTOCOL_SILENT_GROUP:
	{
		callbackAllRes((msg_result_t*)res);
		break;
	}
	case HWVOICE_PROTOCOL_ACK+HWVOICE_PROTOCOL_GET_GROUPS:
	{
		callbackGetGroupList((msg_get_group_res_t*)res);
		break;
	}
	case HWVOICE_PROTOCOL_ACK+HWVOICE_PROTOCOL_GET_GROUP:
	{
		callbackGetGroup((msg_get_group_res_t*)res);
		break;
	}
	case HWVOICE_PROTOCOL_GROUP_RECEIVING:
	{
		callbackGroupReceive((msg_group_receive_data_t*)res);
		break;
	}
	case HWVOICE_PROTOCOL_ACK+HWVOICE_PROTOCOL_GET_USERS:
	case HWVOICE_PROTOCOL_ACK+HWVOICE_PROTOCOL_GET_USER:
	{
		callbackGetUsers((msg_get_users_res_t*)res);
		break;
	}




	default:
		break;


	}


	return 0;
}

int on_talk_voice_json(const char *jsonStr,int len){
	LOGE("on talk_voice_json");
//	return 0;
	if(talkRes==NULL)return -1;
	if(talkRes->callbackObj==NULL)return -1;

	JNIEnv *env = NULL;
	//	if(talkRes->jvm->GetEnv((void **)&env,JNI_VERSION_1_4)!=JNI_OK){
	//		LOGE("get env error");
	//		return;
	//	}

	JavaVM * _jvm = talkRes->jvm;
	if(_jvm->AttachCurrentThread( &env, NULL) != JNI_OK) {
		LOGE("%s: AttachCurrentThread() failed", __FUNCTION__);
		return 0;
	}

	jclass cls = env->GetObjectClass(talkRes->callbackObj);

	jmethodID mid = env->GetMethodID(cls, "onAudioJson", "(Ljava/lang/String;)V");


//	LOGE("%s",jsonStr);
//	LOGE("~~~~~~~~~   strlen=%d      len=%d ",strlen(jsonStr),len);




	jstring str = env->NewStringUTF(jsonStr);
//	jstring str = env->NewString((const jchar*)jsonStr,len);



	env->CallVoidMethod(talkRes->callbackObj,mid,str);

	LOGE("call on audio json ok");

	env->DeleteLocalRef(str);

	if (_jvm->DetachCurrentThread() != JNI_OK) {
		LOGE("%s: DetachCurrentThread() failed", __FUNCTION__);
	}

	LOGE("detach talk res thread ok");

	return 0;





#if 0
	if(self==NULL){
		LOGE("self==NULL");
		return 0;
	}

	if(self->obj == NULL){
		LOGE("self.obj = null self.test=%d",self->test);
		return 0;
	}
	if (self->jvm->AttachCurrentThread( &self->env, NULL) != JNI_OK) {
		LOGE("%s: AttachCurrentThread() failed", __FUNCTION__);
		return 0;
	}
	/* get JAVA method first */
	if (!self->jsonMethod_ready) {
		jclass cls;
		cls = self->env->GetObjectClass(self->obj);
		if (cls == NULL) {
			LOGE("FindClass() Error.....");
			if (self->jvm->DetachCurrentThread() != JNI_OK) {
				LOGE("%s: DetachCurrentThread() failed", __FUNCTION__);
			}
			return 0;
		}
		//�ٻ�����еķ���
		self->jsonMid = self->env->GetMethodID(cls, "onAudioJson", "(Ljava/lang/String;)V");
		if (self->jsonMid == NULL) {
			LOGE("GetMethodID() Error.....");
			if (self->jvm->DetachCurrentThread() != JNI_OK) {
				LOGE("%s: DetachCurrentThread() failed", __FUNCTION__);
			}

			return 0;
		}

		self->jsonMethod_ready=1;
	}

	jstring str = self->env->NewStringUTF(jsonStr);

	self->env->CallVoidMethod( self->obj, self->jsonMid, str);
	self->env->DeleteLocalRef(str);

	if (self->jvm->DetachCurrentThread() != JNI_OK) {
		LOGE("%s: DetachCurrentThread() failed", __FUNCTION__);
	}
	/* char* data = (*self.env)->GetByteArrayElements(self.env,self.data_array,0); */
	/* memcpy(data,buf,len); */
	return 0;

#endif
}


static int register2svr(const char *userName,const char *password,const char *mobileId,int channel,
		const char *ip,int port){
	return voice_register2svr(userName,password,mobileId,1,channel,0,0,ip,port,on_talk_voice_fun,on_talk_res_fun,on_talk_voice_json);
}




JNIEXPORT void JNICALL Java_com_howell_formuseum_JNIManager_talkInit
(JNIEnv *env, jobject cls){
	LOGE("talk init");
	if(talkRes==NULL){
		talkRes = (struct TalkStreamResource *)malloc(sizeof(struct TalkStreamResource));
		memset(talkRes,0,sizeof(struct TalkStreamResource));
		env->GetJavaVM(&talkRes->jvm);
		talkRes->method_ready = 0;
	}

}

JNIEXPORT void JNICALL Java_com_howell_formuseum_JNIManager_talkDeInit
(JNIEnv *env, jobject){
	return;//we don't deinit
	if(talkRes!=NULL){
		if(talkRes->callbackObj!=NULL){
			env->DeleteGlobalRef(talkRes->callbackObj);
			talkRes->callbackObj = NULL;
		}
		free(talkRes);
		talkRes = NULL;
		LOGI("talk deInit");
	}
}





JNIEXPORT jboolean JNICALL Java_com_howell_formuseum_JNIManager_talkRegister2service
(JNIEnv *env, jobject, jstring name, jstring pwd, jstring id, jint channel, jstring ip, jint port){

	if(name==NULL){
		LOGE("name = NULL");
	}
	if(pwd==NULL){
		LOGE("pwd==NULL");
	}

	if(id==NULL){
		LOGE("id = NULL");
	}
	if(ip==NULL){
		LOGE("ip==NULL");
	}

	const char *_name 			= env->GetStringUTFChars(name,NULL);
	const char *_pwd			= env->GetStringUTFChars(pwd,NULL);
	const char *_id				= env->GetStringUTFChars(id,NULL);
	const char *_ip				= env->GetStringUTFChars(ip,NULL);

	LOGI("name=%s pwd=%s id=%s ip=%s",_name,_pwd,_id,_ip);
	int ret = register2svr(_name,_pwd,_id,channel,_ip,port);

	env->ReleaseStringUTFChars(name,_name);
	env->ReleaseStringUTFChars(pwd,_pwd);
	env->ReleaseStringUTFChars(id,_id);
	env->ReleaseStringUTFChars(ip,_ip);
	return ret==0?true:false;
}

JNIEXPORT void JNICALL Java_com_howell_formuseum_JNIManager_talkUnregister
(JNIEnv *, jobject){
	int ret = voice_unregister2svr();
	LOGE("voice_unregister2svr ret = %d",ret);
}


JNIEXPORT void JNICALL Java_com_howell_formuseum_JNIManager_talkSetCallbackObject
(JNIEnv *env, jobject, jobject o, jint f){
	if(talkRes==NULL)return;
	switch(f){
	case 0:
		talkRes->callbackObj = env->NewGlobalRef(o);
		break;
	}
}


JNIEXPORT void JNICALL Java_com_howell_formuseum_JNIManager_talkSetCallbackMethodName
(JNIEnv *env, jobject, jstring s, jint f){

	if(talkRes==NULL)return;
	const char * str = env->GetStringUTFChars(s,NULL);
	switch(f){
	case 0:{
		//strcpy(talkRes->registMethodCallBack,str);
		jclass cls = env->GetObjectClass(talkRes->callbackObj);
		talkRes->registMid = env->GetMethodID(cls,str,"(I)V");
	}
	break;
	case 1:
	{
		jclass cls = env->GetObjectClass(talkRes->callbackObj);
		talkRes->dialogListMid = env->GetMethodID(cls,str,"(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;II)V");
	}
	break;
	case 2:
	{
		jclass cls = env->GetObjectClass(talkRes->callbackObj);
		talkRes->resMid = env->GetMethodID(cls,str,"(I)V");
	}
	break;
	case 3:
	{
		jclass cls = env->GetObjectClass(talkRes->callbackObj);
		talkRes->receiveMid = env->GetMethodID(cls,str,"(Ljava/lang/String;Ljava/lang/String;)V");
	}
	break;
	case 4:
	{
		jclass cls = env->GetObjectClass(talkRes->callbackObj);
		talkRes->groupCreatMid = env->GetMethodID(cls,str,"(Ljava/lang/String;I)V");
	}
	break;
	case 5:
	{
		jclass cls = env->GetObjectClass(talkRes->callbackObj);
		talkRes->allResMid = env->GetMethodID(cls,str,"(I)V");
	}
	break;
	case 6:
	{
		jclass cls = env->GetObjectClass(talkRes->callbackObj);
		talkRes->GroupsListMid = env->GetMethodID(cls,str,"([Ljava/lang/String;[Ljava/lang/String;ZII)V");
	}
	break;
	case 7:
	{
		jclass cls = env->GetObjectClass(talkRes->callbackObj);
		talkRes->groupMid = env->GetMethodID(cls,str,"([Ljava/lang/String;[Ljava/lang/String;ZII)V");
	}
	break;
	case 8:
	{
		jclass cls = env->GetObjectClass(talkRes->callbackObj);
		talkRes->groupReceiveMid = env->GetMethodID(cls,str,"(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;I)V");
	}
	break;
	case 9:
	{
		jclass cls = env->GetObjectClass(talkRes->callbackObj);
		talkRes->usersMid = env->GetMethodID(cls,str,"([Ljava/lang/String;IIZZIII)V");
	}
	break;


	default:
		break;
	}
	env->ReleaseStringUTFChars(s,str);
}

JNIEXPORT void JNICALL Java_com_howell_formuseum_JNIManager_talkSetHeartBeat
(JNIEnv *, jobject){
	voice_send_register_heartbeat();
}


JNIEXPORT jint JNICALL Java_com_howell_formuseum_JNIManager_talkGetRegisterState
(JNIEnv *, jobject){
	int state = 0;
	int ret = voice_get_register_state(&state);
	return state;
}

JNIEXPORT void JNICALL Java_com_howell_formuseum_JNIManager_talkGetDialogList
(JNIEnv *env, jobject, jint type){
	voice_get_channel_user_info(type);
}

JNIEXPORT void JNICALL Java_com_howell_formuseum_JNIManager_talkSetNextTarget
(JNIEnv *env, jobject, jobjectArray ids, jobjectArray names){
	if(talkRes==NULL)return;
	int idLen = 0,nameLen = 0;
	char **idArray = NULL;
	char **nameArray = NULL;
	if(ids!=NULL){
		idLen = env->GetArrayLength(ids);
		idArray = (char **)malloc(idLen*sizeof(char *));
		for(int i=0;i<idLen;i++){
			jobject value = env->GetObjectArrayElement(ids,i);
			idArray[i] = (char *)env->GetStringUTFChars((jstring)value,NULL);
			LOGI("id %d   %s       ",i,idArray[i]);
		}
	}
	if(names!=NULL){
		nameLen = env->GetArrayLength(names);
		nameArray = (char **)malloc(nameLen*sizeof(char *));
		for(int i=0;i<nameLen;i++){
			jobject value = env->GetObjectArrayElement(names,i);
			nameArray[i] = (char *)env->GetStringUTFChars((jstring)value,NULL);
		}
	}
	//TODO
	int ret = voice_set_target(idArray,idLen,nameArray,nameLen);
	LOGE("set target ret=%d",ret);
	//release
	if(idArray!=NULL){
		for(int i=0;i<idLen;i++){
			//			LOGE("id i=%d   %s",i,idArray[i]);
			env->ReleaseStringUTFChars((jstring)env->GetObjectArrayElement(ids,i),(const char *)idArray[i]);
		}
		free(idArray);
		idArray = NULL;
	}
	if(nameArray!=NULL){
		for(int i=0;i<nameLen;i++){
			//			LOGE("name i=%d   %s",i,nameArray[i]);
			env->ReleaseStringUTFChars((jstring)env->GetObjectArrayElement(names,i),(const char *)nameArray[i]);
		}
		free(nameArray);
		nameArray = NULL;
	}
}

JNIEXPORT jint JNICALL Java_com_howell_formuseum_JNIManager_setBoardcastData
(JNIEnv *env, jobject, jbyteArray bytes, jint len){
	//	int voice_type = 1;
	char *temp = (char *)env->GetByteArrayElements(bytes,NULL);
	if(temp == NULL){
		LOGE("setData temp == null");
		return -1;
	}
	//	int ret = voice_input_voice_data(voice_type,temp,len);
	int ret = voice_input_boardcast_data(0,temp,len);
	env->ReleaseByteArrayElements(bytes,(jbyte*)temp,0);
	return ret;
}

JNIEXPORT void JNICALL Java_com_howell_formuseum_JNIManager_talkSetSilent
(JNIEnv *env, jobject obj, jstring userName){
	const char * _userName = env->GetStringUTFChars(userName,NULL);
	voice_silent(_userName);
	env->ReleaseStringUTFChars(userName,_userName);
}

JNIEXPORT void JNICALL Java_com_howell_formuseum_JNIManager_talkCreateGroup
(JNIEnv *env, jobject, jstring groupName, jobjectArray memberNames, jint memberLen){
	const char * _groupName = env->GetStringUTFChars(groupName,NULL);
	char ** members = NULL;
	int len = env->GetArrayLength(memberNames);
	members = (char **)malloc(memberLen*sizeof(char *));
	for(int i=0;i<memberLen;i++){
		jobject val = env->GetObjectArrayElement(memberNames,i);
		members[i] = (char *)env->GetStringUTFChars((jstring)val,NULL);
	}
	voice_create_group(_groupName,(const char **)members,memberLen);
	//release
	env->ReleaseStringUTFChars(groupName,_groupName);
	if(members!=NULL){
		for(int i=0;i<memberLen;i++){
			env->ReleaseStringUTFChars((jstring)env->GetObjectArrayElement(memberNames,i),(const char *)members[i]);
		}
		free(members);
		members = NULL;
	}
}

JNIEXPORT void JNICALL Java_com_howell_formuseum_JNIManager_talkDeleteGroup
(JNIEnv *env, jobject, jstring groupId){
	const char * id = env->GetStringUTFChars(groupId,NULL);
	voice_delete_group(id);
	env->ReleaseStringUTFChars(groupId,id);
}

JNIEXPORT void JNICALL Java_com_howell_formuseum_JNIManager_talkUpdataGroup
(JNIEnv *env, jobject, jstring id, jstring name, jobjectArray adds, jobjectArray removes){

	const char * _id 		= env->GetStringUTFChars(id,0);
	char * _name 			= NULL;
	char ** _adds 			= NULL;
	char ** _removes 		= NULL;
	int addLen 				= 0;
	int removeLen			= 0;
	if(name != NULL){
		_name = (char *)env->GetStringUTFChars(name,0);
	}

	if(adds != NULL){
		addLen = env->GetArrayLength(adds);
		_adds = (char **)malloc(addLen*sizeof(char *));
		for(int i=0;i<addLen;i++){
			jobject val = env->GetObjectArrayElement(adds,i);
			_adds[i] = (char *)env->GetStringUTFChars((jstring)val,NULL);
		}
	}

	if(removes != NULL){
		removeLen = env->GetArrayLength(removes);
		_removes = (char **)malloc(removeLen*sizeof(char *));
		for(int i=0;i<removeLen;i++){
			jobject val = env->GetObjectArrayElement(removes,i);
			_removes[i] = (char *)env->GetStringUTFChars((jstring)val,NULL);
		}
	}
	voice_updata_group(_id,(const char *)_name,(const char **)_adds,addLen,(const char **)_removes,removeLen);

	//release
	env->ReleaseStringUTFChars(id,_id);
	if(_name!=NULL){
		env->ReleaseStringUTFChars(name,_name);
	}
	if(_adds!=NULL){
		for(int i=0;i<addLen;i++){
			env->ReleaseStringUTFChars((jstring)env->GetObjectArrayElement(adds,i),(const char *)_adds[i]);
		}
		free(_adds);
		_adds = NULL;
	}
	if(_removes!=NULL){
		for(int i=0;i<removeLen;i++){
			env->ReleaseStringUTFChars((jstring)env->GetObjectArrayElement(removes,i),(const char *)_removes[i]);
		}
		free(_removes);
		_removes = NULL;
	}
}

JNIEXPORT void JNICALL Java_com_howell_formuseum_JNIManager_talkGetGroups
(JNIEnv *env, jobject, jstring userName){
	const char *name = env->GetStringUTFChars(userName,0);
	LOGI("userName=%s",name);
	voice_get_groups(name);
	env->ReleaseStringUTFChars(userName,name);

	//test
#if 0
	LOGE("get groups test");
	msg_get_group_res_t *res = (msg_get_group_res_t *)malloc(sizeof(msg_get_group_res_t));
	memset(res,0,sizeof(res));
	if(res==NULL){
		LOGE("res==NULL");
		return;
	}
	res->result = 0;
	res->groupLen = 3;

	group_t* groupArray = (group_t*)malloc(3*sizeof(group_t));
	memset(groupArray,0,sizeof(3*sizeof(group_t)));
	for(int i=0;i<3;i++){
		sprintf(groupArray[i].groupId,"id:%d",i);
		sprintf(groupArray[i].groupName,"name:%d",i);
		sprintf(groupArray[i].owner,"owner:%d",i);
		sprintf(groupArray[i].creatTime,"time:%d",i);
		groupArray[i].isSilent = true;

		groupArray[i].membersLen = 2;
		groupArray[i].members[0] = (char*)malloc(strlen("aaaa")+1);
		memset(groupArray[i].members[0],0,strlen("aaaa")+1);
		strcpy(groupArray[i].members[0],"aaaa");
		groupArray[i].members[1] = (char*)malloc(strlen("bbbb")+1);
		memset(groupArray[i].members[1],0,strlen("bbbb")+1);
		strcpy(groupArray[i].members[1],"bbbb");
	}
	res->group = groupArray;
	LOGI("res = %d   len=%d",res->result,res->groupLen);
	for(int i=0;i<res->groupLen;i++){
		LOGI("[%d]  id:%s   name:%s  members[0]=%s members[1]=%s",i,res->group[i].groupId,res->group[i].groupName,res->group[i].members[0],
				res->group[i].members[1]);
	}
	callbackGetGroup((msg_get_group_res_t*)res);

#endif
}

JNIEXPORT void JNICALL Java_com_howell_formuseum_JNIManager_talkGetGroup
(JNIEnv *env, jobject, jstring groupId){
	const char *id = env->GetStringUTFChars(groupId,0);
	voice_get_group(id);
	env->ReleaseStringUTFChars(groupId,id);
}

JNIEXPORT jint JNICALL Java_com_howell_formuseum_JNIManager_setGroupData
(JNIEnv *env, jobject, jstring groupId, jbyteArray bytes, jint len){
	const char *id = env->GetStringUTFChars(groupId,0);
	char *temp = (char *)env->GetByteArrayElements(bytes,NULL);
	if(temp == NULL){
		LOGE("setData temp == null");
		return -1;
	}
	int ret = voice_input_group_data(id,temp,len);
	env->ReleaseStringUTFChars(groupId,id);
	env->ReleaseByteArrayElements(bytes,(jbyte*)temp,0);
	return ret;
}

JNIEXPORT jint JNICALL Java_com_howell_formuseum_JNIManager_setReceiveRes
(JNIEnv *, jobject, jint r,jint s){
	voice_set_receive_result(r,s);
	return 0;
}

JNIEXPORT void JNICALL Java_com_howell_formuseum_JNIManager_talkSilentGroup
(JNIEnv *env, jobject, jstring groupId){
	const char * id = env->GetStringUTFChars(groupId,0);
	voice_silent_group(id);
	env->ReleaseStringUTFChars(groupId,id);
}

JNIEXPORT void JNICALL Java_com_howell_formuseum_JNIManager_talkOnOfflineNotice
(JNIEnv *env, jobject, jstring dialogId, jstring userName, jboolean isOnline){
	const char * id = env->GetStringUTFChars(dialogId,0);
	const char * name = env->GetStringUTFChars(userName,0);
	voice_onOffline_notice(id,name,isOnline);
	env->ReleaseStringUTFChars(dialogId,id);
	env->ReleaseStringUTFChars(userName,name);
}

JNIEXPORT void JNICALL Java_com_howell_formuseum_JNIManager_talkGetUsers
(JNIEnv *env, jobject, jstring userName, jint isOnline, jint isSilent){
	const char * name = env->GetStringUTFChars(userName,0);
	voice_get_users_info(name,isOnline,isSilent);
	env->ReleaseStringUTFChars(userName,name);

	//test
#if 0

	msg_get_users_res_t *res = (msg_get_users_res_t*)malloc(sizeof(msg_get_users_res_t));
	memset(res,0,sizeof(*res));
	res->result = 0;
	res->user_len = 2;
	user_t * userArray = (user_t*)malloc(2*sizeof(user_t));
	//memset(userArray,0,2*sizeof(user_t));
	for(int i=0;i<2;i++){


		sprintf(userArray[i].userId,"user id:%d",i);
		sprintf(userArray[i].userName,"user name:%d",i);
		userArray[i].isSilent = true;
		userArray[i].isOnline = false;
	}
	res->user = userArray;

	for(int i=0;i<res->user_len;i++){
		LOGI("%s",res->user[i].userId);
	}



	res->dialog_len = 4;
	dialog_t* dialogArray = (dialog_t*)malloc(4*sizeof(dialog_t));
	//memset(dialogArray,0,4*sizeof(dialog_t));
	for(int i=0;i<4;i++){
		sprintf(dialogArray[i].dialogId,"dialog id:%d",i);
		sprintf(dialogArray[i].userName,"user name:%d",i);
		sprintf(dialogArray[i].mobileId,"mobile id:%d",i);
		dialogArray[i].mobileType = 0;


	}
	res->dialog = dialogArray;


	for(int i=0;i<res->dialog_len;i++){
		LOGI("%s",res->dialog[i].dialogId);
	}







	LOGE("set test");
	callbackGetUsers((msg_get_users_res_t*)res);



#endif


}

JNIEXPORT void JNICALL Java_com_howell_formuseum_JNIManager_talkGetUser
(JNIEnv *env, jobject, jstring userName, jstring userId){
	const char *name = NULL;
	const char *id	= NULL;
	if(userName!=NULL){
		name = env->GetStringUTFChars(userName,0);
	}
	if(userId != NULL){
		id = env->GetStringUTFChars(userId,0);
	}
	voice_get_user_info(name,id);
	if(name!=NULL){
		env->ReleaseStringUTFChars(userName,name);
	}
	if(id!=NULL){
		env->ReleaseStringUTFChars(userId,id);
	}

}

JNIEXPORT jint JNICALL Java_com_howell_formuseum_JNIManager_setReceiveGroup
(JNIEnv *, jobject, jint res,jint s){
	voice_set_group_receive_result(res,s);
	return 0;
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
	int state = 0;
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
		LOGE("setData temp == null");
		return -1;
	}
	int ret = voice_input_voice_data(voice_type,temp,len);

	env->ReleaseByteArrayElements(bytes,(jbyte*)temp,0);

	return ret;
}


JNIEXPORT jint JNICALL Java_com_howell_formuseum_JNIManager_setAudioData
(JNIEnv *env, jobject, jstring dataStr, jint len){
	LOGI("set audio data");
	const char * tmp = env->GetStringUTFChars(dataStr,0);
	LOGI("len = %d    strlen=%d",len,strlen(tmp));
	//int ret = voice_input_voice_data(1,tmp,strlen(tmp));
	int ret = voice_input_voice_json((char *)tmp,len);

	env->ReleaseStringUTFChars(dataStr,tmp);
	return ret;
}


JNIEXPORT jbyteArray JNICALL Java_com_howell_formuseum_JNIManager_pcm2G711u
(JNIEnv *env, jobject obj, jbyteArray _src,jint srclen,jbyteArray _dst){
	jsize bufsize = env->GetArrayLength(_src);
//	LOGE("pcm  size=%d",bufsize);
	bufsize = srclen;
//	LOGE("srclen=%d",srclen);

#if 0
	unsigned short* src = (unsigned short*)env->GetByteArrayElements(_src, 0);
	unsigned short* dst = (unsigned short*)malloc(sizeof(unsigned short) * (bufsize/2));
	unsigned short i;
	short data=0;
	unsigned short isNegative;
	short nOut;
	short lowByte = 1;
	// -----------------  encoder -------------------------

	for (i = 0; i < bufsize / 2; i++) {
		data = *(src + i);
		data >>= 2;
		isNegative = (data < 0 ? 1 : 0);
		if (isNegative)
			data = -data;
		if (data <= 1) {
			nOut = (char) data;
		} else if (data <= 31) {
			nOut = ((data - 1) >> 1) + 1;
		} else if (data <= 95) {
			nOut = ((data - 31) >> 2) + 16;
		} else if (data <= 223) {
			nOut = ((data - 95) >> 3) + 32;
		} else if (data <= 479) {
			nOut = ((data - 223) >> 4) + 48;
		} else if (data <= 991) {
			nOut = ((data - 479) >> 5) + 64;
		} else if (data <= 2015) {
			nOut = ((data - 991) >> 6) + 80;
		} else if (data <= 4063) {
			nOut = ((data - 2015) >> 7) + 96;
		} else if (data <= 7903) {
			nOut = ((data - 4063) >> 8) + 112;
		} else {
			nOut = 127;
		}
		if (isNegative) {
			nOut = 127 - nOut;
		} else {
			nOut = 255 - nOut;
		}
		if (lowByte)
			*(dst + (i >> 1)) = (nOut & 0x00FF);
		else
			*(dst + (i >> 1)) |= ((nOut << 8) & 0xFF00);
		lowByte ^= 0x1;
	}

	//----------------------------encode  end -------------------------------
#else
	char * src = (char *)env->GetByteArrayElements(_src,0);
	//char *dst = (char *)malloc(sizeof(char)*(bufsize/2));

	char dst[4096];
	int ret = voice_pcm2g711u((const char*)src,(char *)dst,bufsize);
//	LOGI("voice_pcm2g711u ret=%d",ret);

#endif
	unsigned char* pdata = (unsigned char*)(&(dst[0]));
	// 得到的pdata 就是 g711数据。可以转成byte[] 数组，然后发送，或者播放。
	int size =  bufsize / 2;
	//unsigned short* g7data = (unsigned short*)env->GetByteArrayElements(_dst, 0);

	char * g7data = (char *)env->GetByteArrayElements(_dst,0);
	jsize dstSize = env->GetArrayLength(_dst);
//	LOGI("dstSize = %d",dstSize);
	memcpy(g7data, dst, size);

//	env->SetByteArrayRegion(_dst,0,size,(const jbyte *)pdata);

//	for(int i=0;i<100;i++){
//			LOGI("dst=%x",pdata[i]);
//	}
	//free(dst);
	env->ReleaseByteArrayElements(_src,(signed char *)src,0);
	env->ReleaseByteArrayElements(_dst,(signed char *)g7data,0);
	//	env->DeleteLocalRef(_src);
	//	env->DeleteLocalRef(_dst);

	return _dst;
}

JNIEXPORT jbyteArray JNICALL Java_com_howell_formuseum_JNIManager_g711u2Pcm
  (JNIEnv *env, jobject obj, jbyteArray src, jint len, jbyteArray pcmBuf){
	//	LOGE("pcm  size=%d",bufsize);
	jsize bufsize = len;
	int size = 2*len;
	char * _src = (char *)env->GetByteArrayElements(src,0);
	char * _pcm = (char *)env->GetByteArrayElements(pcmBuf,0);

	char *dst = (char *)malloc(size*sizeof(char));
	memset(dst,0,sizeof(char)*2*len);
	voice_g711u2Pcm((const char*)_src,len,dst);
	memcpy(_pcm, dst, size);
	free(dst);
	env->ReleaseByteArrayElements(src,(signed char *)_src,0);
	env->ReleaseByteArrayElements(pcmBuf,(signed char *)_pcm,0);
	return pcmBuf;
}




JNIEXPORT void JNICALL Java_com_howell_formuseum_JNIManager_g711AudioPlay
  (JNIEnv *env, jobject obj, jbyteArray src, jint len){
	char * _src = (char *)env->GetByteArrayElements(src, 0);


	char *dst = (char *)malloc(len*2*sizeof(char));
	memset(dst,0,sizeof(char)*2*len);
	voice_g711u2Pcm((const char*)_src,len,dst);
	talk_audio_play(dst,len*2);


	//voice_g711u2Pcm_callFun((const char *)_src,len);
	env->ReleaseByteArrayElements(src,(signed char *)_src,0);
	free(dst);
}

JNIEXPORT void JNICALL Java_com_howell_formuseum_JNIManager_pcmAudioPlay
  (JNIEnv *env, jobject, jbyteArray src, jint len){
	char * _src = (char *)env->GetByteArrayElements(src, 0);

	talk_audio_play(_src,len);
	//voice_input_voice_data(0,_src,len);
	LOGI("input voice_data");
	env->ReleaseByteArrayElements(src,(signed char *)_src,0);
}








/*
 * yv12gl_jni.cpp
 *
 *  Created on: 2016年5月24日
 *      Author: howell
 */





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

JNIEXPORT void JNICALL Java_com_howell_formuseum_JNIManager_YUVSetCallbackObject
(JNIEnv *env, jobject, jobject obj, jint flag){
	switch(flag){
	case 0:
		yuvSelf.obj = env->NewGlobalRef(obj);
		break;
	default:
		break;
	}
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











