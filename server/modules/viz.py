import matplotlib.pyplot as plt
import networkx as nx

graph = {}
graph["cara"] = {"lounge": "left/E"}
graph["lounge"] = {"cara": "right/N", "sw1": "straight/E"}
graph["sw1"] = {"lounge": "straight/W", "hw1": "left/N", "hw2": "straight/E"}
graph["hw1"] = {"sw1": "right/W", "hw2": "left/E"}
graph["hw2"] = {"hw1": "right/N", "sw2": "left/N", "sw1": "straight/W", "hw_proj": "straight/E"}
graph["sw2"] = {"hw2": "right/W", "hw_proj": "left/E"}
graph["hw_proj"] = {"sw2": "right/N", "hw2": "straight/W"}

# Create a directed graph object
G = nx.DiGraph()

# Add the nodes and edges to the graph
for node in graph:
    for neighbor in graph[node]:
        G.add_edge(node, neighbor)

# Draw the graph
nx.draw(G, with_labels=True)

# Show the graph
plt.show()
