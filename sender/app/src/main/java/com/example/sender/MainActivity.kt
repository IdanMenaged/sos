package com.example.sender

import android.os.Bundle
import android.util.Log
import android.widget.RelativeLayout
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.sender.ui.theme.SenderTheme
import java.io.OutputStream
import java.net.Socket

private const val SERVER_IP = "82.166.89.189" // needs to be changed depending on the server ip
private const val SERVER_PORT = 1234 // needs to match the port server is running on

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
        Log.d("sosbtn", "pressed")
    }) {
        Text("SOS")
    }
}

@Preview(showBackground = false)
@Composable
fun SosButtonPreview() {
    SenderTheme {
        SosButton()
    }
}

//fun sendMessageToServer(ip: String, port: Int, message: String) {
//    var socket: Socket? = null
//    var outputStream: OutputStream? = null
//
//    outputStream.write(message.toByteArray())
//    outputStream.flush()
//}