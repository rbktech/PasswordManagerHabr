#pragma once

#include <openssl/evp.h>

#define SIZE_PASSWORD 4

typedef unsigned char uchar;
typedef unsigned int uint;

class CCipher
{
private:
    /**
     * @brief
     * @param inData
     * @param inSize
     * @param key
     * @param iv
     * @param outData
     * @param outSize
     */
    static void innerEncrypt(const uchar* inData,
        const int& inSize,
        const uchar* key,
        const uchar* iv,
        uchar* outData,
        uint& outSize);

    /**
     * @brief
     * @param inData
     * @param inSize
     * @param key
     * @param iv
     * @param outData
     * @param outSize
     */
    static void innerDecrypt(const uchar* inData,
        const int& inSize,
        const uchar* key,
        const uchar* iv,
        uchar* outData,
        uint& outSize);

    /**
     * @brief
     * @param password
     * @param salt
     * @param key
     */
    static void getKey(const char* password, const uchar* salt, uchar* key);

public:
    CCipher() = default;
    ~CCipher() = default;

    /**
     * @brief
     * @param password
     * @param inData
     * @param inSize
     * @param outData
     * @param outSize
     */
    static void encrypt(const char* password, const uchar* inData, const int& inSize, uchar* outData, uint& outSize);

    /**
     * @brief
     * @param password
     * @param inData
     * @param inSize
     * @param outData
     * @param outSize
     */
    static void decrypt(const char* password, const uchar* inData, const int& inSize, uchar* outData, uint& outSize);
};