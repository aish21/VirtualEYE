# import asyncio
# import websockets

# # create handler for each connection
 
# async def handler(websocket, path):
 
#     data = await websocket.recv()
 
#     reply = f"Data recieved as:  {data}!"
 
#     await websocket.send(reply)
 
# start_server = websockets.serve(handler, "localhost", 8080)
 
# asyncio.get_event_loop().run_until_complete(start_server)
# asyncio.get_event_loop().run_forever()

import socket 
from server_thread import ServerThread

def main():

    #get local ip address of server
    hostname = socket.getfqdn()
    print(hostname)
    local_ip = socket.gethostbyname(hostname)
    print(local_ip)

    #default port for socket
    port = 9090

    #start the server thread
    image_server = ServerThread(local_ip, port)
    image_server.start()


if __name__ == "__main__":
    main()