# Flask imports
# from asyncio.windows_events import NULL
from telnetlib import STATUS
from flask import Flask
from flask import request
from apscheduler.schedulers.background import BackgroundScheduler
import json
import time
import requests

# Module Imports
import modules.editDBdata as editDBdata
import modules.pathCalc as pathCalc

# Globals
databaseFile = r'db/client_server_data.db' 
vmCapList = []

app = Flask(__name__)

@app.route('/clientConnected', methods=['GET', 'POST'])
def process1():
    '''
    1. Check if client exists in the client database
    2. Add Client to database
    3. If client exists + status = PROCESSING, return status + assigned VM IP (client needs to continuously query)

    /clientConnected?client_id=xxxx - to be sent client side
    '''

    # Process the POST data - retrieve the client ID
    client_id = request.args.get('client_id')
    
    if(client_id == None):
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

@app.route('/clientDisconnected', methods=['GET', 'POST'])
def process4():
    '''
    1. Recieve POST request from client
    2. Change status of client in the DB and delete record

    /clientDisconnected?client_id=xxxx - to be sent client side
    '''

    # Process the POST data - retrieve the client ID
    client_id = request.args.get('client_id')
    
    if(client_id == None):
        return "Invalid request format"
    
    else:
        # Create database connection
        conn = editDBdata.createConn(databaseFile)

        if conn is not None:
            with conn:
                clientExists = editDBdata.check_client_exists(conn, client_id)
                if(clientExists[0] == True):
                    stat = editDBdata.update_client(conn, (clientExists[1], 'DISCONNECTED', clientExists[3], clientExists[1]))
                    if(stat[0] == False):
                        return stat[1]
                    else:
                        status = editDBdata.delete_client(conn, client_id)
                else:
                    status = clientExists[1]
        else:
            return "Error! Cannot create database connection"
        
        return status

def process5():
    '''
    1. Function is called every 5-10 seconds: this value can be tweaked
    2. Function checks the server table for VM capacities 
    3. Create new VMs based on capacities 
    4. Update the table with new VMS and capacities
    '''

    # Create database connection
    conn = editDBdata.createConn(databaseFile)
    list_of_low_capacity = editDBdata.check_server_capacity(conn)
    startVM2 = editDBdata.check_vm1_cap(conn)

    if(startVM2):
        startSecondVM()
        print('Second VM has started')
    
    if(len(list_of_low_capacity) == 0):
        print('All servers are ready to accepts clients')

    else:
        for vm in list_of_low_capacity:
            print(vmCapList)
            if(vm not in vmCapList):
                print('here')
                vmCapList.append(vm)
                newVM = pathCalc.createVirtualMachine()
                time.sleep(30)
                status = editDBdata.create_server(conn, newVM)
        
        return status
    

def startSecondVM():
    authority_url = 'https://login.microsoftonline.com/'+TENANT_ID
    context = adal.AuthenticationContext(authority_url)
    token = context.acquire_token_with_client_credentials(
        resource='https://management.azure.com/',
        client_id=CLIENT,
        client_secret=KEY
    )

    print(token["accessToken"])
    authStr = "bearer " + token["accessToken"]

    headers  = {"Authorization": authStr,
                "Content-Type": "application/json"}

    resp = requests.post("https://management.azure.com/subscriptions/9568b8d1-2087-47d1-b8ed-0baa6969f187/resourceGroups/VPS-Server/providers/Microsoft.Compute/virtualMachines/VPShost2/start?api-version=2022-03-01", headers= headers)
    print(str(resp.status_code))

sched = BackgroundScheduler(daemon=True)
sched.add_job(process5,'interval',seconds=60)
sched.start()

sched2 = BackgroundScheduler(daemon=True)
sched2.add_job(process2,'interval',seconds=20)
sched2.start()

if __name__ == '__main__':
    app.run(debug=True, host="0.0.0.0")