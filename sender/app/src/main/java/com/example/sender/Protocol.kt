package com.example.encryptionsandbox

import java.io.InputStream
import java.io.OutputStream
import java.net.Socket
import java.net.ServerSocket
import kotlin.concurrent.thread

object Protocol {

    private const val MSG_LEN_PADDING = 4 // Number of bytes for message length prefix
    private const val MAX_CHUNK_SIZE = 1024
    private const val BIN_DONE = -1 // Marker to indicate end of binary data

    // Add a prefix with the length of the content
    fun addPrefix(content: ByteArray): ByteArray {
        val lengthPrefix = String.format("%04d", content.size).toByteArray() // Pad length to 4 characters
        return lengthPrefix + content
    }

    // Send string content through a socket
    fun send(socket: Socket, content: String) {
        val outputStream = socket.getOutputStream()
        val contentBytes = content.toByteArray() // Convert to bytes
        val prefixedContent = addPrefix(contentBytes)
        outputStream.write(prefixedContent)
        outputStream.flush()
    }

    // Receive string content from a socket
    fun receive(socket: Socket): String {
        val inputStream = socket.getInputStream()

        // Read length prefix
        var lenReceived = 0
        val lengthBuffer = ByteArray(MSG_LEN_PADDING)
        while (lenReceived < MSG_LEN_PADDING) {
            val bytesRead = inputStream.read(lengthBuffer, lenReceived, MSG_LEN_PADDING - lenReceived)
            if (bytesRead == -1) throw IllegalStateException("Connection closed")
            lenReceived += bytesRead
        }
        val contentLength = lengthBuffer.decodeToString().toInt()

        // Read the actual content
        val contentBuffer = ByteArray(contentLength)
        lenReceived = 0
        while (lenReceived < contentLength) {
            val bytesRead = inputStream.read(contentBuffer, lenReceived, contentLength - lenReceived)
            if (bytesRead == -1) throw IllegalStateException("Connection closed")
            lenReceived += bytesRead
        }

        return contentBuffer.decodeToString()
    }

    // Send binary data through a socket
    fun sendBin(socket: Socket, content: ByteArray) {
        val outputStream = socket.getOutputStream()
        var contentToSend = content.copyOf()
        var lenSent = 0

        // Send data in chunks
        while (lenSent < content.size) {
            val chunkSize = minOf(MAX_CHUNK_SIZE, contentToSend.size)
            val chunk = contentToSend.sliceArray(0 until chunkSize)
            contentToSend = contentToSend.sliceArray(chunkSize until contentToSend.size)
            lenSent += chunk.size

            val prefixedChunk = addPrefix(chunk)
            outputStream.write(prefixedChunk)
        }

        // Send BIN_DONE marker
        val binDoneMarker = addPrefix(BIN_DONE.toString().toByteArray())
        outputStream.write(binDoneMarker)
        outputStream.flush()
    }

    // Receive binary data from a socket
    fun receiveBin(socket: Socket): ByteArray {
        val inputStream = socket.getInputStream()
        val data = mutableListOf<Byte>()

        while (true) {
            // Read length prefix
            var lenReceived = 0
            val lengthBuffer = ByteArray(MSG_LEN_PADDING)
            while (lenReceived < MSG_LEN_PADDING) {
                val bytesRead = inputStream.read(lengthBuffer, lenReceived, MSG_LEN_PADDING - lenReceived)
                if (bytesRead == -1) throw IllegalStateException("Connection closed")
                lenReceived += bytesRead
            }
            val contentLength = lengthBuffer.decodeToString().toInt()

            // Read the actual content
            val chunkBuffer = ByteArray(contentLength)
            lenReceived = 0
            while (lenReceived < contentLength) {
                val bytesRead = inputStream.read(chunkBuffer, lenReceived, contentLength - lenReceived)
                if (bytesRead == -1) throw IllegalStateException("Connection closed")
                lenReceived += bytesRead
            }

            // Check if the chunk is BIN_DONE
            if (String(chunkBuffer) == BIN_DONE.toString()) {
                break
            }

            // Add chunk to data
            data.addAll(chunkBuffer.toList())
        }

        return data.toByteArray()
    }
}

fun main() {
    // Server code
    thread {
        val serverSocket = ServerSocket(12345)
        println("Server is running and waiting for a connection...")
        val clientSocket = serverSocket.accept()
        println("Client connected")

        // Test receiving a string message
        val receivedMessage = Protocol.receive(clientSocket)
        println("Server received: $receivedMessage")

        // Test receiving binary data
        val receivedBinary = Protocol.receiveBin(clientSocket)
        println("Server received binary data: ${receivedBinary.decodeToString()}")

        // Server responds with a confirmation message
        Protocol.send(clientSocket, "Message received successfully!")
        clientSocket.close()
        serverSocket.close()
    }

    // Client code
    Thread.sleep(1000) // Wait for the server to start
    val socket = Socket("localhost", 12345)
    println("Connected to server")

    // Test sending a string message
    val message = "Hello, server! This is the client."
    Protocol.send(socket, message)
    println("Client sent: $message")

    // Test sending binary data
    val binaryData = "This is some binary data".toByteArray()
    Protocol.sendBin(socket, binaryData)
    println("Client sent binary data: ${binaryData.decodeToString()}")

    // Client receives a response from the server
    val response = Protocol.receive(socket)
    println("Client received: $response")

    socket.close()
}
