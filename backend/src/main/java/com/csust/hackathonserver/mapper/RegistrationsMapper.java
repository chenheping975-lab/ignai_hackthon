package com.csust.hackathonserver.mapper;

import com.csust.hackathonserver.pojo.Registrations;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
* @author 21201
* @description 针对表【registrations】的数据库操作Mapper
* @createDate 2026-07-04 20:42:41
* @Entity com.csust.hackathonserver.pojo.Registrations
*/
@Mapper
public interface RegistrationsMapper {

    /**
     * 插入报名记录，自动回填主键 id
     */
    int insert(Registrations registration);

    /**
     * 根据活动ID和用户ID查询报名记录（防重复报名）
     */
    Registrations getByEventIdAndUserId(@Param("eventId") Long eventId, @Param("userId") Long userId);

}




