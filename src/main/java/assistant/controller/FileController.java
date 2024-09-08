package assistant.controller;

import assistant.service.DocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/file")
public class FileController {

    private final DocumentService documentService;

    @PostMapping("/multiple")
    public ResponseEntity<String> upload(@RequestParam("files") MultipartFile[] files) {
        log.info("Received {} files", files.length);
        boolean success = documentService.embedAndStore(files);
        return success ? ResponseEntity.ok("Files uploaded successfully") : ResponseEntity.internalServerError().body("Failed to upload files");
    }

}
