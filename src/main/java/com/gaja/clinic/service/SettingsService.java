package com.gaja.clinic.service;

import com.gaja.clinic.model.ClinicSettings;
import org.springframework.web.multipart.MultipartFile;

public interface SettingsService {

    ClinicSettings getSettings();

    void saveSettings(ClinicSettings settings);

    void evictCache();

    String saveLogo(MultipartFile file);

    String saveDoctorSignature(MultipartFile file);

    String savePrescriptionTemplate(MultipartFile file);

    String savePharmacistPhoto(MultipartFile file);
}
