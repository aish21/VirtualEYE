'''
Test Script for Djikstra's Algorithm
'''

import heapq
import time

# Create an empty dictionary to store the edges and their weights
graph = {}

# Add edges to the graph with weights
graph["cara"] = {
  "student lounge": {"weight": 5, "direction": "left/E"},
  "software lab 1": {"weight": 10, "direction": "straight/E"},
}

graph["student lounge"] = {
  "cara": {"weight": 5, "direction": "right/N"},
  "software lab 1": {"weight": 3, "direction": "straight/E"},
}

graph["software lab 1"] = {
  "student lounge": {"weight": 3, "direction": "straight/W"},
  "hardware lab 1": {"weight": 7, "direction": "left/N"},
  "hardware lab 2": {"weight": 8, "direction": "straight/E"},
}

graph["hardware lab 1"] = {
  "software lab 1": {"weight": 7, "direction": "right/W"},
  "hardware lab 2": {"weight": 2, "direction": "left/E"},
}

graph["hardware lab 2"] = {
  "hardware lab 1": {"weight": 2, "direction": "right/N"},
  "software lab 2": {"weight": 4, "direction": "left/N"},
  "software lab 1": {"weight": 8, "direction": "straight/W"},
  "hardware projects lab": {"weight": 6, "direction": "straight/E"},
}

graph["software lab 2"] = {
  "hardware lab 2": {"weight": 4, "direction": "right/W"},
  "hardware projects lab": {"weight": 2, "direction": "left/E"},
}

graph["hardware projects lab"] = {
  "software lab 2": {"weight": 2, "direction": "right/N"},
  "hardware lab 2": {"weight": 6, "direction": "straight/W"},
}

def shortest_path(graph, start, end):
  # Keep track of the distances from the start node
  distances = {node: float("inf") for node in graph}
  distances[start] = 0
  # Keep track of the previous node for each node
  previous = {node: None for node in graph}
  # Create a priority queue to keep track of the nodes to visit
  queue = [(0, start)]
  while queue:
    # Get the node with the shortest distance from the start
    (distance, node) = heapq.heappop(queue)
    # If the node is the end node, return the path
    if node == end:
      path = []
      while node is not None:
        path.append(node)
        node = previous[node]
      return path[::-1]
    # Update the distances for all the neighbors of the node
    for neighbor in graph[node]:
      new_distance = distance + graph[node][neighbor]["weight"]
      if new_distance < distances[neighbor]:
        distances[neighbor] = new_distance
        previous[neighbor] = node
        heapq.heappush(queue, (new_distance, neighbor))

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
