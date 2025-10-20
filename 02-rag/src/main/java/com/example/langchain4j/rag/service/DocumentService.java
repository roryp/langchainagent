package com.example.langchain4j.rag.service;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for processing documents (parsing and chunking).
 */
@Service
public class DocumentService {

    private static final Logger log = LoggerFactory.getLogger(DocumentService.class);
    
    private static final int DEFAULT_CHUNK_SIZE = 300;
    private static final int DEFAULT_CHUNK_OVERLAP = 30;
    
    private final DocumentSplitter splitter;

    public DocumentService() {
        // Configure text splitter
        this.splitter = DocumentSplitters.recursive(
            DEFAULT_CHUNK_SIZE,
            DEFAULT_CHUNK_OVERLAP
        );
    }

    /**
     * Process a document from an input stream.
     *
     * @param inputStream document input stream
     * @param filename filename with extension
     * @return processed document with segments
     */
    public ProcessedDocument processDocument(InputStream inputStream, String filename) {
        log.info("Processing document: {}", filename);
        
        String documentId = UUID.randomUUID().toString();
        
        try {
            String content;
            
            // Parse based on file type
            if (filename.toLowerCase().endsWith(".pdf")) {
                content = parsePdf(inputStream);
            } else {
                // Default to text file parsing
                content = parseText(inputStream);
            }
            
            // Create document with metadata
            Metadata metadata = new Metadata();
            metadata.put("filename", filename);
            metadata.put("documentId", documentId);
            
            Document document = Document.from(content, metadata);
            
            // Split into segments
            List<TextSegment> segments = splitter.split(document);
            
            log.info("Document '{}' processed into {} segments", filename, segments.size());
            
            return new ProcessedDocument(documentId, filename, segments);
            
        } catch (Exception e) {
            log.error("Failed to process document: {}", filename, e);
            throw new RuntimeException("Document processing failed: " + e.getMessage(), e);
        }
    }
    
    /**
     * Parse PDF document.
     */
    private String parsePdf(InputStream inputStream) throws Exception {
        try (PDDocument document = PDDocument.load(inputStream)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }
    
    /**
     * Parse text document.
     */
    private String parseText(InputStream inputStream) {
        return new BufferedReader(
            new InputStreamReader(inputStream, StandardCharsets.UTF_8))
            .lines()
            .collect(Collectors.joining("\n"));
    }

    /**
     * Processed document result.
     */
    public record ProcessedDocument(
        String documentId,
        String filename,
        List<TextSegment> segments
    ) {
    }
}
