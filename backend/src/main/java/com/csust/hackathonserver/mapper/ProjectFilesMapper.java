package com.csust.hackathonserver.mapper;

import com.csust.hackathonserver.pojo.ProjectFiles;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author 21201
* @description 针对表【project_files】的数据库操作Mapper
* @createDate 2026-07-04 20:43:15
* @Entity com.csust.hackathonserver.pojo.ProjectFiles
*/
@Mapper
public interface ProjectFilesMapper {

    @Insert("insert into project_files (project_id, uploaded_by, original_name, stored_name, file_type, " +
            "mime_type, size_bytes, sha256, file_path, feishu_file_token, created_at) values " +
            "(#{projectId}, #{uploadedBy}, #{originalName}, #{storedName}, #{fileType}, #{mimeType}, " +
            "#{sizeBytes}, #{sha256}, #{filePath}, #{feishuFileToken}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ProjectFiles file);

    @Select("select * from project_files where project_id = #{projectId} order by created_at desc")
    List<ProjectFiles> findByProjectId(@Param("projectId") Long projectId);
}




