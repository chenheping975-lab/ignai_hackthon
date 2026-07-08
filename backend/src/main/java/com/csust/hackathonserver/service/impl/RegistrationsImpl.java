package com.csust.hackathonserver.service.impl;

import com.csust.hackathonserver.mapper.RegistrationsMapper;
import com.csust.hackathonserver.pojo.Registrations;
import com.csust.hackathonserver.service.RegistrationsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
