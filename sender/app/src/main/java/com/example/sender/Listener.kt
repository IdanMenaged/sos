/**
 * Idan Menaged
 */

package com.example.sender

import android.util.Log

/**
 * a class that listens to messages from the server on a different socket and thread than the one
 * sending messages. responsible for receiving the sos messages of others
 */
class Listener : ServerCommunicator() {
    init {
        sendNRecv("am_listener")
        while (true) {
            val msg = receiveMessageFromServer()
            if (msg != null) {
                Log.d("Listener", msg)
            }
        }
    }
}