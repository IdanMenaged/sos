import bcrypt

from db import DBManager

SALT_FILE = 'salt.txt'


def main():
    a = Auth()
    a.login('fake', 'password')  # fail
    a.login('idan', 'password')  # success
    a.login('ayelet', 'wrong')  # fail

    # a.signup("newuser", "newpassword")
    # a.login("newuser", "newpassword")


class Auth(DBManager):
    def __init__(self):
        super().__init__()
        with open(SALT_FILE, 'r') as salt_file:
            self.salt = salt_file.read().encode()

    def login(self, username: str, password: str):
        """
        login a user if password is correct
        :param username: account name
        :param password: password for the account
        :return: 'success' on a successful login, 'failure' otherwise
        """
        password = self.hash(password.encode())

        query = 'SELECT password FROM users WHERE name = ?;'
        res = self.exec(query, username)
        if not res:
            print('login failed')
            return 'failure'
        correct_password = res[0][0]  # result is a list of tuples

        if password == correct_password:
            print('login successful')
            return 'success'
        print('login failed')
        return 'failure'

    def signup(self, username: str, password: str):
        """
        add user to db
        :param username: name
        :param password: password
        :return: success or failure
        """
        password = self.hash(password.encode())

        try:
            query = 'INSERT INTO users (name, password) VALUES (?, ?)'
            self.exec(query, username, password)
            return "success"
        except:
            return "failure"

    def hash(self, password):
        return bcrypt.hashpw(password, self.salt)


if __name__ == '__main__':
    main()
