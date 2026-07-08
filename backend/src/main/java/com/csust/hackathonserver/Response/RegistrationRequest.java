package com.csust.hackathonserver.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationRequest {
    private Long eventId;
    private String registrationType;
    private String teamName;
    private String contactName;
    private String contactPhone;
    private String contactEmail;
    private Long trackId;
}