import adal

# Tenant ID for your Azure Subscription
TENANT_ID = '5ef97fc2-9f71-4435-a705-3c9ee58a52f8'

# Your Service Principal App ID
CLIENT = '17b8c581-f781-42d4-8c30-c0dbdd86d166'

# Your Service Principal Password
KEY = 'QsO8Q~eo7vlb_qtB33jETqAs8f1X5oBj6w8-Ocjj'


subscription_id = '9568b8d1-2087-47d1-b8ed-0baa6969f187'

authority_url = 'https://login.microsoftonline.com/'+TENANT_ID
context = adal.AuthenticationContext(authority_url)
token = context.acquire_token_with_client_credentials(
    resource='https://management.azure.com/',
    client_id=CLIENT,
    client_secret=KEY
)

print(token["accessToken"])

'''
{
            
            
            "properties": {
                "location": "centralindia",
                "autoUpgradeMinorVersion": True,
                "publisher": "Microsoft.Azure.Extensions",
                "type": "CustomScript",
                "typeHandlerVersion": "2.0",
                "settings": {
                    "fileUris": "https://runinstallation.blob.core.windows.net/script/installation.sh"
                },
                "protectedSettings": {
                    "storageAccountName": "runinstallation",
                    "storageAccountKey": "IejMM4XI/zWhuelKqyS3qyQSOSbn0OslZPQjybSZNt2Bh0GERmd3y/JBZYja9mhpILmH1BLH/PwE+AStkdx8gA==",
                    "commandToExecute": "sh installation.sh"
                }
            }
        }

# from flask_caching import Cache
# app.config['CACHE_TYPE'] = 'SimpleCache' 
# cache = Cache(app)
# numClients = cache.get("num")



'''
