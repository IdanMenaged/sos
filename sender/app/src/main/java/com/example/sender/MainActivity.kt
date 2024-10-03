package com.example.sender

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.sender.ui.theme.SenderTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.InputStream
import java.io.OutputStream
import java.net.InetSocketAddress
import java.net.Socket

private const val SERVER_IP = "10.0.2.2" // special built-in port that directs to development machine
private const val SERVER_PORT = 4000 // needs to match the port server is running on
private const val MSG_LEN_PADDING = 4 // for formatting messages in a way the server can understand
private const val TIMEOUT = 10000

/**
 * Main activity for the app
 */
class MainActivity : ComponentActivity() {
    /**
     * defines the ui
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SenderTheme {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    SosButton()
                }
            }
        }
    }

    /**
     * defines the SOS button element
     */
    @Composable
    fun SosButton() {
        Button(onClick = {
            CoroutineScope(Dispatchers.IO).launch {
                val serverCommunicator = ServerCommunicator()
                serverCommunicator.sendNRecv("echo hi")
            }
        }) {
            Text("SOS")
        }
    }
}

/**
 * Handles server communication
 */
class ServerCommunicator {
    /**
     * send a request to the server and receive a response.
     * logs the response.
     * msg (String): message to send to the server
     */
    fun sendNRecv(msg: String) {
        var socket: Socket? = null
        var outputStream: OutputStream? = null
        var inputStream: InputStream? = null
        try {
            // Create socket and set timeout
            socket = Socket()
            socket.soTimeout = TIMEOUT
            socket.connect(InetSocketAddress(SERVER_IP, SERVER_PORT), TIMEOUT)

            // Send message to server
            outputStream = socket.getOutputStream()
            sendMessageToServer(msg, outputStream)

            // Receive message from server
            inputStream = socket.getInputStream()
            val response = receiveMessageFromServer(inputStream)

            Log.d("ServerCommunicator", "Response received: $response")

        } catch (e: Exception) {
            Log.e("ServerCommunicator", "Error in sendNRecv", e)
        } finally {
            // Close streams and socket after communication is done
            try {
                outputStream?.close()
                inputStream?.close()
                socket?.close()
            } catch (e: Exception) {
                Log.e("ServerCommunicator", "Error closing resources", e)
            }
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
}
