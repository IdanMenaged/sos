package com.example.sender

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


class SettingsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SettingsScreen()
        }
    }

    @Composable
    fun SettingsScreen() {
        var ipAddress by remember { mutableStateOf("") }
        val context = LocalContext.current // Get the context

        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            // Go back button
            IconButton(
                onClick = {
                    // Cast context to Activity and call finish() to go back to the previous screen
                    (context as? Activity)?.finish()
                },
                modifier = Modifier
                    .padding(top = 16.dp)
                    .align(Alignment.Start)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Go Back",
                    tint = Color.White
                )
            }

            // Heading text
            Text(
                text = "Server Settings",
                style = TextStyle(
                    fontSize = 32.sp, // Set the font size to make it big
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp), // Add space below the title
                textAlign = TextAlign.Center
            )

            // Input field for IP address
            TextField(
                value = ipAddress,
                onValueChange = { ipAddress = it },
                label = { Text("Enter Server IP") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            // Save button
            Button(
                onClick = {
                    // Save the IP address and show a Toast message
                    val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                    with(sharedPreferences.edit()) {
                        putString("server_ip", ipAddress)
                        apply()
                    }
                    Toast.makeText(context, "Server IP updated", Toast.LENGTH_SHORT).show()
                }
            ) {
                Text("Save")
            }
        }
    }
}