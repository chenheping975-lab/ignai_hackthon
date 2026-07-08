package com.csust.hackathonserver.mapper;

import com.csust.hackathonserver.pojo.Teams;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TeamsMapper {

    @Insert("insert into teams (event_id, registration_id, team_name, leader_user_id, status, created_at, updated_at) " +
            "values (#{eventId}, #{registrationId}, #{teamName}, #{leaderUserId}, #{status}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Teams team);

    @Select("select * from teams where id = #{id}")
    Teams findById(@Param("id") Long id);

    @Select("select * from teams where leader_user_id = #{userId}")
    List<Teams> findByLeaderUserId(@Param("userId") Long userId);

    @Select("select * from teams where event_id = #{eventId} and team_name = #{teamName}")
    Teams findByEventIdAndName(@Param("eventId") Long eventId, @Param("teamName") String teamName);

    @Select("select * from teams where event_id = #{eventId}")
    List<Teams> findByEventId(@Param("eventId") Long eventId);
}
