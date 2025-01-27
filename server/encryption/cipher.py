import protocol
from key_exchange import *


class Cipher(object):

    @staticmethod
    def send_recv_key(conn):
        dh = DiffieHellman()
        protocol.Protocol.send_bin(conn,
                               dh.serialize_public_key())  # DH public key
        dh_key_bytes = protocol.Protocol.recv_bin(conn)  # DH public key
        dh_key = dh.deserialize_public_key(dh_key_bytes)

        key = dh.get_key(dh_key)
        return key

    @staticmethod
    def recv_send_key(conn):
        """ recieves client dh key and sends both dh key and aes key """
        dh_key_bytes = protocol.Protocol.recv_bin(conn)
        dh = DiffieHellman()
        dh_key = dh.deserialize_public_key(dh_key_bytes)
        protocol.Protocol.send_bin(conn, dh.serialize_public_key())
        key = dh.get_key(dh_key)
        return key




