package com.bupt.tarecruitment.service;

import com.bupt.tarecruitment.dao.FavoriteDAO;
import com.bupt.tarecruitment.dao.PositionDAO;
import com.bupt.tarecruitment.model.Favorite;
import com.bupt.tarecruitment.model.Position;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 收藏服务类
 * 处理职位收藏相关的业务逻辑
 */
public class FavoriteService {
    
    private FavoriteDAO favoriteDAO;
    private PositionDAO positionDAO;
    
    /**
     * 构造函数
     */
    public FavoriteService() {
        this.favoriteDAO = new FavoriteDAO();
        this.positionDAO = new PositionDAO();
    }
    
    /**
     * 添加收藏
     * 
     * @param taId TA用户ID
     * @param positionId 职位ID
     * @return 创建的收藏对象
     * @throws IllegalArgumentException 如果参数无效或已收藏
     * @throws IOException 如果数据保存失败
     */
    public Favorite addFavorite(String taId, String positionId) 
            throws IllegalArgumentException, IOException {
        
        // 验证参数
        if (taId == null || taId.trim().isEmpty()) {
            throw new IllegalArgumentException("TA ID cannot be empty");
        }
        
        if (positionId == null || positionId.trim().isEmpty()) {
            throw new IllegalArgumentException("Position ID cannot be empty");
        }
        
        // 检查职位是否存在
        Position position = positionDAO.findById(positionId.trim());
        if (position == null) {
            throw new IllegalArgumentException("Position not found");
        }
        
        // 检查是否已收藏
        if (favoriteDAO.isFavorited(taId.trim(), positionId.trim())) {
            throw new IllegalArgumentException("This position is already in your favorites");
        }
        
        // 创建收藏
        Favorite favorite = new Favorite();
        favorite.setFavoriteId(UUID.randomUUID().toString());
        favorite.setTaId(taId.trim());
        favorite.setPositionId(positionId.trim());
        favorite.setCreatedAt(new Date());
        
        // 保存收藏
        favoriteDAO.add(favorite);
        
        return favorite;
    }
    
    /**
     * 取消收藏
     * 
     * @param taId TA用户ID
     * @param positionId 职位ID
     * @throws IllegalArgumentException 如果参数无效或未收藏
     * @throws IOException 如果数据保存失败
     */
    public void removeFavorite(String taId, String positionId) 
            throws IllegalArgumentException, IOException {
        
        // 验证参数
        if (taId == null || taId.trim().isEmpty()) {
            throw new IllegalArgumentException("TA ID cannot be empty");
        }
        
        if (positionId == null || positionId.trim().isEmpty()) {
            throw new IllegalArgumentException("Position ID cannot be empty");
        }
        
        // 查找收藏记录
        Favorite favorite = favoriteDAO.findByTaAndPosition(taId.trim(), positionId.trim());
        if (favorite == null) {
            throw new IllegalArgumentException("This position is not in your favorites");
        }
        
        // 删除收藏
        favoriteDAO.delete(favorite.getFavoriteId());
    }
    
    /**
     * 获取TA的所有收藏职位
     * 
     * @param taId TA用户ID
     * @return 收藏的职位列表
     */
    public List<Position> getFavoritePositions(String taId) {
        if (taId == null || taId.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        // 获取所有收藏记录
        List<Favorite> favorites = favoriteDAO.findByTaId(taId.trim());
        
        // 获取对应的职位
        List<Position> positions = new ArrayList<>();
        for (Favorite favorite : favorites) {
            Position position = positionDAO.findById(favorite.getPositionId());
            if (position != null) {
                positions.add(position);
            }
        }
        
        return positions;
    }
    
    /**
     * 检查TA是否已收藏某职位
     * 
     * @param taId TA用户ID
     * @param positionId 职位ID
     * @return 如果已收藏返回true，否则返回false
     */
    public boolean isFavorited(String taId, String positionId) {
        if (taId == null || taId.trim().isEmpty() || 
            positionId == null || positionId.trim().isEmpty()) {
            return false;
        }
        
        return favoriteDAO.isFavorited(taId.trim(), positionId.trim());
    }
    
    /**
     * 获取职位的收藏数量
     * 
     * @param positionId 职位ID
     * @return 收藏数量
     */
    public int getFavoriteCount(String positionId) {
        if (positionId == null || positionId.trim().isEmpty()) {
            return 0;
        }
        
        return favoriteDAO.findByPositionId(positionId.trim()).size();
    }
    
    /**
     * 获取TA的收藏数量
     * 
     * @param taId TA用户ID
     * @return 收藏数量
     */
    public int getTotalFavorites(String taId) {
        if (taId == null || taId.trim().isEmpty()) {
            return 0;
        }
        
        return favoriteDAO.findByTaId(taId.trim()).size();
    }
}
