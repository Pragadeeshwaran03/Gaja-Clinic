package com.gaja.clinic.service;

import com.gaja.clinic.dto.BillCreateDto;
import com.gaja.clinic.dto.BillSaveResponse;
import com.gaja.clinic.dto.BillSummaryDto;
import com.gaja.clinic.entity.Bill;

public interface BillService {

    BillCreateDto buildBillForm(Integer prescriptionId);

    BillSaveResponse processBill(BillCreateDto dto, String staffName);

    BillSummaryDto getBillSummary(Integer billId);

    Bill getBillForDownload(Integer billId);

    void tryGenerateAndStorePdf(Integer billId);
}
