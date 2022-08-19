import asyncio
import websockets

# create handler for each connection
 
async def handler(websocket, path):

    print('here')
 
    data = await websocket.recv()

    print('here2')
 
    reply = f"Data recieved as:  {data}!"
 
    await websocket.send(reply)
 
start_server = websockets.serve(handler, "localhost", 8080)
 
asyncio.get_event_loop().run_until_complete(start_server)
asyncio.get_event_loop().run_forever()