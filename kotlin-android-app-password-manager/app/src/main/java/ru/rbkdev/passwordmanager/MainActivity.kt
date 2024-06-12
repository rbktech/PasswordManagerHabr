package ru.rbkdev.passwordmanager

import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity

import CloudLib

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val token: String = resources.openRawResource(R.raw.token).readBytes().toString(Charsets.UTF_8)

        CloudLib.load(token, "test/keys_encrypt_upload.xml", "${baseContext.filesDir.absolutePath}/keys.xml")
    }
}