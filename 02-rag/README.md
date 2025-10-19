# Module 02: Retrieval-Augmented Generation (RAG)

## Overview

This module demonstrates how to build a production-ready RAG (Retrieval-Augmented Generation) system using LangChain4j and Azure OpenAI. RAG combines document retrieval with AI generation to provide accurate, context-aware answers based on your own documents.

## What You'll Learn

- **Document Ingestion**: Upload and parse PDF and TXT files
- **Text Chunking**: Split documents into optimal segments for embedding
- **Embeddings**: Generate vector embeddings using Azure OpenAI
- **Vector Storage**: Store and retrieve embeddings efficiently
- **Semantic Search**: Find relevant document segments for queries
- **Context-Aware Generation**: Generate answers grounded in retrieved context
- **Source Attribution**: Track and cite sources in responses

## Architecture

This RAG system is deployed alongside the getting-started chat application, sharing Azure infrastructure:

```
┌─────────────────────────────────────────────────────────────────┐
│  Azure Container Apps Environment                               │
│                                                                  │
│  ┌──────────────────────┐      ┌─────────────────────────────┐ │
│  │  Getting Started App │      │  RAG Application (02-rag)   │ │
│  │  (01-getting-started)│      │                             │ │
│  │                      │      │  ┌────────────────────────┐ │ │
│  │  • Simple Chat       │      │  │  Document Service      │ │ │
│  │  • Conversation      │      │  │  • Upload & Parse      │ │ │
│  │  • Streaming         │      │  │  • Text Splitting      │ │ │
│  │                      │      │  │  • Embedding Storage   │ │ │
│  │  Port: 8080          │      │  └────────────────────────┘ │ │
│  └──────────────────────┘      │                             │ │
│                                 │  ┌────────────────────────┐ │ │
│                                 │  │  RAG Service           │ │ │
│                                 │  │  • Semantic Search     │ │ │
│                                 │  │  • Context Retrieval   │ │ │
│                                 │  │  • Answer Generation   │ │ │
│                                 │  │  • Source Attribution  │ │ │
│                                 │  └────────────────────────┘ │ │
│                                 │                             │ │
│                                 │  Port: 8081                 │ │
│                                 └─────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
                              │
                              │ Both apps use shared resources
                              ▼
         ┌────────────────────────────────────────────┐
         │  Azure OpenAI Service                      │
         │                                            │
         │  • Chat Model (gpt-4o-mini)               │
         │  • Embedding Model (text-embedding-3-small)│
         └────────────────────────────────────────────┘

Deployment: Single "azd up" from 01-getting-started/ deploys both apps
Infrastructure: 01-getting-started/infra/main.bicep defines all resources
```

### How It Works

1. **Document Upload** → Text is split into chunks → Embeddings generated → Stored in vector store (in-memory)
2. **User Query** → Query embedded → Semantic search finds relevant chunks → Context + query sent to LLM → Answer with sources returned
3. **Shared Resources** → Both apps use the same Azure OpenAI deployments and Container Apps Environment

## Prerequisites

### Required Azure Resources

This module **shares infrastructure** with the `01-getting-started` module:

1. **Azure OpenAI Service** with:
   - A chat model deployment (e.g., `gpt-4o-mini`)
   - An embedding model deployment (e.g., `text-embedding-3-small`)
2. **Azure Container Registry** for Docker images
3. **Azure Container Apps Environment** for hosting

All resources are provisioned via the Bicep files in `01-getting-started/infra/`.

### Environment Variables

```bash
export AZURE_OPENAI_ENDPOINT="https://your-resource.openai.azure.com/"
export AZURE_OPENAI_API_KEY="your-api-key"
export AZURE_OPENAI_DEPLOYMENT="gpt-4o-mini"
export AZURE_OPENAI_EMBEDDING_DEPLOYMENT="text-embedding-3-small"
```

## Quick Start

### Option 1: Deploy to Azure (Recommended)

Both the getting-started and RAG apps are deployed together:

```bash
cd 01-getting-started
azd up
```

This deploys:
- Getting Started app: `https://ca-<unique-id>.*.azurecontainerapps.io`
- RAG app: `https://ca-rag-<unique-id>.*.azurecontainerapps.io`

### Option 2: Run Locally

#### 1. Build the Application

```bash
cd 02-rag
mvn clean package spring-boot:repackage -DskipTests
```

#### 2. Run the Application

```bash
java -jar target/rag-0.1.0.jar
```

The application will start on `http://localhost:8081`.

### 3. Test the Application

Use the provided test scripts:

```bash
# Test document upload (local)
cd ../
bash scripts/test-upload.sh http://localhost:8081

# Test RAG queries (local)
bash scripts/test-query.sh http://localhost:8081

# Test on Azure (replace with your URL from azd output)
bash scripts/test-upload.sh https://ca-rag-<unique-id>.*.azurecontainerapps.io
bash scripts/test-query.sh https://ca-rag-<unique-id>.*.azurecontainerapps.io
```

#### Manual Testing

Upload a document:

```bash
curl -X POST http://localhost:8081/api/documents/upload \
  -F "file=@/path/to/your/document.pdf"
```

Response:
```json
{
  "documentId": "abc123",
  "filename": "document.pdf",
  "message": "Document processed successfully",
  "segmentCount": 42
}
```

### 4. Ask Questions

```bash
curl -X POST http://localhost:8081/api/rag/ask \
  -H "Content-Type: application/json" \
  -d '{
    "question": "What is the main topic of the document?",
    "conversationId": "conv-123"
  }'
```

Response:
```json
{
  "answer": "Based on the provided context, the main topic is...",
  "conversationId": "conv-123",
  "sources": [
    {
      "filename": "document.pdf",
      "excerpt": "Relevant excerpt from the document...",
      "relevanceScore": 0.92
    }
  ]
}
```

## Architecture

### Components

```
┌─────────────────────────────────────────────────────────┐
│                    RAG Controller                        │
│                  /api/rag/ask                           │
└────────────────────┬────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────┐
│                    RAG Service                           │
│  1. Embed Query  2. Retrieve  3. Generate  4. Cite     │
└─────┬──────────────┬──────────────┬──────────────┬──────┘
      │              │              │              │
      ▼              ▼              ▼              ▼
┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐
│Embedding │  │  Vector  │  │   Chat   │  │  Source  │
│  Model   │  │  Store   │  │  Model   │  │  Tracker │
└──────────┘  └──────────┘  └──────────┘  └──────────┘
```

### Document Processing Flow

```
PDF/TXT File
     │
     ▼
┌──────────────────┐
│ Document Parser  │  ← ApachePdfBoxDocumentParser
└────────┬─────────┘
         │
         ▼
┌──────────────────┐
│ Text Splitter    │  ← DocumentSplitters.recursive()
└────────┬─────────┘     (300 tokens, 30 overlap)
         │
         ▼
┌──────────────────┐
│ Embedding Model  │  ← Azure OpenAI Embedding
└────────┬─────────┘
         │
         ▼
┌──────────────────┐
│ Vector Store     │  ← InMemoryEmbeddingStore
└──────────────────┘
```

## API Reference

### Document Upload

**Endpoint**: `POST /api/documents/upload`

**Content-Type**: `multipart/form-data`

**Parameters**:
- `file` (required): PDF or TXT file

**Response**:
```json
{
  "documentId": "string",
  "filename": "string",
  "message": "string",
  "segmentCount": number
}
```

**Example**:
```bash
curl -X POST http://localhost:8081/api/documents/upload \
  -F "file=@research-paper.pdf"
```

### RAG Query

**Endpoint**: `POST /api/rag/ask`

**Content-Type**: `application/json`

**Request Body**:
```json
{
  "question": "Your question here",
  "conversationId": "optional-conversation-id"
}
```

**Response**:
```json
{
  "answer": "Generated answer based on documents",
  "conversationId": "conversation-id",
  "sources": [
    {
      "filename": "document.pdf",
      "excerpt": "Relevant excerpt",
      "relevanceScore": 0.95
    }
  ]
}
```

**Example**:
```bash
curl -X POST http://localhost:8081/api/rag/ask \
  -H "Content-Type: application/json" \
  -d '{
    "question": "What are the key findings?",
    "conversationId": "research-session-1"
  }'
```

### Health Checks

**Document Service**:
```bash
curl http://localhost:8081/api/documents/health
```

**RAG Service**:
```bash
curl http://localhost:8081/api/rag/health
```

## Configuration

### Application Properties

Edit `src/main/resources/application.yaml`:

```yaml
# RAG Configuration
rag:
  chunk-size: 300          # Size of text chunks
  chunk-overlap: 30        # Overlap between chunks
  max-results: 5           # Max segments to retrieve
  min-score: 0.7          # Minimum relevance score

# Azure OpenAI
azure:
  openai:
    temperature: 0.2       # Response randomness (0-1)
    max-tokens: 1000      # Max response length
    max-retries: 3        # Retry attempts
```

### Chunk Size Recommendations

| Document Type | Chunk Size | Overlap |
|--------------|------------|---------|
| Technical docs | 300-500 | 30-50 |
| Research papers | 400-600 | 50-80 |
| General text | 200-400 | 20-40 |

### Vector Store Options

#### In-Memory (Development)

Default for local development. Fast but not persistent.

```yaml
spring:
  profiles:
    active: dev
```

#### Qdrant (Production)

High-performance vector database. Add to `pom.xml`:

```xml
<dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j-qdrant</artifactId>
    <version>${langchain4j.version}</version>
</dependency>
```

Configure in `application-prod.yaml`:

```yaml
qdrant:
  host: localhost
  port: 6333
  api-key: ${QDRANT_API_KEY}
```

#### Azure AI Search (Production)

Enterprise-grade search service. Add to `pom.xml`:

```xml
<dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j-azure-ai-search</artifactId>
    <version>${langchain4j.version}</version>
</dependency>
```

Configure in `application-prod.yaml`:

```yaml
azure:
  search:
    endpoint: ${AZURE_SEARCH_ENDPOINT}
    api-key: ${AZURE_SEARCH_KEY}
    index-name: documents
```

## Examples

### Example 1: Research Assistant

```bash
# Upload research papers
curl -X POST http://localhost:8081/api/documents/upload \
  -F "file=@paper1.pdf"
curl -X POST http://localhost:8081/api/documents/upload \
  -F "file=@paper2.pdf"

# Ask questions
curl -X POST http://localhost:8081/api/rag/ask \
  -H "Content-Type: application/json" \
  -d '{
    "question": "What are the main differences between the two methodologies?"
  }'
```

### Example 2: Documentation Q&A

```bash
# Upload documentation
curl -X POST http://localhost:8081/api/documents/upload \
  -F "file=@api-docs.txt"

# Query documentation
curl -X POST http://localhost:8081/api/rag/ask \
  -H "Content-Type: application/json" \
  -d '{
    "question": "How do I authenticate API requests?"
  }'
```

### Example 3: Multi-Document Analysis

```bash
# Upload multiple files
for file in docs/*.pdf; do
  curl -X POST http://localhost:8081/api/documents/upload \
    -F "file=@$file"
done

# Ask comparative questions
curl -X POST http://localhost:8081/api/rag/ask \
  -H "Content-Type: application/json" \
  -d '{
    "question": "Compare the findings across all documents"
  }'
```

## How It Works

### 1. Document Ingestion

When you upload a document:

1. **Parse**: Extract text from PDF or read TXT file
2. **Split**: Divide into overlapping chunks (300 tokens, 30 overlap)
3. **Embed**: Generate vector embeddings for each chunk
4. **Store**: Save embeddings with metadata in vector store

### 2. Query Processing

When you ask a question:

1. **Embed Query**: Convert question to vector embedding
2. **Semantic Search**: Find top 5 most similar document chunks
3. **Filter**: Keep only chunks above 0.7 similarity threshold
4. **Build Context**: Combine relevant chunks into context
5. **Generate**: Use LLM to answer based on context
6. **Cite Sources**: Include source references in response

### 3. Relevance Scoring

Scores range from 0 to 1:
- **0.9-1.0**: Highly relevant, exact match
- **0.8-0.9**: Very relevant, strong semantic match
- **0.7-0.8**: Relevant, good semantic match
- **<0.7**: Not relevant (filtered out)

## Troubleshooting

### Issue: No relevant documents found

**Problem**: RAG returns "I cannot answer based on the context"

**Solutions**:
1. Lower `min-score` in configuration (try 0.6)
2. Reduce `chunk-size` for more granular matching
3. Increase `max-results` to retrieve more candidates
4. Verify documents uploaded successfully

### Issue: Out of memory errors

**Problem**: Application crashes with `OutOfMemoryError`

**Solutions**:
1. Increase JVM heap: `java -Xmx2g -jar ...`
2. Use Qdrant or Azure AI Search instead of in-memory store
3. Reduce chunk size to create fewer embeddings
4. Process documents in batches

### Issue: Slow response times

**Problem**: RAG queries take too long

**Solutions**:
1. Reduce `max-results` (fewer chunks to process)
2. Use cached embeddings
3. Consider Azure AI Search for large datasets
4. Optimize chunk size (larger = fewer chunks)

### Issue: Poor answer quality

**Problem**: Answers are generic or miss key information

**Solutions**:
1. Increase `max-results` to include more context
2. Adjust chunk size and overlap
3. Lower `min-score` to include more candidates
4. Use a more capable chat model (GPT-4 vs GPT-3.5)
5. Improve system prompt in `RagService`

## Best Practices

### Document Preparation

1. **Clean Text**: Remove headers, footers, page numbers
2. **Structure**: Use clear headings and sections
3. **Format**: PDF preferred for layout preservation
4. **Size**: Keep documents under 5MB for optimal processing

### Chunking Strategy

1. **Overlap**: Always use overlap to preserve context at boundaries
2. **Size**: Balance between context and precision
3. **Boundaries**: Consider natural breakpoints (paragraphs, sections)

### Query Optimization

1. **Specific Questions**: More specific queries get better results
2. **Context**: Include relevant context in your question
3. **Iterative**: Refine questions based on initial responses

### Production Deployment

1. **Vector Store**: Use Qdrant or Azure AI Search, not in-memory
2. **Monitoring**: Track query latency, relevance scores, token usage
3. **Caching**: Cache embeddings and common queries
4. **Rate Limiting**: Implement rate limits on upload and query endpoints
5. **Security**: Validate file types, scan for malware

## Deployment

### Azure Deployment

The RAG application is deployed alongside the getting-started app using a single infrastructure definition:

```bash
cd 01-getting-started
azd up
```

This creates:
- **Shared Infrastructure**: Azure OpenAI, Container Registry, Container Apps Environment
- **Two Container Apps**: 
  - `ca-<unique-id>` - Getting Started app (port 8080)
  - `ca-rag-<unique-id>` - RAG app (port 8081)

#### Get Deployment URLs

```bash
cd 01-getting-started
azd env get-values | grep APP_URL
```

Output:
```
APP_URL="https://ca-ozhotol5yje76.livelyplant-8c187f15.eastus2.azurecontainerapps.io"
RAG_APP_URL="https://ca-rag-ozhotol5yje76.livelyplant-8c187f15.eastus2.azurecontainerapps.io"
```

#### Update Deployment

To deploy code changes:

```bash
cd 01-getting-started
azd deploy
```

This rebuilds Docker images and updates both container apps.

## Testing

### Automated Test Scripts

Run comprehensive test suites:

```bash
# Test on Azure
export RAG_URL="https://ca-rag-<unique-id>.*.azurecontainerapps.io"
bash scripts/test-upload.sh $RAG_URL
bash scripts/test-query.sh $RAG_URL

# Test locally
bash scripts/test-upload.sh http://localhost:8081
bash scripts/test-query.sh http://localhost:8081
```

### Unit Tests

Run unit tests:
```bash
mvn test
```

### Manual Integration Tests

Test document upload:
```bash
./test-upload.sh
```

Test RAG query:
```bash
./test-query.sh
```

### Performance Testing

Load test with 100 concurrent users:
```bash
ab -n 1000 -c 100 -p query.json -T application/json \
  http://localhost:8081/api/rag/ask
```

## Next Steps

After completing this module, you'll be ready for:

- **Module 03: Agents & Tools** - Multi-step reasoning and function calling
- **Module 04: Production** - Observability, content safety, and scaling

## Resources

- [LangChain4j RAG Documentation](https://docs.langchain4j.dev/tutorials/rag)
- [Azure OpenAI Embeddings Guide](https://learn.microsoft.com/azure/ai-services/openai/concepts/embeddings)
- [Vector Search Best Practices](https://learn.microsoft.com/azure/search/vector-search-overview)

## License

MIT License - See root LICENSE file