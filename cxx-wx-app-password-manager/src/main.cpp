#define APP 6

#if APP == 0

#include <iostream>

#include "jnicloudelib.h"

int main(int argc, char** argv)
{
    CJNICloudLib cloudLib;

    try {
        cloudLib.init();
        cloudLib.run("upload", "<token>", "test", RESOURCES "/keys.xml");
        cloudLib.run("load", "<token>", "test/keys.xml", RESOURCES "/test/keys.xml");
    } catch(const char* message) {
        std::cout << message << std::endl;
    }

    return 0;
}

#elif APP == 1

#include <iostream>

extern unsigned char _binary_test_txt_start;
extern unsigned char _binary_test_txt_end;
extern unsigned char _binary_test_txt_size;

int main(int argc, char** argv)
{
    unsigned char* pStart = &_binary_test_txt_start;
    unsigned char* pEnd = &_binary_test_txt_end;
    unsigned char* pSize = &_binary_test_txt_size;

    size_t size = reinterpret_cast<uintptr_t>(pSize);

    std::cout << pStart << std::endl;
    std::cout << pEnd << std::endl;
    std::cout << size << std::endl;
    return 0;
}

#elif APP == 2

#include <iostream>

extern unsigned char binary_test_txt_start;
extern unsigned char binary_test_txt_end;
extern unsigned char binary_test_txt_size;

int main(int argc, char** argv)
{
    unsigned char* pStart = &binary_test_txt_start;
    unsigned char* pEnd = &binary_test_txt_end;
    unsigned char* pSize = &binary_test_txt_size;

    uint16_t size0 = reinterpret_cast<uintptr_t>(pSize);
    size_t size1 = pEnd - pStart;

    std::cout << pStart << std::endl;
    std::cout << pEnd << std::endl;
    std::cout << size0 << std::endl;
    std::cout << size1 << std::endl;
    return 0;
}

#elif APP == 3

#include <cstring>
#include <iostream>

#include "cipher.h"

#define SIZE_BUFFER 20

int main(int argc, char** argv)
{
    const char* password = "0011";

    uchar inData[SIZE_BUFFER] = { 0 };
    int inSize = SIZE_BUFFER;

    uchar encryptData[SIZE_BUFFER] = { 0 };
    uint encryptSize = SIZE_BUFFER;

    uchar outData[SIZE_BUFFER] = { 0 };
    uint outSize = SIZE_BUFFER;

    memcpy(inData, "Hello habr", inSize = 10);

    try {
        CCipher::encrypt(password, inData, inSize, encryptData, encryptSize);
        CCipher::decrypt(password, encryptData, encryptSize, outData, outSize);

        std::cout << "In     \tsize: " << inSize << " data: " << inData << std::endl;
        std::cout << "Encrypt\tsize: " << encryptSize << " data: " << encryptData << std::endl;
        std::cout << "Out    \tsize: " << outSize << " data: " << outData << std::endl;

    } catch(...) {
    }

    return 0;
}

#elif APP == 4

#include "xmlfile.h"

#define SIZE_BUFFER 1000

int main(int argc, char** argv)
{
    uchar data[SIZE_BUFFER] = { 0 };
    unsigned int size = SIZE_BUFFER;

    CXMLFile::read(RESOURCES "/keys.xml", data, size);
    CXMLFile::write(RESOURCES "/temp.xml", data, size);
}

#elif APP == 5

#include "xmlfile.h"

#define SIZE_BUFFER 1000

int main(int argc, char** argv)
{
    uchar dataRead[SIZE_BUFFER] = { 0 };
    unsigned int sizeRead = SIZE_BUFFER;

    TVectorItems items;

    uchar dataWrite[SIZE_BUFFER] = { 0 };
    unsigned int sizeWrite = SIZE_BUFFER;

    CXMLFile::read(RESOURCES "/keys.xml", dataRead, sizeRead);

    CXMLFile::parse(dataRead, sizeRead, items);

    CXMLFile::collect(items, dataWrite, sizeWrite);

    CXMLFile::write(RESOURCES "/temp.xml", dataWrite, sizeWrite);
}

#elif APP == 6

#include <iostream>

#include "xmlfile.h"
#include "jnicloudelib.h"
#include "cipher.h"

#define SIZE_BUFFER 8192

int main(int argc, char** argv)
{
    uchar dataDecrypt[SIZE_BUFFER] { 0 };
    uint sizeDecrypt = SIZE_BUFFER;

    uchar dataEncrypt[SIZE_BUFFER] { 0 };
    uint sizeEncrypt = SIZE_BUFFER;

    CJNICloudLib jniCloudLib;

    try {

        jniCloudLib.init();

        CXMLFile::read(RESOURCES "/keys.xml", dataDecrypt, sizeDecrypt);

        CCipher::encrypt("0011", dataDecrypt, sizeDecrypt, dataEncrypt, sizeEncrypt);

        CXMLFile::write(RESOURCES "/keys_encrypt_upload.xml", dataEncrypt, sizeEncrypt);

        jniCloudLib.run("upload", TOKEN, "test", RESOURCES "/keys_encrypt_upload.xml");

        memset(dataDecrypt, 0, sizeDecrypt = SIZE_BUFFER);
        memset(dataEncrypt, 0, sizeEncrypt = SIZE_BUFFER);

        jniCloudLib.run("load", TOKEN, "test/keys_encrypt_upload.xml", RESOURCES "/keys_encrypt_load.xml");

        CXMLFile::read(RESOURCES "/keys_encrypt_load.xml", dataEncrypt, sizeEncrypt);

        CCipher::decrypt("0011", dataEncrypt, sizeEncrypt, dataDecrypt, sizeDecrypt);

        CXMLFile::write(RESOURCES "/keys_decrypt.xml", dataDecrypt, sizeDecrypt);

    } catch(const char* message) {
        std::cout << message << std::endl;
    }
}

#elif APP == 7

#include <wx/app.h>

#include "mainframe.h"

class CApp : public wxApp
{
public:
    bool OnInit() override
    {
        return (new CMainFrame)->Show();
    }
};

IMPLEMENT_APP(CApp)

#endif