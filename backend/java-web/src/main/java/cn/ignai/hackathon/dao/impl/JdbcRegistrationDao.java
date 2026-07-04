package cn.ignai.hackathon.dao.impl;

import cn.ignai.hackathon.dao.RegistrationDao;

import javax.servlet.http.HttpServletRequest;

public class JdbcRegistrationDao implements RegistrationDao {
    @Override
    public long insertDraftFromRequest(HttpServletRequest request) {
        // TODO: implement JDBC insert into registrations and registration_members.
        return System.currentTimeMillis();
    }

    @Override
    public void updateStatus(long registrationId, String status, String note, long adminUserId) {
        // TODO: implement JDBC update and operation_logs insert.
    }
}
