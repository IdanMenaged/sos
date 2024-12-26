"""
Server
Idan Menaged
"""
import importlib
import socket
import sys
from protocol import Protocol
from constants import *
import methods
import threading

from auth import Auth
from social import Social

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
        self.listeners = {}  # user_id: socket
        self.auth = Auth()
        self.social = Social()

        self.method_sources = {self.auth, self.social, methods.Methods}

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

            clnt_thread = threading.Thread(target=self.handle_client,
                                           args=(client_socket, addr))
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

        for user_id, listener_sock in self.listeners.items():
            if client_socket == listener_sock:
                self.listeners.pop(user_id)
                print(f'listener of user {user_id} has been terminated')
                break

        return False

    def handle_req(self, client_socket, req, addr):
        """
        determines a response based on a request
        :param addr: client address, format: (ip, port)
        :param client_socket: client socket
        :param req: request
        :return: response
        """
        cmd, *params = req.split()

        # special exceptions
        # todo: make match case
        if cmd == 'reload':
            res = Server.handle_reload(client_socket)
        elif cmd == 'history':
            res = methods.Methods.history(addr)
        elif cmd == 'send_to':
            res = self.send_to(*params)
        # todo: change am_listener protocol to send user id with it
        elif cmd == 'am_listener':
            self.listeners[params[0]] = client_socket
            print(self.listeners)
            print(f"listener at {addr[0]}")
            res = 'current connection in listening mode'
        else:
            # todo: add auth and social functions to constants.py

            # try all the other method sources available until one succeeds
            res = None
            for source in self.method_sources:
                try:
                    res = getattr(source, cmd)(*params)
                    break
                except:
                    continue

            # if none of the sources worked, return 'illegal command'
            if res is None:
                if cmd in BIN_METHODS:
                    res = b'illegal command'
                else:
                    res = 'illegal command'

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

    def send_to_single(self, user_id, msg):
        """send a message to a certain connected client

        Args:
            target_ip (str): ip of the connected client
            msg (str): message to send

        Returns:
            str: message to send back to the sending client
        """
        if user_id not in self.listeners.keys():
            return f'client {user_id} does not have a listener connection'
        else:
            target_socket = self.listeners[user_id]
            Protocol.send(target_socket, msg)

            return 'message sent'

    # todo: test with actual listener
    def send_to(self, msg: str, *user_ids):
        """
        send a message to multiple ips
        :param msg: message to send
        :param target_ips: ips to send to
        :return: msg detailing which ips were sent to and which failed
        """
        log = []
        for id in user_ids:
            res = self.send_to_single(user_id=id, msg=msg)
            log.append(f'{id}: {res}')
        return "\n".join(log)

if __name__ == '__main__':
    main()
