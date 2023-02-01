import time
import pandas as pd

# Create an empty dictionary to store the edges and their labels
graph = {}

# Add edges to the graph with custom labels
graph["cara"] = {"lounge": "left/E"}
graph["lounge"] = {"cara": "right/N", "sw1": "straight/E"}
graph["sw1"] = {"lounge": "straight/W", "hw1": "left/N", "hw2": "straight/E"}
graph["hw1"] = {"sw1": "right/W", "hw2": "left/E"}
graph["hw2"] = {"hw1": "right/N", "sw2": "left/N", "sw1": "straight/W", "hw_proj": "straight/E"}
graph["sw2"] = {"hw2": "right/W", "hw_proj": "left/E"}
graph["hw_proj"] = {"sw2": "right/N", "hw2": "straight/W"}

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

# Store the data in a list of dictionaries
data = []
for start in graph:
  for end in graph:
    if start != end:
      start_time = time.perf_counter()
      path = shortest_path(graph, start, end)
      end_time = time.perf_counter()
      time_taken = end_time - start_time
      data.append({
        "Start Location to Destination": f"{start} to {end}",
        "Shortest Path Output": path,
        "Time taken": time_taken
      })

# Create a dataframe from the list of dictionaries
df = pd.DataFrame(data)

# Display the data in a table
print(df.to_string(index=False))


# # Find the shortest path between nodes A and G
# shortest_path = shortest_path(graph, "cara", "sw1")

# # Print the shortest path with the edges traversed
# print("Shortest path:", shortest_path)
# print("Edges traversed:")
# for i in range(len(shortest_path)-1):
#   print(f"{shortest_path[i]} to {shortest_path[i+1]}: {graph[shortest_path[i]][shortest_path[i+1]]}")
#   directions.append(graph[shortest_path[i]][shortest_path[i+1]])


# bearings = [item.split('/')[1] for item in directions]
# directions = [direction.split('/')[0] for direction in directions]

# print(bearings)
# print(directions)