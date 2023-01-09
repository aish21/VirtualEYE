# Flask imports
from flask import Flask
from flask import request
from flask import jsonify

# Module Imports
import modules.pathCalc as pathCalc

# Create an empty dictionary to store the edges and their labels
graph = {}

# Add edges to the graph with custom labels
graph["cara"] = {"student lounge": "left/E"}
graph["student lounge"] = {"cara": "right/N", "software lab 1": "straight/E"}
graph["software lab 1"] = {"student lounge": "straight/W", "hardware lab 1": "left/N", "hardware lab 2": "straight/E"}
graph["hardware lab 1"] = {"software lab 1": "right/W", "hardware lab 2": "left/E"}
graph["hardware lab 2"] = {"hardware lab 1": "right/N", "software lab 2": "left/N", "software lab 1": "straight/W", "hardware projects lab": "straight/E"}
graph["software lab 2"] = {"hardware lab 2": "right/W", "hardware projects lab": "left/E"}
graph["hardware projects lab"] = {"software lab 2": "right/N", "hardware lab 2": "straight/W"}

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
    bearings = []

    shortest_path = pathCalc.shortest_path(graph, start_loc, dest_loc)
    
    for i in range(len(shortest_path)-1):
        directions.append(graph[shortest_path[i]][shortest_path[i+1]])
    
    bearings = [item.split('/')[1] for item in directions]
    directions = [direction.split('/')[0] for direction in directions]
    shortest_path.pop(0)

    print(shortest_path)
    print(directions)
    print(bearings)
    
    return jsonify({"path": shortest_path, "directions": directions, "bearings": bearings}) 

if __name__ == '__main__':
    app.run(debug=True, host="0.0.0.0")