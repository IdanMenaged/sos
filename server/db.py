"""
Idan Menaged
manages database interaction
"""

import sqlite3

DB_NAME = 'db.db'

def main():
    """
    create a db, format it, and add data
    :return: None
    """
    db = DBManager()
    db.format_db()
    print(db.exec("INSERT INTO Users (name, password) VALUES (?, ?)",
            'idan', 'password'))
    print(db.exec("INSERT INTO Users (name, password) VALUES (?, ?)",
            'ayelet', '1234'))
    print('done')


class DBManager:
    """
    manages interaction with the database
    """
    def __init__(self):
        """
        create a database manager
        """
        self.conn = sqlite3.connect(DB_NAME, check_same_thread=False)
        self.cursor = self.conn.cursor()

    def format_db(self):
        """
        script to format the table and scheme. RUN ONLY WHEN MAKING A FRESH
        DB! CAN DELETE EXISTING DATA!
        :return: None
        """
        query = """
            CREATE TABLE IF NOT EXISTS Users (
                name TEXT PRIMARY KEY,
                password TEXT NOT NULL,
                connections TEXT
            )
        """
        self.exec(query)

    def exec(self, q: str, *params):
        """
        execute a query
        :param q: query
        :param params: for a parametized query (protects from sql injection).
        example: q = INSERT INTO Users (name, password) VALUES (?, ?),
        params = (name1, password1)
        :return: db response
        """
        if not DBManager.valid_query(q):
            raise Exception(f'query "{q}" is invalid. risk of sql injection')

        # make it possible to insert null values
        params = tuple(None if param == 'NULL' else param for param in params)

        self.cursor.execute(q, params)
        self.conn.commit()
        return self.cursor.fetchall()

    @staticmethod
    def valid_query(q: str):
        """
        protects from sql injection
        :param q: query
        :return: valid or invalid
        """
        return "'" not in q and '"' not in q


if __name__ == '__main__':
    main()
