package com.gaja.clinic.service.impl;

import com.gaja.clinic.model.ClinicSettings;
import com.gaja.clinic.service.AppUrlService;
import com.gaja.clinic.service.SettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Service
@RequiredArgsConstructor
public class AppUrlServiceImpl implements AppUrlService {

    private final SettingsService settingsService;

    @Value("${app.public-base-url:}")
    private String configuredPublicBaseUrl;

    @Value("${app.server-port:5226}")
    private int serverPort;

    @Value("${app.use-sslip-for-local-share:true}")
    private boolean useSslipForLocalShare;

    @Override
    public String getPublicBaseUrl() {
        ClinicSettings settings = settingsService.getSettings();
        if (settings.getPublicBaseUrl() != null && !settings.getPublicBaseUrl().isBlank()) {
            return trimTrailingSlash(settings.getPublicBaseUrl());
        }
        if (configuredPublicBaseUrl != null && !configuredPublicBaseUrl.isBlank()) {
            return trimTrailingSlash(configuredPublicBaseUrl);
        }
        return buildSuggestedLocalUrl();
    }

    @Override
    public String getBillShareUrl(int billId) {
        return getPublicBaseUrl() + "/share/pdf/" + billId;
    }

    @Override
    public String getAbsoluteUrl(String webPath) {
        String path = webPath.startsWith("/") ? webPath : "/" + webPath;
        return getPublicBaseUrl() + path;
    }

    private String buildSuggestedLocalUrl() {
        try {
            String host = InetAddress.getLocalHost().getHostAddress();
            if (useSslipForLocalShare) {
                host = host.replace('.', '-') + ".sslip.io";
            }
            return "http://" + host + ":" + serverPort;
        } catch (UnknownHostException ex) {
            return "http://localhost:" + serverPort;
        }
    }

    private static String trimTrailingSlash(String url) {
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }
}
