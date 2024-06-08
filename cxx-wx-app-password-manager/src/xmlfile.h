#pragma once

#include <string>
#include <vector>

typedef unsigned char uchar;

struct SItem {
    std::string type;
    std::string title;
    std::string login;
    std::string password;
};

typedef std::vector<SItem> TVectorItems;

class CXMLFile
{
public:
    CXMLFile() = default;
    ~CXMLFile() = default;

    static void read(const char* nameFile, uchar* data, unsigned int& size);
    static void write(const char* nameFile, const uchar* data, const unsigned int& size);

    static void parse(unsigned char* inData, const unsigned int& inSize, TVectorItems& outData);
    static void collect(const TVectorItems& inData, uchar* outData, unsigned int& outSize);
};