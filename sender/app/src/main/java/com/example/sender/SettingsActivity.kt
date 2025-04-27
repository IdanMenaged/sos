package com.example.sender

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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

        Column(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)) {
            TextField(
                value = ipAddress,
                onValueChange = { ipAddress = it },
                label = { Text("Server IP Address") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    with (sharedPref.edit()) {
                        putString("server_ip", ipAddress)
                        apply()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save IP Address")
            }
        }
    }

}