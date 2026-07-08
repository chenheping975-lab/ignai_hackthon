package com.csust.hackathonserver.mapper;

import com.csust.hackathonserver.pojo.Votes;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface VotesMapper {

    @Insert("insert into votes (project_id, voter_user_id, voter_key, source_type, created_at) " +
            "values (#{projectId}, #{voterUserId}, #{voterKey}, #{sourceType}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Votes vote);

    @Select("select * from votes where project_id = #{projectId} and voter_key = #{voterKey}")
    Votes findByProjectIdAndVoterKey(@Param("projectId") Long projectId, @Param("voterKey") String voterKey);

    @Select("select count(*) from votes where project_id = #{projectId}")
    int countByProjectId(@Param("projectId") Long projectId);
}
