package com.bupt.tarecruitment.model;

import java.util.Date;
import java.util.Objects;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

/**
 * Position entity published by an MO user.
 */
public class Position {
    private String positionId;    // 中文说明：岗位主键。
    private String moId;          // 中文说明：发布岗位的 MO 用户 ID。
    private String title;         // 中文说明：岗位标题。
    private String description;   // 中文说明：岗位描述。
    private String requirements;  // 中文说明：岗位要求。
    private int hours;            // 中文说明：每周工时。
    private int maxPositions;     // 中文说明：招聘人数上限。
    private PositionStatus status; // 中文说明：岗位开放状态。
    private Date createdAt;       // 中文说明：岗位创建时间。
    private Date deadline;        // 中文说明：申请截止时间。

    /**
     * Creates an empty position entity.
     */
    public Position() {
    }

    /**
     * Creates a position entity without a deadline.
     */
    public Position(String positionId, String moId, String title, String description,
                   String requirements, int hours, int maxPositions, PositionStatus status, Date createdAt) {
        this.positionId = positionId;
        this.moId = moId;
        this.title = title;
        this.description = description;
        this.requirements = requirements;
        this.hours = hours;
        this.maxPositions = maxPositions;
        this.status = status;
        this.createdAt = createdAt;
        this.deadline = null;
    }
    
    /**
     * Creates a position entity with a deadline.
     */
    public Position(String positionId, String moId, String title, String description,
                   String requirements, int hours, int maxPositions, PositionStatus status, 
                   Date createdAt, Date deadline) {
        this.positionId = positionId;
        this.moId = moId;
        this.title = title;
        this.description = description;
        this.requirements = requirements;
        this.hours = hours;
        this.maxPositions = maxPositions;
        this.status = status;
        this.createdAt = createdAt;
        this.deadline = deadline;
    }

    // 中文说明：以下为基础访问器方法。

    public String getPositionId() {
        return positionId;
    }

    public void setPositionId(String positionId) {
        this.positionId = positionId;
    }

    public String getMoId() {
        return moId;
    }

    public void setMoId(String moId) {
        this.moId = moId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRequirements() {
        return requirements;
    }

    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public int getMaxPositions() {
        return maxPositions;
    }

    public void setMaxPositions(int maxPositions) {
        this.maxPositions = maxPositions;
    }

    public PositionStatus getStatus() {
        return status;
    }

    public void setStatus(PositionStatus status) {
        this.status = status;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }
    
    /**
     * Returns whether the application deadline has passed.
     */
    public boolean isExpired() {
        if (deadline == null) {
            return false;
        }
        LocalDate today = LocalDate.now();
        LocalDate deadlineDate = deadline.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return today.isAfter(deadlineDate);
    }
    
    /**
     * Returns the number of days remaining before the deadline.
     *
     * @return operation result
     */
    public int getDaysRemaining() {
        if (deadline == null) {
            return -1;
        }
        LocalDate today = LocalDate.now();
        LocalDate deadlineDate = deadline.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        long diffDays = ChronoUnit.DAYS.between(today, deadlineDate);
        if (diffDays < 0) {
            return 0;
        }
        return (int) diffDays;
    }
    
    /**
     * Returns whether the position can still receive applications.
     */
    public boolean canAcceptApplications() {
        return status == PositionStatus.OPEN && !isExpired();
    }

    /**
     * Compares two position objects by field values.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return hours == position.hours &&
               maxPositions == position.maxPositions &&
               Objects.equals(positionId, position.positionId) &&
               Objects.equals(moId, position.moId) &&
               Objects.equals(title, position.title) &&
               Objects.equals(description, position.description) &&
               Objects.equals(requirements, position.requirements) &&
               status == position.status &&
               Objects.equals(createdAt, position.createdAt) &&
               Objects.equals(deadline, position.deadline);
    }

    /**
     * Returns the hash code of the position object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(positionId, moId, title, description, requirements, 
                          hours, maxPositions, status, createdAt, deadline);
    }

    /**
     * Returns the readable string form of the position object.
     */
    @Override
    public String toString() {
        return "Position{" +
                "positionId='" + positionId + '\'' +
                ", moId='" + moId + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", requirements='" + requirements + '\'' +
                ", hours=" + hours +
                ", maxPositions=" + maxPositions +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", deadline=" + deadline +
                '}';
    }
}
