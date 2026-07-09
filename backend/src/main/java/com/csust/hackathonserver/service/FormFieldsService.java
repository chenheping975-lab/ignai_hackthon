package com.csust.hackathonserver.service;

import com.csust.hackathonserver.pojo.FormFields;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface FormFieldsService {

    /**
     * 查询活动的动态表单字段
     */
    List<FormFields> getFormFields(Long eventId, String target);

    List<FormFields> getAllFormFields(Long eventId, String target);

    FormFields insert(FormFields field);

    int update(FormFields field);
}
