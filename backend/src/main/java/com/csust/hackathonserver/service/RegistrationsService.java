package com.csust.hackathonserver.service;

import com.csust.hackathonserver.pojo.Registrations;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface RegistrationsService {

    Registrations submitRegistration(Registrations registration);

    Registrations getByEventIdAndUserId(Long eventId, Long userId);

    int countAll();

    int countByStatus(String status);

    List<Registrations> findAll();

    List<Registrations> findByStatus(String status);

    Registrations findById(Long id);

    int updateStatus(Long id, String status, String note);
}
