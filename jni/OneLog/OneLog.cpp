// OneLog.cpp : Defines the exported functions for the DLL application.
//

#include "stdafx.h"
#include "OneLog.h"
#include "UsbMonitor.h"
#include <stdio.h>
#include <iostream>

JNIEnv *loopEnv;
jobject loopObj;

JNIEXPORT void JNICALL Java_onelog_BackgroundProcess_mainLoop(JNIEnv *env, jobject thisObj) {
	loopEnv = env;
	loopObj = thisObj;
	createWindow();
	return;
}

void setStringField(jclass cls, jobject obj, const char *name, std::wstring value) {
	jfieldID fieldID = loopEnv->GetFieldID(cls, name, "Ljava/lang/String;");
	if (fieldID == NULL) return;
	jstring strval = loopEnv->NewString((jchar*) value.c_str(), value.length());
	loopEnv->SetObjectField(obj, fieldID, strval);
}

void setLongField(jclass cls, jobject obj, const char *name, DWORD value) {
	jfieldID fieldID = loopEnv->GetFieldID(cls, name, "J");
	if (fieldID == NULL) return;
	loopEnv->SetLongField(obj, fieldID, value);
}

jobject createDeviceObject(std::wstring path, std::wstring volumeName, DWORD serialNumber,
                      std::wstring fileSystemName, DWORD maxComponentLen) {
	jclass deviceClass = loopEnv->FindClass("onelog/UsbDevice");
	if (deviceClass == NULL) {
		printf("C: Could not find UsbDevice class\n");
		fflush(stdout);
		return NULL;
	}
	// std::cout << "Got device class" << std::endl;
	jmethodID deviceConstructor = loopEnv->GetMethodID(deviceClass, "<init>", "()V");
	if (deviceConstructor == NULL) {
		printf("C: Could not find UsbDevice constructor\n");
		fflush(stdout);
		return NULL;
	}
	// std::cout << "Got device constructor" << std::endl;
	jobject deviceObj = loopEnv->NewObject(deviceClass, deviceConstructor, loopObj);

	// Check for any errors in the call
	jboolean exceptionCheck = loopEnv->ExceptionCheck();
	if (exceptionCheck == JNI_TRUE) {
		printf("C: Exception occured when trying to construct UsbDevice\n");
		fflush(stdout);
		return NULL;
	}

	setStringField(deviceClass, deviceObj, "path", path);
	setStringField(deviceClass, deviceObj, "volumeName", volumeName);
	setLongField(deviceClass, deviceObj, "serialNumber", serialNumber);
	setStringField(deviceClass, deviceObj, "fileSystemName", fileSystemName);
	setLongField(deviceClass, deviceObj, "maxComponentLen", maxComponentLen);

	return deviceObj;
}

void passVolumeToJava(std::wstring path, std::wstring volumeName, DWORD serialNumber,
                      std::wstring fileSystemName, DWORD maxComponentLen) {

	jobject deviceObj = createDeviceObject(path, volumeName, serialNumber, fileSystemName, maxComponentLen);
	if (deviceObj == NULL) {
		printf("C: Creating USBDevice failed\n");
		fflush(stdout);
		return;
	}

	jclass javaClass = loopEnv->GetObjectClass(loopObj);
	jmethodID javaMethod = loopEnv->GetMethodID(javaClass, "handleDevice", "(Lonelog/UsbDevice;)V");

	if (javaMethod == NULL) {
		printf("C: Could not find handleDevice method\n");
		fflush(stdout);
		return;
	}
	// std::cout << "Got java method" << std::endl;

	loopEnv->CallVoidMethod(loopObj, javaMethod, deviceObj);

	// Check for any errors in the call
	jboolean exceptionCheck = loopEnv->ExceptionCheck();
	if (exceptionCheck == JNI_TRUE) {
		printf("C: Exception occured when trying to call void handleDevice\n");
		fflush(stdout);
		return;
	}
}
