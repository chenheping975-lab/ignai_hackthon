package com.csust.hackathonserver.service.impl;

import com.csust.hackathonserver.mapper.RegistrationsMapper;
import com.csust.hackathonserver.pojo.Registrations;
import com.csust.hackathonserver.service.RegistrationsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RegistrationsImpl implements RegistrationsService {

    @Autowired
    private RegistrationsMapper registrationsMapper;

    @Override
    public Registrations submitRegistration(Registrations registration) {
        registrationsMapper.insert(registration);
        return registration;
    }

    @Override
    public Registrations getByEventIdAndUserId(Long eventId, Long userId) {
        return registrationsMapper.getByEventIdAndUserId(eventId, userId);
    }

    @Override
    public int countAll() {
        return registrationsMapper.countAll();
    }

    @Override
    public int countByStatus(String status) {
        return registrationsMapper.countByStatus(status);
    }

    @Override
    public List<Registrations> findAll() {
        return registrationsMapper.findAll();
    }

    @Override
    public List<Registrations> findByStatus(String status) {
        return registrationsMapper.findByStatus(status);
    }

    @Override
    public Registrations findById(Long id) {
        return registrationsMapper.findById(id);
    }

    @Override
    public int updateStatus(Long id, String status, String note) {
        return registrationsMapper.updateStatus(id, status, note);
    }
}
