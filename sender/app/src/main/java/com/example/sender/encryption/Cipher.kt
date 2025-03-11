package com.example.sender.encryption

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.sender.MSG_LEN_PADDING
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
            val publicKey = dh.serializePublicKeyToPEM()
            println("sending: $publicKey")

            // Send key
            val formattedMsg = formatMessage(publicKey)
            try {
                conn.getOutputStream().write(formattedMsg.toByteArray())
                conn.getOutputStream().flush()
            } catch (e: Exception) {
                Log.e(TAG, "error sending key", e)
            }

            // receive key
            val otherPublicKey: ByteArray
            try {
                // Get the message length
                val msgLenBytes = ByteArray(MSG_LEN_PADDING)
                conn.getInputStream().read(msgLenBytes)
                val msgLen = String(msgLenBytes).trim().toInt()

                // Read the actual message
                val messageBytes = ByteArray(msgLen)
                conn.getInputStream().read(messageBytes)

                otherPublicKey = messageBytes
                println("received: $otherPublicKey")
            } catch (e: Exception) {
                Log.e(TAG, "error receiving key", e)
                return "".toByteArray()
            }

            // derive key
            val aesKey = dh.deriveAesKey(
                dh.generateSharedSecret(otherPublicKey)
            )
            return aesKey.encoded
        }

        private fun formatMessage(msg: String): String {
            val lengthString = msg.length.toString().padStart(MSG_LEN_PADDING, '0')
            return lengthString + msg
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
    println("aes key: $ownHex")

    socket.close()
}
