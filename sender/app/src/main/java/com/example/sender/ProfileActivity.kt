/**
 * Idan Menaged
 * screen to edit profile data
 */

package com.example.sender

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.sender.ui.theme.SenderTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/**
 * Main activity for the app
 */
class ProfileActivity : ComponentActivity() {
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
                    ProfileForm()
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp), // Adjust padding as needed
                    contentAlignment = Alignment.TopStart
                ) {
                    Button(
                        onClick = {
                            startActivity(
                                Intent(this@ProfileActivity, AppActivity::class.java)
                            )
                        }
                    ) {
                        Text("Go Back")
                    }
                }
            }
        }

        // non ui code
    }

    @Composable
    fun ProfileForm() {
        var connections by remember { mutableStateOf<List<String>>(emptyList()) }
        var newConnectionName by remember { mutableStateOf("") }

        LaunchedEffect(Unit) { // Runs when the composable is first composed
            connections = getConnections()
            if (connections == listOf("null")) {
                connections = emptyList()
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Profile Settings",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White
            )

            // Connections list
            Text(
                "Connections",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White
            )

            LazyColumn(modifier = Modifier.height(150.dp)) {
                items(connections) { connection ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = connection,
                            modifier = Modifier.weight(1f),
                            color = Color.White
                        )
                        IconButton(onClick = {
                            connections = connections.toMutableList().apply { remove(connection) }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete connection",
                                tint = Color.White
                            )
                        }
                    }
                }
            }

            // Add connection
            OutlinedTextField(
                value = newConnectionName,
                onValueChange = { newConnectionName = it },
                label = { Text("New Connection") },
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = {
                    if (newConnectionName.isNotBlank()) {
                        connections = connections + newConnectionName
                        newConnectionName = ""
                    }
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Add Connection")
            }

            // Save changes button
            Button(
                onClick = {
                    // Handle saving password and connections
                    CoroutineScope(Dispatchers.IO).launch {
                        saveProfileSettings(connections)
                    }
                    Toast.makeText(this@ProfileActivity, "connections updated", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Changes")
            }
        }
    }

    private fun saveProfileSettings(connections: List<String>) {
        var formattedConnections = connections.joinToString(",")
        if (formattedConnections == "") {
            formattedConnections = "NULL"
        }

        var username = ""
        openFileInput("user").bufferedReader().useLines { lines ->
            username = lines.first()
        }

        val comm = ServerCommunicator()
        comm.sendNRecv("update_connections $username $formattedConnections")
        comm.closeConnection()
    }

    private suspend fun getConnections(): List<String> {
        return withContext(Dispatchers.IO) { // Run this block on the IO dispatcher
            var username = ""
            openFileInput("user").bufferedReader().useLines { lines ->
                username = lines.first()
            }

            val comm = ServerCommunicator()
            val connections = comm.sendNRecv("get_connections $username")
            comm.closeConnection()

            connections?.split(",") ?: emptyList()
        }
    }

}