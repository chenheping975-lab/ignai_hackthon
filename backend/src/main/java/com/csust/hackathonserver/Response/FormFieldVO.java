package com.csust.hackathonserver.Response;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class FormFieldVO {
    private Long id;
    private Long eventId;
    private String targetType;
    private String fieldKey;
    private String label;
    private String fieldType;
    private Boolean required;
    private List<String> options = new ArrayList<>();
    private String placeholder;
    private Integer sortOrder;
    private Boolean enabled;
}
