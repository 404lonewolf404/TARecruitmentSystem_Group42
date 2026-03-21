package com.bupt.tarecruitment.model;

import java.util.Date;
import java.util.Objects;

/**
 * 申请实体类
 * 表示TA对特定职位的申请请求
 */
public class Application {
    private String applicationId; // 唯一标识符（UUID）
    private String taId;          // 申请者TA的userId
    private String positionId;    // 申请的职位ID
    private ApplicationStatus status; // 状态：PENDING, SELECTED, REJECTED, WITHDRAWN
    private Date appliedAt;       // 申请时间
    private String resumePath;    // 简历文件路径

    /**
     * 默认构造函数
     */
    public Application() {
    }

    /**
     * 完整构造函数
     */
    public Application(String applicationId, String taId, String positionId,
                      ApplicationStatus status, Date appliedAt, String resumePath) {
        this.applicationId = applicationId;
        this.taId = taId;
        this.positionId = positionId;
        this.status = status;
        this.appliedAt = appliedAt;
        this.resumePath = resumePath;
    }

    // Getter和Setter方法

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getTaId() {
        return taId;
    }

    public void setTaId(String taId) {
        this.taId = taId;
    }

    public String getPositionId() {
        return positionId;
    }

    public void setPositionId(String positionId) {
        this.positionId = positionId;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public Date getAppliedAt() {
        return appliedAt;
    }

    public void setAppliedAt(Date appliedAt) {
        this.appliedAt = appliedAt;
    }

    public String getResumePath() {
        return resumePath;
    }

    public void setResumePath(String resumePath) {
        this.resumePath = resumePath;
    }

    /**
     * equals方法 - 基于所有字段比较
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Application that = (Application) o;
        return Objects.equals(applicationId, that.applicationId) &&
               Objects.equals(taId, that.taId) &&
               Objects.equals(positionId, that.positionId) &&
               status == that.status &&
               Objects.equals(appliedAt, that.appliedAt) &&
               Objects.equals(resumePath, that.resumePath);
    }

    /**
     * hashCode方法 - 基于所有字段生成
     */
    @Override
    public int hashCode() {
        return Objects.hash(applicationId, taId, positionId, status, appliedAt, resumePath);
    }

    /**
     * toString方法 - 用于调试
     */
    @Override
    public String toString() {
        return "Application{" +
                "applicationId='" + applicationId + '\'' +
                ", taId='" + taId + '\'' +
                ", positionId='" + positionId + '\'' +
                ", status=" + status +
                ", appliedAt=" + appliedAt +
                ", resumePath='" + resumePath + '\'' +
                '}';
    }
}
