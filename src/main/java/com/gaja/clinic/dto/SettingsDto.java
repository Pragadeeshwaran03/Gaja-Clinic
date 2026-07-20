package com.gaja.clinic.dto;

import com.gaja.clinic.model.ClinicSettings;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class SettingsDto {

    @NotBlank(message = "Clinic name is required")
    @Size(max = 200)
    private String clinicName;

    @Size(max = 200)
    private String tagline;

    @Size(max = 500)
    private String address;

    @Size(max = 30)
    private String phone;

    @Email(message = "Invalid email")
    @Size(max = 150)
    private String email;

    private String logoPath;

    @DecimalMin(value = "0.0", message = "Consultation fee must be non-negative")
    @DecimalMax(value = "100000.0")
    private BigDecimal consultationFeeDefault;

    @Size(max = 200)
    private String smtpHost;

    @Min(1)
    @Max(65535)
    private int smtpPort = 587;

    private boolean smtpEnableSsl = true;

    @Size(max = 200)
    private String smtpUser;

    @Size(max = 200)
    private String smtpPassword;

    @Email(message = "Invalid from email")
    @Size(max = 200)
    private String smtpFromEmail;

    @Size(max = 200)
    private String smtpFromName;

    @Size(max = 200)
    private String doctorDisplayName;

    @Size(max = 200)
    private String doctorQualification;

    @Size(max = 100)
    private String doctorRegistrationNo;

    @Size(max = 200)
    private String doctorSpecialization;

    @Size(max = 200)
    private String consultingHours;

    @Size(max = 300)
    private String prescriptionFooterMessage;

    private String doctorSignaturePath;
    private String prescriptionTemplatePath;

    @Size(max = 300)
    private String publicBaseUrl;

    private String suggestedServerUrl;

    /* Pharmacy public profile */
    @Size(max = 200)
    private String pharmacistDisplayName;

    @Size(max = 100)
    private String pharmacistQualification;

    @Size(max = 200)
    private String pharmacistDesignation;

    @Size(max = 100)
    private String pharmacistRegistrationNo;

    @Size(max = 100)
    private String pharmacistExperience;

    @Size(max = 200)
    private String pharmacistLanguages;

    @Size(max = 200)
    private String pharmacistAvailability;

    @Size(max = 2000)
    private String pharmacistAbout;

    private String pharmacistPhotoPath;

    @Size(max = 20)
    private String pharmacistStatYears;

    @Size(max = 20)
    private String pharmacistStatPatients;

    @Size(max = 20)
    private String pharmacistStatPrescriptions;

    @Size(max = 20)
    private String pharmacistStatSatisfaction;

    @Size(max = 200)
    private String pharmacistBusinessHours;

    public static SettingsDto from(ClinicSettings s, String suggestedServerUrl) {
        SettingsDto dto = new SettingsDto();
        dto.setClinicName(s.getClinicName());
        dto.setTagline(s.getTagline());
        dto.setAddress(s.getAddress());
        dto.setPhone(s.getPhone());
        dto.setEmail(s.getEmail());
        dto.setLogoPath(s.getLogoPath());
        dto.setConsultationFeeDefault(s.getConsultationFeeDefault());
        dto.setSmtpHost(s.getSmtpHost());
        dto.setSmtpPort(s.getSmtpPort());
        dto.setSmtpEnableSsl(s.isSmtpEnableSsl());
        dto.setSmtpUser(s.getSmtpUser());
        dto.setSmtpPassword(s.getSmtpPassword());
        dto.setSmtpFromEmail(s.getSmtpFromEmail());
        dto.setSmtpFromName(s.getSmtpFromName());
        dto.setDoctorDisplayName(s.getDoctorDisplayName());
        dto.setDoctorQualification(s.getDoctorQualification());
        dto.setDoctorRegistrationNo(s.getDoctorRegistrationNo());
        dto.setDoctorSpecialization(s.getDoctorSpecialization());
        dto.setConsultingHours(s.getConsultingHours());
        dto.setPrescriptionFooterMessage(s.getPrescriptionFooterMessage());
        dto.setDoctorSignaturePath(s.getDoctorSignaturePath());
        dto.setPrescriptionTemplatePath(s.getPrescriptionTemplatePath());
        dto.setPublicBaseUrl(s.getPublicBaseUrl());
        dto.setSuggestedServerUrl(suggestedServerUrl);
        dto.setPharmacistDisplayName(s.getPharmacistDisplayName());
        dto.setPharmacistQualification(s.getPharmacistQualification());
        dto.setPharmacistDesignation(s.getPharmacistDesignation());
        dto.setPharmacistRegistrationNo(s.getPharmacistRegistrationNo());
        dto.setPharmacistExperience(s.getPharmacistExperience());
        dto.setPharmacistLanguages(s.getPharmacistLanguages());
        dto.setPharmacistAvailability(s.getPharmacistAvailability());
        dto.setPharmacistAbout(s.getPharmacistAbout());
        dto.setPharmacistPhotoPath(s.getPharmacistPhotoPath());
        dto.setPharmacistStatYears(s.getPharmacistStatYears());
        dto.setPharmacistStatPatients(s.getPharmacistStatPatients());
        dto.setPharmacistStatPrescriptions(s.getPharmacistStatPrescriptions());
        dto.setPharmacistStatSatisfaction(s.getPharmacistStatSatisfaction());
        dto.setPharmacistBusinessHours(s.getPharmacistBusinessHours());
        return dto;
    }

    public ClinicSettings toClinicSettings() {
        ClinicSettings s = new ClinicSettings();
        s.setClinicName(clinicName);
        s.setTagline(tagline);
        s.setAddress(address);
        s.setPhone(phone);
        s.setEmail(email);
        s.setLogoPath(logoPath != null ? logoPath : "/images/logo.svg");
        s.setConsultationFeeDefault(consultationFeeDefault != null ? consultationFeeDefault : new BigDecimal("200.00"));
        s.setSmtpHost(smtpHost != null ? smtpHost : "");
        s.setSmtpPort(smtpPort);
        s.setSmtpEnableSsl(smtpEnableSsl);
        s.setSmtpUser(smtpUser != null ? smtpUser : "");
        s.setSmtpPassword(smtpPassword != null ? smtpPassword : "");
        s.setSmtpFromEmail(smtpFromEmail != null ? smtpFromEmail : "");
        s.setSmtpFromName(smtpFromName != null ? smtpFromName : "");
        s.setDoctorDisplayName(doctorDisplayName != null ? doctorDisplayName : "");
        s.setDoctorQualification(doctorQualification != null ? doctorQualification : "");
        s.setDoctorRegistrationNo(doctorRegistrationNo != null ? doctorRegistrationNo : "");
        s.setDoctorSpecialization(doctorSpecialization != null ? doctorSpecialization : "");
        s.setConsultingHours(consultingHours != null ? consultingHours : "");
        s.setPrescriptionFooterMessage(prescriptionFooterMessage != null ? prescriptionFooterMessage
                : "Bring the Prescription for the next visit.");
        s.setDoctorSignaturePath(doctorSignaturePath != null ? doctorSignaturePath : "");
        s.setPrescriptionTemplatePath(prescriptionTemplatePath != null ? prescriptionTemplatePath : "");
        s.setPublicBaseUrl(publicBaseUrl != null ? publicBaseUrl.trim() : "");
        s.setPharmacistDisplayName(pharmacistDisplayName != null ? pharmacistDisplayName : "");
        s.setPharmacistQualification(pharmacistQualification != null ? pharmacistQualification : "");
        s.setPharmacistDesignation(pharmacistDesignation != null ? pharmacistDesignation : "");
        s.setPharmacistRegistrationNo(pharmacistRegistrationNo != null ? pharmacistRegistrationNo : "");
        s.setPharmacistExperience(pharmacistExperience != null ? pharmacistExperience : "");
        s.setPharmacistLanguages(pharmacistLanguages != null ? pharmacistLanguages : "");
        s.setPharmacistAvailability(pharmacistAvailability != null ? pharmacistAvailability : "");
        s.setPharmacistAbout(pharmacistAbout != null ? pharmacistAbout : "");
        s.setPharmacistPhotoPath(pharmacistPhotoPath != null ? pharmacistPhotoPath : "/images/pharmacist.jpg");
        s.setPharmacistStatYears(pharmacistStatYears != null ? pharmacistStatYears : "5");
        s.setPharmacistStatPatients(pharmacistStatPatients != null ? pharmacistStatPatients : "3000");
        s.setPharmacistStatPrescriptions(pharmacistStatPrescriptions != null ? pharmacistStatPrescriptions : "10000");
        s.setPharmacistStatSatisfaction(pharmacistStatSatisfaction != null ? pharmacistStatSatisfaction : "98");
        s.setPharmacistBusinessHours(pharmacistBusinessHours != null ? pharmacistBusinessHours : "");
        return s;
    }
}
