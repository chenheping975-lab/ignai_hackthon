package com.csust.hackathonserver.pojo;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @TableName events
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Events {
    private Long id;

    private String title;

    private String subtitle;

    private String location;

    private String description;

    private Object status;

    private Integer registrationOpen;

    private Integer ratingEnabled;

    private Integer voteEnabled;

    private LocalDateTime registrationDeadline;

    private LocalDateTime submissionDeadline;

    private LocalDateTime ratingStartAt;

    private LocalDateTime ratingEndAt;

    private String officialSiteUrl;

    private String benchmarkSiteUrl;

    private Long createdBy;

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
        Events other = (Events) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getTitle() == null ? other.getTitle() == null : this.getTitle().equals(other.getTitle()))
            && (this.getSubtitle() == null ? other.getSubtitle() == null : this.getSubtitle().equals(other.getSubtitle()))
            && (this.getLocation() == null ? other.getLocation() == null : this.getLocation().equals(other.getLocation()))
            && (this.getDescription() == null ? other.getDescription() == null : this.getDescription().equals(other.getDescription()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getRegistrationOpen() == null ? other.getRegistrationOpen() == null : this.getRegistrationOpen().equals(other.getRegistrationOpen()))
            && (this.getRatingEnabled() == null ? other.getRatingEnabled() == null : this.getRatingEnabled().equals(other.getRatingEnabled()))
            && (this.getVoteEnabled() == null ? other.getVoteEnabled() == null : this.getVoteEnabled().equals(other.getVoteEnabled()))
            && (this.getRegistrationDeadline() == null ? other.getRegistrationDeadline() == null : this.getRegistrationDeadline().equals(other.getRegistrationDeadline()))
            && (this.getSubmissionDeadline() == null ? other.getSubmissionDeadline() == null : this.getSubmissionDeadline().equals(other.getSubmissionDeadline()))
            && (this.getRatingStartAt() == null ? other.getRatingStartAt() == null : this.getRatingStartAt().equals(other.getRatingStartAt()))
            && (this.getRatingEndAt() == null ? other.getRatingEndAt() == null : this.getRatingEndAt().equals(other.getRatingEndAt()))
            && (this.getOfficialSiteUrl() == null ? other.getOfficialSiteUrl() == null : this.getOfficialSiteUrl().equals(other.getOfficialSiteUrl()))
            && (this.getBenchmarkSiteUrl() == null ? other.getBenchmarkSiteUrl() == null : this.getBenchmarkSiteUrl().equals(other.getBenchmarkSiteUrl()))
            && (this.getCreatedBy() == null ? other.getCreatedBy() == null : this.getCreatedBy().equals(other.getCreatedBy()))
            && (this.getCreatedAt() == null ? other.getCreatedAt() == null : this.getCreatedAt().equals(other.getCreatedAt()))
            && (this.getUpdatedAt() == null ? other.getUpdatedAt() == null : this.getUpdatedAt().equals(other.getUpdatedAt()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getTitle() == null) ? 0 : getTitle().hashCode());
        result = prime * result + ((getSubtitle() == null) ? 0 : getSubtitle().hashCode());
        result = prime * result + ((getLocation() == null) ? 0 : getLocation().hashCode());
        result = prime * result + ((getDescription() == null) ? 0 : getDescription().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getRegistrationOpen() == null) ? 0 : getRegistrationOpen().hashCode());
        result = prime * result + ((getRatingEnabled() == null) ? 0 : getRatingEnabled().hashCode());
        result = prime * result + ((getVoteEnabled() == null) ? 0 : getVoteEnabled().hashCode());
        result = prime * result + ((getRegistrationDeadline() == null) ? 0 : getRegistrationDeadline().hashCode());
        result = prime * result + ((getSubmissionDeadline() == null) ? 0 : getSubmissionDeadline().hashCode());
        result = prime * result + ((getRatingStartAt() == null) ? 0 : getRatingStartAt().hashCode());
        result = prime * result + ((getRatingEndAt() == null) ? 0 : getRatingEndAt().hashCode());
        result = prime * result + ((getOfficialSiteUrl() == null) ? 0 : getOfficialSiteUrl().hashCode());
        result = prime * result + ((getBenchmarkSiteUrl() == null) ? 0 : getBenchmarkSiteUrl().hashCode());
        result = prime * result + ((getCreatedBy() == null) ? 0 : getCreatedBy().hashCode());
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
        sb.append(", title=").append(title);
        sb.append(", subtitle=").append(subtitle);
        sb.append(", location=").append(location);
        sb.append(", description=").append(description);
        sb.append(", status=").append(status);
        sb.append(", registrationOpen=").append(registrationOpen);
        sb.append(", ratingEnabled=").append(ratingEnabled);
        sb.append(", voteEnabled=").append(voteEnabled);
        sb.append(", registrationDeadline=").append(registrationDeadline);
        sb.append(", submissionDeadline=").append(submissionDeadline);
        sb.append(", ratingStartAt=").append(ratingStartAt);
        sb.append(", ratingEndAt=").append(ratingEndAt);
        sb.append(", officialSiteUrl=").append(officialSiteUrl);
        sb.append(", benchmarkSiteUrl=").append(benchmarkSiteUrl);
        sb.append(", createdBy=").append(createdBy);
        sb.append(", createdAt=").append(createdAt);
        sb.append(", updatedAt=").append(updatedAt);
        sb.append("]");
        return sb.toString();
    }
}