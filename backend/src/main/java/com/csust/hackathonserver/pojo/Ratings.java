package com.csust.hackathonserver.pojo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @TableName ratings
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ratings {
    private Long id;

    private Long projectId;

    private Long raterUserId;

    private String raterKey;

    private Object sourceType;

    private BigDecimal score;

    private String comment;

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
        Ratings other = (Ratings) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getProjectId() == null ? other.getProjectId() == null : this.getProjectId().equals(other.getProjectId()))
            && (this.getRaterUserId() == null ? other.getRaterUserId() == null : this.getRaterUserId().equals(other.getRaterUserId()))
            && (this.getRaterKey() == null ? other.getRaterKey() == null : this.getRaterKey().equals(other.getRaterKey()))
            && (this.getSourceType() == null ? other.getSourceType() == null : this.getSourceType().equals(other.getSourceType()))
            && (this.getScore() == null ? other.getScore() == null : this.getScore().equals(other.getScore()))
            && (this.getComment() == null ? other.getComment() == null : this.getComment().equals(other.getComment()))
            && (this.getCreatedAt() == null ? other.getCreatedAt() == null : this.getCreatedAt().equals(other.getCreatedAt()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getProjectId() == null) ? 0 : getProjectId().hashCode());
        result = prime * result + ((getRaterUserId() == null) ? 0 : getRaterUserId().hashCode());
        result = prime * result + ((getRaterKey() == null) ? 0 : getRaterKey().hashCode());
        result = prime * result + ((getSourceType() == null) ? 0 : getSourceType().hashCode());
        result = prime * result + ((getScore() == null) ? 0 : getScore().hashCode());
        result = prime * result + ((getComment() == null) ? 0 : getComment().hashCode());
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
        sb.append(", raterUserId=").append(raterUserId);
        sb.append(", raterKey=").append(raterKey);
        sb.append(", sourceType=").append(sourceType);
        sb.append(", score=").append(score);
        sb.append(", comment=").append(comment);
        sb.append(", createdAt=").append(createdAt);
        sb.append("]");
        return sb.toString();
    }
}