import importlib
import sys
import threading

from PIL import ImageGrab
import glob
import os
import shutil
import subprocess
from constants import *
from protocol import Protocol

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
    def take_screenshot():
        """
        take a screenshot and save it to SCREENSHOT_PATH
        :return: response to send back
        """
        im = ImageGrab.grab()
        im.save(SCREENSHOT_PATH)
        return 'screenshot takenn'

    @staticmethod
    def send_file(file_path):
        """
        read a file and get its contents
        :param file_path: path to the file to send
        :return: contents of the file
        """
        with open(file_path, 'rb') as file:
            content = file.read()
        return content

    @staticmethod
    def dir(path):
        """
        list all files in a directory
        :param path: path to the directory
        :return: string representing all files in that directory
        """
        return str(glob.glob(rf'{path}\*.*'))

    @staticmethod
    def delete(path):
        """
        delete a file
        :param path: path to the file
        :return: response to send
        """
        os.remove(path)
        return 'file deleted'

    @staticmethod
    def copy(origin, destination):
        """
        copy a file to another directory
        :param origin: path to the file
        :param destination: path to the dir the file should be copied to
        """
        shutil.copy(origin, destination)
        return 'file copied'

    @staticmethod
    def execute(program):
        """
        run a program
        :param program: name of the program
        :return: response to be sent back
        """
        subprocess.call(program)
        return 'program executed'

    @staticmethod
    def echo(msg):
        """
        send back the msg
        :param msg: a message
        :return: msg
        """
        return msg

    @staticmethod
    def quit():
        """
        send a code to terminate connection with the server
        :return: quit code
        """
        return 'quit'

    @staticmethod
    def handle_reload(sock):
        """
        to be called on the server after a reload
        saves the new data into 'methods.py' and re-imports
        :return: response to send back
        """
        Protocol.send(sock, 'ready for reload')
        data = Protocol.receive_bin(sock)
        Methods.save_to_file(METHODS_PATH, data)

        importlib.reload(sys.modules[__name__])
        return 'module reloaded'

    @staticmethod
    def save_to_file(path, content):
        """
        saves binary data onto a file
        :param path: path to the file
        :param content: data to write
        :return: response to send back
        """
        with open(path, 'wb') as file:
            file.write(content)
        return f'saved to {path}'
