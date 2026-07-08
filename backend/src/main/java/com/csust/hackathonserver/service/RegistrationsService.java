package com.csust.hackathonserver.service;

import com.csust.hackathonserver.pojo.Registrations;
import org.springframework.stereotype.Service;

@Service
public interface RegistrationsService {

    /**
     * 提交报名
     */
    Registrations submitRegistration(Registrations registration);

    /**
     * 根据活动ID和用户ID查询报名记录（防重复报名）
     */
    Registrations getByEventIdAndUserId(Long eventId, Long userId);
}
