"""
Idan Menaged
protocol for sending messages between server and client
"""

import base64
from my_aes import AESCipher

MSG_LEN_PADDING = 4  # n of bytes to put in front of the content to show its length
MAX_CHUNK_SIZE = 1024
BIN_DONE = -1  # Code to send when a binary transfer is over


class Protocol:
    """
    Communication protocol between server and client
    """

    @staticmethod
    def send(conn, content):
        """
        Send an encrypted message over the socket with Base64 encoding.
        :param conn: Tuple (socket, key)
        :param content: String content to send
        """
        socket, key = conn
        content_bytes = content.encode()  # Convert to bytes
        encrypted = AESCipher.encrypt(key, content_bytes)  # Encrypt with AES
        encoded = base64.b64encode(encrypted).decode()  # Base64 encode
        socket.sendall((encoded + "\n").encode())  # Ensure newline termination

    @staticmethod
    def receive(conn):
        """
        Receive an encrypted message over the socket and decode it.
        :param conn: Tuple (socket, key)
        :return: Decrypted string content
        """
        socket, key = conn
        received_data = socket.makefile().readline().strip()  # Read one line
        try:
            decoded = base64.b64decode(received_data)  # Decode Base64
            decrypted = AESCipher.decrypt(key, decoded)  # Decrypt AES
            return decrypted.decode()
        except base64.binascii.Error as e:
            print(f"[ERROR] Base64 decoding failed: {e}")
            return None

    @staticmethod
    def send_bin(conn, content):
        """
        Send binary data with Base64 encoding.
        :param conn: Tuple (socket, key)
        :param content: Binary content to send
        """
        socket, key = conn
        while content:
            chunk = content[:MAX_CHUNK_SIZE]
            content = content[MAX_CHUNK_SIZE:]

            encrypted = AESCipher.encrypt(key, chunk)  # Encrypt with AES
            encoded = base64.b64encode(encrypted).decode() + "\n"  # Base64 encode + newline
            socket.sendall(encoded.encode())

        # Send the termination signal
        bin_done = str(BIN_DONE).encode()
        encrypted_done = AESCipher.encrypt(key, bin_done)
        encoded_done = base64.b64encode(encrypted_done).decode() + "\n"
        socket.sendall(encoded_done.encode())

    @staticmethod
    def receive_bin(conn):
        """
        Receive binary data and decode it from Base64.
        :param conn: Tuple (socket, key)
        :return: Decrypted binary data
        """
        socket, key = conn
        data = b''

        while True:
            received_data = socket.makefile().readline().strip()  # Read one line
            try:
                decoded = base64.b64decode(received_data)  # Decode Base64
                decrypted = AESCipher.decrypt(key, decoded)  # Decrypt AES

                if decrypted == str(BIN_DONE).encode():
                    break

                data += decrypted
            except base64.binascii.Error as e:
                print(f"[ERROR] Base64 decoding failed: {e}")
                break

        return data
