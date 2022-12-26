# Flask imports
from flask import Flask
from flask import request
import json
import time
import requests

# Module Imports
import modules.pathCalc as pathCalc

# Create an empty dictionary to store the edges and their labels
graph = {}

# Add edges to the graph with custom labels
graph["cara"] = {"lounge": "left"}
graph["lounge"] = {"cara": "right", "sw1": "straight"}
graph["sw1"] = {"lounge": "straight", "hw1": "left", "hw2": "straight"}
graph["hw1"] = {"sw1": "right", "hw2": "left"}
graph["hw2"] = {"hw1": "right", "sw2": "left", "sw1": "straight", "hw_proj": "straight"}
graph["sw2"] = {"hw2": "right", "hw_proj": "left"}
graph["hw_proj"] = {"sw2": "right", "hw2": "straight"}

app = Flask(__name__)

@app.route('/sendLoc', methods=['GET', 'POST'])
def process1():
    '''
    /sendLoc?startLoc=xxxx&destLoc=xxxx - to be sent from client side
    '''

    # Process the POST data - retrieve the user's instructions
    start_loc = request.args.get('startLoc')
    dest_loc = request.args.get('destLoc')
    directions = []

    shortest_path = pathCalc.shortest_path(graph, start_loc, dest_loc)
    
    for i in range(len(shortest_path)-1):
        directions.append(graph[shortest_path[i]][shortest_path[i+1]])
    
    return (shortest_path, directions)

if __name__ == '__main__':
    app.run(debug=True, host="0.0.0.0")