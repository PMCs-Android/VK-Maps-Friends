package com.example.mapsfriends

import java.nio.charset.Charset
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class CryptoManager {

    private val key = "your-encryption-key".padEnd(32, '0').toByteArray(Charset.forName("UTF-8"))
    private val charset = Charset.forName("UTF-8")

    fun encrypt(data: String): String {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val secretKeySpec = SecretKeySpec(key, "AES")
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec)
        val encryptedBytes = cipher.doFinal(data.toByteArray(charset))
        val iv = cipher.iv
        return "${iv.joinToString(",")}|${String(encryptedBytes, Charset.forName("ISO-8859-1"))}"
    }

    fun decrypt(encryptedData: String): String {
        val parts = encryptedData.split("|")
        if (parts.size != 2) {
            throw IllegalArgumentException("Неверный формат зашифрованных данных")
        }
        val iv = IvParameterSpec(parts[0].split(",").map { it.toByte() }.toByteArray())
        val encryptedBytes = parts[1].toByteArray(Charset.forName("ISO-8859-1"))
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val secretKeySpec = SecretKeySpec(key, "AES")
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, iv)
        return String(cipher.doFinal(encryptedBytes), charset)
    }
}