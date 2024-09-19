MSG_LEN_PADDING = 4  # n of bytes to put in front of the content to show its len
MAX_CHUNK_SIZE = 1024
BIN_DONE = -1  # code to send when a binary is over


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

        socket.send(content)

    @staticmethod
    def receive(socket):
        """
        :param socket: comm socket
        :return: string with content
        """
        content_len = 0
        len_received = 0
        while len_received < MSG_LEN_PADDING:
            packet = socket.recv(MSG_LEN_PADDING)
            len_received += len(packet)
            content_len += int(packet.decode())

        len_received = 0
        content = ""
        while len_received < content_len:
            packet = socket.recv(content_len)
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
        len_sent = 0
        len_to_send = len(content)
        while len_sent < len_to_send:
            chunk_size = min(MAX_CHUNK_SIZE, len(content))  # sometimes the content is not perfectly divisible by
            # MAX_CHUNK_SIZE
            chunk = content[:chunk_size]
            content = content[chunk_size:]
            len_sent += len(chunk)
            socket.send(Protocol.add_prefix(chunk))
        socket.send(Protocol.add_prefix(str(BIN_DONE).encode()))

    @staticmethod
    def receive_bin(socket):
        """
        receive binary data
        :param socket: socket
        :return: data received
        """
        data = b''
        while True:
            content_len = 0
            len_received = 0
            while len_received < MSG_LEN_PADDING:
                packet = socket.recv(MSG_LEN_PADDING)
                len_received += len(packet)
                content_len += int(packet.decode())

            len_received = 0
            chunk = b""
            while len_received < content_len:
                packet = socket.recv(content_len)
                len_received += len(packet)
                chunk += packet

            if chunk == b'-1':
                break

            data += chunk

        return data
