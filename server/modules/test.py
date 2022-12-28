import jsonify

# Create an empty dictionary to store the edges and their labels
graph = {}
directions = []

# Add edges to the graph with custom labels
graph["cara"] = {"lounge": "left"}
graph["lounge"] = {"cara": "right", "sw1": "straight"}
graph["sw1"] = {"lounge": "straight", "hw1": "left", "hw2": "straight"}
graph["hw1"] = {"sw1": "right", "hw2": "left"}
graph["hw2"] = {"hw1": "right", "sw2": "left", "sw1": "straight", "hw_proj": "straight"}
graph["sw2"] = {"hw2": "right", "hw_proj": "left"}
graph["hw_proj"] = {"sw2": "right", "hw2": "straight"}

# Function to find the shortest path between two nodes using breadth-first search
def shortest_path(graph, start, end):
  # Keep track of visited nodes
  visited = []
  # Keep track of nodes to be checked using a queue
  queue = [[start]]
  # Loop through the queue
  while queue:
    # Get the first path in the queue
    path = queue.pop(0)
    # Get the last node in the path
    node = path[-1]
    # If the node has not been visited
    if node not in visited:
      # Mark it as visited
      visited.append(node)
      # Add all the neighboring nodes to the queue
      for neighbor in graph[node]:
        new_path = list(path)
        new_path.append(neighbor)
        queue.append(new_path)
        # If the neighbor is the end node, return the path
        if neighbor == end:
          return new_path
  # If no path was found, return None
  return None

# Find the shortest path between nodes A and G
shortest_path = shortest_path(graph, "cara", "hw_proj")

# Print the shortest path with the edges traversed
print("Shortest path:", shortest_path)
print("Edges traversed:")
for i in range(len(shortest_path)-1):
  print(f"{shortest_path[i]} to {shortest_path[i+1]}: {graph[shortest_path[i]][shortest_path[i+1]]}")
  directions.append(graph[shortest_path[i]][shortest_path[i+1]])

print(jsonify({"path": shortest_path, "dir": directions}))
