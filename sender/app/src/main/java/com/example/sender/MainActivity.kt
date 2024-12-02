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
import com.example.sender.ui.login.LoginActivity
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

        // start listener
//        val intent = Intent(this, ListenerService::class.java)
//        startService(intent)

        // go to login screen (for testing)
        val loginIntent = Intent(this, LoginActivity::class.java)
        startActivity(loginIntent)

    }

    /**
     * defines the SOS button element
     */
    @Composable
    fun SosButton() {
        Button(onClick = {
            CoroutineScope(Dispatchers.IO).launch {
                val serverCommunicator = ServerCommunicator()
                serverCommunicator.sendNRecv("send_to sos 10.20.75.67")  // change ip based
                // on testing env
                serverCommunicator.closeConnection()
            }
        }) {
            Text("SOS")
        }
    }
}