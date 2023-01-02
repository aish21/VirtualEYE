# Flask imports
from flask import Flask
from flask import request
from flask import jsonify
import time
import requests

# Module Imports
import modules.pathCalc as pathCalc

# Create an empty dictionary to store the edges and their labels
graph = {}

# Add edges to the graph with custom labels
graph["cara"] = {"student lounge": "left"}
graph["student lounge"] = {"cara": "right", "software lab 1": "straight"}
graph["software lab 1"] = {"student lounge": "straight", "hardware lab 1": "left", "hardware lab 2": "straight"}
graph["hardware lab 1"] = {"software lab 1": "right", "hardware lab 2": "left"}
graph["hardware lab 2"] = {"hardware lab 1": "right", "software lab 2": "left", "software lab 1": "straight", "hardware projects lab": "straight"}
graph["software lab 2"] = {"hardware lab 2": "right", "hardware projects lab": "left"}
graph["hardware projects lab"] = {"software lab 2": "right", "hardware lab 2": "straight"}

app = Flask(__name__)

@app.route('/sendLoc', methods=['GET', 'POST'])
def process1():
    '''
    /sendLoc?startLoc=xxxx&destLoc=xxxx - to be sent from client side
    '''

    # Process the POST data - retrieve the user's instructions
    start_loc = request.args.get('startLoc')
    dest_loc = request.args.get('destLoc')
    print(start_loc)
    print(dest_loc)
    directions = []

    shortest_path = pathCalc.shortest_path(graph, start_loc, dest_loc)
    
    for i in range(len(shortest_path)-1):
        directions.append(graph[shortest_path[i]][shortest_path[i+1]])
    
    return jsonify({"path": shortest_path, "directions": directions}) 

if __name__ == '__main__':
    app.run(debug=True, host="0.0.0.0")