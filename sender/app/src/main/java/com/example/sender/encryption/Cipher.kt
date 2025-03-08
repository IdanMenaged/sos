package com.example.sender.encryption

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import java.net.Socket
import java.io.InputStream
import java.io.OutputStream
import java.security.MessageDigest
import javax.crypto.spec.SecretKeySpec

class Cipher {
    companion object {
        private const val TAG = "Cipher"

        @RequiresApi(Build.VERSION_CODES.O)

        fun sendRecvKey(conn: Socket): ByteArray {
            val dh = DiffieHellman()

            // Send the client's public key to the server
            sendBin(conn.getOutputStream(), dh.serializePublicKeyToPEM().toByteArray()) // DH public key

            // Receive the server's public key
            val dhKeyBytes = recvBin(conn.getInputStream()) // DH public key
            val dhKey = dhKeyBytes

            // Generate the shared secret using the received public key
            val sharedSecret = dh.generateSharedSecret(dhKey)

            // Hash the shared secret to a fixed 32-byte length suitable for AES (AES-256)
            val digest = MessageDigest.getInstance("SHA-256")
            val hashedSecret = digest.digest(sharedSecret) // This will give you a 32-byte key

            // Derive a SecretKeySpec using the hashed shared secret for AES
            val aesKey = SecretKeySpec(hashedSecret, "AES")

            return aesKey.encoded // This is the final AES key (32 bytes)
        }

        fun recvSendKey(conn: Socket): ByteArray {
            val dhKeyBytes = recvBin(conn.getInputStream())
            val dh = DiffieHellman()
            sendBin(conn.getOutputStream(), dh.publicKey)
            return dh.generateSharedSecret(dhKeyBytes)
        }

        // todo: magic numbers
        @SuppressLint("NewApi")
        private fun sendBin(outputStream: OutputStream, data: ByteArray) {
            val lengthPrefix = "%04d".format(data.size) // Ensures a 4-character length prefix
            val encodedData = lengthPrefix.toByteArray(Charsets.UTF_8) + data
            Log.d(TAG, "encoded: " + String(encodedData))
            Log.d(TAG, "length: $lengthPrefix")
            Log.d(TAG, "og size: ${data.size}")
            Log.d(TAG, "data: $data")
            outputStream.write(encodedData)
            outputStream.flush()
        }

        @SuppressLint("NewApi")
        private fun recvBin(inputStream: InputStream): ByteArray {
            val lengthBuffer = ByteArray(4)
            inputStream.read(lengthBuffer, 0, 4) // Read the first 4 bytes for the length
            val lengthString = lengthBuffer.toString(Charsets.UTF_8)
            val messageLength = lengthString.trim().toInt() // Convert length string to an integer

            val messageBuffer = ByteArray(messageLength)
            inputStream.read(messageBuffer, 0, messageLength) // Read the exact number of bytes
            return messageBuffer
        }

    }
}
