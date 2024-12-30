/**
 * Idan Menaged
 * screen to edit profile data
 */

package com.example.sender

import android.os.Bundle
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.sender.ui.theme.SenderTheme


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
                    // todo: add button to go back to app activity
                }
            }
        }

        // non ui code
    }

    /**
     * a form that lets you change password and connections
     */
    @Composable
    fun ProfileForm() {
        var password by remember { mutableStateOf("") }
        var connections by remember { mutableStateOf(mutableListOf("Connection 1", "Connection 2")) }
        var newConnectionName by remember { mutableStateOf("") }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Profile Settings",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White
            )

            // Password field
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("New Password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation()
            )

            // Connections list
            Text("Connections",
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
                        Text(text = connection,
                            modifier = Modifier.weight(1f),
                            color = Color.White
                        )
                        IconButton(onClick = {
                            connections.remove(connection)
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
                        connections.add(newConnectionName)
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
                    //saveProfileSettings(password, connections)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Changes")
            }
        }
    }
}