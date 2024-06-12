package ru.rbkdev.passwordmanager

import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity

import CloudLib

import javax.crypto.Cipher

class MainActivity : AppCompatActivity() {

    private var mCipher: CCipher? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mCipher = CCipher(resources)

        val token = resources.openRawResource(R.raw.token).readBytes().toString(Charsets.UTF_8)

        CloudLib.load(token, "test/keys_encrypt_upload.xml", "${baseContext.filesDir.absolutePath}/keys.xml")

        mCipher?.let { cipher ->
            cipher.process("0011".toCharArray(), "asd".toByteArray(), Cipher.ENCRYPT_MODE)
        }
    }
}