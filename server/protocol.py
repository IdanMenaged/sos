"""
Idan Menaged
protocol for sending messages between server and client
"""

from my_aes import AESCipher

MSG_LEN_PADDING = 4  # n of bytes to put in front of the content to show its
# len
MAX_CHUNK_SIZE = 1024
BIN_DONE = -1  # code to send when a binary is over
MIN_CONTENT_LEN = 0
MIN_LEN_RECEIVED = 0
MIN_LEN_SENT = 0
INITIAL_DATA = b''
INITIAL_CHUNK_DATA = b''


class Protocol:
    """
    communication protocol between server and client
    """
    @staticmethod
    def add_prefix(content):
        """
        adds a prefix containing the length of the content
        :param content: content
        :return: content with the prefix
        """
        return str(len(content)).zfill(MSG_LEN_PADDING).encode() + content

    @staticmethod
    def send(conn, content):
        socket, key = conn
        """
        :param conn: socket and key
        :param content: string with the content to be sent
        :return: None
        """
        print(f"sending: {content}")
        content = content.encode()  # convert to bytes
        content = AESCipher.encrypt(key, content)  # encrypt with aes
        print(f'enc: {content}')
        content = Protocol.add_prefix(content)

        socket.send(content)

    @staticmethod
    def receive(conn):
        """
        :param conn: socket and key
        :return: stricontent_lenng with content
        """
        socket, key = conn
        content_len = MIN_CONTENT_LEN
        len_received = MIN_LEN_RECEIVED
        while len_received < MSG_LEN_PADDING:
            packet = socket.recv(MSG_LEN_PADDING - len_received)
            len_received += len(packet)
            content_len += int(packet.decode())

        len_received = MIN_LEN_RECEIVED
        content = ""
        while len_received < content_len:
            packet = socket.recv(content_len - len_received)
            len_received += len(packet)
            content += packet.decode()

        # decrypt
        print(f"received enc: {content}")
        print(f'as bytes: {content.encode()}')
        content = AESCipher.decrypt(key, content.encode())
        print(f'received decrypted: {content}')

        return content

    @staticmethod
    def send_bin(conn, content):
        """
        send binary data
        :param conn: socket and key
        :param content: content to send
        """
        socket, key = conn
        len_sent = MIN_LEN_SENT
        len_to_send = len(content)
        while len_sent < len_to_send:
            chunk_size = min(MAX_CHUNK_SIZE, len(content))  # sometimes the
            # content is not perfectly divisible by

            # MAX_CHUNK_SIZE
            chunk = content[:chunk_size]
            content = content[chunk_size:]
            len_sent += len(chunk)

            if key is not None:
                chunk = AESCipher.encrypt(key, chunk)  # encrypt with aes
            socket.send(Protocol.add_prefix(chunk))
        bin_done = BIN_DONE
        bin_done = str(bin_done).encode()
        if key is not None:
            bin_done = AESCipher.encrypt(key, bin_done)
            bin_done = Protocol.add_prefix(bin_done)
            socket.send(bin_done)

    @staticmethod
    def receive_bin(conn):
        """
        receive binary data
        :param conn: socket and key
        :return: data received
        """
        socket, key = conn
        data = INITIAL_DATA
        while True:
            content_len = MIN_CONTENT_LEN
            len_received = MIN_LEN_RECEIVED
            while len_received < MSG_LEN_PADDING:
                packet = socket.recv(MSG_LEN_PADDING)
                len_received += len(packet)
                if packet != b"":
                    content_len += int(packet.decode())

            len_received = MIN_LEN_RECEIVED
            chunk = INITIAL_CHUNK_DATA
            while len_received < content_len:
                packet = socket.recv(content_len - len_received)
                len_received += len(packet)
                chunk += packet

            # decrypt
            if key is not None:
                chunk = AESCipher.decrypt(key, chunk)

            if chunk == str(BIN_DONE).encode():
                break

            data += chunk
            if len_received == content_len:
                break

        return data
