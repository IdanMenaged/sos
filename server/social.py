from calendar import firstweekday

from auth import DBManager


def main():
    s = Social()
    print(s.add_connection("idan", "ayelet"))
    #print(s.remove_connection(1, 2))
    print(s.get_connections("idan"))


class Social(DBManager):
    # todo: remove all id usage
    def add_connection(self, befriender: str, befriended: str):
        """
        add the befriended name to the befriender's connections field
        :param befriender: name of the befriender
        :param befriended: name of the befriended
        :return true on success, false on failure
        """
        try:
            q = f'SELECT connections FROM users WHERE name = ?'
            res = self.exec(q, befriender)
            connections = res[0][0]
            if connections is None:
                connections = befriended
            else:
                connections = set(connections.split(','))
                connections.add(str(befriended))
                connections = ','.join(connections)

            q = 'UPDATE users SET connections = ? WHERE name = ?'
            self.exec(q, connections, befriender)
        except Exception as e:
            print(e)
            return 'False'
        return 'True'

    def remove_connection(self, remover: int, removed: int):
        """
        remove a connection from a user
        :param remover: id of the removing user
        :param removed: id of the removed user
        :return: True if successful, False if not.
        """
        try:
            q = 'SELECT connections FROM users WHERE id = ?'
            res = self.exec(q, remover)

            friends: list = res[0][0].split(',')
            friends.remove(str(removed))

            connections = ','.join(friends)

            # edge case
            if connections == '':
                connections = None

            q = 'UPDATE users SET connections = ? WHERE id = ?'
            self.exec(q, connections, remover)
        except Exception as e:
            print(e)
            return 'False'
        return 'True'

    def get_connections(self, name: str):
        """
        get all connections
        :param name: username
        :return: comma separated list of names
        """
        q = 'SELECT connections FROM users WHERE name = ?'
        res = self.exec(q, name)
        return res[0][0] if res[0][0] is not None else ""


if __name__ == '__main__':
    main()
