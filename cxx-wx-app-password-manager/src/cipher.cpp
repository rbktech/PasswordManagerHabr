#include "cipher.h"

#define SIZE_KEY 32
#define SIZE_SALT 256

extern unsigned char _binary_iv_start;
extern unsigned char _binary_salt_start;

/** PUBLIC */

void CCipher::encrypt(const char* password, const uchar* inText, const int& inSize, uchar* outText, uint& outSize)
{
    uchar key[SIZE_KEY] = { 0 };

    getKey(password, &_binary_salt_start, key);

    innerEncrypt(inText, inSize, key, &_binary_iv_start, outText, outSize);
}

void CCipher::decrypt(const char* password, const uchar* inText, const int& inSize, uchar* outText, uint& outSize)
{
    uchar key[SIZE_KEY] = { 0 };

    getKey(password, &_binary_salt_start, key);

    innerDecrypt(inText, inSize, key, &_binary_iv_start, outText, outSize);
}

/** PRIVATE */

void CCipher::getKey(const char* password, const uchar* salt, uchar* key)
{
    int result = PKCS5_PBKDF2_HMAC(password, SIZE_PASSWORD, salt, SIZE_SALT, 1324, EVP_sha512(), SIZE_KEY, key);
    if(result != 1)
        throw "PKCS5_PBKDF2_HMAC: error";
}

void CCipher::innerEncrypt(const uchar* inData,
    const int& inSize,
    const uchar* key,
    const uchar* iv,
    uchar* outData,
    uint& outSize)
{
    int iResult = 0;
    int length = 0;

    EVP_CIPHER_CTX* ctx = EVP_CIPHER_CTX_new();
    if(ctx == nullptr)
        throw "EVP_CIPHER_CTX_new: error";

    iResult = EVP_EncryptInit_ex(ctx, EVP_aes_256_cbc(), nullptr, key, iv);
    if(!iResult)
        throw "EVP_EncryptInit_ex: error";

    iResult = EVP_EncryptUpdate(ctx, outData, &length, inData, inSize);
    if(!iResult)
        throw "EVP_EncryptUpdate: error";

    outSize = length;

    iResult = EVP_EncryptFinal_ex(ctx, outData + length, &length);
    if(!iResult)
        throw "EVP_EncryptFinal_ex: error";

    outSize += length;

    EVP_CIPHER_CTX_free(ctx);
}

void CCipher::innerDecrypt(const uchar* inData,
    const int& inSize,
    const uchar* key,
    const uchar* iv,
    uchar* outData,
    uint& outSize)
{
    int iResult = 0;
    int length = 0;

    EVP_CIPHER_CTX* ctx = EVP_CIPHER_CTX_new();
    if(ctx == nullptr)
        throw "EVP_CIPHER_CTX_new: error";

    iResult = EVP_DecryptInit_ex(ctx, EVP_aes_256_cbc(), nullptr, key, iv);
    if(!iResult)
        throw "EVP_DecryptInit_ex: error";

    iResult = EVP_DecryptUpdate(ctx, outData, &length, inData, inSize);
    if(!iResult)
        throw "EVP_DecryptUpdate: error";

    outSize = length;

    iResult = EVP_DecryptFinal_ex(ctx, outData + length, &length);
    if(!iResult)
        throw "EVP_DecryptFinal_ex: error";

    outSize += length;

    EVP_CIPHER_CTX_free(ctx);
}