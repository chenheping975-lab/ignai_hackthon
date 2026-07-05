package com.csust.hackathonserver.pojo;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @TableName form_fields
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormFields {
    private Long id;

    private Long eventId;

    private Object targetType;

    private String fieldKey;

    private String label;

    private Object fieldType;

    private Integer required;

    private Object optionsJson;

    private String placeholder;

    private Integer sortOrder;

    private Integer enabled;

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
        FormFields other = (FormFields) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getEventId() == null ? other.getEventId() == null : this.getEventId().equals(other.getEventId()))
            && (this.getTargetType() == null ? other.getTargetType() == null : this.getTargetType().equals(other.getTargetType()))
            && (this.getFieldKey() == null ? other.getFieldKey() == null : this.getFieldKey().equals(other.getFieldKey()))
            && (this.getLabel() == null ? other.getLabel() == null : this.getLabel().equals(other.getLabel()))
            && (this.getFieldType() == null ? other.getFieldType() == null : this.getFieldType().equals(other.getFieldType()))
            && (this.getRequired() == null ? other.getRequired() == null : this.getRequired().equals(other.getRequired()))
            && (this.getOptionsJson() == null ? other.getOptionsJson() == null : this.getOptionsJson().equals(other.getOptionsJson()))
            && (this.getPlaceholder() == null ? other.getPlaceholder() == null : this.getPlaceholder().equals(other.getPlaceholder()))
            && (this.getSortOrder() == null ? other.getSortOrder() == null : this.getSortOrder().equals(other.getSortOrder()))
            && (this.getEnabled() == null ? other.getEnabled() == null : this.getEnabled().equals(other.getEnabled()))
            && (this.getCreatedAt() == null ? other.getCreatedAt() == null : this.getCreatedAt().equals(other.getCreatedAt()))
            && (this.getUpdatedAt() == null ? other.getUpdatedAt() == null : this.getUpdatedAt().equals(other.getUpdatedAt()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getEventId() == null) ? 0 : getEventId().hashCode());
        result = prime * result + ((getTargetType() == null) ? 0 : getTargetType().hashCode());
        result = prime * result + ((getFieldKey() == null) ? 0 : getFieldKey().hashCode());
        result = prime * result + ((getLabel() == null) ? 0 : getLabel().hashCode());
        result = prime * result + ((getFieldType() == null) ? 0 : getFieldType().hashCode());
        result = prime * result + ((getRequired() == null) ? 0 : getRequired().hashCode());
        result = prime * result + ((getOptionsJson() == null) ? 0 : getOptionsJson().hashCode());
        result = prime * result + ((getPlaceholder() == null) ? 0 : getPlaceholder().hashCode());
        result = prime * result + ((getSortOrder() == null) ? 0 : getSortOrder().hashCode());
        result = prime * result + ((getEnabled() == null) ? 0 : getEnabled().hashCode());
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
        sb.append(", targetType=").append(targetType);
        sb.append(", fieldKey=").append(fieldKey);
        sb.append(", label=").append(label);
        sb.append(", fieldType=").append(fieldType);
        sb.append(", required=").append(required);
        sb.append(", optionsJson=").append(optionsJson);
        sb.append(", placeholder=").append(placeholder);
        sb.append(", sortOrder=").append(sortOrder);
        sb.append(", enabled=").append(enabled);
        sb.append(", createdAt=").append(createdAt);
        sb.append(", updatedAt=").append(updatedAt);
        sb.append("]");
        return sb.toString();
    }
}