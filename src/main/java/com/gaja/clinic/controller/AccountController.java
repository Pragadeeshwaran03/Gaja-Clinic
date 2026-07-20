package com.gaja.clinic.controller;

import com.gaja.clinic.dto.LoginDto;
import com.gaja.clinic.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {

    @GetMapping("/login")
    public String login(
            @RequestParam(value = "returnUrl", required = false) String returnUrl,
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            @AuthenticationPrincipal CustomUserDetails user,
            Model model) {

        if (user != null) {
            return "redirect:" + roleHomePath(user.getRoleName());
        }

        LoginDto loginDto = new LoginDto();
        loginDto.setReturnUrl(returnUrl);
        model.addAttribute("loginDto", loginDto);

        if (error != null) {
            model.addAttribute("errorMessage", "Invalid email/username or password.");
        }
        if (logout != null) {
            model.addAttribute("successMessage", "You have been logged out.");
        }

        return "account/login";
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "account/access-denied";
    }

    private String roleHomePath(String roleName) {
        return switch (roleName) {
            case "MainAdmin" -> "/main-admin/home";
            case "Doctor" -> "/doctor/patient/create";
            case "Pharmacy" -> "/pharmacy/prescription";
            default -> "/";
        };
    }
}
