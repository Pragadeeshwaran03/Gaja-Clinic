package com.gaja.clinic.controller.doctor;

import com.gaja.clinic.dto.ConsultationCreateDto;
import com.gaja.clinic.entity.Prescription;
import com.gaja.clinic.exception.ResourceNotFoundException;
import com.gaja.clinic.security.CustomUserDetails;
import com.gaja.clinic.service.ConsultationService;
import com.gaja.clinic.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/doctor/consultation")
@PreAuthorize("hasRole('Doctor')")
@RequiredArgsConstructor
public class ConsultationController {

    private final PatientService patientService;
    private final ConsultationService consultationService;

    @GetMapping("/create")
    public String createForm(@AuthenticationPrincipal CustomUserDetails user,
                             @RequestParam Integer patientId,
                             Model model) {
        var patient = patientService.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

        ConsultationCreateDto dto = new ConsultationCreateDto();
        dto.setPatientId(patientId);
        dto.setPatientName(patient.getName());

        model.addAttribute("user", user);
        model.addAttribute("consultationCreateDto", dto);
        return "doctor/consultation-create";
    }

    @PostMapping("/create")
    public String create(@AuthenticationPrincipal CustomUserDetails user,
                         @Valid @ModelAttribute ConsultationCreateDto dto,
                         BindingResult bindingResult,
                         Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("user", user);
            return "doctor/consultation-create";
        }

        Prescription prescription = consultationService.startConsultation(dto.getPatientId(), user.getUserId());
        return "redirect:/doctor/prescription/create?prescriptionId=" + prescription.getId();
    }
}
