#include "mainframe.h"

#include <wx/button.h>
#include <wx/sizer.h>
#include <iostream>

#define INDEX_LABEL_TITLE 0
#define INDEX_LABEL_LOGIN 1
#define INDEX_LABEL_PASSWORD 2
#define INDEX_LABEL_TYPE 3

CMainFrame::CMainFrame()
    : wxFrame(nullptr, NewControlId(), wxT("Password manager"))
    , mDataEncrypt { 0 }
    , mSizeEncrypt(0)
    , mDataDecrypt{ 0 }
    , mSizeDecrypt(0)
{
    wxButton* btnLoad = new wxButton(this, NewControlId(), wxT("Load"));
    wxButton* btnUpload = new wxButton(this, NewControlId(), wxT("Upload"));

    mGrid = new wxGrid(this, NewControlId());
    mGrid->CreateGrid(0, 4);
    mGrid->SetColLabelValue(INDEX_LABEL_TITLE, "title");
    mGrid->SetColLabelValue(INDEX_LABEL_LOGIN, "login");
    mGrid->SetColLabelValue(INDEX_LABEL_PASSWORD, "password");
    mGrid->SetColLabelValue(INDEX_LABEL_TYPE, "type");
    mGrid->HideRowLabels();

    wxBoxSizer* hBox = nullptr;
    wxBoxSizer* mainBox = new wxBoxSizer(wxVERTICAL);

    hBox = new wxBoxSizer(wxHORIZONTAL);
    hBox->Add(btnLoad, 1);
    hBox->Add(btnUpload, 1);
    mainBox->Add(hBox, 0, wxEXPAND);
    mainBox->Add(mGrid);

    SetSizerAndFit(mainBox);

    Bind(wxEVT_BUTTON, &CMainFrame::OnLoad, this, btnLoad->GetId());
    Bind(wxEVT_BUTTON, &CMainFrame::OnUpload, this, btnUpload->GetId());

    try {
        mJNICloudLib.init();
    } catch(const char* message) {
        std::cout << message << std::endl;
    }
}

void CMainFrame::OnLoad(wxCommandEvent& WXUNUSED(event))
{
    int nRows = 0;

    mItems.clear();
    memset(mDataDecrypt, 0, mSizeDecrypt = SIZE_BUFFER);
    memset(mDataEncrypt, 0, mSizeEncrypt = SIZE_BUFFER);
    nRows = mGrid->GetNumberRows();
    if(nRows != 0)
        mGrid->DeleteRows(0, mGrid->GetNumberRows());

    try {

        mJNICloudLib.run("load", TOKEN, "test/keys_encrypt_upload.xml", RESOURCES "/keys_encrypt_load.xml");

        CXMLFile::read(RESOURCES "/keys_encrypt_load.xml", mDataEncrypt, mSizeEncrypt);

        CCipher::decrypt("0011", mDataEncrypt, mSizeEncrypt, mDataDecrypt, mSizeDecrypt);

        CXMLFile::parse(mDataDecrypt, mSizeDecrypt, mItems);

    } catch(const char* message) {
        std::cout << message << std::endl;
    }

    for(auto& item : mItems) {

        nRows = mGrid->GetNumberRows();
        mGrid->AppendRows();
        mGrid->SetCellValue(nRows, INDEX_LABEL_TITLE, item.title);
        mGrid->SetCellValue(nRows, INDEX_LABEL_LOGIN, item.login);
        mGrid->SetCellValue(nRows, INDEX_LABEL_PASSWORD, item.password);
        mGrid->SetCellValue(nRows, INDEX_LABEL_TYPE, item.type);
    }

    this->Fit();
}

void CMainFrame::OnUpload(wxCommandEvent& WXUNUSED(event))
{
    int nRows = 0;

    mItems.clear();
    memset(mDataDecrypt, 0, mSizeDecrypt = SIZE_BUFFER);
    memset(mDataEncrypt, 0, mSizeEncrypt = SIZE_BUFFER);

    nRows = mGrid->GetNumberRows();
    for(int iRow = 0; iRow < nRows; iRow++) {

        SItem item;
        item.title = mGrid->GetCellValue(iRow, INDEX_LABEL_TITLE);
        item.login = mGrid->GetCellValue(iRow, INDEX_LABEL_LOGIN);
        item.password = mGrid->GetCellValue(iRow, INDEX_LABEL_PASSWORD);
        item.type = mGrid->GetCellValue(iRow, INDEX_LABEL_TYPE);
        mItems.emplace_back(item);
    }

    try {

        if(mItems.empty() == true)
            throw "error: items is empty";

        CXMLFile::collect(mItems, mDataDecrypt, mSizeDecrypt);

        CCipher::encrypt("0011", mDataDecrypt, mSizeDecrypt, mDataEncrypt, mSizeEncrypt);

        CXMLFile::write(RESOURCES "/keys_encrypt_upload.xml", mDataEncrypt, mSizeEncrypt);

        mJNICloudLib.run("upload", TOKEN, "test", RESOURCES "/keys_encrypt_upload.xml");

    } catch(const char* message) {
        std::cout << message << std::endl;
    }
}