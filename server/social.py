from auth import DBManager


def main():
    s = Social()
    print(s.get_connections("idan"))


class Social(DBManager):
    def get_connections(self, name: str):
        """
        get all connections
        :param name: username
        :return: comma separated list of names
        """
        q = 'SELECT connections FROM users WHERE name = ?'
        res = self.exec(q, name)
        return res[0][0] if res[0][0] is not None else ""

    def update_connections(self, name: str, new_connections: str):
        """
        overwrite the connections field
        :param name: user's name
        :param new_connections: comma separated list of connections
        :return:
        """
        q = 'UPDATE users SET connections = ? WHERE name = ?'
        self.exec(q, new_connections, name)


if __name__ == '__main__':
    main()
