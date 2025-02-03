package com.example.sender

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

/**
 * Idan Menaged
 *
 * A class that listens to messages from the server on a different socket and thread than the one
 * sending messages. Responsible for receiving the SOS messages of others.
 */

class Listener(private val context: Context) : ServerCommunicator() {

    private val channelId = "listener_notifications"
    private val notificationId = 1

    init {
        // Create a notification channel
        createNotificationChannel()

        var username = ""
        context.openFileInput("user").bufferedReader().useLines { lines ->
            username = lines.first()
        }

        val handler = Handler(Looper.getMainLooper())  // Handler for main thread

        Thread {
            while (true) {
                sendNRecv("am_listener $username")
                val msg = receiveMessageFromServer()
                if (msg != null) {
                    Log.d("Listener", msg)

                    // Show push notification
                    // if not showing, check "show as pop up" is turned on in the notification
                    // settings
                    val parsedMsg = parseMessage(msg)
                    val sender = parsedMsg[0]
                    val lat = parsedMsg[1]
                    val lon = parsedMsg[2]

                    handler.post {
                        sendNotification(
                            "SOS from $sender",
                            "click here to see their location",
                            lat,
                            lon
                        )
                    }
                }
            }
        }.start()  // Start the listener in a separate thread
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Listener Notifications"
            val descriptionText = "Notifications for new messages"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    // todo: open location when clicking
    @SuppressLint("MissingPermission")
    private fun sendNotification(title: String, message: String, lat: String, lon: String) {
        // handle on click
        val gmmIntentUri = Uri.parse("geo:$lat,$lon?q=$lat,$lon")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
            setPackage("com.google.android.apps.maps")
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            mapIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Replace with your app's icon
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        with(NotificationManagerCompat.from(context)) {
            notify(notificationId, notification)
        }
    }

    /**
     * parse the message from the server
     * @param msg msg received
     * @return sender, latitude, longitude
     */
    private fun parseMessage(msg: String): List<String> {
        return msg.split(',').map { it.trim() }
    }
}
