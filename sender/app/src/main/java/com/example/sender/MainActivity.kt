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
import java.io.OutputStream
import java.net.InetSocketAddress
import java.net.Socket

private const val SERVER_IP = "placeholder" // needs to be changed depending on the server ip
private const val SERVER_PORT = 1234 // needs to match the port server is running on
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
            sendMessageToServer("echo hi")
        }
    }) {
        Text("SOS")
    }
}

private fun sendMessageToServer(msg: String) {
    try {
        val socket = Socket()
        socket.connect(InetSocketAddress(SERVER_IP, SERVER_PORT), TIMEOUT)

        val outputStream: OutputStream = socket.getOutputStream()
        outputStream.write(msg.toByteArray())
        outputStream.flush()

        outputStream.close()
        socket.close()
    } catch (e: Exception) {
        Log.e("sosbtn", "error in sendMessageToServer", e)
    }
}