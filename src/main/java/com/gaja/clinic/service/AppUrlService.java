package com.gaja.clinic.service;

public interface AppUrlService {

    String getPublicBaseUrl();

    String getBillShareUrl(int billId);

    String getAbsoluteUrl(String webPath);
}
