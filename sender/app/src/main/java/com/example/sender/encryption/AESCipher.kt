package com.example.sender.encryption

import android.annotation.SuppressLint
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import java.security.SecureRandom
import java.security.MessageDigest

object AESCipher {
    private const val AES_ALGORITHM = "AES"
    private const val AES_TRANSFORMATION = "AES/CBC/PKCS7Padding"
    private const val KEY_SIZE = 32 // 256-bit key
    private const val BLOCK_SIZE = 16

    @SuppressLint("NewApi")
    fun encrypt(key: ByteArray, raw: ByteArray): String {
        val iv = ByteArray(BLOCK_SIZE)
        SecureRandom().nextBytes(iv)

        val cipher = Cipher.getInstance(AES_TRANSFORMATION)
        val secretKey = SecretKeySpec(key, AES_ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, IvParameterSpec(iv))

        val encrypted = cipher.doFinal(raw)
        return Base64.getEncoder().encodeToString(iv + encrypted)
    }

    @SuppressLint("NewApi")
    fun decrypt(key: ByteArray, enc: String): ByteArray {
        val decoded = Base64.getDecoder().decode(enc)
        val iv = decoded.copyOfRange(0, BLOCK_SIZE)
        val cipherText = decoded.copyOfRange(BLOCK_SIZE, decoded.size)

        val cipher = Cipher.getInstance(AES_TRANSFORMATION)
        val secretKey = SecretKeySpec(key, AES_ALGORITHM)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, IvParameterSpec(iv))

        return cipher.doFinal(cipherText)
    }

    fun generateKey(): ByteArray {
        val randomKey = ByteArray(KEY_SIZE)
        SecureRandom().nextBytes(randomKey)
        return MessageDigest.getInstance("SHA-256").digest(randomKey)
    }
}

fun main() {
    val key = AESCipher.generateKey()
    val message = "aa".repeat(100).toByteArray()

    val encrypted = AESCipher.encrypt(key, message)
    val decrypted = AESCipher.decrypt(key, encrypted)

    println("Encrypted: $encrypted")
    println("Decrypted: ${String(decrypted)}")
}
