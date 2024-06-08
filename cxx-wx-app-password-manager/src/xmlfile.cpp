#include "xmlfile.h"

#include <wx/file.h>
#include <wx/xml/xml.h>
#include <wx/mstream.h>

void CXMLFile::read(const char* nameFile, uchar* data, unsigned int& size)
{
    wxFile file;
    bool bResult = false;

    bResult = file.Open(nameFile, wxFile::read);
    if(bResult == false)
        throw "File not open";

    size = file.Read(data, size);

    file.Close();
}

void CXMLFile::write(const char* nameFile, const uchar* data, const unsigned int& size)
{
    wxFile file;
    bool bResult = false;

    bResult = file.Open(nameFile, wxFile::write);
    if(bResult == false)
        throw "File not open";

    file.Write(data, size);

    file.Close();
}

void round(wxXmlNode* node, TVectorItems& outData)
{
    wxXmlAttribute* attr = nullptr;

    if(node != nullptr) {

        outData.emplace_back();
        outData.back().type = node->GetName();

        attr = node->GetAttributes();

        do {

            wxString name = attr->GetName();
            wxString value = attr->GetValue();

            if(name == wxT("title"))
                outData.back().title = value;

            if(name == wxT("login"))
                outData.back().login = value;

            if(name == wxT("password"))
                outData.back().password = value;

        } while((attr = attr->GetNext()) != nullptr);

        round(node->GetChildren(), outData);

        round(node->GetNext(), outData);
    }
}

void CXMLFile::parse(unsigned char* inData, const unsigned int& inSize, TVectorItems& outData)
{
    wxXmlDocument xmlDoc;
    wxMemoryInputStream stream(inData, inSize);

    if(xmlDoc.Load(stream) == false)
        throw "Raw data not load to xml";

    round(xmlDoc.GetRoot()->GetChildren(), outData);
}

void CXMLFile::collect(const TVectorItems& inData, uchar* outData, unsigned int& outSize)
{
    wxXmlDocument xmlDoc;
    wxMemoryOutputStream stream;

    wxXmlNode* root = nullptr;
    wxXmlNode* block = nullptr;
    wxXmlNode* item = nullptr;

    root = new wxXmlNode(wxXML_ELEMENT_NODE, wxT("root"));

    for(auto& p : inData) {

        if(root != nullptr && p.type == wxT("block")) {
            block = new wxXmlNode(wxXML_ELEMENT_NODE, wxT("block"));
            block->AddAttribute(wxT("title"), p.title);
            block->AddAttribute(wxT("login"), p.login);
            block->AddAttribute(wxT("password"), p.password);
            root->AddChild(block);
        }

        if(block != nullptr && p.type == wxT("item")) {
            item = new wxXmlNode(wxXML_ELEMENT_NODE, wxT("item"));
            item->AddAttribute(wxT("title"), p.title);
            item->AddAttribute(wxT("login"), p.login);
            item->AddAttribute(wxT("password"), p.password);
            block->AddChild(item);
        }
    }

    xmlDoc.SetRoot(root);
    xmlDoc.Save(stream);

    stream.CopyTo(outData, outSize = stream.GetSize());
}