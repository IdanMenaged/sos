from auth import DBManager


def main():
    s = Social()
    s.add_connection(1, 2)


class Social(DBManager):
    # TODO: finish add_connection, write remove_connection function
    def add_connection(self, befriender: int, befriended: int):
        """
        add the befriended id to the befriender's connections field
        :param befriender: id of the befriender
        :param befriended: id of the befriended
        """
        # TODO: get existing data, add befriended, update db
        q = f'SELECT connections FROM users WHERE id = ?'
        res = self.exec(q, befriender)
        connections = res[0][0]
        if connections is None:
            connections = ''

        connections += f', {befriended}'

        q = ''


if __name__ == '__main__':
    main()
