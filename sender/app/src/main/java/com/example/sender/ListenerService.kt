package com.example.sender

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ListenerService: Service() {
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Toast.makeText(
            applicationContext, "Listener service has started running in the background",
            Toast.LENGTH_SHORT
        ).show()

        Log.d("ListenerService","Starting Service")

        CoroutineScope(Dispatchers.IO).launch {
            val listener = Listener()
        }


        return START_STICKY
    }

    override fun onDestroy() {
        Toast.makeText(
            applicationContext, "Service execution completed",
            Toast.LENGTH_SHORT
        ).show()
        Log.d("ListenerService","Service Stopped")
        super.onDestroy()
    }
}