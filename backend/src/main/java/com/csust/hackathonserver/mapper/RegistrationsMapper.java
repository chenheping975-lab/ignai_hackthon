package com.csust.hackathonserver.mapper;

import com.csust.hackathonserver.pojo.Registrations;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
* @author 21201
* @description 针对表【registrations】的数据库操作Mapper
* @createDate 2026-07-04 20:42:41
* @Entity com.csust.hackathonserver.pojo.Registrations
*/
@Mapper
public interface RegistrationsMapper {

    int insert(Registrations registration);

    Registrations getByEventIdAndUserId(@Param("eventId") Long eventId, @Param("userId") Long userId);

    @Select("select count(*) from registrations")
    int countAll();

    @Select("select count(*) from registrations where status = #{status}")
    int countByStatus(@Param("status") String status);

    @Select("select * from registrations order by created_at desc")
    List<Registrations> findAll();

    @Select("select * from registrations where status = #{status} order by created_at desc")
    List<Registrations> findByStatus(@Param("status") String status);

    @Select("select * from registrations where id = #{id}")
    Registrations findById(@Param("id") Long id);

    @Update("update registrations set status = #{status}, review_note = #{note}, reviewed_at = now() where id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status, @Param("note") String note);
}




