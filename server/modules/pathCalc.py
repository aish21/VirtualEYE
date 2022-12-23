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
