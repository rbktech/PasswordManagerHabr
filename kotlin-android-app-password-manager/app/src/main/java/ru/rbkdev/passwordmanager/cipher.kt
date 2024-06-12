package ru.rbkdev.passwordmanager

import android.content.res.Resources

import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

class CCipher(resources: Resources) {

    private var mIv: ByteArray = resources.openRawResource(R.raw.iv).readBytes()
    private var mSalt: ByteArray = resources.openRawResource(R.raw.salt).readBytes()

    /**
     * Cipher.DECRYPT_MODE
     * Cipher.ENCRYPT_MODE
     */
    fun process(password: CharArray?, inText: ByteArray?, typeCipher: Int) : ByteArray? {

        var outText: ByteArray? = null

        try {

            val pbKeySpec: PBEKeySpec = PBEKeySpec(password, mSalt, 1324, 256)
            val secretKeyFactory: SecretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512")
            val keyBytes: ByteArray = secretKeyFactory.generateSecret(pbKeySpec).encoded

            val keySpec: SecretKeySpec = SecretKeySpec(keyBytes, "AES")
            val ivSpec: IvParameterSpec = IvParameterSpec(mIv)

            val cipher: Cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            cipher.init(typeCipher, keySpec, ivSpec)
            outText = cipher.doFinal(inText)

        } catch (exception: Exception) {
            exception.printStackTrace()
        }

        return outText
    }
}