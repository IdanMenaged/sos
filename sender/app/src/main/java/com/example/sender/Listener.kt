/**
 * Idan Menaged
 */

package com.example.sender

/**
 * a class that listens to messages from the server on a different socket and thread than the one
 * sending messages. responsible for receiving the sos messages of others
 */
class Listener {  // todo: make inheritance
    private val serverCommunicator = ServerCommunicator()

    init {
        serverCommunicator.sendNRecv("am_listener")
        serverCommunicator.receiveMessageFromServer()
    }
}