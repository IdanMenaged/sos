import socket

from protocol import Protocol
from key_exchange import *


class Cipher(object):
    @staticmethod
    def recv_send_key(conn):
        """ recieves client dh key and sends both dh key and aes key """
        dh = DiffieHellman()

        # receive key
        other_public_key = Protocol.receive_bin(conn)
        other_public_key = dh.deserialize_public_key(other_public_key)

        # send key
        own_public_key = dh.serialize_public_key()
        Protocol.send_bin(conn, own_public_key)

        # derive key
        aes_key = dh.get_key(other_public_key)
        return aes_key


if __name__ == '__main__':
    # init socket
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    sock.bind(('0.0.0.0', 4000))
    sock.listen(1)

    client_sock, addr = sock.accept()

    # perform key exchange
    key = Cipher.recv_send_key((client_sock, None))
    print(f'aes key: {key.hex()}')

    client_sock.close()
    sock.close()
