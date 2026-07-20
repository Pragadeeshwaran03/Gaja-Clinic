package com.gaja.clinic.service;

public interface ExcelExportService {

    byte[] exportPatients();

    byte[] exportBills();

    byte[] exportPdfArchive();
}
