# Challenges - RAG (Retrieval-Augmented Generation)

Test your understanding of RAG concepts with these hands-on challenges!

## 🎯 Challenge 1: Multi-Document Upload

**Difficulty**: ⭐ Easy

**Task**: Modify the upload endpoint to accept multiple files at once.

**Requirements**:
- Accept an array of files instead of a single file
- Process all files and return total segment count
- Handle errors gracefully if one file fails

**Hints**:
- Look at Spring's `@RequestParam MultipartFile[]` parameter
- Use a loop to process each file
- Consider using try-catch for individual file errors

**Bonus**: Return a detailed response showing which files succeeded/failed

---

## 🎯 Challenge 2: Document Metadata

**Difficulty**: ⭐⭐ Medium

**Task**: Add metadata to each document segment (filename, upload date, document type).

**Requirements**:
- Store filename with each segment
- Add timestamp when document was uploaded
- Detect document type (PDF, TXT, etc.)
- Use metadata in RAG responses

**Hints**:
- Use `Metadata` class in LangChain4j
- `TextSegment.metadata()` can store key-value pairs
- Modify the ingestion process to add metadata

**Bonus**: Filter documents by date or type in queries

---

## 🎯 Challenge 3: Improved Chunking Strategy

**Difficulty**: ⭐⭐ Medium

**Task**: Implement smarter document chunking that respects sentence boundaries.

**Requirements**:
- Don't split in the middle of sentences
- Ensure chunks are meaningful
- Maintain overlap between chunks
- Test with different chunk sizes

**Hints**:
- Use `DocumentSplitters.recursive()` with custom parameters
- Experiment with `maxSegmentSize` and `maxOverlapSize`
- Test with real documents

**Bonus**: Add paragraph-aware splitting

---

## 🎯 Challenge 4: Source Citation

**Difficulty**: ⭐⭐ Medium

**Task**: Enhance RAG responses to show which document and page the answer came from.

**Requirements**:
- Show document filename in response
- Include page number if available
- Show relevance score for each source
- Format as a clear citation

**Hints**:
- Use `ContentRetriever.findRelevant()` to get sources
- Parse metadata from segments
- Format response with source information

**Example Output**:
```
Answer: LangChain4j is a Java framework...

Sources:
- introduction.pdf (Page 3) - Relevance: 0.89
- getting-started.txt (Paragraph 1) - Relevance: 0.76
```

---

## 🎯 Challenge 5: Question Rephrasing

**Difficulty**: ⭐⭐⭐ Hard

**Task**: Improve retrieval by rephrasing user questions before searching.

**Requirements**:
- Use LLM to rephrase vague questions
- Generate multiple query variations
- Retrieve documents for all variations
- Combine results intelligently

**Hints**:
- Create a separate chat model call for rephrasing
- Use prompt: "Rephrase this question to be more specific: {question}"
- Merge and deduplicate retrieved segments

**Bonus**: Generate 3 different query angles and combine results

---

## 🎯 Challenge 6: Conversation Context in RAG

**Difficulty**: ⭐⭐⭐ Hard

**Task**: Make RAG remember previous questions in a conversation.

**Requirements**:
- Store conversation history per session
- Use previous Q&A pairs to improve retrieval
- Handle follow-up questions (e.g., "Tell me more about that")
- Clear context when conversation ends

**Hints**:
- Use `ChatMemory` with RAG
- Store `conversationId` with history
- Include previous messages in retrieval context

**Example**:
```
User: What is LangChain4j?
Bot: LangChain4j is a Java framework...

User: How do I install it? (← should understand "it" = LangChain4j)
Bot: To install LangChain4j, add this Maven dependency...
```

---

## 🎯 Challenge 7: Hybrid Search

**Difficulty**: ⭐⭐⭐ Hard

**Task**: Combine semantic search with keyword search for better results.

**Requirements**:
- Implement BM25 keyword search
- Implement embedding-based semantic search
- Combine results with weighted scoring
- Compare performance vs semantic-only

**Hints**:
- Use `EmbeddingStore` for semantic search
- Implement simple keyword matching separately
- Merge results with reciprocal rank fusion

**Bonus**: Make weights configurable (e.g., 70% semantic, 30% keyword)

---

## 🎯 Challenge 8: Re-ranking Results

**Difficulty**: ⭐⭐⭐⭐ Expert

**Task**: Add a re-ranking step to improve retrieval quality.

**Requirements**:
- Retrieve top 20 documents initially
- Use LLM to re-rank based on relevance
- Return only top 5 most relevant
- Measure improvement in answer quality

**Hints**:
- Retrieve more documents than needed
- Create prompt: "Rank these passages by relevance to: {question}"
- Parse LLM response to get rankings

**Bonus**: Implement cross-encoder re-ranking

---

## 🎯 Challenge 9: Document Versioning

**Difficulty**: ⭐⭐⭐⭐ Expert

**Task**: Support multiple versions of the same document.

**Requirements**:
- Track document versions (v1, v2, v3, etc.)
- Allow querying specific versions or "latest"
- Show version info in responses
- Handle version conflicts

**Hints**:
- Add version metadata to segments
- Filter by version during retrieval
- Store version history

**Example**:
```
Upload: manual_v1.pdf
Upload: manual_v2.pdf (updates previous)

Query: "What's new in version 2?"
Response: Compares v1 and v2...
```

---

## 🎯 Challenge 10: RAG with Images

**Difficulty**: ⭐⭐⭐⭐⭐ Expert

**Task**: Extend RAG to work with images in documents.

**Requirements**:
- Extract images from PDFs
- Generate image descriptions using vision model
- Index image descriptions as text
- Return relevant images in responses

**Hints**:
- Use Azure Computer Vision or GPT-4 Vision
- Store image data/URL with segments
- Include image references in answers

**Bonus**: Support OCR for text in images

---

## 📊 Challenge Tracking

Mark your progress:

- [ ] Challenge 1: Multi-Document Upload
- [ ] Challenge 2: Document Metadata
- [ ] Challenge 3: Improved Chunking Strategy
- [ ] Challenge 4: Source Citation
- [ ] Challenge 5: Question Rephrasing
- [ ] Challenge 6: Conversation Context in RAG
- [ ] Challenge 7: Hybrid Search
- [ ] Challenge 8: Re-ranking Results
- [ ] Challenge 9: Document Versioning
- [ ] Challenge 10: RAG with Images

---

## 🏆 Achievement Levels

- **Beginner**: Complete 1-3 challenges
- **Intermediate**: Complete 4-6 challenges
- **Advanced**: Complete 7-9 challenges
- **Expert**: Complete all 10 challenges

---

## 💡 Learning Resources

- [LangChain4j RAG Documentation](https://docs.langchain4j.dev/tutorials/rag)
- [Azure OpenAI Embeddings](https://learn.microsoft.com/azure/ai-services/openai/concepts/embeddings)
- [Vector Search Best Practices](https://www.pinecone.io/learn/vector-search/)

---

## 🤝 Share Your Solutions

Found an interesting solution? Share it:
- Open a pull request with your implementation
- Create a discussion thread
- Help others learn!

---

**Previous:** [02-rag README](README.md) | **Next:** [03-prompt-engineering](../03-prompt-engineering/README.md)
