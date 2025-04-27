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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.sender.ui.theme.SenderTheme
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign


class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SenderTheme {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    // content here
                    LoginForm()
                }
            }
        }

        // non content code here
    }

    @Composable
    fun LoginForm() {
        var username by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var isLoading by remember { mutableStateOf(false) }
        val context = LocalContext.current

        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            // Top Row with Settings button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = {
                        val intent = Intent(this@LoginActivity, SettingsActivity::class.java)
                        intent.putExtra("previous_activity", this@LoginActivity::class.java.name)
                        startActivity(intent)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = Color.White
                    )
                }
            }

            // Big title at the top
            Text(
                text = "Login",
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

            // First TextField
            TextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            // Second TextField
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Submit Button
                Button(
                    onClick = {
                        isLoading = true
                        CoroutineScope(Dispatchers.IO).launch {
                            val serverCommunicator = ServerCommunicator(this@LoginActivity)
                            val serverCommand = "login $username $password"
                            val response =
                                serverCommunicator.sendNRecv(serverCommand) // Send data from both fields
                            serverCommunicator.closeConnection()
                            isLoading = false

                            if (response != null) {
                                handleLoginResponse(response, username)
                            }
                        }
                    },
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White
                        )
                    } else {
                        Text("Submit")
                    }
                }

                Spacer(modifier = Modifier.width(16.dp)) // Add spacing between buttons

                // Link/Button to Navigate
                Button(
                    onClick = {
                        val intent = Intent(context, SignupActivity::class.java)
                        context.startActivity(intent)
                    },
                ) {
                    Text("Don't have an account?")
                }
            }
        }
    }


    /**
     * handle response from server after a login attempt
     * @param res response from server
     */
    private fun handleLoginResponse(res: String, username: String) {
        if (res == "success") {
            // store user in local storage
            val filename = "user"
            openFileOutput(filename, MODE_PRIVATE).use {
                it.write(username.toByteArray())
            }

            // check username stored
            // un-comment for testing
//            openFileInput(filename).bufferedReader().useLines { lines ->
//                val username = lines.first()
//                Log.d("Login", "user=$username")
//            }

            val intent = Intent(this, AppActivity::class.java)
            startActivity(intent)
        }
        else {
            runOnUiThread() {
                Toast.makeText(
                    this, "Login failed!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

}