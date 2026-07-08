package com.csust.hackathonserver.mapper;

import com.csust.hackathonserver.pojo.Projects;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
* @author 21201
* @description 针对表【projects】的数据库操作Mapper
* @createDate 2026-07-04 20:43:13
* @Entity com.csust.hackathonserver.pojo.Projects
*/
@Mapper
public interface ProjectsMapper {

    @Select("select * from projects where status = #{status} order by created_at desc")
    List<Projects> getPublicProjects(@Param("status") String status);

    @Select("select count(*) from projects")
    int countAll();

    @Select("select count(*) from projects where status = #{status}")
    int countByStatus(@Param("status") String status);

    @Select("select * from projects order by created_at desc")
    List<Projects> findAll();

    @Select("select * from projects where id = #{id}")
    Projects findById(@Param("id") Long id);

    @Update("update projects set status = #{status} where id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status);

    @Insert("insert into projects (event_id, team_id, track_id, title, tagline, description, demo_url, code_url, " +
            "status, created_at, updated_at) values (#{eventId}, #{teamId}, #{trackId}, #{title}, #{tagline}, " +
            "#{description}, #{demoUrl}, #{codeUrl}, #{status}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Projects project);

    @Update("update projects set title = #{title}, tagline = #{tagline}, description = #{description}, " +
            "demo_url = #{demoUrl}, code_url = #{codeUrl}, track_id = #{trackId}, updated_at = now() where id = #{id}")
    int update(Projects project);

    @Update("update projects set status = 'submitted', submitted_at = now() where id = #{id}")
    int submit(@Param("id") Long id);

    @Select("select * from projects where team_id = #{teamId}")
    Projects findByTeamId(@Param("teamId") Long teamId);

    @Select("select p.* from projects p inner join teams t on p.team_id = t.id where t.leader_user_id = #{userId}")
    List<Projects> findByUserId(@Param("userId") Long userId);
}




