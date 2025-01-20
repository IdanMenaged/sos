/**
 * Idan Menaged
 * main screen for a sender client
 */

package com.example.sender

import Geolocation
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.sender.ui.theme.SenderTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


/**
 * Main activity for the app
 */
class AppActivity : ComponentActivity() {
    /**
     * defines the ui
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SenderTheme {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Profile button in top-right corner
                    IconButton(
                        onClick = {
                            startActivity(Intent(this@AppActivity, ProfileActivity::class.java))
                        },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                            .size(56.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "User Profile",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Main SOS button remains centered
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        SosButton()
                    }
                }
            }
        }

        // start listener
        val intent = Intent(this, ListenerService::class.java)
        startService(intent)
    }


    /**
     * defines the SOS button element
     */
    @Composable
    fun SosButton() {
        val geo = Geolocation(this)
        Button(onClick = {
//            CoroutineScope(Dispatchers.IO).launch {
//                val serverCommunicator = ServerCommunicator()
//                var username = ""
//                openFileInput("user").bufferedReader().useLines { lines ->
//                    username = lines.first()
//                }
//
//                val connections = serverCommunicator.sendNRecv("get_connections $username")
//                val formattedConnections = connections?.replace(",", " ")
//                if (formattedConnections != null) {
//                    Log.d("SOS btn", formattedConnections)
//                }
//
//                serverCommunicator.sendNRecv("send_to sos $username $formattedConnections")
//
//                serverCommunicator.closeConnection()
                val loc = geo.getLocation()
//            }
        }) {
            Text("SOS")
        }
    }
}