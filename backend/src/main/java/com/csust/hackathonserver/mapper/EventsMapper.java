package com.csust.hackathonserver.mapper;

import com.csust.hackathonserver.pojo.Events;
import jdk.jfr.Event;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
* @author 21201
* @description 针对表【events】的数据库操作Mapper
* @createDate 2026-07-04 20:42:21
* @Entity com.csust.hackathonserver.pojo.Events
*/
@Mapper
public interface EventsMapper {

    @Select("select * from events where status = 'active' order by created_at desc limit 1")
    Events getCurrentEvent();
}




