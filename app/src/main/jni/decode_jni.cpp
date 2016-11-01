/*
 * decode_jni.cpp
 *
 *  Created on: 2016年5月24日
 *      Author: howell
 */


#include <jni.h>
#include <pthread.h>
#include <android/log.h>
#include <stdio.h>
#include <string.h>
#include "hwplay/stream_type.h"
#include "hwplay/play_def.h"
#include <unistd.h>
#include "net_sdk.h"
#include "com_howell_formuseum_JNIManager.h"
#define LOGI(...) ((void)__android_log_print(ANDROID_LOG_INFO, "decode_jni", __VA_ARGS__))
#define LOGW(...) ((void)__android_log_print(ANDROID_LOG_WARN, "decode_jni", __VA_ARGS__))
#define LOGE(...) ((void)__android_log_print(ANDROID_LOG_ERROR, "decode_jni", __VA_ARGS__))

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
		env->DeleteGlobalRef(res->obj);
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



