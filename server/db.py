"""
Idan Menaged
manages database interaction
"""

import sqlite3

DB_NAME = 'db.db'


class DBManager:
    # todo: construct db and test
    """
    manages interaction with the database
    """
    def __init__(self):
        """
        create a database manager
        """
        self.conn = sqlite3.connect(DB_NAME)
        self.cursor = self.conn.cursor()

    def exec(self, q: str):
        """
        execute a query
        :param q: query
        :return: db response
        """
        if not DBManager.valid_query(q):
            raise Exception(f'query "{q}" is invalid. risk of sql injection')

        self.cursor.execute(q)
        return self.cursor.fetchall()

    @staticmethod
    def valid_query(q: str):
        """
        protects from sql injection
        :param q: query
        :return: valid or invalid
        """
        return "'" not in q and '"' not in q
