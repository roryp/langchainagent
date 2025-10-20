targetScope = 'subscription'

@minLength(1)
@maxLength(64)
@description('Name of the environment used for resource naming')
param environmentName string

@minLength(1)
@description('Primary location for all resources')
param location string

// OpenAI parameters
@description('Location for Azure OpenAI resource')
param openAiLocation string = 'eastus'

@description('SKU name for Azure OpenAI')
param openAiSkuName string = 'S0'

@description('Model deployments for Azure OpenAI')
param openAiDeployments array = [
  {
    name: 'gpt-4o-mini'
    model: {
      format: 'OpenAI'
      name: 'gpt-4o-mini'
      version: '2024-07-18'
    }
    sku: {
      name: 'Standard'
      capacity: 20
    }
  }
  {
    name: 'text-embedding-3-small'
    model: {
      format: 'OpenAI'
      name: 'text-embedding-3-small'
      version: '1'
    }
    sku: {
      name: 'Standard'
      capacity: 20
    }
  }
]

// Tags
var tags = {
  'azd-env-name': environmentName
}

// Generate unique token for resource naming
var resourceToken = uniqueString(subscription().id, environmentName, location)

// Resource Group
resource rg 'Microsoft.Resources/resourceGroups@2024-03-01' = {
  name: 'rg-${environmentName}'
  location: location
  tags: tags
}

// User Assigned Managed Identity
module managedIdentity 'core/security/managed-identity.bicep' = {
  name: 'managed-identity'
  scope: rg
  params: {
    name: 'id-${resourceToken}'
    location: location
    tags: tags
  }
}

// Log Analytics Workspace
module logAnalytics 'core/monitor/log-analytics.bicep' = {
  name: 'log-analytics'
  scope: rg
  params: {
    name: 'log-${resourceToken}'
    location: location
    tags: tags
  }
}

// Container Registry
module containerRegistry 'core/host/container-registry.bicep' = {
  name: 'container-registry'
  scope: rg
  params: {
    name: 'acr${resourceToken}'
    location: location
    tags: tags
    managedIdentityPrincipalId: managedIdentity.outputs.principalId
  }
}

// Container Apps Environment
module containerAppsEnvironment 'core/host/container-apps-environment.bicep' = {
  name: 'container-apps-environment'
  scope: rg
  params: {
    name: 'cae-${resourceToken}'
    location: location
    tags: tags
    logAnalyticsWorkspaceName: logAnalytics.outputs.name
  }
}

// Azure OpenAI
module openAi 'core/ai/cognitiveservices.bicep' = {
  name: 'openai'
  scope: rg
  params: {
    name: 'aoai-${resourceToken}'
    location: openAiLocation
    tags: tags
    kind: 'OpenAI'
    sku: openAiSkuName
    deployments: openAiDeployments
    managedIdentityPrincipalId: managedIdentity.outputs.principalId
    principalType: 'ServicePrincipal'
  }
}

// Azure AI Services (Foundry) for Agent Service
module aiServices 'core/ai/aiservices.bicep' = {
  name: 'aiservices'
  scope: rg
  params: {
    name: resourceToken
    location: location
    tags: tags
    openAiName: openAi.outputs.name
    openAiResourceGroupName: rg.name
    openAiKey: openAi.outputs.key
  }
}

// Container App - Getting Started
module app 'core/host/container-app.bicep' = {
  name: 'app'
  scope: rg
  params: {
    name: 'ca-${resourceToken}'
    location: location
    tags: union(tags, { 'azd-service-name': 'app' })
    containerAppsEnvironmentName: containerAppsEnvironment.outputs.name
    containerRegistryName: containerRegistry.outputs.name
    managedIdentityName: managedIdentity.outputs.name
    openAiName: openAi.outputs.name
    openAiApiKey: openAi.outputs.key
    targetPort: 8080
    env: [
      {
        name: 'AZURE_OPENAI_ENDPOINT'
        value: openAi.outputs.endpoint
      }
      {
        name: 'AZURE_OPENAI_DEPLOYMENT'
        value: 'gpt-4o-mini'
      }
      {
        name: 'AZURE_OPENAI_EMBEDDING_DEPLOYMENT'
        value: 'text-embedding-3-small'
      }
      {
        name: 'SPRING_PROFILES_ACTIVE'
        value: 'azure'
      }
    ]
  }
}

// Container App - RAG
module ragApp 'core/host/container-app.bicep' = {
  name: 'rag-app'
  scope: rg
  params: {
    name: 'ca-rag-${resourceToken}'
    location: location
    tags: union(tags, { 'azd-service-name': 'rag' })
    containerAppsEnvironmentName: containerAppsEnvironment.outputs.name
    containerRegistryName: containerRegistry.outputs.name
    managedIdentityName: managedIdentity.outputs.name
    openAiName: openAi.outputs.name
    openAiApiKey: openAi.outputs.key
    targetPort: 8081
    env: [
      {
        name: 'AZURE_OPENAI_ENDPOINT'
        value: openAi.outputs.endpoint
      }
      {
        name: 'AZURE_OPENAI_DEPLOYMENT'
        value: 'gpt-4o-mini'
      }
      {
        name: 'AZURE_OPENAI_EMBEDDING_DEPLOYMENT'
        value: 'text-embedding-3-small'
      }
      {
        name: 'SPRING_PROFILES_ACTIVE'
        value: 'dev'
      }
    ]
  }
}

// Container App - Agents & Tools (Azure AI Agent Service Client)
module agentsApp 'core/host/container-app.bicep' = {
  name: 'agents-app'
  scope: rg
  params: {
    name: 'ca-agents-${resourceToken}'
    location: location
    tags: union(tags, { 'azd-service-name': 'agents' })
    containerAppsEnvironmentName: containerAppsEnvironment.outputs.name
    containerRegistryName: containerRegistry.outputs.name
    managedIdentityName: managedIdentity.outputs.name
    openAiName: openAi.outputs.name
    openAiApiKey: openAi.outputs.key
    targetPort: 8082
    env: [
      {
        name: 'AZURE_AI_PROJECT_ENDPOINT'
        value: aiServices.outputs.endpoint
      }
      {
        name: 'AZURE_AI_PROJECT_NAME'
        value: aiServices.outputs.projectName
      }
      {
        name: 'AZURE_OPENAI_ENDPOINT'
        value: openAi.outputs.endpoint
      }
      {
        name: 'AZURE_OPENAI_API_KEY'
        secretRef: 'azure-openai-api-key'
      }
      {
        name: 'AZURE_OPENAI_DEPLOYMENT'
        value: 'gpt-4o-mini'
      }
      {
        name: 'TOOLS_BASE_URL'
        value: 'https://ca-agents-${resourceToken}.${containerAppsEnvironment.outputs.defaultDomain}'
      }
      {
        name: 'SPRING_PROFILES_ACTIVE'
        value: 'azure'
      }
    ]
  }
}

// Outputs
output AZURE_LOCATION string = location
output AZURE_TENANT_ID string = tenant().tenantId
output AZURE_RESOURCE_GROUP_ID string = rg.id
output AZURE_CONTAINER_REGISTRY_ENDPOINT string = containerRegistry.outputs.loginServer
output AZURE_CONTAINER_REGISTRY_NAME string = containerRegistry.outputs.name
output AZURE_CONTAINER_APPS_ENVIRONMENT_ID string = containerAppsEnvironment.outputs.id
output AZURE_CONTAINER_APPS_ENVIRONMENT_NAME string = containerAppsEnvironment.outputs.name

output AZURE_OPENAI_ENDPOINT string = openAi.outputs.endpoint
output AZURE_OPENAI_DEPLOYMENT string = 'gpt-4o-mini'
output AZURE_OPENAI_EMBEDDING_DEPLOYMENT string = 'text-embedding-3-small'
output AZURE_OPENAI_NAME string = openAi.outputs.name

output APP_URL string = app.outputs.uri
output APP_NAME string = app.outputs.name

output RAG_APP_URL string = ragApp.outputs.uri
output RAG_APP_NAME string = ragApp.outputs.name

output AGENTS_APP_URL string = agentsApp.outputs.uri
output AGENTS_APP_NAME string = agentsApp.outputs.name

// Azure AI Services (Foundry) for agents
output AZURE_AI_SERVICES_ENDPOINT string = aiServices.outputs.endpoint
output AZURE_AI_SERVICES_PROJECT_NAME string = aiServices.outputs.projectName
output AZURE_AI_SERVICES_HUB_NAME string = aiServices.outputs.hubName
