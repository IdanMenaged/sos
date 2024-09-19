PORT = 4000
EXIT_CODES = {'quit'}
BIN_METHODS = {'send_file'}  # methods using binary data and thus using the special protocol for binary
SAVE_FILE_TO = 'c:\\test_folder\\client'  # dir to save files to
METHODS_PATH = 'methods.py'
PARAM_COUNTS = {
    'take_screenshot': 0,
    'send_file': 1,
    'dir': 1,
    'delete': 1,
    'copy': 2,
    'execute': 1,
    'echo': 1,
    'quit': 0,
    'exit': 0,
    'reload': 0,
    'history': 0
}
