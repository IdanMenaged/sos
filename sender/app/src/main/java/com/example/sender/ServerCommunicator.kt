/**
 * Idan Menaged
 */

package com.example.sender

import android.util.Log
import java.io.InputStream
import java.io.OutputStream
import java.net.InetSocketAddress
import java.net.Socket

private const val SERVER_IP = "10.0.2.2" // special built-in port that directs to development machine
private const val SERVER_PORT = 4000 // needs to match the port server is running on
private const val MSG_LEN_PADDING = 4 // for formatting messages in a way the server can understand
private const val TIMEOUT = 10000


/**
 * Handles server communication
 */
class ServerCommunicator {
    private val socket = initSocket()
    private val outputStream = socket?.getOutputStream()
    private val inputStream = socket?.getInputStream()

    /**
     * send a request to the server and receive a response.
     * logs the response.
     * msg (String): message to send to the server
     */
    fun sendNRecv(msg: String) {
        try {
            // Send message to server
            if (outputStream != null) {
                sendMessageToServer(msg, outputStream)
            }
            if (inputStream != null) {
                val response = receiveMessageFromServer(inputStream)
                Log.d("ServerCommunicator", "Response received: $response")
            }
            else {
                Log.e("ServerCommunicator", "message could not be sent. socket is null")
            }
        } catch (e: Exception) {
            Log.e("ServerCommunicator", "Error in sendNRecv", e)
        }
    }

    fun closeConnection() {
        // todo: close connection in a different function
        try {
            outputStream?.close()
            inputStream?.close()
            socket?.close()
        } catch (e: Exception) {
            Log.e("ServerCommunicator", "Error closing resources", e)
        }
    }

    /**
     * send a message to the server
     * msg (String): message to send to the server
     * outputStream (OutputStream): output stream of the socket
     */
    private fun sendMessageToServer(msg: String, outputStream: OutputStream) {
        val formattedMsg = formatMessage(msg)
        try {
            // Send the message
            outputStream.write(formattedMsg)
            outputStream.flush()
        } catch (e: Exception) {
            Log.e("ServerCommunicator", "Error in sendMessageToServer", e)
        }
    }

    /**
     * receive a message from the server.
     * inputStream (InputStream): input stream of the socket
     * returns (String): message received
     */
    private fun receiveMessageFromServer(inputStream: InputStream): String? {
        try {
            // Get the message length
            val msgLenBytes = ByteArray(MSG_LEN_PADDING)
            inputStream.read(msgLenBytes)
            val msgLen = String(msgLenBytes).trim().toInt()

            // Read the actual message
            val messageBytes = ByteArray(msgLen)
            inputStream.read(messageBytes)

            return String(messageBytes)
        } catch (e: Exception) {
            Log.e("ServerCommunicator", "Error in receiveMessageFromServer", e)
        }

        return null
    }

    /**
     * adds a prefix to the message to denote it's length
     */
    private fun formatMessage(msg: String): ByteArray {
        val lengthString = msg.length.toString().padStart(MSG_LEN_PADDING, '0')
        return lengthString.toByteArray(Charsets.UTF_8) + msg.toByteArray(Charsets.UTF_8)
    }

    /**
     * initiate a socket with connection to the server
     */
    private fun initSocket(): Socket? {
        try {
            val socket = Socket()
            socket.soTimeout = TIMEOUT
            socket.connect(InetSocketAddress(SERVER_IP, SERVER_PORT), TIMEOUT)

            return socket
        } catch (e: Exception) {
            Log.e("ServerCommunicator", "error initiating server: $e")
            return null
        }
    }
}
