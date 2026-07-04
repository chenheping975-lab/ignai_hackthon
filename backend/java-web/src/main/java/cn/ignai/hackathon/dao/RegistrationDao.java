package cn.ignai.hackathon.dao;

import javax.servlet.http.HttpServletRequest;

public interface RegistrationDao {
    long insertDraftFromRequest(HttpServletRequest request);

    void updateStatus(long registrationId, String status, String note, long adminUserId);
}
