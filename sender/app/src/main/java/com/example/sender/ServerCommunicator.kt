package com.example.sender

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.example.sender.encryption.AESCipher
import com.example.sender.encryption.Cipher
import java.io.InputStream
import java.io.OutputStream
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketTimeoutException

private const val SERVER_PORT = 4000
const val MSG_LEN_PADDING = 4
private const val TIMEOUT = 10000

@SuppressLint("NewApi")
open class ServerCommunicator(context: Context) {
    private var socket: Socket?
    private var key: ByteArray
    private val outputStream: OutputStream?
    private val inputStream: InputStream?
    private var serverIp: String

    init {
        serverIp = getServerIpFromPreferences(context)
        Log.d("ServerCommunicator", "new server communicator talking to $serverIp")
        socket = initSocket()
        key = socket?.let { Cipher.sendRecvKey(it) }!!
        Log.d("ServerCommunicator", "key: ${String(key)}")

        outputStream = socket?.getOutputStream()
        inputStream = socket?.getInputStream()
    }

    fun sendNRecv(msg: String): String? {
        try {
            if (outputStream != null) {
                sendMessageToServer(msg)
            }
            if (inputStream != null) {
                val response = receiveMessageFromServer()
                Log.d("ServerCommunicator", "Response received: $response")
                return response
            } else {
                Log.e("ServerCommunicator", "message could not be sent. socket is null")
            }
        } catch (e: Exception) {
            Log.e("ServerCommunicator", "Error in sendNRecv", e)
        }
        return "no response"
    }

    fun closeConnection() {
        try {
            outputStream?.close()
            inputStream?.close()
            socket?.close()
        } catch (e: Exception) {
            Log.e("ServerCommunicator", "Error closing resources", e)
        }
    }

    private fun sendMessageToServer(msg: String) {
        Log.d("ServerCommunicator", "sending: $msg")
        val formattedMsg = formatMessage(msg)
        try {
            outputStream?.write(formattedMsg)
            outputStream?.flush()
        } catch (e: Exception) {
            Log.e("ServerCommunicator", "Error in sendMessageToServer", e)
        }
    }

    fun receiveMessageFromServer(): String? {
        try {
            val msgLenBytes = ByteArray(MSG_LEN_PADDING)
            inputStream?.read(msgLenBytes)
            val msgLen = String(msgLenBytes).trim().toInt()

            val messageBytes = ByteArray(msgLen)
            inputStream?.read(messageBytes)

            Log.d("ServerCommunicator", "received enc: ${String(messageBytes)}")
            Log.d("ServerCommunicator", "key: $key")
            val decrypted = AESCipher.decrypt(key, String(messageBytes))

            return String(decrypted)
        } catch (e: SocketTimeoutException) {
            if (this is Listener) {
                Log.w("Listener", "connection timed out, reconnecting")
            } else {
                Log.e("ServerCommunicator", "error in receiveMessageFromServer", e)
            }
        } catch (e: Exception) {
            Log.e("ServerCommunicator", "Error in receiveMessageFromServer", e)
        }
        return null
    }

    private fun formatMessage(msg: String): ByteArray {
        val encrypted = AESCipher.encrypt(key, msg.toByteArray())
        val lengthString = encrypted.length.toString().padStart(MSG_LEN_PADDING, '0')
        return lengthString.toByteArray(Charsets.UTF_8) + encrypted.toByteArray(Charsets.UTF_8)
    }

    private fun initSocket(): Socket? {
        try {
            val socket = Socket()
            socket.soTimeout = TIMEOUT
            socket.connect(InetSocketAddress(serverIp, SERVER_PORT), TIMEOUT)
            return socket
        } catch (e: Exception) {
            Log.e("ServerCommunicator", "error initiating server: $e")
            return null
        }
    }

    private fun getServerIpFromPreferences(context: Context): String {
        val sharedPref = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return sharedPref.getString("server_ip", "")!!
    }
}
