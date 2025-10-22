param name string
param location string = resourceGroup().location
param tags object = {}
param kind string = 'OpenAI'
param sku string = 'S0'
param deployments array = []

resource cognitiveServices 'Microsoft.CognitiveServices/accounts@2024-10-01' = {
  name: name
  location: location
  tags: tags
  kind: kind
  sku: {
    name: sku
  }
  properties: {
    customSubDomainName: name
    publicNetworkAccess: 'Enabled'
    networkAcls: {
      defaultAction: 'Allow'
    }
  }
}

@batchSize(1)
resource deployment 'Microsoft.CognitiveServices/accounts/deployments@2024-10-01' = [for deploy in deployments: {
  parent: cognitiveServices
  name: deploy.name
  sku: deploy.sku
  properties: {
    model: deploy.model
  }
}]

output id string = cognitiveServices.id
output name string = cognitiveServices.name
output endpoint string = cognitiveServices.properties.endpoint
output deploymentNames array = [for (deploy, i) in deployments: deployment[i].name]
output key string = cognitiveServices.listKeys().key1
