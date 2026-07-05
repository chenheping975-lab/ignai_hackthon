package com.csust.hackathonserver.mapper;

import com.csust.hackathonserver.pojo.Users;
import org.apache.ibatis.annotations.Mapper;

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
}




