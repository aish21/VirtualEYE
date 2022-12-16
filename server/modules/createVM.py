'''
Test Script for Azure VM creation during runtime
'''

import adal
import requests
import json
import base64
from random import choice

# Tenant ID for your Azure Subscription
TENANT_ID = '5ef97fc2-9f71-4435-a705-3c9ee58a52f8'

# Your Service Principal App ID
CLIENT = '17b8c581-f781-42d4-8c30-c0dbdd86d166'

# Your Service Principal Password
KEY = 'QsO8Q~eo7vlb_qtB33jETqAs8f1X5oBj6w8-Ocjj'

subscription_id = '9568b8d1-2087-47d1-b8ed-0baa6969f187'
exclusionList = [1,2]

def createVirtualMachine():
    '''
    1. Authorize the use of APIs by getting access token
    2. Update header with authorization string
    3. Create Network Service Group
    4. Create Public IP
    5. Create Network Interface Card
    6. Create Virtual Machine - name based on the number of servers present in the database
    7. Return the IP + Name of the newly created virtual machine
    '''

    # For naming convention
    numServers = choice([i for i in range(3,100) if i not in exclusionList])
    exclusionList.append(numServers)
    print(numServers)

    # Steps 1 and 2: Get access token and update request header 
    authority_url = 'https://login.microsoftonline.com/'+TENANT_ID
    context = adal.AuthenticationContext(authority_url)
    token = context.acquire_token_with_client_credentials(
        resource='https://management.azure.com/',
        client_id=CLIENT,
        client_secret=KEY
    )

    authStr = "bearer " + token["accessToken"]

    headers  = {"Authorization": authStr,
                "Content-Type": "application/json"}

    # Step 3: Create Network Service Group
    # PUT https://management.azure.com/subscriptions/{subscriptionId}/resourceGroups/{resourceGroupName}/providers/Microsoft.Network/networkSecurityGroups/{networkSecurityGroupName}?api-version=2022-01-01

    nsgData = {
        "properties": {
            "securityRules": [
                {
                    "name": "SSH",
                    "properties": {
                        "protocol": "TCP",
                        "sourcePortRange": "*",
                        "destinationPortRange": "22",
                        "sourceAddressPrefix": "*",
                        "destinationAddressPrefix": "*",
                        "access": "Allow",
                        "priority": 300,
                        "direction": "Inbound",
                        "sourcePortRanges": [],
                        "destinationPortRanges": [],
                        "sourceAddressPrefixes": [],
                        "destinationAddressPrefixes": []
                    }
                },
                {
                    "name": "HTTP",
                    "properties": {
                        "protocol": "TCP",
                        "sourcePortRange": "*",
                        "destinationPortRange": "80",
                        "sourceAddressPrefix": "*",
                        "destinationAddressPrefix": "*",
                        "access": "Allow",
                        "priority": 320,
                        "direction": "Inbound",
                        "sourcePortRanges": [],
                        "destinationPortRanges": [],
                        "sourceAddressPrefixes": [],
                        "destinationAddressPrefixes": []
                    }
                },
                {
                    "name": "HTTPS",
                    "properties": {
                        "protocol": "TCP",
                        "sourcePortRange": "*",
                        "destinationPortRange": "443",
                        "sourceAddressPrefix": "*",
                        "destinationAddressPrefix": "*",
                        "access": "Allow",
                        "priority": 340,
                        "direction": "Inbound",
                        "sourcePortRanges": [],
                        "destinationPortRanges": [],
                        "sourceAddressPrefixes": [],
                        "destinationAddressPrefixes": []
                    }
                },
                {
                    "name": "Websocket",
                    "properties": {
                        "protocol": "*",
                        "sourcePortRange": "*",
                        "destinationPortRange": "9090",
                        "sourceAddressPrefix": "*",
                        "destinationAddressPrefix": "*",
                        "access": "Allow",
                        "priority": 200,
                        "direction": "Inbound",
                        "sourcePortRanges": [],
                        "destinationPortRanges": [],
                        "sourceAddressPrefixes": [],
                        "destinationAddressPrefixes": []
                    }
                },
                {
                    "name": "listening_port",
                    "properties": {
                        "protocol": "*",
                        "sourcePortRange": "*",
                        "destinationPortRange": "80",
                        "sourceAddressPrefix": "*",
                        "destinationAddressPrefix": "*",
                        "access": "Allow",
                        "priority": 100,
                        "direction": "Inbound",
                        "sourcePortRanges": [],
                        "destinationPortRanges": [],
                        "sourceAddressPrefixes": [],
                        "destinationAddressPrefixes": []
                    }
                }
            ]
        },
        "location": "centralindia"
    }

    nsgData = json.dumps(nsgData)
    nsgName = 'VPShost_nsg_'+str(numServers)
    nsgData_resp = requests.put("https://management.azure.com/subscriptions/9568b8d1-2087-47d1-b8ed-0baa6969f187/resourceGroups/VPS-Server/providers/Microsoft.Network/networkSecurityGroups/"+nsgName+"?api-version=2022-01-01", headers= headers, data=nsgData)
    print(nsgData_resp.status_code)
    # if(nsgData_resp.status_code != 200 or nsgData_resp.status_code != 201):
    #     return (nsgData_resp.status_code)


    # Step 4:Create Public IP
    # PUT https://management.azure.com/subscriptions/{subscriptionId}/resourceGroups/{resourceGroupName}/providers/Microsoft.Network/publicIPAddresses/{publicIpAddressName}?api-version=2022-01-01

    ipData = {
        "properties": {
            "publicIPAllocationMethod": "Static",
            "idleTimeoutInMinutes": 4,
            "publicIPAddressVersion": "IPv4"
        },
        "sku": {
            "name": "Basic",
            "tier": "Regional"
        },
        "location": "centralindia"
    }

    ipData = json.dumps(ipData)
    ipName = 'VPShost_ip_'+str(numServers)
    ipData_resp = requests.put("https://management.azure.com/subscriptions/9568b8d1-2087-47d1-b8ed-0baa6969f187/resourceGroups/VPS-Server/providers/Microsoft.Network/publicIPAddresses/"+ipName+"?api-version=2022-01-01", headers= headers, data=ipData)
    print(ipData_resp.status_code)
    # if(ipData_resp.status_code != 200 or ipData_resp.status_code != 201):
    #     return (ipData_resp.content)


    # Step 5: Create Network Interface Card
    # PUT https://management.azure.com/subscriptions/{subscriptionId}/resourceGroups/{resourceGroupName}/providers/Microsoft.Network/networkInterfaces/{networkInterfaceName}?api-version=2022-01-01

    nicData = {
        "properties": {
            "enableAcceleratedNetworking": True,
            "enableIPForwarding": False,
            "networkSecurityGroup": {
                "id": "/subscriptions/9568b8d1-2087-47d1-b8ed-0baa6969f187/resourceGroups/VPS-Server/providers/Microsoft.Network/networkSecurityGroups/"+nsgName
            },
            "ipConfigurations": [
                {
                    "name": "ipconfig1",
                    "properties": {
                        "publicIPAddress": {
                            "id": "/subscriptions/9568b8d1-2087-47d1-b8ed-0baa6969f187/resourceGroups/VPS-Server/providers/Microsoft.Network/publicIPAddresses/"+ipName
                        },
                        "subnet": {
                            "id": "/subscriptions/9568b8d1-2087-47d1-b8ed-0baa6969f187/resourceGroups/VPS-Server/providers/Microsoft.Network/virtualNetworks/VPS-Server-vnet/subnets/default"
                        }
                    }
                }
            ]
        },
        "location": "centralindia"
    }

    nicData = json.dumps(nicData)
    nicName = 'VPShost_nic_'+str(numServers)
    nicData_resp = requests.put("https://management.azure.com/subscriptions/9568b8d1-2087-47d1-b8ed-0baa6969f187/resourceGroups/VPS-Server/providers/Microsoft.Network/networkInterfaces/"+nicName+"?api-version=2022-01-01", headers= headers, data=nicData)
    print(nicData_resp.status_code)
    # if(nicData_resp.status_code != 200 or nicData_resp.status_code != 201):
    #     return (nicData_resp.content)


    # Step 6: Create Virtual Machine
    # PUT https://management.azure.com/subscriptions/{subscription-id}/resourceGroups/{resourceGroupName}/providers/Microsoft.Compute/virtualMachines/{vmName}?api-version=2017-12-01

    file = open("./installation.sh", "r")
    a = file.read()
    CUSTOM_DATA = base64.b64encode(a.encode('utf-8')).decode('latin-1')

    vmName = 'VPShost_'+str(numServers)
    vmOSName = 'VPShost'+str(numServers)
    print(vmName)

    vmData = {
        "location": "centralindia",
        "name": vmName,
        "properties": {
            "hardwareProfile": {
                "vmSize": "Standard_D2s_v3"
            },
            "storageProfile": {
                "imageReference": {
                    "sku": "20_04-lts-gen2",
                    "publisher": "canonical",
                    "version": "latest",
                    "offer": "0001-com-ubuntu-server-focal"
                },
                "osDisk": {
                    "osType": "Linux",
                    "caching": "ReadWrite",
                    "managedDisk": {
                        "storageAccountType": "Standard_LRS"
                    },
                    "name": vmName+"-OSDisk",
                    "createOption": "FromImage"
                }
            },
            "osProfile": {
                "computerName": vmOSName,
                "adminUsername": "ip3d",
                "linuxConfiguration": {
                    "disablePasswordAuthentication": True,
                    "ssh": {
                        "publicKeys": [
                            {
                                "path": "/home/ip3d/.ssh/authorized_keys",
                                "keyData": "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABgQDBDkAuFj8Kzh7mVUuFxvcOTLzK3VO8V9BLszOibw4uIuhQ0KEtTrhBb/hI0MJF/1FRrCrMIsjsIsBHuCG5ChVIeYF3BUfeOUql7sBPxjV8CxIKPChgVoOjzt04VmdiVOVBXwUifW3lfaSwTE2F9fQtrc+zFCv7X0rttXibkdf0o6GsTXhN8OSKmWqC1yb2YI6D8AFzKcNkLmKAIcSgYVW2gjcpN3KGWAvYq5Ng87uWAicWJ1KF5Wn7wPAmz3Qlv5sj6Ko6R2PoxJ822nLr7sZRmXqE6XZaQZ/hnJG9x886jJtcoSYr0ApXQcIsDQoV3P089u8XzLpoxVDBMahe5+uFNm35Gqo6nWD1ygI6x4+8HqupQJMwXRLu8QVGyBYhq8lZnv92BvcZCpanFN1wqbyXlLg70PfVBwfVFUGffzbcH7tWBnf+udpfjZNpBKzKDcZpEF1rz4dPfU0McqoRVb0EE7bMKFzMurgg4byiuFnO6uxW89AcuFLFLqCfuE7kak0= generated-by-azure"
                            }
                        ]
                    }
                },
                "customData": CUSTOM_DATA
            },
            "networkProfile": {
                "networkInterfaces": [
                    {
                        "id": "/subscriptions/9568b8d1-2087-47d1-b8ed-0baa6969f187/resourceGroups/VPS-Server/providers/Microsoft.Network/networkInterfaces/"+nicName,
                        "properties": {
                            "primary": True
                        }
                    }
                ]
            }
        }
    }
    vmData = json.dumps(vmData)
    vmData_resp = requests.put("https://management.azure.com/subscriptions/9568b8d1-2087-47d1-b8ed-0baa6969f187/resourceGroups/VPS-Server/providers/Microsoft.Compute/virtualMachines/"+vmName+"?api-version=2017-12-01", headers= headers, data=vmData)
    print(vmData_resp.status_code)

    # if(vmData_resp.status_code != 200 or vmData_resp.status_code != 201):
    #     return (vmData_resp.content)

    # Step 7: Return the IP of newly created Virtual Machine 
    # GET https://management.azure.com/subscriptions/{subscriptionId}/resourceGroups/{resourceGroupName}/providers/Microsoft.Network/publicIPAddresses/{publicIpAddressName}?api-version=2022-01-01
    
    getIP = requests.get("https://management.azure.com/subscriptions/9568b8d1-2087-47d1-b8ed-0baa6969f187/resourceGroups/VPS-Server/providers/Microsoft.Network/publicIPAddresses/"+ipName+"?api-version=2022-01-01", headers= headers)
    
    # if(getIP.status_code != 200 or getIP.status_code != 201):
    #     return (getIP.content)
    # else:
    getIP = json.loads(getIP.content)
    ipAddr = getIP["properties"]["ipAddress"]

    startVM = requests.post("https://management.azure.com/subscriptions/9568b8d1-2087-47d1-b8ed-0baa6969f187/resourceGroups/VPS-Server/providers/Microsoft.Compute/virtualMachines/"+vmName+"/start?api-version=2022-03-01", headers= headers)
    print(startVM.status_code)

    return (ipAddr, vmName, '10')