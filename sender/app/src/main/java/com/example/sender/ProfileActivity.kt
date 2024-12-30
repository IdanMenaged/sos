/**
 * Idan Menaged
 * screen to edit profile data
 */

package com.example.sender

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
     * a form that lets you change name, password, and connections
     */
    @Composable
    fun ProfileForm() {
        // todo: make the form
    }
}