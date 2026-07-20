package com.gaja.clinic.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RoleBasedAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        String returnUrl = request.getParameter("returnUrl");
        if (returnUrl != null && !returnUrl.isBlank() && isLocalUrl(returnUrl)) {
            redirectStrategy.sendRedirect(request, response, returnUrl);
            return;
        }

        if (authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            String redirect = switch (userDetails.getRoleName()) {
                case "MainAdmin" -> "/main-admin/home";
                case "Doctor" -> "/doctor/patient/create";
                case "Pharmacy" -> "/pharmacy/prescription";
                default -> "/";
            };
            redirectStrategy.sendRedirect(request, response, redirect);
            return;
        }

        redirectStrategy.sendRedirect(request, response, "/");
    }

    private boolean isLocalUrl(String url) {
        return url.startsWith("/") && !url.startsWith("//");
    }
}
