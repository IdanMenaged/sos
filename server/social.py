from calendar import firstweekday

from auth import DBManager


def main():
    s = Social()
    print(s.add_connection(1, 2))
    s.remove_connection(1, 2)


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
                connections += f',{befriended}'  # TODO: handle duplicate ids

            q = 'UPDATE users SET connections = ? WHERE id = ?'
            self.exec(q, connections, befriender)
        except Exception as e:
            print(e)
            return False
        return True

    def remove_connection(self, remover: int, removed: int):
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


if __name__ == '__main__':
    main()
