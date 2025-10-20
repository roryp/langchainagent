// Azure AI Foundry Hub and Project for Agents
param name string
param location string = resourceGroup().location
param tags object = {}
param openAiName string
param openAiResourceGroupName string
@secure()
param openAiKey string

// Azure AI Hub (workspace)
resource aiHub 'Microsoft.MachineLearningServices/workspaces@2024-04-01' = {
  name: 'hub-${name}'
  location: location
  tags: tags
  identity: {
    type: 'SystemAssigned'
  }
  properties: {
    description: 'Azure AI Hub for LangChain4j Agents'
    friendlyName: 'LangChain4j AI Hub'
    keyVault: null // Will use default key vault
    storageAccount: null // Will use default storage
    applicationInsights: null // Optional
    containerRegistry: null // Optional
    hbiWorkspace: false
    publicNetworkAccess: 'Enabled'
  }
  kind: 'Hub'
  sku: {
    name: 'Basic'
    tier: 'Basic'
  }
}

// Azure AI Project (within the hub)
resource aiProject 'Microsoft.MachineLearningServices/workspaces@2024-04-01' = {
  name: 'proj-${name}'
  location: location
  tags: tags
  identity: {
    type: 'SystemAssigned'
  }
  properties: {
    description: 'Azure AI Project for LangChain4j Agents'
    friendlyName: 'LangChain4j Agents Project'
    hubResourceId: aiHub.id
    publicNetworkAccess: 'Enabled'
  }
  kind: 'Project'
  sku: {
    name: 'Basic'
    tier: 'Basic'
  }
}

// Connect Azure OpenAI to the project
resource openAiConnection 'Microsoft.MachineLearningServices/workspaces/connections@2024-04-01' = {
  name: 'azureopenai-connection'
  parent: aiProject
  properties: {
    category: 'AzureOpenAI'
    authType: 'ApiKey'
    isSharedToAll: true
    target: 'https://${openAiName}.openai.azure.com/'
    credentials: {
      key: openAiKey
    }
    metadata: {
      ApiVersion: '2024-02-01'
      ApiType: 'azure'
      ResourceId: resourceId(openAiResourceGroupName, 'Microsoft.CognitiveServices/accounts', openAiName)
    }
  }
}

// Output for connecting the agent service
output hubId string = aiHub.id
output hubName string = aiHub.name
output projectId string = aiProject.id
output projectName string = aiProject.name
output endpoint string = 'https://${location}.api.azureml.ms' // AI Foundry API endpoint
output principalId string = aiProject.identity.principalId
