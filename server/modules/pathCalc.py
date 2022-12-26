'''
1 - E4:7E:DB:B2:0D:3C
2 - E3:2D:87:49:E5:BF
3 - CF:BD:6D:B7:8E:7D
4 - D8:3F:BB:F5:EF:5E
5 - F1:C6:5F:C8:71:9D
6 - D6:65:D2:8F:C5:8F
7 - E9:4E:48:02:C6:84
8 - CF:99:D4:6A:7A:75

import ast

list_string = "[1, 2, 3, 4, 5]"

# Use ast.literal_eval() to parse the string and return the corresponding list
my_list = ast.literal_eval(list_string)

print(my_list)  # Output: [1, 2, 3, 4, 5]

'''

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

# # Find the shortest path between nodes A and G
# shortest_path = shortest_path(graph, "cara", "hw1")

# # Print the shortest path with the edges traversed
# print("Shortest path:", shortest_path)
# print("Edges traversed:")
# for i in range(len(shortest_path)-1):
#   print(f"{shortest_path[i]} to {shortest_path[i+1]}: {graph[shortest_path[i]][shortest_path[i+1]]}")
