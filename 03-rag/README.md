# Module 03: RAG (Retrieval-Augmented Generation)

Build a RAG system that answers questions based on your documents using LangChain4j and Azure OpenAI.

## Features

- **Document Upload**: Web UI for easy document ingestion (TXT, PDF)
- **Smart Chunking**: Recursive text splitting with overlap (300 tokens)
- **Vector Embeddings**: Azure OpenAI text-embedding-3-small
- **Semantic Search**: Find relevant content with similarity scoring
- **Source Attribution**: Answers include source references with relevance scores
- **In-Memory Store**: Fast vector storage (resets on restart)

## Quick Start

### 1. Set Environment Variables

```bash
cd 03-rag
source ../.env  # or manually export variables
```

Required variables (from `.env` in project root):
- `AZURE_OPENAI_ENDPOINT`
- `AZURE_OPENAI_API_KEY`
- `AZURE_OPENAI_DEPLOYMENT` (e.g., gpt-5)
- `AZURE_OPENAI_EMBEDDING_DEPLOYMENT` (e.g., text-embedding-3-small)

### 2. Run Application

```bash
mvn spring-boot:run
```

### 3. Open Web UI

Navigate to: **http://localhost:8081**

**Step 1**: Upload a document (TXT works best, PDF supported)  
**Step 2**: Ask questions about the document  
**Step 3**: Get AI-generated answers with source references

## Document Format Tips

✅ **Best Results**: Clean TXT files with clear structure  
✅ **Good**: Well-formatted PDFs with selectable text  
❌ **Poor**: Scanned PDFs, images, tables without structure

**File Size**: Keep under 5MB for best performance

## API Endpoints

Test via command line:

```bash
# Upload document
curl -X POST "http://localhost:8081/api/documents/upload" \
  -F "file=@document.txt"

# Ask question
curl -X POST "http://localhost:8081/api/rag/ask" \
  -H "Content-Type: application/json" \
  -d '{
    "question": "What is the main topic?",
    "conversationId": null,
    "maxResults": 5
  }'
```

## Configuration

Key settings in `application.yaml`:

```yaml
rag:
  chunk-size: 300        # Tokens per chunk
  chunk-overlap: 30      # Overlap for context continuity
  max-results: 5         # Chunks to retrieve
  min-score: 0.5         # Minimum similarity score (0.5 = balanced)
```

**Similarity Scores:**
- **0.7-1.0**: Highly relevant (excellent match)
- **0.5-0.7**: Relevant (good match)
- **<0.5**: Filtered out (low relevance)

## Architecture

**Storage:** InMemoryEmbeddingStore (⚠️ data lost on restart)  
**Production:** Use persistent store (Qdrant, Azure AI Search)

**Components:**
- `DocumentService` - PDF/TXT parsing and chunking
- `EmbeddingService` - Generate and store embeddings
- `RagService` - Semantic search + answer generation

## Troubleshooting

**"No relevant documents found"**
- Re-upload document after restart (in-memory store is empty)
- Try simpler, more direct questions
- Lower `min-score` in application.yaml

**Source shows "undefined"**
- Clear browser cache and refresh

**Upload fails**
- Check file size (<10MB limit)
- Verify file format (TXT or PDF with selectable text)

## Next Steps

**Module 04:** [AI Agents with Tools](../04-tools/README.md)