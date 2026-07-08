package com.csust.hackathonserver.service;

import com.csust.hackathonserver.pojo.Ratings;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface RatingsServices {
    Ratings addRating(Ratings rating);
    List<Ratings> findByProjectId(Long projectId);
    Ratings findByProjectIdAndRaterKey(Long projectId, String raterKey);
    int countByProjectId(Long projectId);
}
