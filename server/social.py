from auth import DBManager


class Social(DBManager):
    # TODO: finish add_connection, write remove_connection function
    def add_connection(self, befriender: int, befriended: int):
        """
        add the befriended id to the befriender's connections field
        :param befriender: id of the befriender
        :param befriended: id of the befriended
        """
        # TODO: get existing data, add befriended, update db
