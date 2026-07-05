package com.csust.hackathonserver.pojo;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @TableName registrations
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Registrations {
    private Long id;

    private Long eventId;

    private Long userId;

    private Object registrationType;

    private String teamName;

    private String contactName;

    private String contactPhone;

    private String contactEmail;

    private Long trackId;

    private Object status;

    private Object payloadJson;

    private Object aiTagsJson;

    private String aiSummary;

    private String reviewNote;

    private Long reviewedBy;

    private LocalDateTime reviewedAt;

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
        Registrations other = (Registrations) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getEventId() == null ? other.getEventId() == null : this.getEventId().equals(other.getEventId()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getRegistrationType() == null ? other.getRegistrationType() == null : this.getRegistrationType().equals(other.getRegistrationType()))
            && (this.getTeamName() == null ? other.getTeamName() == null : this.getTeamName().equals(other.getTeamName()))
            && (this.getContactName() == null ? other.getContactName() == null : this.getContactName().equals(other.getContactName()))
            && (this.getContactPhone() == null ? other.getContactPhone() == null : this.getContactPhone().equals(other.getContactPhone()))
            && (this.getContactEmail() == null ? other.getContactEmail() == null : this.getContactEmail().equals(other.getContactEmail()))
            && (this.getTrackId() == null ? other.getTrackId() == null : this.getTrackId().equals(other.getTrackId()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getPayloadJson() == null ? other.getPayloadJson() == null : this.getPayloadJson().equals(other.getPayloadJson()))
            && (this.getAiTagsJson() == null ? other.getAiTagsJson() == null : this.getAiTagsJson().equals(other.getAiTagsJson()))
            && (this.getAiSummary() == null ? other.getAiSummary() == null : this.getAiSummary().equals(other.getAiSummary()))
            && (this.getReviewNote() == null ? other.getReviewNote() == null : this.getReviewNote().equals(other.getReviewNote()))
            && (this.getReviewedBy() == null ? other.getReviewedBy() == null : this.getReviewedBy().equals(other.getReviewedBy()))
            && (this.getReviewedAt() == null ? other.getReviewedAt() == null : this.getReviewedAt().equals(other.getReviewedAt()))
            && (this.getCreatedAt() == null ? other.getCreatedAt() == null : this.getCreatedAt().equals(other.getCreatedAt()))
            && (this.getUpdatedAt() == null ? other.getUpdatedAt() == null : this.getUpdatedAt().equals(other.getUpdatedAt()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getEventId() == null) ? 0 : getEventId().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getRegistrationType() == null) ? 0 : getRegistrationType().hashCode());
        result = prime * result + ((getTeamName() == null) ? 0 : getTeamName().hashCode());
        result = prime * result + ((getContactName() == null) ? 0 : getContactName().hashCode());
        result = prime * result + ((getContactPhone() == null) ? 0 : getContactPhone().hashCode());
        result = prime * result + ((getContactEmail() == null) ? 0 : getContactEmail().hashCode());
        result = prime * result + ((getTrackId() == null) ? 0 : getTrackId().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getPayloadJson() == null) ? 0 : getPayloadJson().hashCode());
        result = prime * result + ((getAiTagsJson() == null) ? 0 : getAiTagsJson().hashCode());
        result = prime * result + ((getAiSummary() == null) ? 0 : getAiSummary().hashCode());
        result = prime * result + ((getReviewNote() == null) ? 0 : getReviewNote().hashCode());
        result = prime * result + ((getReviewedBy() == null) ? 0 : getReviewedBy().hashCode());
        result = prime * result + ((getReviewedAt() == null) ? 0 : getReviewedAt().hashCode());
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
        sb.append(", userId=").append(userId);
        sb.append(", registrationType=").append(registrationType);
        sb.append(", teamName=").append(teamName);
        sb.append(", contactName=").append(contactName);
        sb.append(", contactPhone=").append(contactPhone);
        sb.append(", contactEmail=").append(contactEmail);
        sb.append(", trackId=").append(trackId);
        sb.append(", status=").append(status);
        sb.append(", payloadJson=").append(payloadJson);
        sb.append(", aiTagsJson=").append(aiTagsJson);
        sb.append(", aiSummary=").append(aiSummary);
        sb.append(", reviewNote=").append(reviewNote);
        sb.append(", reviewedBy=").append(reviewedBy);
        sb.append(", reviewedAt=").append(reviewedAt);
        sb.append(", createdAt=").append(createdAt);
        sb.append(", updatedAt=").append(updatedAt);
        sb.append("]");
        return sb.toString();
    }
}