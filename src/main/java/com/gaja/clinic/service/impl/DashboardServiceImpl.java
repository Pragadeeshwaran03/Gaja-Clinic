package com.gaja.clinic.service.impl;

import com.gaja.clinic.dto.ActivityItemDto;
import com.gaja.clinic.dto.DashboardDto;
import com.gaja.clinic.entity.Bill;
import com.gaja.clinic.entity.Patient;
import com.gaja.clinic.entity.Prescription;
import com.gaja.clinic.repository.BillRepository;
import com.gaja.clinic.repository.DoctorRepository;
import com.gaja.clinic.repository.PatientRepository;
import com.gaja.clinic.repository.PrescriptionRepository;
import com.gaja.clinic.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final BillRepository billRepository;

    @Override
    @Transactional(readOnly = true)
    public DashboardDto getDashboard() {
        LocalDate today = LocalDate.now();
        LocalDateTime todayStart = today.atStartOfDay();
        LocalDateTime tomorrowStart = today.plusDays(1).atStartOfDay();
        LocalDateTime monthStart = today.withDayOfMonth(1).atStartOfDay();

        DashboardDto dto = new DashboardDto();
        dto.setTotalPatients(patientRepository.count());
        dto.setTotalDoctors(doctorRepository.countByIsActiveTrue());
        dto.setTotalPrescriptions(prescriptionRepository.count());
        dto.setTotalBills(billRepository.count());
        dto.setRevenueToday(billRepository.sumFinalAmountBetween(todayStart, tomorrowStart));
        dto.setRevenueThisMonth(billRepository.sumFinalAmountBetween(monthStart, tomorrowStart));

        List<ActivityItemDto> activities = new ArrayList<>();

        for (Patient p : patientRepository.findTop5ByOrderByCreatedDateDesc()) {
            ActivityItemDto item = new ActivityItemDto();
            item.setType("patient");
            item.setTimestamp(p.getCreatedDate());
            item.setDescription("New patient registered: " + p.getName() + " (" + p.getPatientCode() + ")");
            activities.add(item);
        }

        for (Prescription p : prescriptionRepository.findTop5ByOrderByDateCreatedDesc()) {
            ActivityItemDto item = new ActivityItemDto();
            item.setType("prescription");
            item.setTimestamp(p.getDateCreated());
            String patientName = p.getPatient() != null ? p.getPatient().getName() : "Patient";
            item.setDescription("Prescription " + p.getPrescriptionNumber() + " for " + patientName);
            activities.add(item);
        }

        for (Bill b : billRepository.findTop5ByOrderByCreatedDateDesc()) {
            ActivityItemDto item = new ActivityItemDto();
            item.setType("bill");
            item.setTimestamp(b.getCreatedDate());
            String patientName = b.getPatient() != null ? b.getPatient().getName() : "Patient";
            item.setDescription(String.format("Bill %s — ₹%,.2f (%s)", b.getBillNo(), b.getFinalAmount(), patientName));
            activities.add(item);
        }

        activities.sort(Comparator.comparing(ActivityItemDto::getTimestamp).reversed());
        if (activities.size() > 15) {
            activities = activities.subList(0, 15);
        }
        dto.setRecentActivities(activities);
        return dto;
    }
}
