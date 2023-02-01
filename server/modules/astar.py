'''
Test Script for A Star Algorithm
'''
import heapq
import time

# Create an empty dictionary to store the edges and their labels
graph = {}

# Add edges to the graph with custom labels
graph["cara"] = {"student lounge": (1, "left/E")}
graph["student lounge"] = {"cara": (1, "right/N"), "software lab 1": (1, "straight/E")}
graph["software lab 1"] = {"student lounge": (1, "straight/W"), "hardware lab 1": (1, "left/N"), "hardware lab 2": (1, "straight/E")}
graph["hardware lab 1"] = {"software lab 1": (1, "right/W"), "hardware lab 2": (1, "left/E")}
graph["hardware lab 2"] = {"hardware lab 1": (1, "right/N"), "software lab 2": (1, "left/N"), "software lab 1": (1, "straight/W"), "hardware projects lab": (1, "straight/E")}
graph["software lab 2"] = {"hardware lab 2": (1, "right/W"), "hardware projects lab": (1, "left/E")}
graph["hardware projects lab"] = {"software lab 2": (1, "right/N"), "hardware lab 2": (1, "straight/W")}

# Define the heuristic function
def heuristic(node, end):
  # Use Manhattan distance as heuristic
  # Replace with appropriate heuristic as required
  node_val = int(node[-1]) if node[-1].isdigit() else 0
  end_val = int(end[-1]) if end[-1].isdigit() else 0
  return abs(node_val - end_val)

def a_star(graph, start, end):
  # Keep track of nodes to be checked
  heap = [(0, [start])]
  # Keep track of visited nodes
  visited = []
  # Loop until heap is empty
  while heap:
    (cost, path) = heapq.heappop(heap)
    node = path[-1]
    if node == end:
      return path
    if node not in visited:
      visited.append(node)
      for neighbor in graph[node]:
        cost_to_neighbor = graph[node][neighbor][0]
        new_cost = cost + cost_to_neighbor
        new_path = path + [neighbor]
        heapq.heappush(heap, (new_cost + heuristic(neighbor, end), new_path))
  return None

for start in graph:
  for end in graph:
    if start != end:
        
      start_time = time.perf_counter()
      path = a_star(graph, start, end)
      end_time = time.perf_counter()
      time_taken = end_time - start_time

      print(f"Shortest path from {start} to {end}: {path}")
      print(f"Time taken to find the shortest path: {time_taken} seconds")
      print(" ")
