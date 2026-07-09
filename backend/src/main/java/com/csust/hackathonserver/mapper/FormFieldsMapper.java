package com.csust.hackathonserver.mapper;

import com.csust.hackathonserver.pojo.FormFields;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
* @author 21201
* @description 针对表【form_fields】的数据库操作Mapper
* @createDate 2026-07-04 20:41:54
* @Entity com.csust.hackathonserver.pojo.FormFields
*/
@Mapper
public interface FormFieldsMapper {

    @Select("select * from form_fields where event_id = #{eventId} and target_type = #{target} and enabled = 1 order by sort_order")
    List<FormFields> findByEventIdAndTarget(@Param("eventId") Long eventId, @Param("target") String target);

    @Select("select * from form_fields where event_id = #{eventId} and target_type = #{target} order by sort_order")
    List<FormFields> findAllByEventIdAndTarget(@Param("eventId") Long eventId, @Param("target") String target);

    @Insert("insert into form_fields (event_id, target_type, field_key, label, field_type, required, " +
            "options_json, placeholder, sort_order, enabled, created_at, updated_at) values " +
            "(#{eventId}, #{targetType}, #{fieldKey}, #{label}, #{fieldType}, #{required}, " +
            "#{optionsJson}, #{placeholder}, #{sortOrder}, #{enabled}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(FormFields field);

    @Update("update form_fields set label = #{label}, field_type = #{fieldType}, required = #{required}, " +
            "options_json = #{optionsJson}, placeholder = #{placeholder}, sort_order = #{sortOrder}, " +
            "enabled = #{enabled}, updated_at = now() where id = #{id}")
    int update(FormFields field);
}




