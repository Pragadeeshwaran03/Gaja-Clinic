package com.gaja.clinic.service;

public interface PdfStorageService {

    String storePdf(byte[] pdfBytes, String baseFileName);

    java.nio.file.Path resolvePhysicalPath(String webPath);
}
