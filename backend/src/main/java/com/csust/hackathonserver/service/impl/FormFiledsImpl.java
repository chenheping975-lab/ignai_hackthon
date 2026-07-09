package com.csust.hackathonserver.service.impl;

import com.csust.hackathonserver.mapper.FormFieldsMapper;
import com.csust.hackathonserver.pojo.FormFields;
import com.csust.hackathonserver.service.FormFieldsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FormFiledsImpl implements FormFieldsService {

    @Autowired
    private FormFieldsMapper formFieldsMapper;

    @Override
    public List<FormFields> getFormFields(Long eventId, String target) {
        return formFieldsMapper.findByEventIdAndTarget(eventId, target);
    }

    @Override
    public List<FormFields> getAllFormFields(Long eventId, String target) {
        return formFieldsMapper.findAllByEventIdAndTarget(eventId, target);
    }

    @Override
    public FormFields insert(FormFields field) {
        formFieldsMapper.insert(field);
        return field;
    }

    @Override
    public int update(FormFields field) {
        return formFieldsMapper.update(field);
    }
}
