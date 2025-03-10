package com.example.sender

import com.example.encryptionsandbox.DiffieHellman
import com.example.encryptionsandbox.Protocol
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.net.ServerSocket
import java.net.Socket
import java.security.Security
import kotlin.concurrent.thread


class Cipher {

    companion object {
        /**
         * Sends the Diffie-Hellman public key and receives the peer's public key.
         * Uses the exchanged keys to generate a shared secret key.
         * @param conn Pair of (Socket, key) representing the connection and encryption key.
         * @return The generated shared secret key.
         */
        fun sendRecvKey(conn: Socket): ByteArray {
            val dh = DiffieHellman()
            // Send DH public key
            Protocol.sendBin(conn, dh.serializePublicKey())
            // Receive DH public key from the peer
            val dhKeyBytes = Protocol.receiveBin(conn)
            val dhKey = dh.deserializePublicKey(dhKeyBytes)
            // Generate shared key
            return dh.getKey(dhKey)
        }

        /**
         * Receives the client's Diffie-Hellman key, sends the server's DH key, and generates a shared key.
         * @param conn Pair of (Socket, key) representing the connection and encryption key.
         * @return The generated shared secret key.
         */
        fun recvSendKey(conn: Socket): ByteArray {
            // Receive DH public key from the peer
            val dhKeyBytes = Protocol.receiveBin(conn)
            val dh = DiffieHellman()
            val dhKey = dh.deserializePublicKey(dhKeyBytes)
            // Send DH public key
            Protocol.sendBin(conn, dh.serializePublicKey())
            // Generate shared key
            return dh.getKey(dhKey)
        }
    }
}

fun main() {
    // Server-side: Receives the client's key, sends its own key, and generates a shared secret.
    Security.addProvider(BouncyCastleProvider())
    thread {
        val serverSocket = ServerSocket(12345)
        println("Server is running and waiting for a connection...")
        val clientSocket = serverSocket.accept()
        println("Client connected")

        // Receive and send keys using `recvSendKey`
        val sharedKey = Cipher.recvSendKey(clientSocket)
        println("Server generated shared key: ${sharedKey.contentToString()}")

        clientSocket.close()
        serverSocket.close()
    }

    // Client-side: Sends its key, receives the server's key, and generates a shared secret.
    Thread.sleep(1000) // Wait for the server to start
    val socket = Socket("localhost", 12345)
    println("Connected to server")

    // Send and receive keys using `sendRecvKey`
    val sharedKey = Cipher.sendRecvKey(socket)
    println("Client generated shared key: ${sharedKey.contentToString()}")

    socket.close()
}

