"""
Idan Menaged
protocol for sending messages between server and client
"""

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
    def send(socket, content):
        """
        :param socket: comm socket
        :param content: string with the content to be sent
        :return: None
        """
        content = content.encode()  # convert to bytes
        content = Protocol.add_prefix(content)

        print(content)

        socket.send(content)

    @staticmethod
    def receive(socket):
        """
        :param socket: comm socket
        :return: stricontent_lenng with content
        """
        content_len = MIN_CONTENT_LEN
        len_received = MIN_LEN_RECEIVED
        while len_received < MSG_LEN_PADDING:
            packet = socket.recv(MSG_LEN_PADDING - len_received)  # todo: test
            len_received += len(packet)
            content_len += int(packet.decode())  # todo: make int only at the
            # end

        len_received = MIN_LEN_RECEIVED
        content = ""
        while len_received < content_len:
            packet = socket.recv(content_len - len_received)
            len_received += len(packet)
            content += packet.decode()
        return content

    @staticmethod
    def send_bin(socket, content):
        """
        send binary data
        :param socket: socket
        :param content: content to send
        """
        len_sent = MIN_LEN_SENT
        len_to_send = len(content)
        while len_sent < len_to_send:
            chunk_size = min(MAX_CHUNK_SIZE, len(content))  # sometimes the
            # content is not perfectly divisible by

            # MAX_CHUNK_SIZE
            chunk = content[:chunk_size]
            content = content[chunk_size:]
            len_sent += len(chunk)
            socket.send(Protocol.add_prefix(chunk))
        socket.send(Protocol.add_prefix(str(BIN_DONE).encode()))

    @staticmethod
    def receive_bin(socket):  # todo: fix same as receive
        """
        receive binary data
        :param socket: socket
        :return: data received
        """
        data = INITIAL_DATA
        while True:
            content_len = MIN_CONTENT_LEN
            len_received = MIN_LEN_RECEIVED
            while len_received < MSG_LEN_PADDING:
                packet = socket.recv(MSG_LEN_PADDING)
                len_received += len(packet)
                content_len += int(packet.decode())

            len_received = MIN_LEN_RECEIVED
            chunk = INITIAL_CHUNK_DATA
            while len_received < content_len:
                packet = socket.recv(content_len)
                len_received += len(packet)
                chunk += packet

            if chunk == str(BIN_DONE).encode():
                break

            data += chunk

        return data
