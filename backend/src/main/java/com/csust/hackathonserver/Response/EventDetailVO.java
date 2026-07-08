package com.csust.hackathonserver.Response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class EventDetailVO {
    private Long id;
    private String title;
    private String subtitle;
    private String location;
    private String description;
    private String status;
    private Boolean registrationOpen;
    private Boolean ratingEnabled;
    private Boolean voteEnabled;
    private LocalDateTime registrationDeadline;
    private LocalDateTime submissionDeadline;
    private LocalDateTime ratingStartAt;
    private LocalDateTime ratingEndAt;
    private String officialSiteUrl;
    private String benchmarkSiteUrl;
    private List<TrackVO> tracks = new ArrayList<>();
    private List<FormFieldVO> registrationFields = new ArrayList<>();
}
