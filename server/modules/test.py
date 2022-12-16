import requests
from flask import Flask
from flask import request
import json

app = Flask(__name__)
# @app.route('/vmCap', methods=['POST'])
# def process3():
#     ''' 
#     Recieve POST request from VPS Server - indicating the VM Capacity 
#     '''
    
#     # Process the VM data
#     capData = request.data
#     print(capData)
#     jsonData = json.loads(capData)
#     print(jsonData["VM_IP"])
#     return capData

# x = 5
# print('foo'+str(x))

# x = "test124"
# y = "http://"+x+"98"
# print(y)

# numServers = 2

# def testFN():
#     x = numServers + 1
#     print(x)

# testFN()
# from random import choice
# exclusionList = [1,2]

# def fn():
#     for i in range(25):
#         result = choice([i for i in range(3,100) if i not in exclusionList])
#         print(result)
#         exclusionList.append(result)
#         print(exclusionList)

# fn()

# @app.route('/clientConnected', methods=['GET', 'POST'])
# def query_example():
#     # if key doesn't exist, returns None
#     clientID = request.args.get('client_id')

#     return clientID

# if __name__ == '__main__':
#     app.run(debug=True)