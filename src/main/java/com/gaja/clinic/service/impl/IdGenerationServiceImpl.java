package com.gaja.clinic.service.impl;

import com.gaja.clinic.repository.BillRepository;
import com.gaja.clinic.repository.PatientRepository;
import com.gaja.clinic.repository.PrescriptionRepository;
import com.gaja.clinic.service.IdGenerationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class IdGenerationServiceImpl implements IdGenerationService {

    private final PatientRepository patientRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final BillRepository billRepository;

    @Override
    @Transactional(readOnly = true)
    public String generatePatientId() {
        return nextCode("PAT", 3,
                patientRepository.findTopByOrderByIdDesc()
                        .map(p -> p.getPatientCode())
                        .orElse(null));
    }

    @Override
    @Transactional(readOnly = true)
    public String generatePrescriptionNumber() {
        return nextCode("PRES", 4,
                prescriptionRepository.findTopByOrderByIdDesc()
                        .map(p -> p.getPrescriptionNumber())
                        .orElse(null));
    }

    @Override
    @Transactional(readOnly = true)
    public String generateBillNo() {
        return nextCode("BILL", 4,
                billRepository.findTopByOrderByIdDesc()
                        .map(b -> b.getBillNo())
                        .orElse(null));
    }

    private String nextCode(String prefix, int prefixLength, String lastCode) {
        int next = 1;
        if (lastCode != null && !lastCode.isBlank() && lastCode.startsWith(prefix)) {
            String numPart = lastCode.substring(prefixLength);
            try {
                next = Integer.parseInt(numPart) + 1;
            } catch (NumberFormatException ignored) {
                next = 1;
            }
        }
        return prefix + String.format("%05d", next);
    }
}
