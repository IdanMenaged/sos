package com.example.sender

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ServerSettingsScreen()
        }
    }

    @Composable
    fun ServerSettingsScreen() {
        val context = LocalContext.current

        // Load the saved IP when screen starts
        val sharedPref = context.getSharedPreferences("server_settings", Context.MODE_PRIVATE)
        var ipAddress by remember { mutableStateOf(sharedPref.getString("server_ip", "") ?: "") }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Top Row with Go Back button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                Button(
                    onClick = {
                        context.startActivity(Intent(context, AppActivity::class.java))
                    }
                ) {
                    Text("Go Back")
                }
            }

            Text(
                text = "Server Settings",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            )

            TextField(
                value = ipAddress,
                onValueChange = { ipAddress = it },
                label = { Text("Server IP Address") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    with(sharedPref.edit()) {
                        putString("server_ip", ipAddress)
                        apply()
                    }
                    Toast.makeText(context, "Server IP updated!", Toast.LENGTH_SHORT).show()
                    Log.d("SettingsActivity",
                        "server ip changed to " +
                                "${sharedPref.getString("server_ip", "")}")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save IP Address")
            }
        }
    }
}