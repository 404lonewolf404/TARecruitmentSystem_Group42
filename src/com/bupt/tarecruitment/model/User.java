package com.bupt.tarecruitment.model;

import java.util.Date;
import java.util.Objects;

/**
 * 用户实体�?
 * 表示系统中的用户（TA、MO或Admin�?
 */
public class User {
    private String userId;        // 唯一标识符（UUID�?
    private String name;          // 用户姓名
    private String email;         // 邮箱（唯一�?
    private String password;      // 密码（应加密存储�?
    private UserRole role;        // 角色：TA, MO, ADMIN
    private String skills;        // TA技能（仅TA角色使用�?
    private String cvPath;        // CV文件路径（仅TA角色使用�?
    private Date createdAt;       // 创建时间

    /**
     * 默认构造函�?
     */
    public User() {
    }

    /**
     * 完整构造函�?
     */
    public User(String userId, String name, String email, String password, 
                UserRole role, String skills, String cvPath, Date createdAt) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.skills = skills;
        this.cvPath = cvPath;
        this.createdAt = createdAt;
    }

    // Getter和Setter方法

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public String getCvPath() {
        return cvPath;
    }

    public void setCvPath(String cvPath) {
        this.cvPath = cvPath;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * equals方法 - 基于userId比较
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(userId, user.userId) &&
               Objects.equals(name, user.name) &&
               Objects.equals(email, user.email) &&
               Objects.equals(password, user.password) &&
               role == user.role &&
               Objects.equals(skills, user.skills) &&
               Objects.equals(cvPath, user.cvPath) &&
               Objects.equals(createdAt, user.createdAt);
    }

    /**
     * hashCode方法 - 基于userId生成
     */
    @Override
    public int hashCode() {
        return Objects.hash(userId, name, email, password, role, skills, cvPath, createdAt);
    }

    /**
     * toString方法 - 用于调试
     */
    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", skills='" + skills + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
