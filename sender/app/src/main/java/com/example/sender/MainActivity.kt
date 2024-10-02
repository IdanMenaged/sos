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

private const val SERVER_IP = "10.0.2.2" // special built in port that directs to development
// machine (i.e. computer hosting the emulator)
private const val SERVER_PORT = 4000 // needs to match the port server is running on
private const val MSG_LEN_PADDING = 4 // for formatting messages in a way the server can understand
private const val TIMEOUT = 10000

/**
 * UI of the app
 */
class MainActivity : ComponentActivity() {
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
}

/**
 * defines the SOS button
 */
@Composable
fun SosButton() {
    Button(onClick = {
        CoroutineScope(Dispatchers.IO).launch {
            sendNRecv("echo hi")
        }
    }) {
        Text("SOS")
    }
}

/**
 * sends a message to the server and receives a response
 * msg: string - the message to send (i.e. 'echo hi')
 */
private fun sendNRecv(msg: String) {
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

        Log.d("sosbtn", "response received: $response")

    } catch (e: Exception) {
        Log.e("sosbtn", "error in send_n_recv", e)
    } finally {
        // Close streams and socket after communication is done
        try {
            outputStream?.close()
            inputStream?.close()
            socket?.close()
        } catch (e: Exception) {
            Log.e("sosbtn", "error closing resources", e)
        }
    }
}

/**
 * sends a message to the server
 * msg: string - message to send (i.e. 'echo hi')
 * outputStream: OutputStream - the output stream of the socket
 */
private fun sendMessageToServer(msg: String, outputStream: OutputStream) {
    val formattedMsg = formatMessage(msg)
    try {
        // Send the message
        outputStream.write(formattedMsg)
        outputStream.flush()
    } catch (e: Exception) {
        Log.e("sosbtn", "error in sendMessageToServer", e)
    }
}


/**
 * receives a message from the server (i.e. if request was 'echo hi' the message would be 'hi')
 * inputStream: InputStream - the socket's input stream
 * returns: String - the received message
 */
private fun receiveMessageFromServer(inputStream: InputStream): String? {
    try {
        // Get the message length
        val msg_len_bytes = ByteArray(MSG_LEN_PADDING)
        inputStream.read(msg_len_bytes)
        val msg_len = String(msg_len_bytes).trim().toInt()

        // Read the actual message
        val message_bytes = ByteArray(msg_len)
        inputStream.read(message_bytes)

        return String(message_bytes)
    } catch (e: Exception) {
        Log.e("sosbtn", "error in receiveMessageFromServer", e)
    }

    return null
}



/**
 * adds the length in front of the msg and converts it to bytes
 * i.e. hi -> 0002hi
 */
private fun formatMessage(msg: String): ByteArray {
    val lengthString = msg.length.toString().padStart(MSG_LEN_PADDING, '0')
    val result = lengthString.toByteArray(Charsets.UTF_8) + msg.toByteArray(Charsets.UTF_8)
    return result
}