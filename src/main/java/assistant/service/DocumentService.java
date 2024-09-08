package assistant.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentService {

    private final VectorStore vectorStore;

    private final OllamaEmbeddingModel embeddingModel;

    public boolean embedAndStore(MultipartFile[] files) {
        List<Document> documents = Arrays.stream(files)
                .map(this::createDocument)
                .map(this::embedDocument)
                .toList();
        vectorStore.add(documents);
        return true;
    }

    public Document embedDocument(Document document) {
        float[] embedding = embeddingModel.embed(document);
        document.setEmbedding(embedding);
        return document;
    }

    public Document createDocument(MultipartFile file) {
        String content = ContentExtractor.extractContent(file);
        return new Document(content);
    }

}
