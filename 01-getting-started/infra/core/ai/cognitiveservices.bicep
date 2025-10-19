param name string
param location string = resourceGroup().location
param tags object = {}
param kind string = 'OpenAI'
param sku string = 'S0'
param deployments array = []
param managedIdentityPrincipalId string = ''
param principalType string = 'ServicePrincipal'

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

// Assign Cognitive Services OpenAI User role if principal ID is provided
resource cognitiveServicesOpenAIUser 'Microsoft.Authorization/roleAssignments@2022-04-01' = if (managedIdentityPrincipalId != '') {
  name: guid(cognitiveServices.id, managedIdentityPrincipalId, '5e0bd9bd-7b93-4f28-af87-19fc36ad61bd')
  scope: cognitiveServices
  properties: {
    roleDefinitionId: subscriptionResourceId('Microsoft.Authorization/roleDefinitions', '5e0bd9bd-7b93-4f28-af87-19fc36ad61bd')
    principalId: managedIdentityPrincipalId
    principalType: principalType
  }
}

output id string = cognitiveServices.id
output name string = cognitiveServices.name
output endpoint string = cognitiveServices.properties.endpoint
output deploymentNames array = [for (deploy, i) in deployments: deployment[i].name]
