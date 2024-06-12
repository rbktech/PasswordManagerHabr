package ru.rbkdev.passwordmanager

import CloudLib

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity

import javax.crypto.Cipher

class MainActivity : AppCompatActivity() {

    private var mCipher: CCipher? = null

    private var mToken: String = ""
    private var mPathLocal: String = ""
    private var mPassword: CharArray = charArrayOf()
    private var mResult: String = ""

    companion object {

        const val INDEX_LABEL_TITLE: Int = 0
        const val INDEX_LABEL_LOGIN: Int =  1
        const val INDEX_LABEL_PASSWORD: Int =  2
        const val INDEX_LABEL_TYPE: Int =  3
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mCipher = CCipher(resources)

        mToken = resources.openRawResource(R.raw.token).readBytes().toString(Charsets.UTF_8)
        mPassword = charArrayOf( '0', '0', '1', '1')
        mPathLocal = "${baseContext.filesDir.absolutePath}/keys_encrypt_upload.xml"

        val btnLoad: Button = findViewById(R.id.btnLoad)
        val btnUpload: Button = findViewById(R.id.btnUpload)
        val tableLayout: TableLayout = findViewById(R.id.tableLayout)

        btnLoad.setOnClickListener {

            mResult = "error"

            if(CloudLib.load(mToken, "test/keys_encrypt_upload.xml", mPathLocal) == true) {

                val dataEncrypt: ByteArray? = CXMLFile.read(mPathLocal)
                if(dataEncrypt != null) {

                    mCipher?.let { cipher ->

                        val dataDecrypt: ByteArray? = cipher.process(mPassword, dataEncrypt, Cipher.DECRYPT_MODE)
                        if(dataDecrypt != null) {

                            val list: List<DItem> = CXMLFile.parse(dataDecrypt)
                            if(list.isEmpty() == false) {

                                tableLayout.removeAllViews()

                                list.forEach {

                                    val tableRow = TableRow(this)

                                    val title = EditText(this)
                                    title.gravity = Gravity.CENTER
                                    title.setText(it.title)
                                    tableRow.addView(title)

                                    val login = EditText(this)
                                    login.gravity = Gravity.CENTER
                                    login.setText(it.login)
                                    tableRow.addView(login)

                                    val password = EditText(this)
                                    password.gravity = Gravity.CENTER
                                    password.setText(it.password)
                                    tableRow.addView(password)

                                    val type = EditText(this)
                                    type.gravity = Gravity.CENTER
                                    type.setText(it.type)
                                    tableRow.addView(type)

                                    tableLayout.addView(tableRow)

                                    mResult = "success"
                                }
                            }
                        }
                    }
                }
            }

            Toast.makeText(baseContext, mResult, Toast.LENGTH_SHORT).show()
        }

        btnUpload.setOnClickListener {

            mResult = "error"

            val list: MutableList<DItem> = mutableListOf()

            val rowCount: Int = tableLayout.childCount
            for(iRow in 0 until rowCount) {

                val rowView: View = tableLayout.getChildAt(iRow)
                if(rowView is TableRow) {

                    val item = DItem("", "", "", "")

                    val colCount = rowView.childCount
                    for(iCol in 0 until colCount) {

                        val itemView = rowView.getChildAt(iCol)
                        if(itemView is EditText) {

                            when (iCol) {
                                INDEX_LABEL_TITLE -> item.title = itemView.text.toString()
                                INDEX_LABEL_LOGIN -> item.login = itemView.text.toString()
                                INDEX_LABEL_PASSWORD -> item.password = itemView.text.toString()
                                INDEX_LABEL_TYPE -> item.type = itemView.text.toString()
                            }
                        }
                    }

                    list.add(item)
                }
            }

            if(list.isNotEmpty()) {

                val dataDecrypt: ByteArray = CXMLFile.collect(list)
                if(dataDecrypt.isNotEmpty()) {

                    mCipher?.let { cipher ->

                        val dataEncrypt: ByteArray? = cipher.process(mPassword, dataDecrypt, Cipher.ENCRYPT_MODE)
                        if(dataEncrypt != null) {

                            CXMLFile.write(mPathLocal, dataEncrypt)

                            if(CloudLib.upload(mToken, "test", mPathLocal) == true) {
                                mResult = "success"
                            }
                        }
                    }
                }
            }

            Toast.makeText(baseContext, mResult, Toast.LENGTH_SHORT).show()
        }
    }
}