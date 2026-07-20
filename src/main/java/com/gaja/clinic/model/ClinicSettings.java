package com.gaja.clinic.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ClinicSettings {

    private String clinicName = "Gaja Clinic & Medicals";
    private String tagline = "Your Health, Our Priority";
    private String address = "";
    private String phone = "";
    private String email = "";
    private String logoPath = "/images/logo.jpg";
    private BigDecimal consultationFeeDefault = new BigDecimal("200.00");
    private String smtpHost = "";
    private int smtpPort = 587;
    private boolean smtpEnableSsl = true;
    private String smtpUser = "";
    private String smtpPassword = "";
    private String smtpFromEmail = "";
    private String smtpFromName = "";
    private String doctorDisplayName = "";
    private String doctorQualification = "";
    private String doctorRegistrationNo = "";
    private String doctorSpecialization = "";
    private String consultingHours = "";
    private String prescriptionFooterMessage = "Wishing You Good Health";
    private String doctorSignaturePath = "";
    private String prescriptionTemplatePath = "";
    private String publicBaseUrl = "";

    /* Pharmacy public profile */
    private String pharmacistDisplayName = "KARTHIK. S";
    private String pharmacistQualification = "B. Pharm";
    private String pharmacistDesignation = "Registered Pharmacist";
    private String pharmacistRegistrationNo = "49846 A1";
    private String pharmacistExperience = "—";
    private String pharmacistLanguages = "—";
    private String pharmacistAvailability = "—";
    private String pharmacistAbout = "";
    private String pharmacistPhotoPath = "/images/pharmacist.jpg";
    private String pharmacistStatYears = "5";
    private String pharmacistStatPatients = "3000";
    private String pharmacistStatPrescriptions = "10000";
    private String pharmacistStatSatisfaction = "98";
    private String pharmacistBusinessHours = "Mon – Sat, Evening Consultation";
}
