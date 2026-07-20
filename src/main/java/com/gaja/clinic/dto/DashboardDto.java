package com.gaja.clinic.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class DashboardDto {

    private long totalPatients;
    private long totalDoctors;
    private long totalPrescriptions;
    private long totalBills;
    private BigDecimal revenueToday = BigDecimal.ZERO;
    private BigDecimal revenueThisMonth = BigDecimal.ZERO;
    private List<ActivityItemDto> recentActivities = new ArrayList<>();
}
