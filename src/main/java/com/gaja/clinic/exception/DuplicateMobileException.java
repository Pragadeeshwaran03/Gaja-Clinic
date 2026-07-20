package com.gaja.clinic.exception;

public class DuplicateMobileException extends RuntimeException {

    private final String existingPatientName;
    private final String existingPatientCode;

    public DuplicateMobileException(String existingPatientName, String existingPatientCode) {
        super("Mobile number already registered");
        this.existingPatientName = existingPatientName;
        this.existingPatientCode = existingPatientCode;
    }

    public String getExistingPatientName() {
        return existingPatientName;
    }

    public String getExistingPatientCode() {
        return existingPatientCode;
    }
}
