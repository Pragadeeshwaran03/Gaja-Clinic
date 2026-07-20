package com.gaja.clinic.controller;

import com.gaja.clinic.dto.PharmacyProfileDto;
import com.gaja.clinic.model.ClinicSettings;
import com.gaja.clinic.service.SettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final SettingsService settingsService;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("title", "Gaja Clinic");
        return "home/index";
    }

    @GetMapping("/medicals")
    public String pharmacyProfile(Model model) {
        ClinicSettings settings = settingsService.getSettings();
        model.addAttribute("pharmacy", PharmacyProfileDto.from(settings));
        return "public/pharmacy-profile";
    }
}
