package com.gaja.clinic.service.impl;

import com.gaja.clinic.dto.PdfArchiveFilterDto;
import com.gaja.clinic.entity.PdfRecord;
import com.gaja.clinic.exception.ResourceNotFoundException;
import com.gaja.clinic.repository.PdfRecordRepository;
import com.gaja.clinic.service.PdfArchiveService;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PdfArchiveServiceImpl implements PdfArchiveService {

    private final PdfRecordRepository pdfRecordRepository;

    @Override
    @Transactional(readOnly = true)
    public List<PdfRecord> search(PdfArchiveFilterDto filter) {
        Specification<PdfRecord> spec = buildSpecification(filter);
        return pdfRecordRepository.findAll(spec, org.springframework.data.domain.Sort.by(
                org.springframework.data.domain.Sort.Order.desc("visitDate"),
                org.springframework.data.domain.Sort.Order.desc("createdDate")));
    }

    @Override
    @Transactional(readOnly = true)
    public PdfRecord getById(Integer id) {
        return pdfRecordRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("PDF record not found"));
    }

    private Specification<PdfRecord> buildSpecification(PdfArchiveFilterDto filter) {
        return (root, query, cb) -> {
            if (query != null && PdfRecord.class.equals(query.getResultType())) {
                root.fetch("patient", JoinType.LEFT);
                root.fetch("doctor", JoinType.LEFT).fetch("user", JoinType.LEFT);
                root.fetch("bill", JoinType.LEFT);
                query.distinct(true);
            }

            List<Predicate> predicates = new ArrayList<>();

            if (filter.getPatientName() != null && !filter.getPatientName().isBlank()) {
                Join<?, ?> patient = root.join("patient", JoinType.LEFT);
                predicates.add(cb.like(cb.lower(patient.get("name")),
                        "%" + filter.getPatientName().trim().toLowerCase() + "%"));
            }

            if (filter.getMobile() != null && !filter.getMobile().isBlank()) {
                Join<?, ?> patient = root.join("patient", JoinType.LEFT);
                predicates.add(cb.like(patient.get("mobile"), "%" + filter.getMobile().trim() + "%"));
            }

            if (filter.getDoctorName() != null && !filter.getDoctorName().isBlank()) {
                Join<?, ?> doctor = root.join("doctor", JoinType.LEFT);
                Join<?, ?> user = doctor.join("user", JoinType.LEFT);
                predicates.add(cb.like(cb.lower(cb.coalesce(user.get("fullName"), "")),
                        "%" + filter.getDoctorName().trim().toLowerCase() + "%"));
            }

            if (filter.getDateFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("visitDate"),
                        filter.getDateFrom().atStartOfDay()));
            }

            if (filter.getDateTo() != null) {
                LocalDateTime end = filter.getDateTo().atTime(LocalTime.MAX);
                predicates.add(cb.lessThanOrEqualTo(root.get("visitDate"), end));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
