# Flask imports
from flask import Flask
from flask import request
import json
import time
import requests

# Module Imports
import modules.editDBdata as editDBdata
import modules.pathCalc as pathCalc

app = Flask(__name__)

@app.route('/sendLoc', methods=['GET', 'POST'])
def process1():
    '''
    /sendLoc?user_loc=xxxx - to be sent from client side
    '''

    start_loc = ['cara', '']

    # Process the POST data - retrieve the user's instructions
    locations = request.args.get('user_loc')

    start_dest = locations.split(" to ")
    start_loc = start_dest[0]
    dest_loc = start_dest[1]
    
    if(locations == None):
        return "Invalid request format"
    
    else:
        # Create database connection
        conn = editDBdata.createConn(databaseFile)

        if conn is not None:
            with conn:
                clientExists = editDBdata.check_client_exists(conn, client_id)
                if(clientExists[0] == True):
                    if(clientExists[3] == 'NULL'):
                        status = 'IN_QUEUE'
                    else:
                        status = clientExists[3]
                else:
                    editDBdata.create_client(conn, (client_id, 'IN_QUEUE', 'NULL'))
                    status = "IN_QUEUE"
        else:
            return "Error! Cannot create database connection"
        
        return status

def process2():
    '''
    1. Retrieve client databse contents based on status - get all the IN QUEUE clients
    2. Check the VM capacity in server database
    3. Assign VM to the client 
    4. Update client database 
    5. Update server capacity
    '''

    # Create database connection
    conn = editDBdata.createConn(databaseFile)
    clients_in_queue = editDBdata.traverse_clients(conn)
    
    if(len(clients_in_queue) == 0):
        print("No clients in queue")
    else:
        for client in clients_in_queue:
            server_cap_list = editDBdata.enough_server_cap(conn)
            print(server_cap_list)
            updateClient = editDBdata.update_client(conn, (client, "PROCESSING", server_cap_list[0][0], client))
            newServerCap = server_cap_list[0][2] - 1
            updateServerCap = editDBdata.update_server(conn, (server_cap_list[0][0], server_cap_list[0][1], str(newServerCap), server_cap_list[0][0]))
        
        return updateClient[1]

@app.route('/vmCap', methods=['POST'])
def process3():
    ''' 
    1. Recieve POST request from VPS Server - indicating the VM Capacity
    2. Update DB with new capacity
    '''
    
    # Process the VM data
    capData = json.loads(request.data)
    VM_IP = capData["VM_IP"]
    VM_CAP = capData["VM_CAP"]

    conn = editDBdata.createConn(databaseFile)
    if conn is not None:
        with conn:
            serverExists = editDBdata.check_server_exists(conn, VM_IP)
            if(serverExists[0] == True):
                status = editDBdata.update_server(conn, (serverExists[1], serverExists[2], VM_CAP, serverExists[1]))
            else:
                status = serverExists[1]
    else:
        return "Error! Cannot create database connection"
    
    return status

if __name__ == '__main__':
    app.run(debug=True, host="0.0.0.0")