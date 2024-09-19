"""
Idan Menaged
"""
import importlib
import socket
import sys
from protocol import Protocol
from constants import *
import methods
import threading

IP = '0.0.0.0'
SIM_USERS = 1


def main():
    """
    construct a server and run it
    """
    server = Server(IP, PORT)
    server.handle_clients()


class Server:
    """
    server that can communicate with client
    """
    def __init__(self, ip, port):
        """
        constructor
        """
        try:
            self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            self.sock.bind((ip, port))
            self.sock.listen(SIM_USERS)
        except socket.error as msg:
            print(f'Connection failure {msg}\nterminating program')
            sys.exit(1)

    def handle_clients(self):
        """
        handle clients continuously
        """
        while True:
            client_socket, addr = self.sock.accept()
            methods.Methods.new_hist(addr)
            clnt_thread = threading.Thread(target=self.handle_client, args=(client_socket, addr))
            clnt_thread.start()

    def handle_client(self, client_socket, addr):
        """
        handle a single client and send them a response based on their request
        :return: should server terminate?
        """

        while True:
            try:
                req = Protocol.receive(client_socket).lower()
                methods.Methods.add_to_hist(addr, req)

                res = self.handle_req(client_socket, req, addr)

                if req.split()[0] in BIN_METHODS:
                    Protocol.send_bin(client_socket, res)
                else:
                    Protocol.send(client_socket, res)

                if res in EXIT_CODES:
                    break
            except socket.error as e:
                print("booz!!", e)
                break
            except Exception as e:
                print("booozzz!!!", e)
                break

        client_socket.close()
        return False

    @staticmethod
    def handle_req(client_socket, req, addr):
        """
        determines a response based on a request
        :param client_socket: client socket
        :param req: request
        :return: response
        """
        cmd, *params = req.split()

        # special exception
        if cmd == 'reload':
            res = Server.handle_reload(client_socket)
        elif cmd == 'history':
            res = methods.Methods.history(addr)
        else:
            try:
                res = getattr(methods.Methods, cmd)(*params)
            except:
                if cmd in BIN_METHODS:
                    res = b'illegal command'
                else:
                    res = 'illegal command'

            # for testing errors
            # res = getattr(methods.Methods, cmd)(*params)

        return res

    @staticmethod
    def handle_reload(sock):
        """
        to be called on the server after a reload
        saves the new data into 'methods.py' and re-imports
        :return: response to send back
        """
        Protocol.send(sock, 'ready for reload')
        data = Protocol.receive_bin(sock)
        methods.Methods.save_to_file(METHODS_PATH, data)

        importlib.reload(sys.modules['methods'])
        return 'module reloaded'


if __name__ == '__main__':
    main()
