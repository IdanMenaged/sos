/**
 * Idan Menaged
 * main screen for a sender client
 */

package com.example.sender

import Geolocation
import VoiceRecorder
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
    private lateinit var geo: Geolocation

    /**
     * defines the ui
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SenderTheme {
                MainScreen()
            }
        }

        // start listener
        // todo: rename listener to avoid confusion with speech recognition system
        val intent = Intent(this, ListenerService::class.java)
        startService(intent)


    }


    /**
     * defines the SOS button element
     */
    @Composable
    fun SosButton() {
        Button(onClick = {
            val location = geo.getLocation()

            CoroutineScope(Dispatchers.IO).launch {
                val serverCommunicator = ServerCommunicator()
                var username = ""
                openFileInput("user").bufferedReader().useLines { lines ->
                    username = lines.first()
                }

                val connections = serverCommunicator.sendNRecv("get_connections $username")
                val formattedConnections = connections?.replace(",", " ")
                if (formattedConnections != null) {
                    Log.d("SOS btn", formattedConnections)
                }

                serverCommunicator.sendNRecv(
                    "send_to $username,${location.latitude},${location.longitude} $formattedConnections"
                )

                serverCommunicator.closeConnection()
            }
            // TODO: listen continuously rather than on button press
            //val voiceRecorder = VoiceRecorder(this)
        }) {
            Text("SOS")
        }
    }

    @Composable
    fun MainScreen() {
        var isGeolocationReady by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            geo = Geolocation(this@AppActivity) {
                isGeolocationReady = true
            }
        }

        if (!isGeolocationReady) {
            LoadingScreen()
        }
        else {
            MainContent()
        }
    }

    @Composable
    fun LoadingScreen() {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }

    @Composable
    fun MainContent() {
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