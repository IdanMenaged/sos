package com.example.sender.encryption

import android.annotation.SuppressLint
import android.util.Log
import java.security.*
import java.security.spec.X509EncodedKeySpec
import java.util.Base64
import javax.crypto.KeyAgreement
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

class DiffieHellman {
    // todo: serialize key with PEM format and change Cipher to use the serialized keys
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
        val keyFactory = KeyFactory.getInstance("EC")
        val otherPublicKey = keyFactory.generatePublic(X509EncodedKeySpec(otherPublicKeyBytes))

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
}

@SuppressLint("NewApi")
fun main() {
    val alice = DiffieHellman()
    val bob = DiffieHellman()

    val aliceSharedSecret = alice.generateSharedSecret(bob.publicKey)
    val bobSharedSecret = bob.generateSharedSecret(alice.publicKey)

    val aliceKey = alice.deriveKey(aliceSharedSecret)
    val bobKey = bob.deriveKey(bobSharedSecret)

    println("Alice Key: ${Base64.getEncoder().encodeToString(aliceKey.encoded)}")
    println("Bob Key: ${Base64.getEncoder().encodeToString(bobKey.encoded)}")
}
