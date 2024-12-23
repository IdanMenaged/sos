from auth import DBManager


def main():
    s = Social()
    print(s.add_connection(1, 2))


class Social(DBManager):
    # TODO: write remove_connection function
    def add_connection(self, befriender: int, befriended: int):
        """
        add the befriended id to the befriender's connections field
        :param befriender: id of the befriender
        :param befriended: id of the befriended
        :return true on success, false on failure
        """
        q = f'SELECT connections FROM users WHERE id = ?'
        res = self.exec(q, befriender)
        connections = res[0][0]
        if connections is None:
            connections = befriended
        else:
            connections += f',{befriended}'  # TODO: handle duplicate ids

        q = 'UPDATE users SET connections = ? WHERE id = ?'
        self.exec(q, connections, befriender)


if __name__ == '__main__':
    main()
