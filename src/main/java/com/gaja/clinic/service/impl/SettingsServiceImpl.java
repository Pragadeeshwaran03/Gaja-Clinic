package com.gaja.clinic.service.impl;

import com.gaja.clinic.config.CacheConfig;
import com.gaja.clinic.entity.Setting;
import com.gaja.clinic.model.ClinicSettings;
import com.gaja.clinic.repository.SettingRepository;
import com.gaja.clinic.service.SettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SettingsServiceImpl implements SettingsService {

    private final SettingRepository settingRepository;

    @Value("${app.clinic.name:Gaja Clinic & Medicals}")
    private String defaultClinicName;

    @Value("${app.clinic.tagline:Your Health, Our Priority}")
    private String defaultTagline;

    @Value("${app.clinic.address:}")
    private String defaultAddress;

    @Value("${app.clinic.phone:}")
    private String defaultPhone;

    @Value("${app.public-base-url:}")
    private String defaultPublicBaseUrl;

    @Value("${spring.mail.host:}")
    private String defaultSmtpHost;

    @Value("${spring.mail.port:587}")
    private int defaultSmtpPort;

    @Value("${spring.mail.username:}")
    private String defaultSmtpUser;

    @Value("${spring.mail.password:}")
    private String defaultSmtpPassword;

    @Value("${app.static.filesystem-path:src/main/resources/static}")
    private String staticFilesystemPath;

    private Path staticRoot;

    @jakarta.annotation.PostConstruct
    void initStaticRoot() throws IOException {
        staticRoot = Path.of(System.getProperty("user.dir"), staticFilesystemPath).toAbsolutePath().normalize();
        Files.createDirectories(staticRoot.resolve("images"));
        Files.createDirectories(staticRoot.resolve("uploads/prescription"));
    }

    @Override
    @Cacheable(CacheConfig.CLINIC_SETTINGS_CACHE)
    @Transactional(readOnly = true)
    public ClinicSettings getSettings() {
        Map<String, String> dict = settingRepository.findAll().stream()
                .collect(Collectors.toMap(Setting::getKey, s -> s.getValue() != null ? s.getValue() : "",
                        (a, b) -> b, LinkedHashMap::new));

        ClinicSettings settings = new ClinicSettings();
        settings.setClinicName(get(dict, "ClinicName", defaultClinicName));
        settings.setTagline(get(dict, "Tagline", defaultTagline));
        settings.setAddress(get(dict, "Address", defaultAddress));
        settings.setPhone(get(dict, "Phone", defaultPhone));
        settings.setEmail(get(dict, "Email", "info@gajaclinic.local"));
        settings.setLogoPath(get(dict, "LogoPath", "/images/logo.svg"));
        settings.setConsultationFeeDefault(parseDecimal(get(dict, "ConsultationFeeDefault", "200.00")));
        settings.setSmtpHost(get(dict, "SmtpHost", defaultSmtpHost));
        settings.setSmtpPort(parseInt(get(dict, "SmtpPort", String.valueOf(defaultSmtpPort)), defaultSmtpPort));
        settings.setSmtpEnableSsl(!"false".equalsIgnoreCase(get(dict, "SmtpEnableSsl", "true")));
        settings.setSmtpUser(get(dict, "SmtpUser", defaultSmtpUser));
        settings.setSmtpPassword(get(dict, "SmtpPassword", defaultSmtpPassword));
        settings.setSmtpFromEmail(get(dict, "SmtpFromEmail", defaultSmtpUser));
        settings.setSmtpFromName(get(dict, "SmtpFromName", defaultClinicName));
        settings.setDoctorDisplayName(get(dict, "DoctorDisplayName", ""));
        settings.setDoctorQualification(get(dict, "DoctorQualification", ""));
        settings.setDoctorRegistrationNo(get(dict, "DoctorRegistrationNo", ""));
        settings.setDoctorSpecialization(get(dict, "DoctorSpecialization", ""));
        settings.setConsultingHours(get(dict, "ConsultingHours", ""));
        settings.setPrescriptionFooterMessage(get(dict, "PrescriptionFooterMessage",
                "Bring the Prescription for the next visit."));
        settings.setDoctorSignaturePath(get(dict, "DoctorSignaturePath", ""));
        settings.setPrescriptionTemplatePath(get(dict, "PrescriptionTemplatePath", ""));
        settings.setPublicBaseUrl(get(dict, "PublicBaseUrl", defaultPublicBaseUrl));
        settings.setPharmacistDisplayName(get(dict, "PharmacistDisplayName", "KARTHIK. S"));
        settings.setPharmacistQualification(get(dict, "PharmacistQualification", "B. Pharm"));
        settings.setPharmacistDesignation(get(dict, "PharmacistDesignation", "Registered Pharmacist"));
        settings.setPharmacistRegistrationNo(get(dict, "PharmacistRegistrationNo", "49846 A1"));
        settings.setPharmacistExperience(get(dict, "PharmacistExperience", "—"));
        settings.setPharmacistLanguages(get(dict, "PharmacistLanguages", "—"));
        settings.setPharmacistAvailability(get(dict, "PharmacistAvailability", "—"));
        settings.setPharmacistAbout(get(dict, "PharmacistAbout", ""));
        settings.setPharmacistPhotoPath(get(dict, "PharmacistPhotoPath", "/images/pharmacist.jpg"));
        settings.setPharmacistStatYears(get(dict, "PharmacistStatYears", "5"));
        settings.setPharmacistStatPatients(get(dict, "PharmacistStatPatients", "3000"));
        settings.setPharmacistStatPrescriptions(get(dict, "PharmacistStatPrescriptions", "10000"));
        settings.setPharmacistStatSatisfaction(get(dict, "PharmacistStatSatisfaction", "98"));
        settings.setPharmacistBusinessHours(get(dict, "PharmacistBusinessHours", "Mon – Sat, Evening Consultation"));
        return settings;
    }

    @Override
    @CacheEvict(value = CacheConfig.CLINIC_SETTINGS_CACHE, allEntries = true)
    @Transactional
    public void saveSettings(ClinicSettings settings) {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("ClinicName", settings.getClinicName());
        map.put("Tagline", settings.getTagline());
        map.put("Address", settings.getAddress());
        map.put("Phone", settings.getPhone());
        map.put("Email", settings.getEmail());
        map.put("LogoPath", settings.getLogoPath());
        map.put("ConsultationFeeDefault", settings.getConsultationFeeDefault().toPlainString());
        map.put("SmtpHost", settings.getSmtpHost());
        map.put("SmtpPort", String.valueOf(settings.getSmtpPort()));
        map.put("SmtpEnableSsl", String.valueOf(settings.isSmtpEnableSsl()));
        map.put("SmtpUser", settings.getSmtpUser());
        map.put("SmtpPassword", settings.getSmtpPassword());
        map.put("SmtpFromEmail", settings.getSmtpFromEmail());
        map.put("SmtpFromName", settings.getSmtpFromName());
        map.put("DoctorDisplayName", settings.getDoctorDisplayName());
        map.put("DoctorQualification", settings.getDoctorQualification());
        map.put("DoctorRegistrationNo", settings.getDoctorRegistrationNo());
        map.put("DoctorSpecialization", settings.getDoctorSpecialization());
        map.put("ConsultingHours", settings.getConsultingHours());
        map.put("PrescriptionFooterMessage", settings.getPrescriptionFooterMessage());
        map.put("DoctorSignaturePath", settings.getDoctorSignaturePath());
        map.put("PrescriptionTemplatePath", settings.getPrescriptionTemplatePath());
        map.put("PublicBaseUrl", settings.getPublicBaseUrl());
        map.put("PharmacistDisplayName", settings.getPharmacistDisplayName());
        map.put("PharmacistQualification", settings.getPharmacistQualification());
        map.put("PharmacistDesignation", settings.getPharmacistDesignation());
        map.put("PharmacistRegistrationNo", settings.getPharmacistRegistrationNo());
        map.put("PharmacistExperience", settings.getPharmacistExperience());
        map.put("PharmacistLanguages", settings.getPharmacistLanguages());
        map.put("PharmacistAvailability", settings.getPharmacistAvailability());
        map.put("PharmacistAbout", settings.getPharmacistAbout());
        map.put("PharmacistPhotoPath", settings.getPharmacistPhotoPath());
        map.put("PharmacistStatYears", settings.getPharmacistStatYears());
        map.put("PharmacistStatPatients", settings.getPharmacistStatPatients());
        map.put("PharmacistStatPrescriptions", settings.getPharmacistStatPrescriptions());
        map.put("PharmacistStatSatisfaction", settings.getPharmacistStatSatisfaction());
        map.put("PharmacistBusinessHours", settings.getPharmacistBusinessHours());

        List<String> keys = List.copyOf(map.keySet());
        Map<String, Setting> existing = settingRepository.findByKeyIn(keys).stream()
                .collect(Collectors.toMap(Setting::getKey, s -> s));

        for (Map.Entry<String, String> entry : map.entrySet()) {
            Setting row = existing.get(entry.getKey());
            if (row != null) {
                row.setValue(entry.getValue());
            } else {
                Setting setting = new Setting();
                setting.setKey(entry.getKey());
                setting.setValue(entry.getValue());
                settingRepository.save(setting);
            }
        }
    }

    @Override
    @CacheEvict(value = CacheConfig.CLINIC_SETTINGS_CACHE, allEntries = true)
    public void evictCache() {
        // cache eviction handled by annotation
    }

    @Override
    @CacheEvict(value = CacheConfig.CLINIC_SETTINGS_CACHE, allEntries = true)
    @Transactional
    public String saveLogo(MultipartFile file) {
        String webPath = saveImageUpload(file, "images", "logo");
        ClinicSettings settings = getSettings();
        settings.setLogoPath(webPath);
        saveSettings(settings);
        return webPath;
    }

    @Override
    @CacheEvict(value = CacheConfig.CLINIC_SETTINGS_CACHE, allEntries = true)
    @Transactional
    public String saveDoctorSignature(MultipartFile file) {
        String webPath = saveImageUpload(file, "uploads/prescription", "doctor-signature");
        ClinicSettings settings = getSettings();
        settings.setDoctorSignaturePath(webPath);
        saveSettings(settings);
        return webPath;
    }

    @Override
    @CacheEvict(value = CacheConfig.CLINIC_SETTINGS_CACHE, allEntries = true)
    @Transactional
    public String savePrescriptionTemplate(MultipartFile file) {
        String webPath = saveImageUpload(file, "uploads/prescription", "prescription-template");
        ClinicSettings settings = getSettings();
        settings.setPrescriptionTemplatePath(webPath);
        saveSettings(settings);
        return webPath;
    }

    @Override
    @CacheEvict(value = CacheConfig.CLINIC_SETTINGS_CACHE, allEntries = true)
    @Transactional
    public String savePharmacistPhoto(MultipartFile file) {
        String webPath = saveImageUpload(file, "images", "pharmacist");
        ClinicSettings settings = getSettings();
        settings.setPharmacistPhotoPath(webPath);
        saveSettings(settings);
        return webPath;
    }

    private String saveImageUpload(MultipartFile file, String subDir, String baseName) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        String original = file.getOriginalFilename();
        String ext = original != null && original.contains(".")
                ? original.substring(original.lastIndexOf('.')).toLowerCase()
                : ".png";
        if (!ext.equals(".png") && !ext.equals(".jpg") && !ext.equals(".jpeg") && !ext.equals(".webp")) {
            throw new IllegalArgumentException("File must be PNG, JPG, or WEBP.");
        }

        try {
            Path dir = staticRoot.resolve(subDir);
            Files.createDirectories(dir);
            String fileName = baseName + ext;
            Path target = dir.resolve(fileName);
            Files.write(target, file.getBytes());
            return "/" + subDir.replace('\\', '/') + "/" + fileName;
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to save upload: " + ex.getMessage(), ex);
        }
    }

    private static String get(Map<String, String> dict, String key, String fallback) {
        String val = dict.get(key);
        return val != null && !val.isBlank() ? val : fallback;
    }

    private static BigDecimal parseDecimal(String value) {
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException ex) {
            return new BigDecimal("200.00");
        }
    }

    private static int parseInt(String value, int fallback) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            return fallback;
        }
    }
}
