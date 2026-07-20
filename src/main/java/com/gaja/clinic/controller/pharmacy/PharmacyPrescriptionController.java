package com.gaja.clinic.controller.pharmacy;

import com.gaja.clinic.entity.Prescription;
import com.gaja.clinic.security.CustomUserDetails;
import com.gaja.clinic.service.PharmacyPrescriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/pharmacy/prescription")
@PreAuthorize("hasRole('Pharmacy')")
@RequiredArgsConstructor
public class PharmacyPrescriptionController {

    private final PharmacyPrescriptionService pharmacyPrescriptionService;

    @GetMapping({"", "/"})
    public String index(@AuthenticationPrincipal CustomUserDetails user, Model model) {
        List<Prescription> pending = pharmacyPrescriptionService.listPendingPrescriptions();
        model.addAttribute("user", user);
        model.addAttribute("prescriptions", pending);
        return "pharmacy/pending-prescriptions";
    }

    @GetMapping("/{id}")
    public String details(@AuthenticationPrincipal CustomUserDetails user,
                          @PathVariable Integer id,
                          Model model) {
        Prescription prescription = pharmacyPrescriptionService.getPendingPrescriptionDetails(id);
        model.addAttribute("user", user);
        model.addAttribute("prescription", prescription);
        return "pharmacy/prescription-details";
    }
}
