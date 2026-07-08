package com.csust.hackathonserver.service.impl;

import com.csust.hackathonserver.mapper.RatingsMapper;
import com.csust.hackathonserver.pojo.Ratings;
import com.csust.hackathonserver.service.RatingsServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RatingsImpl implements RatingsServices {
    @Autowired
    private RatingsMapper ratingsMapper;

    @Override
    public Ratings addRating(Ratings rating) {
        ratingsMapper.insert(rating);
        return rating;
    }

    @Override
    public List<Ratings> findByProjectId(Long projectId) {
        return ratingsMapper.findByProjectId(projectId);
    }

    @Override
    public Ratings findByProjectIdAndRaterKey(Long projectId, String raterKey) {
        return ratingsMapper.findByProjectIdAndRaterKey(projectId, raterKey);
    }

    @Override
    public int countByProjectId(Long projectId) {
        return ratingsMapper.countByProjectId(projectId);
    }
}
