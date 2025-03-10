"""
Server
Idan Menaged
"""
import socket
import sys
from protocol import Protocol
from constants import *
import methods
import threading

from auth import Auth
from cipher import Cipher
from social import Social

IP = '0.0.0.0'
SIM_USERS = 1
SOCKET_INDEX = 0


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
        self.listeners = {}  # user_id: connection
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

            clnt_thread = threading.Thread(target=self.handle_client,
                                           args=(client_socket, addr))
            clnt_thread.start()

    def handle_client(self, client_socket, addr):
        """
        handle a single client and send them a response based on their request
        :return: should server terminate?
        """
        key = Cipher.recv_send_key((client_socket, None))
        conn = (client_socket, key)

        while True:
            try:
                req = Protocol.receive(conn).lower()

                res = self.handle_req(conn, req, addr)

                if req.split()[0] in BIN_METHODS:
                    Protocol.send_bin(conn, res)
                else:
                    Protocol.send(conn, res)

                if res in EXIT_CODES:
                    break
            except socket.error as e:
                print("booz!!", e)
                break
            #except Exception as e:
            #    print("booozzz!!!", e)
            #    break

        client_socket.close()

        for user_id, listener_conn in self.listeners.items():
            if client_socket == listener_conn[SOCKET_INDEX]:
                self.listeners.pop(user_id)
                print(f'listener of user {user_id} has been terminated')
                break

        return False

    def handle_req(self, conn, req, addr):
        """
        determines a response based on a request
        :param addr: client address, format: (ip, port)
        :param conn: client socket and key
        :param req: request
        :return: response
        """
        print(f"received req: {req}")
        cmd, *params = req.split()

        # special exceptions
        if cmd == 'send_to':
            res = self.send_to(*params)
        elif cmd == 'am_listener':
            self.listeners[params[0]] = conn
            print(self.listeners)
            print(f"listener at {addr[0]}")
            res = 'current connection in listening mode'
        else:
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

    def send_to_single(self, user_id, msg):
        """send a message to a certain connected client

        Args:
            user_id (str): user id of the connected client
            msg (str): message to send

        Returns:
            str: message to send back to the sending client
        """
        if user_id not in self.listeners.keys():
            return f'client {user_id} does not have a listener connection'
        else:
            target_conn = self.listeners[user_id]
            Protocol.send(target_conn, msg)

            return 'message sent'

    def send_to(self, msg: str, *user_ids):
        """
        send a message to multiple ips
        :param msg: message to send
        :param user_ids: ids of users to send message to
        :return: msg detailing which ips were sent to and which failed
        """
        log = []
        for id in user_ids:
            res = self.send_to_single(user_id=id, msg=msg)
            log.append(f'{id}: {res}')
        return "\n".join(log)

if __name__ == '__main__':
    main()
