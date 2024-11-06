/**
 * Idan Menaged
 */

package com.example.sender

/**
 * a class that listens to messages from the server on a different socket and thread than the one
 * sending messages. responsible for receiving the sos messages of others
 */
class Listener : ServerCommunicator() {
    // todo: go back to listening after a message
    init {
        sendNRecv("am_listener")
        receiveMessageFromServer()
    }
}