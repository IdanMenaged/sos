import base64
import hashlib

# Use PyCryptodomeX
from Cryptodome.Random import get_random_bytes
from Cryptodome.Cipher import AES


class AESCipher(object):

    @staticmethod
    def encrypt(key, raw):
        raw = AESCipher._pad(raw)
        iv = get_random_bytes(AES.block_size)
        cipher = AES.new(key, AES.MODE_CBC, iv)
        b = base64.b64encode(iv + cipher.encrypt(raw))
        return b

    @staticmethod
    def decrypt(key, enc):
        enc = base64.b64decode(enc + b'==')
        iv = enc[:AES.block_size]
        cipher = AES.new(key, AES.MODE_CBC, iv)
        return AESCipher._unpad(cipher.decrypt(enc[AES.block_size:]))

    @staticmethod
    def _pad(s):
        bs = AES.block_size
        padding_length = bs - len(s) % bs
        return s + bytes([padding_length] * padding_length)

    @staticmethod
    def _unpad(s):
        return s[:-s[-1]]

    @staticmethod
    def generate_key():
        key = get_random_bytes(32)
        return hashlib.sha256(key).digest()


def main():
    key = AESCipher.generate_key()
    enc = AESCipher.encrypt(key, ("aa" * 100).encode())
    dec = AESCipher.decrypt(key, enc)
    print(enc, dec)


if __name__ == "__main__":
    main()
