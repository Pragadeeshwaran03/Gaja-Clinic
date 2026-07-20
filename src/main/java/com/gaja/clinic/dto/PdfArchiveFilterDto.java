package com.gaja.clinic.dto;

import com.gaja.clinic.entity.PdfRecord;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PdfArchiveFilterDto {

    private String patientName;
    private String mobile;
    private String doctorName;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateFrom;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateTo;

    private List<PdfRecord> records = new ArrayList<>();
}
