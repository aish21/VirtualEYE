'''
Test Script for DFS Algorithm
'''
import time

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

def shortest_path(graph, start, end):
  # Keep track of visited nodes
  visited = []
  # Keep track of nodes to be checked using a stack
  stack = [[start]]
  # Loop through the stack
  while stack:
    # Get the last path in the stack
    path = stack.pop()
    # Get the last node in the path
    node = path[-1]
    # If the node has not been visited
    if node not in visited:
      # Mark it as visited
      visited.append(node)
      # Add all the neighboring nodes to the stack
      for neighbor in reversed(graph[node]):
        new_path = list(path)
        new_path.append(neighbor)
        stack.append(new_path)
        # If the neighbor is the end node, return the path
        if neighbor == end:
          return new_path
  # If no path was found, return None
  return None

for start in graph:
  for end in graph:
    if start != end:
        
      start_time = time.perf_counter()
      path = shortest_path(graph, start, end)
      end_time = time.perf_counter()
      time_taken = end_time - start_time

      print(f"Shortest path from {start} to {end}: {path}")
      print(f"Time taken to find the shortest path: {time_taken} seconds")
      print(" ")