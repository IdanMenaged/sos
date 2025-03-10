package com.example.encryptionsandbox

import android.os.Build
import androidx.annotation.RequiresApi
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.*
import java.security.spec.X509EncodedKeySpec
import javax.crypto.KeyAgreement
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import java.util.Base64

// Ensure the Bouncy Castle provider is used for cryptography
fun addBouncyCastleProvider() {
    if (Security.getProvider("BC") == null) {
        Security.addProvider(BouncyCastleProvider())
    }
}

class DiffieHellman {
    private val keyPair: KeyPair
    val publicKey: PublicKey

    init {
        val keyPairGenerator = KeyPairGenerator.getInstance("EC", "BC")
        keyPairGenerator.initialize(384) // Equivalent to SECP384R1
        keyPair = keyPairGenerator.generateKeyPair()
        publicKey = keyPair.public
    }

    fun serializePublicKey(): ByteArray {
        return publicKey.encoded
    }

    fun deserializePublicKey(data: ByteArray): PublicKey {
        val keySpec = X509EncodedKeySpec(data)
        val keyFactory = KeyFactory.getInstance("EC", "BC")
        return keyFactory.generatePublic(keySpec)
    }

    fun getKey(otherPublicKey: PublicKey): ByteArray {
        val keyAgreement = KeyAgreement.getInstance("ECDH", "BC")
        keyAgreement.init(keyPair.private)
        keyAgreement.doPhase(otherPublicKey, true)

        // Derive shared secret
        val sharedSecret = keyAgreement.generateSecret()

        // Use HKDF to derive a key (simulated here with SHA-256 hashing)
        val keySpec = PBEKeySpec(sharedSecret.toString(Charsets.UTF_8).toCharArray())
        val secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        return secretKeyFactory.generateSecret(keySpec).encoded
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun main() {
    addBouncyCastleProvider()

    val alice = DiffieHellman()
    val bob = DiffieHellman()

    val bobKey = bob.getKey(alice.publicKey)
    println("Bob's derived key: ${Base64.getEncoder().encodeToString(bobKey)}")

    val aliceKey = alice.getKey(bob.publicKey)
    println("Alice's derived key: ${Base64.getEncoder().encodeToString(aliceKey)}")

    println("equals: ${Base64.getEncoder().encodeToString(bobKey) == Base64.getEncoder().encodeToString(aliceKey)}")
}
