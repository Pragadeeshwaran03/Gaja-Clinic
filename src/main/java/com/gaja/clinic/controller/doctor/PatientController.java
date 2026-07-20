package com.gaja.clinic.controller.doctor;

import com.gaja.clinic.dto.PatientCreateDto;
import com.gaja.clinic.dto.PatientHistoryDto;
import com.gaja.clinic.entity.Patient;
import com.gaja.clinic.exception.DuplicateMobileException;
import com.gaja.clinic.security.CustomUserDetails;
import com.gaja.clinic.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
@RequestMapping("/doctor/patient")
@PreAuthorize("hasRole('Doctor')")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @GetMapping("/search")
    @ResponseBody
    public Map<String, Object> search(@RequestParam String mobile) {
        var results = patientService.searchByMobile(mobile);
        if (results.isEmpty()) {
            return Map.of("exists", false);
        }
        var patients = results.stream().map(r -> Map.of(
                "patientId", r.getPatientId(),
                "patientCode", r.getPatientCode(),
                "name", r.getName(),
                "historyUrl", r.getHistoryUrl()
        )).toList();
        return Map.of("exists", true, "patients", patients);
    }

    @GetMapping("/search-by-code")
    @ResponseBody
    public Map<String, Object> searchByCode(@RequestParam String code) {
        return patientService.searchByPatientCode(code)
                .map(result -> Map.<String, Object>of(
                        "exists", true,
                        "patientId", result.getPatientId(),
                        "patientCode", result.getPatientCode(),
                        "name", result.getName(),
                        "historyUrl", result.getHistoryUrl()
                ))
                .orElse(Map.of("exists", false));
    }

    @GetMapping("/search-unified")
    @ResponseBody
    public Map<String, Object> searchUnified(@RequestParam String q) {
        var results = patientService.searchByMobileOrName(q.trim());
        if (results.isEmpty()) {
            return Map.of("exists", false);
        }
        var patients = results.stream().map(r -> Map.of(
                "patientId",   r.getPatientId(),
                "patientCode", r.getPatientCode(),
                "name",        r.getName(),
                "historyUrl",  r.getHistoryUrl()
        )).toList();
        return Map.of("exists", true, "patients", patients);
    }

    @GetMapping("/create")
    public String createForm(@AuthenticationPrincipal CustomUserDetails user,
                             Model model) {
        model.addAttribute("user", user);
        model.addAttribute("patientCreateDto", new PatientCreateDto());
        return "doctor/patient-create";
    }

    @PostMapping("/create")
    public String create(@AuthenticationPrincipal CustomUserDetails user,
                         @Valid @ModelAttribute("patientCreateDto") PatientCreateDto dto,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        model.addAttribute("user", user);

        if (bindingResult.hasErrors()) {
            return "doctor/patient-create";
        }

        try {
            Patient patient = patientService.registerPatient(dto);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Patient " + patient.getName() + " registered successfully.");
            return "redirect:/doctor/consultation/create?patientId=" + patient.getId();
        } catch (DuplicateMobileException ex) {
            bindingResult.rejectValue("mobile", "duplicate.mobile",
                    "Mobile number already registered to " + ex.getExistingPatientName()
                            + " (" + ex.getExistingPatientCode() + "). "
                            + "Use search to start consultation or view history.");
            return "doctor/patient-create";
        }
    }

    @GetMapping("/history/{id}")
    public String history(@AuthenticationPrincipal CustomUserDetails user,
                          @PathVariable Integer id,
                          Model model) {
        PatientHistoryDto history = patientService.getPatientHistory(id);
        model.addAttribute("user", user);
        model.addAttribute("history", history);
        return "doctor/patient-history";
    }
}
