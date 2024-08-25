package json.spring_assistant.service;

import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.io.RandomAccessStreamCache;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final EmbeddingModel embeddingModel;

    private final VectorStore vectorStore;

    public boolean embedAndStore(MultipartFile[] files) {
        List<Document> documents = Arrays.stream(files)
                .map(this::getInputText)
                .map(this::embedDocument)
                .toList();
        vectorStore.add(documents);
        return true;
    }

    private Document embedDocument(Document document) {
        float[] embedding = embeddingModel.embed(document);
        document.setEmbedding(embedding);
        return document;
    }

    private Document getInputText(MultipartFile file) {
        if (file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No file uploaded");
        }

        if (!Objects.requireNonNull(file.getOriginalFilename()).endsWith(".pdf")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only PDF files are supported");
        }

        try (InputStream inputStream = file.getInputStream()) {
            // Load PDF document
            PDDocument document = new PDDocument((RandomAccessStreamCache.StreamCacheCreateFunction) inputStream);

            // Extract text from the PDF
            PDFTextStripper pdfStripper = new PDFTextStripper();
            String text = pdfStripper.getText(document);

            // Close the document
            document.close();

            return new Document(text);

        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to read file", e);
        }
    }


}
