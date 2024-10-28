"""
Idan Menaged
methods the server can execute. not an exhaustive list.
"""

import threading

SCREENSHOT_PATH = 'c:\\test_folder\\server\\screen.jpg'
FILE_PATH = 'c:\\test_folder\\client'


class Methods:
    """
    methods server can execute
    """
    hist = {}  # history of commands sent in the format of { (ip, port): ['placeholder'] }
    lock = threading.Lock()

    @staticmethod
    def new_hist(addr):
        """
        insert new entry to history
        """
        Methods.lock.acquire()
        Methods.hist[addr] = []
        Methods.lock.release()

    @staticmethod
    def add_to_hist(addr, req):
        """
        add request to history
        """
        Methods.lock.acquire()
        Methods.hist[addr].append(req)
        Methods.lock.release()

    @staticmethod
    def history(addr):
        """
        list the command history
        """
        Methods.lock.acquire()
        res = str(Methods.hist[addr])
        Methods.lock.release()
        return res

    @staticmethod
    def echo(msg):
        """
        send back the msg
        used for testing
        :param msg: a message
        :return: msg
        """
        print(f'echoing "{msg}"')
        return msg

    @staticmethod
    def quit():
        """
        send a code to terminate connection with the server
        :return: quit code
        """
        return 'quit'