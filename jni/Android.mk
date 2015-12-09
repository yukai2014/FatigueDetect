LOCAL_PATH := $(call my-dir)  
include $(CLEAR_VARS)  
LOCAL_LDLIBS:=-L$(SYSROOT)/usr/lib -llog
OPENCV_LIB_TYPE:=STATIC

ifeq ("$(wildcard $(OPENCV_MK_PATH))","")  
#try to load OpenCV.mk from default install location  
include E:\安装文件\OpenCV-2.4.9-android-sdk\sdk\native\jni\OpenCV.mk 
else  
include $(OPENCV_MK_PATH)  
endif  

LOCAL_LDLIBS    += -lm -llog -ljnigraphics

LOCAL_MODULE    := eye
LOCAL_SRC_FILES := findEyeCenter.cpp blink_detection.cpp  findEyeCorner.cpp helpers.cpp main.cpp

include $(BUILD_SHARED_LIBRARY)
