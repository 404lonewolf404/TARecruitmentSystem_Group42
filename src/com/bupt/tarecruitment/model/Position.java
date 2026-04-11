package com.bupt.tarecruitment.model;

import java.util.Date;
import java.util.Objects;

/**
 * 职位实体类
 * 表示MO创建的助教工作岗位
 */
public class Position {
    private String positionId;    // 唯一标识符（UUID）
    private String moId;          // 发布者MO的userId
    private String title;         // 职位标题
    private String description;   // 职位描述
    private String requirements;  // 职位要求
    private int hours;            // 工作时长（小时/周）
    private int maxPositions;     // 招聘名额（需要招聘的TA数量）
    private PositionStatus status; // 状态：OPEN, CLOSED
    private Date createdAt;       // 创建时间
    private Date deadline;        // 申请截止日期（可选）

    /**
     * 默认构造函数
     */
    public Position() {
    }

    /**
     * 完整构造函数
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
     * 扩展构造函数（包含截止日期）
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

    // Getter和Setter方法

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
     * 检查职位是否已过期
     */
    public boolean isExpired() {
        if (deadline == null) {
            return false;
        }
        return new Date().after(deadline);
    }
    
    /**
     * 获取剩余天数（如果有截止日期）
     * @return 剩余天数，如果已过期返回0，如果没有截止日期返回-1
     */
    public int getDaysRemaining() {
        if (deadline == null) {
            return -1;
        }
        long diff = deadline.getTime() - new Date().getTime();
        if (diff < 0) {
            return 0;
        }
        return (int) (diff / (1000 * 60 * 60 * 24));
    }
    
    /**
     * 检查职位是否可以接受申请
     */
    public boolean canAcceptApplications() {
        return status == PositionStatus.OPEN && !isExpired();
    }

    /**
     * equals方法 - 基于所有字段比较
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
     * hashCode方法 - 基于所有字段生成
     */
    @Override
    public int hashCode() {
        return Objects.hash(positionId, moId, title, description, requirements, 
                          hours, maxPositions, status, createdAt, deadline);
    }

    /**
     * toString方法 - 用于调试
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
