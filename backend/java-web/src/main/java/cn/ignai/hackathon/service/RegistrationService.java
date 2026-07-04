package cn.ignai.hackathon.service;

import javax.servlet.http.HttpServletRequest;

public interface RegistrationService {
    long createFromRequest(HttpServletRequest request);

    void review(long registrationId, String status, String note, long adminUserId);
}
