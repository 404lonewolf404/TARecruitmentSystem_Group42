package com.bupt.tarecruitment.model;

import java.util.Date;

/**
 * Favorite entity for bookmarked positions.
 */
public class Favorite {
    
    private String favoriteId;      // 中文说明：收藏记录主键。
    private String taId;            // 中文说明：收藏该岗位的 TA 用户 ID。
    private String positionId;      // 中文说明：被收藏的岗位 ID。
    private Date createdAt;         // 中文说明：收藏创建时间。
    
    /**
     * Creates an empty favorite entity.
     */
    public Favorite() {
    }
    
    /**
     * Creates a favorite entity with all fields.
     */
    public Favorite(String favoriteId, String taId, String positionId, Date createdAt) {
        this.favoriteId = favoriteId;
        this.taId = taId;
        this.positionId = positionId;
        this.createdAt = createdAt;
    }
    
    // 中文说明：以下为基础访问器方法。
    
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
