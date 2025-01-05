/**
 * Idan Menaged
 */

package com.example.sender

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * service to run the Listener class
 */
class ListenerService: Service() {
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    /**
     * start the listener and notify the user
     */
    @SuppressLint("NewApi")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Toast.makeText(
            applicationContext, "Listener service has started running in the background",
            Toast.LENGTH_SHORT
        ).show()

        Log.d("ListenerService","Starting Service")

        val notificationChannel = NotificationChannel("listener_service_channel",
            "Listener Service", NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = "service that listens to the server"
        }

        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)

        val notification = NotificationCompat.Builder(this, "listener_service_channel")
            .setContentTitle("Listener Service")
            .setContentText("Listening for events")
            .build()

        startForeground(1, notification)

        CoroutineScope(Dispatchers.IO).launch {
            val listener = Listener(this@ListenerService)
        }


        return START_STICKY
    }

    /**
     * notify the user on service destruction
     */
    override fun onDestroy() {
        Toast.makeText(
            applicationContext, "Service execution completed",
            Toast.LENGTH_SHORT
        ).show()
        Log.d("ListenerService","Service Stopped")
        super.onDestroy()
    }
}