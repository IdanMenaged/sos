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
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.InetSocketAddress
import java.net.Socket

private const val SERVER_IP = "10.0.2.2" // special built in port that directs to development
// machine (i.e. computer hosting the emulator)
private const val SERVER_PORT = 4000 // needs to match the port server is running on
private const val MSG_LEN_PADDING = 4 // for formatting messages in a way the server can understand
private const val TIMEOUT = 10000

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

@Composable
fun SosButton() {
    Button(onClick = {
        CoroutineScope(Dispatchers.IO).launch {
            send_n_recv("echo hi")
        }
    }) {
        Text("SOS")
    }
}

private fun send_n_recv(msg: String) {
    try {
        val socket = Socket()
        socket.soTimeout = TIMEOUT // read timeout
        socket.connect(InetSocketAddress(SERVER_IP, SERVER_PORT), TIMEOUT)

        sendMessageToServer(msg, socket)
        val response = receiveMessageFromServer(socket)
    } catch (e: Exception) {
        Log.e("sosbtn", "error in send_n_recv", e)
    }
}

private fun sendMessageToServer(msg: String, socket: Socket) {
    val formattedMsg = formatMessage(msg)

    try {
        val outputStream: OutputStream = socket.getOutputStream()
        outputStream.write(formattedMsg)
        outputStream.flush()

//        outputStream.close()
//        socket.close()
    } catch (e: Exception) {
        Log.e("sosbtn", "error in sendMessageToServer", e)
    }
}

private fun receiveMessageFromServer(socket: Socket): String? {
    try {

        val inputStream: InputStream = socket.getInputStream()
        val reader = BufferedReader(InputStreamReader(inputStream))

        // get msg len
        val msg_len_bytes = ByteArray(MSG_LEN_PADDING)
        inputStream.read(msg_len_bytes)

        val msg_len = String(msg_len_bytes).trim().toInt()

        val message_bytes = ByteArray(msg_len)
        inputStream.read(message_bytes)

        val message = String(message_bytes)

        // Close the input stream and socket
//        reader.close()
//        socket.close()

        Log.d("sosbtn", "message received: $message")
        return message
    } catch (e: Exception) {
        Log.e("sosbtn", "error in receiveMessageFromServer", e)
    }

    return null
}


/**
 * adds the length in front of the msg and converts it to bytes
 */
private fun formatMessage(msg: String): ByteArray {
    val lengthString = msg.length.toString().padStart(MSG_LEN_PADDING, '0')
    val result = lengthString.toByteArray(Charsets.UTF_8) + msg.toByteArray(Charsets.UTF_8)
    return result
}