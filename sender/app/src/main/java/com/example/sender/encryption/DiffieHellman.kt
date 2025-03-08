package com.example.sender.encryption

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import java.security.*
import java.security.spec.X509EncodedKeySpec
import java.util.Base64
import javax.crypto.KeyAgreement
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

class DiffieHellman {
    private val keyPair: KeyPair = generateKeyPair()
    val publicKey: ByteArray = keyPair.public.encoded

    init {
        Log.d("DH", String(publicKey))
    }

    private fun generateKeyPair(): KeyPair {
        val keyGen = KeyPairGenerator.getInstance("EC")
        keyGen.initialize(384)
        return keyGen.generateKeyPair()
    }

    fun generateSharedSecret(otherPublicKeyBytes: ByteArray): ByteArray {
        val otherPublicKey = deserializePublicKeyFromPEM(String(otherPublicKeyBytes))
        val keyAgreement = KeyAgreement.getInstance("ECDH")
        keyAgreement.init(keyPair.private)
        keyAgreement.doPhase(otherPublicKey, true)
        return keyAgreement.generateSecret()
    }

    fun deriveKey(sharedSecret: ByteArray): SecretKey {
        val digest = MessageDigest.getInstance("SHA-256")
        val keyBytes = digest.digest(sharedSecret).copyOf(32)
        return SecretKeySpec(keyBytes, "AES")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun serializePublicKeyToPEM(): String {
        val base64Encoded = Base64.getEncoder().encodeToString(publicKey)
        return """
            -----BEGIN PUBLIC KEY-----
            $base64Encoded
            -----END PUBLIC KEY-----
        """.trimIndent()
    }

    // Function to deserialize the PEM-encoded public key
    @SuppressLint("NewApi")
    fun deserializePublicKeyFromPEM(pem: String): PublicKey {
        val base64Encoded = pem
            .replace("-----BEGIN PUBLIC KEY-----", "")
            .replace("-----END PUBLIC KEY-----", "")
            .replace("\n", "")
        val keyBytes = Base64.getDecoder().decode(base64Encoded)
        val keyFactory = KeyFactory.getInstance("EC")
        return keyFactory.generatePublic(X509EncodedKeySpec(keyBytes))
    }
}
