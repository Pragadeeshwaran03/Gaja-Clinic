package com.gaja.clinic.controller.doctor;

import com.gaja.clinic.dto.PrescriptionCreateDto;
import com.gaja.clinic.dto.PrescriptionItemDto;
import com.gaja.clinic.dto.PrescriptionSaveResponse;
import com.gaja.clinic.entity.Prescription;
import com.gaja.clinic.entity.PrescriptionItem;
import com.gaja.clinic.security.CustomUserDetails;
import com.gaja.clinic.service.PrescriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/doctor/prescription")
@PreAuthorize("hasRole('Doctor')")
@RequiredArgsConstructor
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    @GetMapping("/create")
    public String createForm(@AuthenticationPrincipal CustomUserDetails user,
                             @RequestParam Integer prescriptionId,
                             Model model) {
        Prescription prescription = prescriptionService.loadForEditing(prescriptionId);

        PrescriptionCreateDto dto = new PrescriptionCreateDto();
        dto.setPrescriptionId(prescription.getId());
        dto.setPatientCondition(prescription.getPatientCondition() != null ? prescription.getPatientCondition() : "");
        dto.setNotes(prescription.getNotes() != null ? prescription.getNotes() : "");

        List<PrescriptionItemDto> items = new ArrayList<>();
        if (prescription.getItems() != null && !prescription.getItems().isEmpty()) {
            for (PrescriptionItem item : prescription.getItems()) {
                PrescriptionItemDto itemDto = new PrescriptionItemDto();
                itemDto.setMedicineName(item.getMedicineName());
                itemDto.setDosage(item.getDosage() != null ? item.getDosage() : "");
                itemDto.setQuantity(item.getQuantity() != null ? item.getQuantity() : 1);
                itemDto.setDuration(item.getDuration() != null ? item.getDuration() : "");
                itemDto.setInstructions(item.getInstructions() != null ? item.getInstructions() : "");
                items.add(itemDto);
            }
        } else {
            items.add(new PrescriptionItemDto());
        }
        dto.setItems(items);

        model.addAttribute("user", user);
        model.addAttribute("prescription", prescription);
        model.addAttribute("prescriptionCreateDto", dto);
        return "doctor/prescription-create";
    }

    @PostMapping("/create")
    @ResponseBody
    public PrescriptionSaveResponse create(@ModelAttribute PrescriptionCreateDto dto) {
        try {
            return prescriptionService.saveItems(dto);
        } catch (Exception ex) {
            return PrescriptionSaveResponse.error("Error saving prescription: " + ex.getMessage());
        }
    }

    @GetMapping("/success")
    public String success(@AuthenticationPrincipal CustomUserDetails user,
                          @RequestParam Integer prescriptionId,
                          Model model) {
        Prescription prescription = prescriptionService.loadWithDetails(prescriptionId);
        model.addAttribute("user", user);
        model.addAttribute("prescription", prescription);
        return "doctor/prescription-success";
    }
}
