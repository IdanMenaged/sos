package com.example.sender.encryption

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.sender.ServerCommunicator
import java.net.Socket
import java.io.InputStream
import java.io.OutputStream
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.security.MessageDigest
import java.util.Base64
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
            println("shared secret: ${String(sharedSecret)}") // for tests

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

@RequiresApi(Build.VERSION_CODES.O)
fun main() {
    // init socket
    val socket = Socket()
    socket.soTimeout = 9999999
    socket.connect(InetSocketAddress("127.0.0.1", 4000), 9999999)

    // perform key exchange
    val key = Cipher.sendRecvKey(socket)
    val ownHex = key.joinToString("") { "%02x".format(it) }
    println(ownHex)

    socket.close()
}
