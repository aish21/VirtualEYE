''' Python Script to create tables, insert data and modify data in the database '''

import sqlite3
from sqlite3 import Error

def createConn(dbFile):
    '''
    Create a database connection to the SQLite database specified by dbFile

    :param dbFile: Database file created in createDB.py
    :return: Connection object or None
    
    '''
    
    conn = None
    try:
        conn = sqlite3.connect(dbFile)
        return conn
    except Error as e:
        print(e)

    return conn

def createTable(conn, create_table_sql):
    ''' 
    Create a table from the create_table_sql statement

    :param conn: Connection object
    :param create_table_sql: CREATE TABLE statement
    :return:

    '''
    try:
        c = conn.cursor()
        c.execute(create_table_sql)
    except Error as e:
        print(e)

def create_path(conn, path):
    '''
    Create a new path in the table

    :param conn:
    :param path:
    :return: path_id
    
    '''
    sql = ''' INSERT INTO paths(user_path,node_list,dir_list)
              VALUES(?,?,?) '''
    cur = conn.cursor()
    cur.execute(sql, path)
    conn.commit()
    return True

def retrieve_path(conn, user_path):
    """
    Query all rows in the table

    :param conn: the connection object
    :return: path
    """
    cur = conn.cursor()
    cur.execute("SELECT * FROM paths")

    rows = cur.fetchall()
    
    for row in rows:
        if(user_path == row[0]):
            return (True, row[0], row[1], row[2])
    return (False, "Path does not exist")

def main():
    ''' Driver Code '''
    
    databaseFile = r'../db/loc_path_data.db'

    pathTable = """ CREATE TABLE IF NOT EXISTS paths (
	                    user_path text PRIMARY KEY,
	                    node_list text NOT NULL,
	                    dir_list text
                );"""
    
    # Create database connection
    conn = createConn(databaseFile)

    # Create tables
    if conn is not None:
        createTable(conn, pathTable)
    else:
        print('Error! Cannot create the database connection')
    
    # Add entries
    with conn:
        path1 = ('cara->lounge', 
                '[("cara", "E4:7E:DB:B2:0D:3C"), ("lounge", "E3:2D:87:49:E5:BF")]', 
                '["left", "FINISH"]')
        
        path2 = ('cara->hw1', 
                '[("cara", "E4:7E:DB:B2:0D:3C"), ("lounge", "E3:2D:87:49:E5:BF"), ("sw1", "CF:BD:6D:B7:8E:7D"), ("washroom", "D8:3F:BB:F5:EF:5E"), ("hw1", "F1:C6:5F:C8:71:9D")]', 
                '["left", "straight", "straight", "left", "FINISH"]')
        
        path3 = ('cara->hw2', 
                '[("cara", "E4:7E:DB:B2:0D:3C"), ("lounge", "E3:2D:87:49:E5:BF"), ("sw1", "CF:BD:6D:B7:8E:7D"), ("washroom", "D8:3F:BB:F5:EF:5E"), ("hw2", "F1:C6:5F:C8:71:9D")]', 
                '["left", "straight", "straight", "straight", "FINISH"]')
        
        path4 = ('cara->sw1', 
                '[("cara", "E4:7E:DB:B2:0D:3C"), ("lounge", "E3:2D:87:49:E5:BF"), ("sw1", "CF:BD:6D:B7:8E:7D")]', 
                '["left", "straight", "FINISH"]')


        path1Stat = create_path(conn, path1)
    
    return (True)
        
    
if __name__ == '__main__':
    main()

