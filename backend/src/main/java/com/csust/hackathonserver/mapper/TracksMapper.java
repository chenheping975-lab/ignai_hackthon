package com.csust.hackathonserver.mapper;

import com.csust.hackathonserver.pojo.Tracks;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author 21201
* @description 针对表【tracks】的数据库操作Mapper
* @createDate 2026-07-04 20:39:13
* @Entity com.csust.hackathonserver.pojo.Tracks
*/
@Mapper
public interface TracksMapper {

    @Select("select * from tracks where event_id = #{eventId} order by sort_order asc, id asc")
    List<Tracks> findByEventId(@Param("eventId") Long eventId);

    @Delete("delete from tracks where event_id = #{eventId}")
    int deleteByEventId(@Param("eventId") Long eventId);

    @Insert("""
            insert into tracks (event_id, name, description, sort_order, created_at, updated_at)
            values (#{eventId}, #{name}, #{description}, #{sortOrder}, now(), now())
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertTrack(Tracks track);
}



