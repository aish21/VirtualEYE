''' Python Script to create an SQL database for the load balancing server '''

import sqlite3
from sqlite3 import Error

def create_connection(db_file):

    ''' Create a database connection to an SQLite database ''' 
    
    conn = None
    try:
        conn = sqlite3.connect(db_file)
        print(sqlite3.version)
    except Error as e:
        print(e)
    
    finally:
        if conn:
            conn.close()


if __name__ == '__main__':

    # Change file name to create different databases
    create_connection(r"/home/gv/Desktop/VPS_DEMO/vps-load-balancing-server/db/client_server_data.db")