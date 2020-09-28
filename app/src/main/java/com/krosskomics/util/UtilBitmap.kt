package com.krosskomics.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.SecretKeySpec

object UtilBitmap {
    /**
     * 키가되는 문자열
     */
    private const val KeyString = "EndofKing"
    fun UtilBitmap() {
        // TODO Auto-generated constructor stub
    }

    /**
     * 바이트를 읽어서 비트맵으로 변환
     * @param data
     * @return
     */
    fun byteTobitmap(data: ByteArray): Bitmap? {
        return BitmapFactory.decodeByteArray(data, 0, data.size)
    }

    /**
     * 암호화 하기
     * @param clear
     * @return
     * @throws Exception
     */
    @Throws(Exception::class)
    fun encrypt(clear: ByteArray?): ByteArray? {
        val skeySpec =
            SecretKeySpec(getKey(), "AES")
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec)
        return cipher.doFinal(clear)
    }

    /**
     * 암호화 해제
     * @param encrypted
     * @return
     * @throws Exception
     */
    @Throws(Exception::class)
    fun decrypt(encrypted: ByteArray?): ByteArray? {
        val skeySpec =
            SecretKeySpec(getKey(), "AES")
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.DECRYPT_MODE, skeySpec)
        return cipher.doFinal(encrypted)
    }

    private fun getKey(): ByteArray? {
        return try {
            val keyStart = KeyString.toByteArray()
            val kgen = KeyGenerator.getInstance("AES")
            val sr: SecureRandom
            sr = SecureRandom.getInstance("SHA1PRNG")
            sr.setSeed(keyStart)
            kgen.init(128, sr) // 192 and 256 bits may not be available
            val skey = kgen.generateKey()
            skey.encoded
        } catch (e: NoSuchAlgorithmException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
            null
        }
    }

    fun bitmapToByteArray(bitmap: Bitmap?): ByteArray? {
        val stream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        return stream.toByteArray()
    }
}