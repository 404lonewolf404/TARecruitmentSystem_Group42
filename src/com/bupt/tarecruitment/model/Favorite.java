package com.bupt.tarecruitment.model;

import java.util.Date;

/**
 * 收藏模型类
 * 表示TA收藏的职位
 */
public class Favorite {
    
    private String favoriteId;      // 收藏ID
    private String taId;            // TA用户ID
    private String positionId;      // 职位ID
    private Date createdAt;         // 收藏时间
    
    /**
     * 默认构造函数
     */
    public Favorite() {
    }
    
    /**
     * 完整构造函数
     */
    public Favorite(String favoriteId, String taId, String positionId, Date createdAt) {
        this.favoriteId = favoriteId;
        this.taId = taId;
        this.positionId = positionId;
        this.createdAt = createdAt;
    }
    
    // Getters and Setters
    
    public String getFavoriteId() {
        return favoriteId;
    }
    
    public void setFavoriteId(String favoriteId) {
        this.favoriteId = favoriteId;
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
    
    public Date getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public String toString() {
        return "Favorite{" +
                "favoriteId='" + favoriteId + '\'' +
                ", taId='" + taId + '\'' +
                ", positionId='" + positionId + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
