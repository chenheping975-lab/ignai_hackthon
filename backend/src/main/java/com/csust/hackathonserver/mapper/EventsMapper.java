package com.csust.hackathonserver.mapper;

import com.csust.hackathonserver.pojo.Events;
import org.apache.ibatis.annotations.Mapper;
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

    @Select("select * from events where id = #{id} limit 1")
    Events findById(@Param("id") Long id);

    /**
     * 查询所有活动（按创建时间倒序），供 PageHelper 分页拦截
     */
    @Select("select * from events order by created_at desc")
    List<Events> findAll();

    @Update("""
            update events
            set title = #{title},
                subtitle = #{subtitle},
                location = #{location},
                description = #{description},
                status = #{status},
                registration_open = #{registrationOpen},
                rating_enabled = #{ratingEnabled},
                vote_enabled = #{voteEnabled},
                registration_deadline = #{registrationDeadline},
                submission_deadline = #{submissionDeadline},
                rating_start_at = #{ratingStartAt},
                rating_end_at = #{ratingEndAt},
                official_site_url = #{officialSiteUrl},
                benchmark_site_url = #{benchmarkSiteUrl},
                updated_at = now()
            where id = #{id}
            """)
    int updateEventConfig(Events event);
}



