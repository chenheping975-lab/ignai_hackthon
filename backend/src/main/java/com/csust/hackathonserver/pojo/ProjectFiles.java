package com.csust.hackathonserver.pojo;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @TableName project_files
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectFiles {
    private Long id;

    private Long projectId;

    private Long uploadedBy;

    private String originalName;

    private String storedName;

    private Object fileType;

    private String mimeType;

    private Long sizeBytes;

    private String sha256;

    private String filePath;

    private String feishuFileToken;

    private LocalDateTime createdAt;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        ProjectFiles other = (ProjectFiles) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getProjectId() == null ? other.getProjectId() == null : this.getProjectId().equals(other.getProjectId()))
            && (this.getUploadedBy() == null ? other.getUploadedBy() == null : this.getUploadedBy().equals(other.getUploadedBy()))
            && (this.getOriginalName() == null ? other.getOriginalName() == null : this.getOriginalName().equals(other.getOriginalName()))
            && (this.getStoredName() == null ? other.getStoredName() == null : this.getStoredName().equals(other.getStoredName()))
            && (this.getFileType() == null ? other.getFileType() == null : this.getFileType().equals(other.getFileType()))
            && (this.getMimeType() == null ? other.getMimeType() == null : this.getMimeType().equals(other.getMimeType()))
            && (this.getSizeBytes() == null ? other.getSizeBytes() == null : this.getSizeBytes().equals(other.getSizeBytes()))
            && (this.getSha256() == null ? other.getSha256() == null : this.getSha256().equals(other.getSha256()))
            && (this.getFilePath() == null ? other.getFilePath() == null : this.getFilePath().equals(other.getFilePath()))
            && (this.getFeishuFileToken() == null ? other.getFeishuFileToken() == null : this.getFeishuFileToken().equals(other.getFeishuFileToken()))
            && (this.getCreatedAt() == null ? other.getCreatedAt() == null : this.getCreatedAt().equals(other.getCreatedAt()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getProjectId() == null) ? 0 : getProjectId().hashCode());
        result = prime * result + ((getUploadedBy() == null) ? 0 : getUploadedBy().hashCode());
        result = prime * result + ((getOriginalName() == null) ? 0 : getOriginalName().hashCode());
        result = prime * result + ((getStoredName() == null) ? 0 : getStoredName().hashCode());
        result = prime * result + ((getFileType() == null) ? 0 : getFileType().hashCode());
        result = prime * result + ((getMimeType() == null) ? 0 : getMimeType().hashCode());
        result = prime * result + ((getSizeBytes() == null) ? 0 : getSizeBytes().hashCode());
        result = prime * result + ((getSha256() == null) ? 0 : getSha256().hashCode());
        result = prime * result + ((getFilePath() == null) ? 0 : getFilePath().hashCode());
        result = prime * result + ((getFeishuFileToken() == null) ? 0 : getFeishuFileToken().hashCode());
        result = prime * result + ((getCreatedAt() == null) ? 0 : getCreatedAt().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", projectId=").append(projectId);
        sb.append(", uploadedBy=").append(uploadedBy);
        sb.append(", originalName=").append(originalName);
        sb.append(", storedName=").append(storedName);
        sb.append(", fileType=").append(fileType);
        sb.append(", mimeType=").append(mimeType);
        sb.append(", sizeBytes=").append(sizeBytes);
        sb.append(", sha256=").append(sha256);
        sb.append(", filePath=").append(filePath);
        sb.append(", feishuFileToken=").append(feishuFileToken);
        sb.append(", createdAt=").append(createdAt);
        sb.append("]");
        return sb.toString();
    }
}