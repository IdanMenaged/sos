package com.example.sender.encryption

import android.annotation.SuppressLint
import java.net.Socket
import java.io.InputStream
import java.io.OutputStream
import java.util.Base64

class Cipher {
    companion object {
        fun sendRecvKey(conn: Socket): ByteArray {
            val dh = DiffieHellman()
            sendBin(conn.getOutputStream(), dh.publicKey) // DH public key
            val dhKeyBytes = recvBin(conn.getInputStream()) // DH public key
            val dhKey = dhKeyBytes

            return dh.generateSharedSecret(dhKey)
        }

        fun recvSendKey(conn: Socket): ByteArray {
            val dhKeyBytes = recvBin(conn.getInputStream())
            val dh = DiffieHellman()
            sendBin(conn.getOutputStream(), dh.publicKey)
            return dh.generateSharedSecret(dhKeyBytes)
        }

        @SuppressLint("NewApi")
        private fun sendBin(outputStream: OutputStream, data: ByteArray) {
            val encodedData = Base64.getEncoder().encodeToString(data)
            outputStream.write(encodedData.toByteArray())
            outputStream.flush()
        }

        @SuppressLint("NewApi")
        private fun recvBin(inputStream: InputStream): ByteArray {
            val receivedData = inputStream.bufferedReader().readLine()
            return Base64.getDecoder().decode(receivedData)
        }
    }
}
