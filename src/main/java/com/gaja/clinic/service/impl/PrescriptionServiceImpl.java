package com.gaja.clinic.service.impl;

import com.gaja.clinic.dto.PrescriptionCreateDto;
import com.gaja.clinic.dto.PrescriptionItemDto;
import com.gaja.clinic.dto.PrescriptionSaveResponse;
import com.gaja.clinic.entity.Prescription;
import com.gaja.clinic.entity.PrescriptionItem;
import com.gaja.clinic.entity.PrescriptionStatus;
import com.gaja.clinic.exception.ResourceNotFoundException;
import com.gaja.clinic.repository.PrescriptionItemRepository;
import com.gaja.clinic.repository.PrescriptionRepository;
import com.gaja.clinic.service.PrescriptionService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PrescriptionServiceImpl implements PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final PrescriptionItemRepository prescriptionItemRepository;
    private final Validator validator;

    @Override
    @Transactional(readOnly = true)
    public Prescription loadForEditing(Integer prescriptionId) {
        return prescriptionRepository.findPendingDetailsById(prescriptionId, PrescriptionStatus.PENDING_PHARMACY)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription not found or already processed"));
    }

    @Override
    @Transactional
    public PrescriptionSaveResponse saveItems(PrescriptionCreateDto dto) {
        List<PrescriptionItemDto> items = dto.getItems() == null ? List.of() : dto.getItems().stream()
                .filter(item -> item.getMedicineName() != null && !item.getMedicineName().isBlank())
                .toList();

        if (items.isEmpty()) {
            return PrescriptionSaveResponse.error("At least one medicine with a name is required.");
        }

        List<String> validationErrors = new ArrayList<>();
        for (PrescriptionItemDto item : items) {
            for (ConstraintViolation<PrescriptionItemDto> violation : validator.validate(item)) {
                validationErrors.add(violation.getMessage());
            }
        }
        if (!validationErrors.isEmpty()) {
            return PrescriptionSaveResponse.error(String.join("; ", validationErrors));
        }

        Prescription prescription = prescriptionRepository
                .findByIdAndStatus(dto.getPrescriptionId(), PrescriptionStatus.PENDING_PHARMACY)
                .orElse(null);

        if (prescription == null) {
            return PrescriptionSaveResponse.error("Prescription not found or already processed.");
        }

        prescription.setPatientCondition(dto.getPatientCondition() != null && !dto.getPatientCondition().isBlank()
                ? dto.getPatientCondition().trim() : null);
        prescription.setNotes(dto.getNotes() != null && !dto.getNotes().isBlank() ? dto.getNotes().trim() : null);
        prescriptionRepository.save(prescription);

        for (PrescriptionItemDto itemDto : items) {
            PrescriptionItem item = new PrescriptionItem();
            item.setPrescriptionId(prescription.getId());
            item.setMedicineName(itemDto.getMedicineName().trim());
            item.setDosage(trimOrNull(itemDto.getDosage()));
            item.setDuration(trimOrNull(itemDto.getDuration()));
            item.setInstructions(trimOrNull(itemDto.getInstructions()));
            item.setQuantity(itemDto.getQuantity());
            prescriptionItemRepository.save(item);
        }

        String redirectUrl = "/doctor/prescription/success?prescriptionId=" + prescription.getId();
        return PrescriptionSaveResponse.ok(redirectUrl);
    }

    @Override
    @Transactional(readOnly = true)
    public Prescription loadWithDetails(Integer prescriptionId) {
        return prescriptionRepository.findByIdWithItemsAndPatient(prescriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription not found"));
    }

    private static String trimOrNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
