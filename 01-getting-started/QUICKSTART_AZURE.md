# Quick Start Guide - Azure Deployment

## Prerequisites Check
```bash
# Verify tools are installed
az --version          # Should be 2.50.0+
azd version          # Should be 1.5.0+
docker --version     # Any recent version
java -version        # Should be 21+
mvn --version        # Should be 3.8+
```

## First Time Setup (5 minutes)

### 1. Login to Azure
```bash
az login
azd auth login
```

### 2. Initialize Environment
```bash
cd 01-getting-started
azd init
```
- Press Enter to use current directory
- Environment name: `langchain4j-dev` (or your preference)

### 3. Deploy Everything
```bash
azd up
```
Select:
- **Subscription**: Your Azure subscription
- **Location**: `eastus` (recommended for OpenAI availability)

This will:
- ✅ Create all Azure resources (~5 minutes)
- ✅ Build Docker image (~2 minutes)
- ✅ Push to Azure Container Registry (~1 minute)
- ✅ Deploy to Container Apps (~2 minutes)

**Total time**: ~10 minutes for first deployment

## Get Application URL
```bash
azd env get-values | grep APP_URL
```

## Test the Application

### Basic Chat
```bash
curl -X POST https://{your-app-url}/api/chat \
  -H 'Content-Type: application/json' \
  -d '{"message":"What is Azure OpenAI?"}'
```

### Conversational Chat
```bash
# Start conversation
CONV_ID=$(curl -X POST https://{your-app-url}/api/conversation/start | jq -r '.conversationId')

# Chat with context
curl -X POST https://{your-app-url}/api/conversation/chat \
  -H 'Content-Type: application/json' \
  -d "{\"conversationId\":\"$CONV_ID\",\"message\":\"My name is Alex\"}"

curl -X POST https://{your-app-url}/api/conversation/chat \
  -H 'Content-Type: application/json' \
  -d "{\"conversationId\":\"$CONV_ID\",\"message\":\"What is my name?\"}"
```

## View Logs
```bash
azd monitor --logs
```

## Update Code
After making code changes:
```bash
azd deploy
```

## Clean Up
```bash
azd down
```

## Common Issues

### Issue: OpenAI model not found
**Wait 2-3 minutes** - Model deployments take time after provisioning

### Issue: Container app won't start
Check logs:
```bash
azd monitor --logs
```

### Issue: Can't find environment
List environments:
```bash
azd env list
azd env select {environment-name}
```

## Environment Variables Reference

After `azd provision`, these are automatically available:

| Variable | Purpose | Example Value |
|----------|---------|---------------|
| `AZURE_OPENAI_ENDPOINT` | OpenAI API endpoint | `https://aoai-xyz.openai.azure.com/` |
| `AZURE_OPENAI_DEPLOYMENT` | Chat model name | `gpt-4o-mini` |
| `AZURE_OPENAI_EMBEDDING_DEPLOYMENT` | Embedding model | `text-embedding-3-small` |
| `APP_URL` | Application URL | `https://ca-xyz.eastus.azurecontainerapps.io` |

View all:
```bash
azd env get-values
```

## Cost Management

Current configuration costs approximately **$60-80/month** including:
- Container App (always on): ~$50/month
- Azure OpenAI (usage-based): ~$10-30/month for moderate use
- Other services: ~$10/month

### Save Money for Development
Edit `infra/core/host/container-app.bicep`:
```bicep
scale: {
  minReplicas: 0  // Scale to zero when idle (saves ~$50/month)
  maxReplicas: 10
}
```

Then update:
```bash
azd provision
```

## Next Steps

1. ✅ **Test all endpoints** using the curl commands above
2. 📊 **View metrics** in Azure Portal → Container Apps
3. 🔍 **Check OpenAI usage** in Azure Portal → Azure OpenAI
4. 📚 **Read full docs**: See `infra/README.md`
5. 🚀 **Move to Module 02**: RAG with embeddings already deployed!

## Support

- **Azure Issues**: Check `infra/README.md` troubleshooting section
- **Application Issues**: Check logs with `azd monitor --logs`
- **GitHub Issues**: Open issue in repository

## Model Information

### GPT-4o-mini
- **Max Tokens**: 128K context window
- **TPM**: 20,000 (configurable)
- **Cost**: $0.150/1M input, $0.600/1M output tokens
- **Use**: Chat, conversation, function calling

### Text-Embedding-3-Small  
- **Dimensions**: 1536
- **Max Input**: 8,191 tokens
- **TPM**: 20,000 (configurable)
- **Cost**: $0.020/1M tokens
- **Use**: RAG, semantic search, similarity

Both models are deployed and ready to use!
