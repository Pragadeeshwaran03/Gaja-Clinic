package com.gaja.clinic.controller.admin;

import com.gaja.clinic.dto.SettingsDto;
import com.gaja.clinic.model.ClinicSettings;
import com.gaja.clinic.security.CustomUserDetails;
import com.gaja.clinic.service.SettingsService;
import com.gaja.clinic.util.NetworkUrlHelper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/main-admin/settings")
@PreAuthorize("hasRole('MainAdmin')")
@RequiredArgsConstructor
@Slf4j
public class SettingsController {

    private final SettingsService settingsService;

    @Value("${app.server-port:5226}")
    private int serverPort;

    @Value("${app.use-sslip-for-local-share:true}")
    private boolean useSslip;

    @GetMapping
    public String index(@AuthenticationPrincipal CustomUserDetails user, Model model) {
        ClinicSettings settings = settingsService.getSettings();
        String suggested = NetworkUrlHelper.buildSuggestedLocalUrl(serverPort, useSslip);
        SettingsDto dto = SettingsDto.from(settings, suggested);
        model.addAttribute("user", user);
        model.addAttribute("settingsDto", dto);
        return "admin/settings";
    }

    @PostMapping
    @ResponseBody
    public Map<String, Object> save(@Valid @ModelAttribute SettingsDto settingsDto,
                                    BindingResult bindingResult,
                                    @RequestParam(value = "logoFile", required = false) MultipartFile logoFile,
                                    @RequestParam(value = "doctorSignatureFile", required = false) MultipartFile doctorSignatureFile,
                                    @RequestParam(value = "prescriptionTemplateFile", required = false) MultipartFile prescriptionTemplateFile,
                                    @RequestParam(value = "pharmacistPhotoFile", required = false) MultipartFile pharmacistPhotoFile) {
        Map<String, Object> response = new LinkedHashMap<>();
        try {
            if (bindingResult.hasErrors()) {
                String errors = bindingResult.getAllErrors().stream()
                        .map(e -> e.getDefaultMessage())
                        .collect(Collectors.joining(" | "));
                response.put("success", false);
                response.put("message", errors);
                return response;
            }

            ClinicSettings current = settingsService.getSettings();
            ClinicSettings updated = settingsDto.toClinicSettings();
            updated.setLogoPath(current.getLogoPath());
            updated.setDoctorSignaturePath(current.getDoctorSignaturePath());
            updated.setPrescriptionTemplatePath(current.getPrescriptionTemplatePath());
            updated.setPharmacistPhotoPath(current.getPharmacistPhotoPath());

            if (logoFile != null && !logoFile.isEmpty()) {
                updated.setLogoPath(settingsService.saveLogo(logoFile));
            }
            if (doctorSignatureFile != null && !doctorSignatureFile.isEmpty()) {
                updated.setDoctorSignaturePath(settingsService.saveDoctorSignature(doctorSignatureFile));
            }
            if (prescriptionTemplateFile != null && !prescriptionTemplateFile.isEmpty()) {
                updated.setPrescriptionTemplatePath(settingsService.savePrescriptionTemplate(prescriptionTemplateFile));
            }
            if (pharmacistPhotoFile != null && !pharmacistPhotoFile.isEmpty()) {
                updated.setPharmacistPhotoPath(settingsService.savePharmacistPhoto(pharmacistPhotoFile));
            }

            settingsService.saveSettings(updated);
            log.info("Clinic settings updated");

            response.put("success", true);
            response.put("message", "Settings saved successfully.");
            response.put("logoPath", updated.getLogoPath());
            response.put("doctorSignaturePath", updated.getDoctorSignaturePath());
            response.put("prescriptionTemplatePath", updated.getPrescriptionTemplatePath());
            response.put("pharmacistPhotoPath", updated.getPharmacistPhotoPath());
            return response;
        } catch (Exception ex) {
            log.error("Failed to save clinic settings", ex);
            response.put("success", false);
            response.put("message", "Failed to save settings: " + ex.getMessage());
            return response;
        }
    }
}
