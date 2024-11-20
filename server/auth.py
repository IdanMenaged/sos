from db import DBManager


class Auth(DBManager):
    def login(self, username: str, password: str):
        """
        login a user if password is correct
        :param username: account name
        :param password: password for the account
        :return: 'success' on a successful login, 'failure' otherwise
        """
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


if __name__ == '__main__':
    a = Auth()
    a.login('fake', 'password')  # fail
    a.login('idan', 'password')  # success
    a.login('ayelet', 'wrong')  # fail