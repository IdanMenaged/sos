from calendar import firstweekday

from auth import DBManager


def main():
    s = Social()
    print(s.add_connection(1, 2))
    print(s.remove_connection(1, 2))
    print(s.find_user_id("idan"))


class Social(DBManager):
    def add_connection(self, befriender: int, befriended: int):
        """
        add the befriended id to the befriender's connections field
        :param befriender: id of the befriender
        :param befriended: id of the befriended
        :return true on success, false on failure
        """
        try:
            q = f'SELECT connections FROM users WHERE id = ?'
            res = self.exec(q, befriender)
            connections = res[0][0]
            if connections is None:
                connections = befriended
            else:
                connections = set(connections.split(','))
                connections.add(str(befriended))
                connections = ','.join(connections)

            q = 'UPDATE users SET connections = ? WHERE id = ?'
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

    def find_user_id(self, name: str):
        """
        search user id using name
        :param name: searched user's name
        :return: search results
        """
        q = 'SELECT id FROM users WHERE name = ?'
        res = self.exec(q, name)

        # format res more neatly
        out = []
        for t in res:
            out.append(t[0])

        return str(out)


if __name__ == '__main__':
    main()
