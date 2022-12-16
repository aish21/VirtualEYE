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

def create_client(conn, client):
    '''
    Create a new client in the Client Table

    :param conn:
    :param client:
    :return: client id
    
    '''
    sql = ''' INSERT INTO clients(client_id,status,VM_IP)
              VALUES(?,?,?) '''
    cur = conn.cursor()
    cur.execute(sql, client)
    conn.commit()
    return cur.lastrowid

def create_server(conn, server):
    '''
    Create a new server in the Server Table

    :param conn:
    :param server:
    :return: vm_ip
    
    '''
    sql = ''' INSERT INTO servers(VM_IP,VM_NAME,VM_Capacity)
              VALUES(?,?,?) '''
    cur = conn.cursor()
    cur.execute(sql, server)
    conn.commit()
    return True

def update_client(conn, client):
    '''
    Update client_id, status, and vm_ip of a client

    :param conn:
    :param client:
    :return: client id
    
    '''
    try:
        sql = ''' UPDATE clients
                SET client_id = ? ,
                    status = ? ,
                    VM_IP = ?
                WHERE client_id = ?'''
        cur = conn.cursor()
        cur.execute(sql, client)
        conn.commit()
        return (True, "Client successfully updated")
    except:
        return (False, "Client could not be updated at this time")

def update_server(conn, server):
    '''
    Update vm_name, vm_ip, and vm_capacity of a server

    :param conn:
    :param server:
    :return: vm_ip
    
    '''
    try:
        sql = ''' UPDATE servers
                SET VM_IP = ? ,
                    VM_NAME = ? ,
                    VM_Capacity = ?
                WHERE VM_IP = ?'''
        cur = conn.cursor()
        cur.execute(sql, server)
        conn.commit()
        return "Server successfully updated"
    except:
        return "Server could not be updated at this time"
    

def check_client_exists(conn, client_id):
    """
    Query all rows in the clients table

    :param conn: the connection object
    :return:
    """
    cur = conn.cursor()
    cur.execute("SELECT * FROM clients") 

    rows = cur.fetchall()
    
    for row in rows:
        if(client_id == row[0]):
            return (True, row[0], row[1], row[2])
    return (False, "Client does not exist")

def check_server_exists(conn, vm_ip):
    """
    Query all rows in the server table

    :param conn: the connection object
    :return: status
    """
    cur = conn.cursor()
    cur.execute("SELECT * FROM servers")

    rows = cur.fetchall()
    
    for row in rows:
        if(vm_ip == row[0]):
            return (True, row[0], row[1], row[2])
    return (False, "Server does not exist")

def delete_client(conn, client_id):
    """
    Delete a client by client_id
    :param conn:  Connection to the SQLite database
    :param id: id of the client
    :return: status
    """
    try:
        sql = 'DELETE FROM clients WHERE client_id=?'
        cur = conn.cursor()
        cur.execute(sql, (client_id,))
        conn.commit()
        return "Client successfully deleted"
    except:
        return "Client could not be deleted at this time"

def check_server_capacity(conn):
    """
    Query all rows in the server table

    :param conn: the connection object
    :return: status
    """
    lessCapacity = []
    cur = conn.cursor()
    cur.execute("SELECT * FROM servers")
    notEnough = False

    rows = cur.fetchall()
    
    for row in rows:
        if(int(row[2]) < 3):
            lessCapacity.append(row[0])
            notEnough = True
    if(notEnough):
        return lessCapacity
    else:
        return []

def enough_server_cap(conn):
    """
    Query all rows in the server table

    :param conn: the connection object
    :return: servers with enough capacity
    """

    server_cap_list = []
    cur = conn.cursor()
    cur.execute("SELECT * FROM servers")

    rows = cur.fetchall()

    for row in rows:
        if(int(row[2]) >= 10):
            server_cap_list.append([row[0], row[1], int(row[2])])
            break
    
    return server_cap_list

def traverse_clients(conn):
    """
    Query all rows in the clients table

    :param conn: the connection object
    :return: client list
    """
    client_list = []
    cur = conn.cursor()
    cur.execute("SELECT * FROM clients WHERE status='IN_QUEUE'")

    rows = cur.fetchall()
    
    for row in rows:
        client_list.append(row[0])
            
    return client_list

def check_vm1_cap(conn):
    '''
    Query server to find Main VM (1) capacity
    :param conn: the Connection object
    :return: Boolean, if VM2 needs to be started
    '''
    cur = conn.cursor()
    cur.execute("SELECT * FROM servers WHERE VM_IP='20.192.9.118'")

    rows = cur.fetchall()

    for row in rows:
        if(int(row[2]) < 10):
            return True
    return False

def main():
    ''' Driver Code '''
    
    databaseFile = r'../db/client_server_data.db'

    clientTable = """ CREATE TABLE IF NOT EXISTS clients (
	                    client_id text PRIMARY KEY,
	                    status text NOT NULL,
	                    VM_IP text
                );"""
    
    serverTable = """ CREATE TABLE IF NOT EXISTS servers (
	                    VM_IP text PRIMARY KEY,
	                    VM_NAME text NOT NULL,
	                    VM_Capacity text
                );"""
    
    # Create database connection
    conn = createConn(databaseFile)

    # Create tables
    if conn is not None:
        createTable(conn, clientTable)
        createTable(conn, serverTable)
    else:
        print('Error! Cannot create the database connection')
    
    # Add entries
    with conn:
        server1 = ('20.192.9.118', 'VPShost', '10')
        server2 = ('20.235.80.246', 'VPShost2', '10')
        server1Stat = create_server(conn, server1)
        server2Stat = create_server(conn, server2)
        # update_client(conn, (2, '2015-01-04', '2015-01-06', 2))
        # check_server_exists(conn)
    
    return (server1Stat, server2Stat)
        
    
if __name__ == '__main__':
    main()

