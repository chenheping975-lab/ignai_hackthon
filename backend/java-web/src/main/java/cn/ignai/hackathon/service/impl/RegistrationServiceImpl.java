package cn.ignai.hackathon.service.impl;

import cn.ignai.hackathon.dao.RegistrationDao;
import cn.ignai.hackathon.dao.impl.JdbcRegistrationDao;
import cn.ignai.hackathon.service.RegistrationService;

import javax.servlet.http.HttpServletRequest;

public class RegistrationServiceImpl implements RegistrationService {
    private final RegistrationDao registrationDao = new JdbcRegistrationDao();

    @Override
    public long createFromRequest(HttpServletRequest request) {
        // TODO: parse JSON body, validate dynamic fields, prevent duplicated contact, create AI task.
        return registrationDao.insertDraftFromRequest(request);
    }

    @Override
    public void review(long registrationId, String status, String note, long adminUserId) {
        // TODO: validate status transition, write operation log, create mail draft task.
        registrationDao.updateStatus(registrationId, status, note, adminUserId);
    }
}
