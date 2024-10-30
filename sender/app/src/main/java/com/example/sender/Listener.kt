/**
 * Idan Menaged
 */

package com.example.sender

/**
 * a class that listens to messages from the server on a different socket and thread than the one
 * sending messages. responsible for receiving the sos messages of others
 */
class Listener {
    val serverCommunicator = ServerCommunicator()
    // todo: add system for server to differentiate senders and listeners
    // todo: send an "i'm a listener" message
    // todo: wait for data
}