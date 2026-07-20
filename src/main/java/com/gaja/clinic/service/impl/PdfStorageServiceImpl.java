package com.gaja.clinic.service.impl;

import com.gaja.clinic.service.PdfStorageService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
public class PdfStorageServiceImpl implements PdfStorageService {

    private static final DateTimeFormatter TIMESTAMP = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    @Value("${app.pdf.filesystem-path:src/main/resources/static/pdfs}")
    private String filesystemPath;

    private Path pdfRoot;

    @PostConstruct
    void init() throws IOException {
        pdfRoot = Path.of(System.getProperty("user.dir"), filesystemPath).toAbsolutePath().normalize();
        Files.createDirectories(pdfRoot);
        log.info("PDF storage directory: {}", pdfRoot);
    }

    @Override
    public String storePdf(byte[] pdfBytes, String baseFileName) {
        String safeName = baseFileName.replaceAll("[^a-zA-Z0-9_-]", "_");
        String fullName = safeName + "_" + LocalDateTime.now().format(TIMESTAMP) + ".pdf";
        Path target = pdfRoot.resolve(fullName);
        try {
            Files.write(target, pdfBytes);
            return "/pdfs/" + fullName;
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to store PDF: " + ex.getMessage(), ex);
        }
    }

    public Path resolvePhysicalPath(String webPath) {
        String relative = webPath.startsWith("/pdfs/") ? webPath.substring("/pdfs/".length()) : webPath;
        return pdfRoot.resolve(relative).normalize();
    }
}
