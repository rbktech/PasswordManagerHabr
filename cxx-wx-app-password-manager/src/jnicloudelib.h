#pragma once

#include <jni.h>

#ifdef DYNAMIC_JDK_LINK
#include <windows.h>
#endif // DYNAMIC_JDK_LINK

class CJNICloudLib
{
private:
    JavaVM* mJavaVM;
    JNIEnv* mJNIEnv;

#ifdef DYNAMIC_JDK_LINK
    HINSTANCE mInstance;
#endif // DYNAMIC_JDK_LINK

    void exception(const char* message);

public:
    CJNICloudLib();
    ~CJNICloudLib();

    void init();
    void run(const char* nameMethod, const char* token, const char* remotePath, const char* localPath);
};
