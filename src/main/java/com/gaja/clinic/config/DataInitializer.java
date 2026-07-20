package com.gaja.clinic.config;

import com.gaja.clinic.entity.Doctor;
import com.gaja.clinic.entity.Role;
import com.gaja.clinic.entity.Setting;
import com.gaja.clinic.entity.User;
import com.gaja.clinic.repository.DoctorRepository;
import com.gaja.clinic.repository.RoleRepository;
import com.gaja.clinic.repository.SettingRepository;
import com.gaja.clinic.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements ApplicationRunner {

    private static final String DEFAULT_PASSWORD = "1234";

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final SettingRepository settingRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (roleRepository.count() == 0) {
            seedRolesAndUsers();
        }
        seedDefaultSettings();
        log.info("Database initialization complete.");
    }

    private void seedRolesAndUsers() {
        Role mainAdminRole = createRole("MainAdmin");
        Role doctorRole = createRole("Doctor");
        Role pharmacyRole = createRole("Pharmacy");

        String passwordHash = passwordEncoder.encode(DEFAULT_PASSWORD);

        User adminUser = createUser("admin", "admin@gaja.local", "Main Administrator", passwordHash, mainAdminRole);
        User doctorUser = createUser("doctor", "doctor@gaja.local", "Dr. Gaja", passwordHash, doctorRole);
        createUser("pharmacy", "pharmacy@gaja.local", "Pharmacy Staff", passwordHash, pharmacyRole);

        Doctor doctor = new Doctor();
        doctor.setUserId(doctorUser.getId());
        doctor.setSpecialization("General Medicine");
        doctor.setQualification("MBBS");
        doctor.setIsActive(true);
        doctor.setCreatedDate(LocalDateTime.now());
        doctorRepository.save(doctor);

        log.info("Seeded default roles and users (password: {}).", DEFAULT_PASSWORD);
    }

    private Role createRole(String name) {
        Role role = new Role();
        role.setName(name);
        return roleRepository.save(role);
    }

    private User createUser(String username, String email, String fullName, String passwordHash, Role role) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setFullName(fullName);
        user.setPasswordHash(passwordHash);
        user.setRoleId(role.getId());
        user.setRole(role);
        return userRepository.save(user);
    }

    private void seedDefaultSettings() {
        Map<String, String> defaults = new LinkedHashMap<>();
        defaults.put("ClinicName", "Gaja Clinic & Medicals");
        defaults.put("Tagline", "Your Health, Our Priority");
        defaults.put("Address", "No.39, 2nd Main Road, Chitlapakkam, Tambaram Sanatorium, Chennai - 600047");
        defaults.put("Phone", "98844 43162");
        defaults.put("Email", "info@gajaclinic.local");
        defaults.put("LogoPath", "/images/logo.svg");
        defaults.put("ConsultationFeeDefault", "200.00");
        defaults.put("DoctorDisplayName", "Roopa Krishnan");
        defaults.put("DoctorQualification", "MBBS");
        defaults.put("DoctorRegistrationNo", "199315");
        defaults.put("DoctorSpecialization", "GENERAL PHYSICIAN");
        defaults.put("ConsultingHours", "Evening, Monday to Saturday");
        defaults.put("PrescriptionFooterMessage", "Wishing You Good Health");
        defaults.put("PharmacistDisplayName", "KARTHIK. S");
        defaults.put("PharmacistQualification", "B. Pharm");
        defaults.put("PharmacistDesignation", "Registered Pharmacist");
        defaults.put("PharmacistRegistrationNo", "49846 A1");
        defaults.put("PharmacistExperience", "—");
        defaults.put("PharmacistLanguages", "—");
        defaults.put("PharmacistAvailability", "—");
        defaults.put("PharmacistPhotoPath", "/images/pharmacist.jpg");
        defaults.put("PharmacistStatYears", "5");
        defaults.put("PharmacistStatPatients", "3000");
        defaults.put("PharmacistStatPrescriptions", "10000");
        defaults.put("PharmacistStatSatisfaction", "98");
        defaults.put("PharmacistBusinessHours", "Mon – Sat, Evening Consultation");

        List<String> keys = List.copyOf(defaults.keySet());
        Map<String, Setting> existing = settingRepository.findByKeyIn(keys).stream()
                .collect(Collectors.toMap(Setting::getKey, s -> s));

        for (Map.Entry<String, String> entry : defaults.entrySet()) {
            if (!existing.containsKey(entry.getKey())) {
                Setting setting = new Setting();
                setting.setKey(entry.getKey());
                setting.setValue(entry.getValue());
                settingRepository.save(setting);
            }
        }
    }
}
