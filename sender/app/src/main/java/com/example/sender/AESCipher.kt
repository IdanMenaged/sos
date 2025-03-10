package com.example.encryptionsandbox

import android.os.Build
import androidx.annotation.RequiresApi
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object AESCipher {

    private const val AES_MODE = "AES/CBC/PKCS5Padding"
    private const val KEY_SIZE = 32 // 256 bits (32 bytes)

    @RequiresApi(Build.VERSION_CODES.O)
    fun encrypt(key: ByteArray, raw: ByteArray): String {
        val iv = ByteArray(16) // AES block size
        SecureRandom().nextBytes(iv)
        val ivSpec = IvParameterSpec(iv)
        val secretKey = SecretKeySpec(key, "AES")

        val cipher = Cipher.getInstance(AES_MODE)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec)

        val encrypted = cipher.doFinal(raw)
        val encryptedData = iv + encrypted
        return Base64.getEncoder().encodeToString(encryptedData)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun decrypt(key: ByteArray, enc: String): ByteArray {
        val decoded = Base64.getDecoder().decode(enc)
        val iv = decoded.sliceArray(0 until 16) // Extract the IV
        val encryptedData = decoded.sliceArray(16 until decoded.size)

        val ivSpec = IvParameterSpec(iv)
        val secretKey = SecretKeySpec(key, "AES")

        val cipher = Cipher.getInstance(AES_MODE)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec)

        return cipher.doFinal(encryptedData)
    }

    fun generateKey(): ByteArray {
        val randomBytes = ByteArray(KEY_SIZE)
        SecureRandom().nextBytes(randomBytes)
        return MessageDigest.getInstance("SHA-256").digest(randomBytes)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun main() {
    // Generate a random AES key
    val key = AESCipher.generateKey()

    val data = "aa".repeat(100).toByteArray()
    val encrypted = AESCipher.encrypt(key, data)
    println("Encrypted: $encrypted")

    val decrypted = AESCipher.decrypt(key, encrypted)
    println("Decrypted: ${String(decrypted)}")
}
