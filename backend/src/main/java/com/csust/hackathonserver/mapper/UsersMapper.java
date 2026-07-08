package com.csust.hackathonserver.mapper;

import com.csust.hackathonserver.pojo.Users;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
* @author 21201
* @description 针对表【users】的数据库操作Mapper
* @createDate 2026-07-04 23:04:03
* @Entity com.csust.hackathonserver.pojo.Users
*/
@Mapper
public interface UsersMapper {

    void register(Users user);

    int login(Users user);

    Users getByAccount(String account);

    @Select("select count(*) from users where role = #{role}")
    int countByRole(@Param("role") String role);

    @Select("select * from users where role = #{role} limit 1")
    Users findOneByRole(@Param("role") String role);

    @Select("select * from users where id = #{id}")
    Users findById(@Param("id") Long id);

    @Select("select * from users order by created_at desc")
    List<Users> findAll();

    @Update("update users set password_hash = #{passwordHash} where id = #{id}")
    int updatePassword(@Param("id") Long id, @Param("passwordHash") String passwordHash);

    @Update("update users set email = #{email} where id = #{id}")
    int updateEmail(@Param("id") Long id, @Param("email") String email);

    @Update("update users set status = #{status} where id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status);
}




