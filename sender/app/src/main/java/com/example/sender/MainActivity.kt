/**
 * Idan Menaged
 * main screen for a sender client
 */

package com.example.sender

import android.content.Intent
import android.os.Bundle
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

        // create listener thread
//        CoroutineScope(Dispatchers.IO).launch {
//            val listener = Listener()
//            // todo: needs to run even when app is closed and when server goes down and back up
//        }

        val intent = Intent(this, ListenerService::class.java)
        startService(intent)
    }

    /**
     * defines the SOS button element
     */
    @Composable
    fun SosButton() {
        Button(onClick = {
            CoroutineScope(Dispatchers.IO).launch {
                val serverCommunicator = ServerCommunicator()
                serverCommunicator.sendNRecv("send_to sos 127.0.0.1")
                serverCommunicator.closeConnection()
            }
        }) {
            Text("SOS")
        }
    }
}