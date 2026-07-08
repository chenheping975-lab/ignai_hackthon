package com.csust.hackathonserver.mapper;

import com.csust.hackathonserver.pojo.Tracks;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TracksMapper {

    @Select("select * from tracks where event_id = #{eventId} order by sort_order")
    List<Tracks> findByEventId(@Param("eventId") Long eventId);

    @Select("select * from tracks where id = #{id}")
    Tracks findById(@Param("id") Long id);

    @Insert("insert into tracks (event_id, name, description, sort_order, created_at, updated_at) " +
            "values (#{eventId}, #{name}, #{description}, #{sortOrder}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Tracks track);
}
