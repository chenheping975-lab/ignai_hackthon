package com.csust.hackathonserver.pojo;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @TableName projects
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Projects {
    private Long id;

    private Long eventId;

    private Long teamId;

    private Long trackId;

    private String title;

    private String tagline;

    private String description;

    private String demoUrl;

    private String codeUrl;

    private Long coverFileId;

    private Object status;

    private LocalDateTime submittedAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

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
        Projects other = (Projects) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getEventId() == null ? other.getEventId() == null : this.getEventId().equals(other.getEventId()))
            && (this.getTeamId() == null ? other.getTeamId() == null : this.getTeamId().equals(other.getTeamId()))
            && (this.getTrackId() == null ? other.getTrackId() == null : this.getTrackId().equals(other.getTrackId()))
            && (this.getTitle() == null ? other.getTitle() == null : this.getTitle().equals(other.getTitle()))
            && (this.getTagline() == null ? other.getTagline() == null : this.getTagline().equals(other.getTagline()))
            && (this.getDescription() == null ? other.getDescription() == null : this.getDescription().equals(other.getDescription()))
            && (this.getDemoUrl() == null ? other.getDemoUrl() == null : this.getDemoUrl().equals(other.getDemoUrl()))
            && (this.getCodeUrl() == null ? other.getCodeUrl() == null : this.getCodeUrl().equals(other.getCodeUrl()))
            && (this.getCoverFileId() == null ? other.getCoverFileId() == null : this.getCoverFileId().equals(other.getCoverFileId()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getSubmittedAt() == null ? other.getSubmittedAt() == null : this.getSubmittedAt().equals(other.getSubmittedAt()))
            && (this.getCreatedAt() == null ? other.getCreatedAt() == null : this.getCreatedAt().equals(other.getCreatedAt()))
            && (this.getUpdatedAt() == null ? other.getUpdatedAt() == null : this.getUpdatedAt().equals(other.getUpdatedAt()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getEventId() == null) ? 0 : getEventId().hashCode());
        result = prime * result + ((getTeamId() == null) ? 0 : getTeamId().hashCode());
        result = prime * result + ((getTrackId() == null) ? 0 : getTrackId().hashCode());
        result = prime * result + ((getTitle() == null) ? 0 : getTitle().hashCode());
        result = prime * result + ((getTagline() == null) ? 0 : getTagline().hashCode());
        result = prime * result + ((getDescription() == null) ? 0 : getDescription().hashCode());
        result = prime * result + ((getDemoUrl() == null) ? 0 : getDemoUrl().hashCode());
        result = prime * result + ((getCodeUrl() == null) ? 0 : getCodeUrl().hashCode());
        result = prime * result + ((getCoverFileId() == null) ? 0 : getCoverFileId().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getSubmittedAt() == null) ? 0 : getSubmittedAt().hashCode());
        result = prime * result + ((getCreatedAt() == null) ? 0 : getCreatedAt().hashCode());
        result = prime * result + ((getUpdatedAt() == null) ? 0 : getUpdatedAt().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", eventId=").append(eventId);
        sb.append(", teamId=").append(teamId);
        sb.append(", trackId=").append(trackId);
        sb.append(", title=").append(title);
        sb.append(", tagline=").append(tagline);
        sb.append(", description=").append(description);
        sb.append(", demoUrl=").append(demoUrl);
        sb.append(", codeUrl=").append(codeUrl);
        sb.append(", coverFileId=").append(coverFileId);
        sb.append(", status=").append(status);
        sb.append(", submittedAt=").append(submittedAt);
        sb.append(", createdAt=").append(createdAt);
        sb.append(", updatedAt=").append(updatedAt);
        sb.append("]");
        return sb.toString();
    }
}