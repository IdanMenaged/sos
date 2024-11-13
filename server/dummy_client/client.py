"""
Idan Menaged
testing client
real client will connect through the app
"""

import os.path
import socket
import sys
from protocol import Protocol
from constants import *
from methods import Methods
import methods

IP = '127.0.0.1'


def main():
    """
    constructs a client and runs it
    """
    client = Client(IP, PORT)
    client.handle_user_input()


class Client:
    """
    client to communicate with server
    """
    def __init__(self, ip, port):
        """
        constructor
        """
        try:
            self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            self.sock.connect((ip, port))
        except socket.error as msg:
            print(f'Connection failure: {msg}\nterminating program')
            sys.exit(1)

    @staticmethod
    def valid_request(req):
        """
        :param req: request
        :return: is req valid
        """
        cmd, *params = req.split()
        return cmd in PARAM_COUNTS.keys() and (len(params) == PARAM_COUNTS[cmd] or PARAM_COUNTS[cmd] == -1)

    def send_request_to_server(self, req):
        """
        :param req: request
        sends req to server
        """
        Protocol.send(self.sock, req)

    def handle_server_response(self, req):
        """
        recv and handle server response
        :param req: request
        :return: response
        """
        try:
            if req.split()[0] in BIN_METHODS:
                res = Protocol.receive_bin(self.sock)
            else:
                res = Protocol.receive(self.sock)
        except socket.error as err:
            res = 'quit'

        if res == 'illegal command' or res == b'illegal command':
            return 'illegal command'

        if req.split()[0] == 'send_file':
            base_name = os.path.basename(req.split()[1])
            save_to = os.path.join(methods.FILE_PATH, base_name)
            Methods.save_to_file(save_to, res)
            res = 'file sent'

        elif req.split()[0] == 'reload':
            res = self.handle_reload()

        return res

    def handle_user_input(self):
        """
        recv and send requests to server
        """
        req = ''
        while req not in EXIT_CODES:
            req = input('please enter a request ')

            if not self.valid_request(req):
                print('illegal request')
            else:
                try:
                    self.send_request_to_server(req)
                except socket.error:
                    req = 'quit'
                res = self.handle_server_response(req)
                if res not in EXIT_CODES:
                    print(res)

    def handle_reload(self):
        """
        to be called whenever a user wants to reload
        sends the server the contents of 'methods.py' and get a response
        :return: server response
        """
        content = Methods.send_file(METHODS_PATH)
        Protocol.send_bin(self.sock, content)
        return Protocol.receive(self.sock)


if __name__ == '__main__':
    main()
