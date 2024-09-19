package com.example.sender

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.sender.ui.theme.SenderTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SenderTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SosButton {  }
                }
            }
        }
    }
}

@Composable
fun SosButton(onClick: () -> Unit) {
    Button(onClick = { onClick() }) {
        Text("SOS")
    }

}

@Preview(showBackground = false)
@Composable
fun SosButtonPreview() {
    SenderTheme {
        SosButton {  }
    }
}