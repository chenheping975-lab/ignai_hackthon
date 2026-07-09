package com.csust.hackathonserver.mapper;

import com.csust.hackathonserver.pojo.FormFields;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author 21201
* @description 针对表【form_fields】的数据库操作Mapper
* @createDate 2026-07-04 20:41:54
* @Entity com.csust.hackathonserver.pojo.FormFields
*/
@Mapper
public interface FormFieldsMapper {

    @Select("""
            select *
            from form_fields
            where event_id = #{eventId}
              and target_type = #{targetType}
              and enabled = 1
            order by sort_order asc, id asc
            """)
    List<FormFields> findByEventIdAndTargetType(@Param("eventId") Long eventId,
                                                @Param("targetType") String targetType);

    @Delete("delete from form_fields where event_id = #{eventId} and target_type = #{targetType}")
    int deleteByEventIdAndTargetType(@Param("eventId") Long eventId,
                                     @Param("targetType") String targetType);

    @Insert("""
            insert into form_fields
                (event_id, target_type, field_key, label, field_type, required,
                 options_json, placeholder, sort_order, enabled, created_at, updated_at)
            values
                (#{eventId}, #{targetType}, #{fieldKey}, #{label}, #{fieldType}, #{required},
                 #{optionsJson}, #{placeholder}, #{sortOrder}, #{enabled}, now(), now())
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertField(FormFields field);
}



