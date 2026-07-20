package com.gaja.clinic.entity;

public enum PrescriptionStatus {
    PENDING_PHARMACY(0),
    COMPLETED(1);

    private final int value;

    PrescriptionStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static PrescriptionStatus fromValue(int value) {
        for (PrescriptionStatus status : values()) {
            if (status.value == value) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown PrescriptionStatus value: " + value);
    }
}
