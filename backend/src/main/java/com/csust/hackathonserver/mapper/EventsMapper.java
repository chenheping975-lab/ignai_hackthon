package com.csust.hackathonserver.mapper;

import com.csust.hackathonserver.pojo.Events;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
* @author 21201
* @description 针对表【events】的数据库操作Mapper
* @createDate 2026-07-04 20:42:21
* @Entity com.csust.hackathonserver.pojo.Events
*/
@Mapper
public interface EventsMapper {

    @Select("select * from events where status = 'published' order by created_at desc limit 1")
    Events getCurrentEvent();

    @Select("select * from events order by created_at desc")
    List<Events> findAll();

    @Select("select * from events where id = #{id}")
    Events findById(@Param("id") Long id);

    @Insert("insert into events (title, subtitle, location, description, status, registration_open, " +
            "rating_enabled, vote_enabled, registration_deadline, submission_deadline, " +
            "rating_start_at, rating_end_at, official_site_url, benchmark_site_url, created_by, created_at, updated_at) " +
            "values (#{title}, #{subtitle}, #{location}, #{description}, #{status}, #{registrationOpen}, " +
            "#{ratingEnabled}, #{voteEnabled}, #{registrationDeadline}, #{submissionDeadline}, " +
            "#{ratingStartAt}, #{ratingEndAt}, #{officialSiteUrl}, #{benchmarkSiteUrl}, " +
            "#{createdBy}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Events event);

    @Update("update events set title = #{title}, subtitle = #{subtitle}, location = #{location}, " +
            "description = #{description}, status = #{status}, " +
            "registration_open = #{registrationOpen}, rating_enabled = #{ratingEnabled}, " +
            "vote_enabled = #{voteEnabled}, registration_deadline = #{registrationDeadline}, " +
            "submission_deadline = #{submissionDeadline}, rating_start_at = #{ratingStartAt}, " +
            "rating_end_at = #{ratingEndAt}, official_site_url = #{officialSiteUrl}, " +
            "benchmark_site_url = #{benchmarkSiteUrl}, updated_at = now() where id = #{id}")
    int update(Events event);

    @Update("update events set registration_open = #{registrationOpen}, rating_enabled = #{ratingEnabled}, " +
            "vote_enabled = #{voteEnabled}, registration_deadline = #{registrationDeadline}, " +
            "submission_deadline = #{submissionDeadline}, rating_start_at = #{ratingStartAt}, " +
            "rating_end_at = #{ratingEndAt} where id = #{id}")
    int updateWindows(Events event);
}




