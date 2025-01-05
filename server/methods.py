"""
Idan Menaged
methods the server can execute. not an exhaustive list.
"""

SCREENSHOT_PATH = 'c:\\test_folder\\server\\screen.jpg'
FILE_PATH = 'c:\\test_folder\\client'


class Methods:
    """
    methods server can execute
    """
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
