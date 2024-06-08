#include "jnicloudelib.h"

#define PATH_LIB RESOURCES"/library"

#ifdef DYNAMIC_JDK_LINK
typedef jint(JNICALL *pCreateJavaVM)(JavaVM**, void**, void*);
#endif // DYNAMIC_JDK_LINK

CJNICloudLib::CJNICloudLib()
{
    mJavaVM = nullptr;
    mJNIEnv = nullptr;

#ifdef DYNAMIC_JDK_LINK
    mInstance = nullptr;
#endif // DYNAMIC_JDK_LINK
}

CJNICloudLib::~CJNICloudLib()
{
    if(mJavaVM != nullptr)
        mJavaVM->DestroyJavaVM();

#ifdef DYNAMIC_JDK_LINK
    if(mInstance != nullptr)
        FreeLibrary(mInstance);
#endif // DYNAMIC_JDK_LINK
}

void CJNICloudLib::init()
{
    jint iResult = 0;
    JavaVMInitArgs vm_args;
    JavaVMOption options[1];
#ifdef DYNAMIC_JDK_LINK
    pCreateJavaVM JNI_CreateJavaVM = nullptr;
#endif // DYNAMIC_JDK_LINK

    const char* path = "-Djava.class.path="  //
                       PATH_LIB "/cloud-lib.jar;"           //
                       PATH_LIB "/commons-logging-1.2.jar;" //
                       PATH_LIB "/httpclient-4.5.13.jar;"   //
                       PATH_LIB "/httpcore-4.4.16.jar;"     //
                       PATH_LIB "/xmlpull-1.1.3.4a.jar;";

    options[0].optionString = const_cast<char*>(path);

    vm_args.version = JNI_VERSION_1_8;
    vm_args.nOptions = 1;
    vm_args.options = options;
    // vm_args.ignoreUnrecognized = JNI_TRUE;

#ifdef DYNAMIC_JDK_LINK
    mInstance = LoadLibrary(JDK "/bin/server/jvm.dll");
    if(mInstance == nullptr)
        throw "error: library Java VM not load";

    JNI_CreateJavaVM = (pCreateJavaVM)GetProcAddress(mInstance, "JNI_CreateJavaVM");
    if(JNI_CreateJavaVM == nullptr)
        throw "error: function Java VM not find";
#endif // DYNAMIC_JDK_LINK

    iResult = JNI_CreateJavaVM(&mJavaVM, (void**)&mJNIEnv, &vm_args);
    if(iResult != 0)
        throw "Java VM not create";
}

void CJNICloudLib::run(const char* nameMethod, const char* token, const char* remotePath, const char* localPath)
{
    jstring arg[3];
    jboolean bResult = 0;
    jclass objClass = nullptr;
    jmethodID objMethod = nullptr;

    const char* signMethod = "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Z";

    if(mJNIEnv == nullptr)
        throw "error: Java VM is not init";

    objClass = mJNIEnv->FindClass("CloudLib");
    if(objClass == nullptr)
        throw "error: java class is not find";

    objMethod = mJNIEnv->GetStaticMethodID(objClass, nameMethod, signMethod);
    if(objMethod == nullptr)
        throw "error: java method is not find";

    arg[0] = mJNIEnv->NewStringUTF(token);
    arg[1] = mJNIEnv->NewStringUTF(remotePath);
    arg[2] = mJNIEnv->NewStringUTF(localPath);

    bResult = mJNIEnv->CallBooleanMethod(objClass, objMethod, arg[0], arg[1], arg[2]);
    if(bResult != true)
        throw "error: java method is not success";
}

void CJNICloudLib::exception(const char* message)
{
    if(mJNIEnv->ExceptionCheck()) {
        mJNIEnv->ExceptionDescribe();
        mJNIEnv->ExceptionClear();
    }

    throw message;
}