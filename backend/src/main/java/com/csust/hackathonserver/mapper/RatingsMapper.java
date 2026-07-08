package com.csust.hackathonserver.mapper;

import com.csust.hackathonserver.pojo.Ratings;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RatingsMapper {

    @Insert("insert into ratings (project_id, rater_user_id, rater_key, source_type, score, comment, created_at) " +
            "values (#{projectId}, #{raterUserId}, #{raterKey}, #{sourceType}, #{score}, #{comment}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Ratings rating);

    @Select("select * from ratings where project_id = #{projectId}")
    List<Ratings> findByProjectId(@Param("projectId") Long projectId);

    @Select("select * from ratings where project_id = #{projectId} and rater_key = #{raterKey}")
    Ratings findByProjectIdAndRaterKey(@Param("projectId") Long projectId, @Param("raterKey") String raterKey);

    @Select("select count(*) from ratings where project_id = #{projectId}")
    int countByProjectId(@Param("projectId") Long projectId);
}
