package com.gaja.clinic.controller.admin;

import com.gaja.clinic.dto.DashboardDto;
import com.gaja.clinic.security.CustomUserDetails;
import com.gaja.clinic.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/main-admin")
@PreAuthorize("hasRole('MainAdmin')")
@RequiredArgsConstructor
public class AdminHomeController {

    private final DashboardService dashboardService;

    @GetMapping({"/home", ""})
    public String home(@AuthenticationPrincipal CustomUserDetails user, Model model) {
        DashboardDto dashboard = dashboardService.getDashboard();
        model.addAttribute("user", user);
        model.addAttribute("dashboard", dashboard);
        return "admin/home";
    }
}
