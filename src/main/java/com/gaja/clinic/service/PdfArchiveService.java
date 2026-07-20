package com.gaja.clinic.service;

import com.gaja.clinic.dto.PdfArchiveFilterDto;
import com.gaja.clinic.entity.PdfRecord;

import java.util.List;

public interface PdfArchiveService {

    List<PdfRecord> search(PdfArchiveFilterDto filter);

    PdfRecord getById(Integer id);
}
