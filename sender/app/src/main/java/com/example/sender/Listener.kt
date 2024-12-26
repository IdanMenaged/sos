package com.example.sender

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast

/**
 * Idan Menaged
 *
 * A class that listens to messages from the server on a different socket and thread than the one
 * sending messages. Responsible for receiving the SOS messages of others.
 */
// TODO: test notification
class Listener(private val context: Context) : ServerCommunicator() {

    init {
        val handler = Handler(Looper.getMainLooper())  // Handler for main thread

        Thread {
            while (true) {
                sendNRecv("am_listener 1")  // todo: get actual user id rather than this placeholder
                val msg = receiveMessageFromServer()
                if (msg != null) {
                    Log.d("Listener", msg)

                    // Show Toast notification on the main thread
                    handler.post {
                        Toast.makeText(context, "New Message: $msg", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }.start()  // Start the listener in a separate thread
    }
}
