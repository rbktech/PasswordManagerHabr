#pragma once

#include <wx/frame.h>
#include <wx/grid.h>

#include "cipher.h"
#include "jnicloudelib.h"
#include "xmlfile.h"

#define SIZE_BUFFER 8192

class CMainFrame : public wxFrame
{
private:
    wxGrid* mGrid;

    TVectorItems mItems;

    uchar mDataEncrypt[SIZE_BUFFER];
    unsigned int mSizeEncrypt;

    uchar mDataDecrypt[SIZE_BUFFER];
    unsigned int mSizeDecrypt;

    CJNICloudLib mJNICloudLib;

    void OnLoad(wxCommandEvent& event);
    void OnUpload(wxCommandEvent& event);

public:
    CMainFrame();
    ~CMainFrame() override = default;
};