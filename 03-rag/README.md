# Module 03: RAG (Retrieval-Augmented Generation)

Build a RAG system that answers questions based on your documents using LangChain4j and Azure OpenAI.

## Features

- Document ingestion (PDF, TXT)
- Vector embeddings via Azure OpenAI
- Semantic search with relevance scoring
- Context-aware answer generation
- Source attribution

## Architecture (Local Development)

```

  Spring Boot App (Port 8081)      
  • DocumentService             
  • RagService                  
  • InMemoryEmbeddingStore      

          ↓

  Azure OpenAI                   
  • gpt-5 (chat)          
  • text-embedding-3-small      

```

**Flow**: 
1. Upload document → Split into chunks → Generate embeddings → Store
2. Ask question → Embed query → Find relevant chunks → Generate answer with sources

## Quick Start (Local Development)

```bash
cd 03-rag
export AZURE_OPENAI_ENDPOINT="https://aoai-xyz.openai.azure.com/"
export AZURE_OPENAI_API_KEY="***"
export AZURE_OPENAI_DEPLOYMENT="gpt-5"
export AZURE_OPENAI_EMBEDDING_DEPLOYMENT="text-embedding-3-small"
mvn spring-boot:run
```
2. Ask question → Embed query → Find relevant chunks → Generate answer with sources

## Prerequisites

- Azure subscription with Azure OpenAI access
- Java 21, Maven 3.9+, Azure CLI, azd CLI

## Quick Start (Local Development)

```bash
cd 03-rag
export AZURE_OPENAI_ENDPOINT="https://aoai-xyz.openai.azure.com/"
export AZURE_OPENAI_API_KEY="***"
export AZURE_OPENAI_DEPLOYMENT="gpt-5"
export AZURE_OPENAI_EMBEDDING_DEPLOYMENT="text-embedding-3-small"
mvn spring-boot:run
```

### Test

```bash
# Upload document
curl -X POST "http://localhost:8081/api/documents/upload" \
  -F "file=@document.pdf"

# Ask question
curl -X POST "http://localhost:8081/api/rag/ask" \
  -H "Content-Type: application/json" \
  -d '{"question": "What is the main topic?", "conversationId": "test"}'
```

## API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/documents/upload` | Upload PDF/TXT (multipart/form-data) |
| POST | `/api/rag/ask` | Ask question based on documents |
| GET | `/api/documents/health` | Health check |
| GET | `/api/rag/health` | Health check |

**Example Response:**
```json
{
  "answer": "Generated answer...",
  "sources": [{"filename": "doc.pdf", "relevanceScore": 0.92}]
}
```

## Configuration

**Key Settings (application.yaml):**
```yaml
rag:
  chunk-size: 300        # Token chunk size
  chunk-overlap: 30      # Overlap between chunks
  max-results: 5         # Max segments retrieved
  min-score: 0.7         # Min relevance score

azure:
  openai:
    reasoning-effort: medium  # Reasoning depth (low, medium, high)
    max-tokens: 1000          # Max response length
```

**Vector Store Options:**
- **Development**: InMemoryEmbeddingStore (default, not persistent)
- **Production**: Qdrant or Azure AI Search (add dependencies to pom.xml)

## Implementation

**Key Components:**
- `DocumentService` - Parse PDFs/TXT, split into chunks, generate embeddings
- `RagService` - Semantic search and answer generation
- `ApachePdfBoxDocumentParser` - PDF text extraction
- `DocumentSplitters.recursive()` - Text chunking (300 tokens, 30 overlap)
- `InMemoryEmbeddingStore` - Vector storage (dev only)

**Query Flow:**
1. Embed query → Search vector store → Retrieve top 5 chunks (min score 0.7)
2. Build context from chunks → Send to LLM → Return answer with sources

**Relevance Scores:** 0.9-1.0 (excellent) | 0.8-0.9 (very good) | 0.7-0.8 (good) | <0.7 (filtered)

## Troubleshooting

**No relevant documents found:**
- Lower `min-score` (try 0.6)
- Verify documents uploaded successfully

**Poor answer quality:**
- Increase `max-results` for more context
- Use more capable model

## Best Practices

**Documents**: Clean text, clear structure, <5MB per file  
**Chunking**: Always use overlap, balance size vs precision  
**Queries**: Be specific, include context  
**Production**: Use persistent vector store (Qdrant/Azure AI Search), add monitoring, rate limiting

## Resources

- [LangChain4j RAG Docs](https://docs.langchain4j.dev/tutorials/rag)
- [Azure OpenAI Embeddings](https://learn.microsoft.com/azure/ai-services/openai/concepts/embeddings)