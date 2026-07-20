package com.gaja.clinic.dto;

import com.gaja.clinic.model.ClinicSettings;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PharmacyProfileDto {

    private String displayName;
    private String qualification;
    private String designation;
    private String registrationNo;
    private String experience;
    private String languages;
    private String availability;
    private String about;
    private String photoPath;
    private String statYears;
    private String statPatients;
    private String statPrescriptions;
    private String statSatisfaction;
    private String phone;
    private String address;
    private String businessHours;
    private String whatsAppUrl;

    private static final String DEFAULT_ABOUT =
            "KARTHIK. S is a Registered Pharmacist at Gaja Clinic & Gaja Medicals, dedicated to ensuring "
                    + "safe and accurate medicine dispensing. He provides professional guidance on prescription "
                    + "medications, patient counseling, medicine usage, and healthcare support with a "
                    + "patient-first approach.";

    public static PharmacyProfileDto from(ClinicSettings settings) {
        PharmacyProfileDto dto = new PharmacyProfileDto();
        dto.setDisplayName(blankToDefault(settings.getPharmacistDisplayName(), "KARTHIK. S"));
        dto.setQualification(blankToDefault(settings.getPharmacistQualification(), "B. Pharm"));
        dto.setDesignation(blankToDefault(settings.getPharmacistDesignation(), "Registered Pharmacist"));
        dto.setRegistrationNo(blankToDefault(settings.getPharmacistRegistrationNo(), "49846 A1"));
        dto.setExperience(blankToDefault(settings.getPharmacistExperience(), "—"));
        dto.setLanguages(blankToDefault(settings.getPharmacistLanguages(), "—"));
        dto.setAvailability(blankToDefault(settings.getPharmacistAvailability(), "—"));
        String about = settings.getPharmacistAbout();
        dto.setAbout(about != null && !about.isBlank() ? about : DEFAULT_ABOUT);
        dto.setPhotoPath(blankToDefault(settings.getPharmacistPhotoPath(), "/images/pharmacist.jpg"));
        dto.setStatYears(blankToDefault(settings.getPharmacistStatYears(), "5"));
        dto.setStatPatients(blankToDefault(settings.getPharmacistStatPatients(), "3000"));
        dto.setStatPrescriptions(blankToDefault(settings.getPharmacistStatPrescriptions(), "10000"));
        dto.setStatSatisfaction(blankToDefault(settings.getPharmacistStatSatisfaction(), "98"));
        dto.setPhone(blankToDefault(settings.getPhone(), "98844 43162"));
        dto.setAddress(blankToDefault(settings.getAddress(),
                "No.39, 2nd Main Road, Chitlapakkam, Tambaram Sanatorium, Chennai - 600047"));
        dto.setBusinessHours(blankToDefault(settings.getPharmacistBusinessHours(),
                blankToDefault(settings.getConsultingHours(), "Mon – Sat, Evening Consultation")));
        dto.setWhatsAppUrl(buildWhatsAppUrl(dto.getPhone()));
        return dto;
    }

    private static String buildWhatsAppUrl(String phone) {
        String digits = phone.replaceAll("\\D", "");
        if (digits.startsWith("0")) {
            digits = digits.substring(1);
        }
        if (!digits.startsWith("91") && digits.length() == 10) {
            digits = "91" + digits;
        }
        return "https://wa.me/" + digits;
    }

    private static String blankToDefault(String value, String fallback) {
        return value != null && !value.isBlank() ? value : fallback;
    }
}
