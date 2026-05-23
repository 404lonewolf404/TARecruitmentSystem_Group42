package com.bupt.tarecruitment.model;

import java.util.Date;
import java.util.Objects;

/**
 * Application entity for a TA's position submission.
 */
public class Application {
    private String applicationId; // 中文说明：申请记录主键。
    private String taId;          // 中文说明：申请人的用户 ID。
    private String positionId;    // 中文说明：目标岗位 ID。
    private ApplicationStatus status; // 中文说明：当前申请状态。
    private Date appliedAt;       // 中文说明：提交申请的时间。
    private String resumePath;    // 中文说明：简历文件相对路径。

    /**
     * Creates an empty application entity.
     */
    public Application() {
    }

    /**
     * Creates an application entity with all fields.
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

    // 中文说明：以下为基础访问器方法。

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
     * Compares two application objects by field values.
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
     * Returns the hash code of the application object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(applicationId, taId, positionId, status, appliedAt, resumePath);
    }

    /**
     * Returns the readable string form of the application object.
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
