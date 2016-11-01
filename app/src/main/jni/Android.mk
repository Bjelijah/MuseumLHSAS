LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE := hwplay
LOCAL_SRC_FILES := libhwplay.so
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/include
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := hwnet
LOCAL_SRC_FILES := libhwnet.a
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/include
include $(PREBUILT_STATIC_LIBRARY)


include $(CLEAR_VARS)
LOCAL_MODULE := voice_test
LOCAL_SRC_FILES := libvoice_test.a
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/include/voice_test



include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := json
LOCAL_SRC_FILES := libjson.a
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/include/json
include $(PREBUILT_STATIC_LIBRARY)


include $(CLEAR_VARS)
LOCAL_MODULE := hw_jni
# Add your application source files here...
LOCAL_SRC_FILES := hw_jni.cpp 
LOCAL_SHARED_LIBRARIES := hwplay voice_test json hwnet
LOCAL_LDFLAGS := -LE:/Android/android-ndk-r10e/sources/cxx-stl/gnu-libstdc++/4.9/libs/armeabi-v7a
LOCAL_LDLIBS := -llog -lgnustl_static -lGLESv2 -lz -ldl -lgcc -pthread
#	-L$(NDK_PLATFORMS_ROOT)/$(TARGET_PLATFORM)/arch-arm/usr/lib -L$(LOCAL_PATH) -lz -ldl -lgcc 
include $(BUILD_SHARED_LIBRARY)



#include $(CLEAR_VARS)
#LOCAL_MODULE := hwnet_jni
# Add your application source files here...
#LOCAL_SRC_FILES := decode_jni.cpp yv12gl_jni.cpp audio_jni.cpp
#LOCAL_SHARED_LIBRARIES :=  hwplay hwnet
#LOCAL_LDFLAGS := -LE:/Android/android-ndk-r10e/sources/cxx-stl/gnu-libstdc++/4.8/libs/armeabi-v7a
#LOCAL_LDLIBS := -llog -lgnustl_static -lGLESv2 -lz -ldl -lgcc
#	-L$(NDK_PLATFORMS_ROOT)/$(TARGET_PLATFORM)/arch-arm/usr/lib -L$(LOCAL_PATH) -lz -ldl -lgcc 
#include $(BUILD_SHARED_LIBRARY)

#include $(CLEAR_VARS)
#LOCAL_MODULE := talk_jni
# Add your application source files here...
#LOCAL_SRC_FILES := talk_jni.cpp audio_jni.cpp
#LOCAL_SHARED_LIBRARIES := hwplay voice_test 
#LOCAL_LDFLAGS := -LE:/Android/android-ndk-r10e/sources/cxx-stl/gnu-libstdc++/4.8/libs/armeabi-v7a
#LOCAL_LDLIBS := -llog -lgnustl_static -lGLESv2 -lz -ldl -lgcc
#	-L$(NDK_PLATFORMS_ROOT)/$(TARGET_PLATFORM)/arch-arm/usr/lib -L$(LOCAL_PATH) -lz -ldl -lgcc 
#include $(BUILD_SHARED_LIBRARY)


