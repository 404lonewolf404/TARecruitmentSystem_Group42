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
 * Service for TA favorite position operations.
 */
public class FavoriteService {

    private FavoriteDAO favoriteDAO;
    private PositionDAO positionDAO;

    /**
     * Creates the favorite service.
     */
    public FavoriteService() {
        this.favoriteDAO = new FavoriteDAO();
        this.positionDAO = new PositionDAO();
    }

    /**
     * Adds a position to the TA's favorites.
     *
     * @param taId taId value
     * @param positionId positionId value
     * @return operation result
     * @throws IllegalArgumentException if operation fails
     * @throws IOException if operation fails
     */
    public Favorite addFavorite(String taId, String positionId)
            throws IllegalArgumentException, IOException {

        // 中文说明：校验必要参数。
        if (taId == null || taId.trim().isEmpty()) {
            throw new IllegalArgumentException("TA ID cannot be empty");
        }

        if (positionId == null || positionId.trim().isEmpty()) {
            throw new IllegalArgumentException("Position ID cannot be empty");
        }

        // 中文说明：确认岗位存在。
        Position position = positionDAO.findById(positionId.trim());
        if (position == null) {
            throw new IllegalArgumentException("Position not found");
        }

        // 中文说明：避免重复收藏。
        if (favoriteDAO.isFavorited(taId.trim(), positionId.trim())) {
            throw new IllegalArgumentException("This position is already in your favorites");
        }

        // 中文说明：组装收藏记录。
        Favorite favorite = new Favorite();
        favorite.setFavoriteId(UUID.randomUUID().toString());
        favorite.setTaId(taId.trim());
        favorite.setPositionId(positionId.trim());
        favorite.setCreatedAt(new Date());

        // 中文说明：持久化收藏记录。
        favoriteDAO.add(favorite);

        return favorite;
    }

    /**
     * Removes a position from the TA's favorites.
     *
     * @param taId taId value
     * @param positionId positionId value
     * @throws IllegalArgumentException if operation fails
     * @throws IOException if operation fails
     */
    public void removeFavorite(String taId, String positionId)
            throws IllegalArgumentException, IOException {

        // 中文说明：校验必要参数。
        if (taId == null || taId.trim().isEmpty()) {
            throw new IllegalArgumentException("TA ID cannot be empty");
        }

        if (positionId == null || positionId.trim().isEmpty()) {
            throw new IllegalArgumentException("Position ID cannot be empty");
        }

        // 中文说明：先查出已有收藏记录。
        Favorite favorite = favoriteDAO.findByTaAndPosition(taId.trim(), positionId.trim());
        if (favorite == null) {
            throw new IllegalArgumentException("This position is not in your favorites");
        }

        // 中文说明：删除收藏记录。
        favoriteDAO.delete(favorite.getFavoriteId());
    }

    /**
     * Loads all favorited positions for a TA.
     *
     * @param taId taId value
     * @return operation result
     */
    public List<Position> getFavoritePositions(String taId) {
        if (taId == null || taId.trim().isEmpty()) {
            return new ArrayList<>();
        }

        // 中文说明：读取 TA 的全部收藏记录。
        List<Favorite> favorites = favoriteDAO.findByTaId(taId.trim());

        // 中文说明：把收藏记录映射成岗位详情列表。
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
     * Returns whether the TA has favorited the target position.
     *
     * @param taId taId value
     * @param positionId positionId value
     * @return operation result
     */
    public boolean isFavorited(String taId, String positionId) {
        if (taId == null || taId.trim().isEmpty()
                || positionId == null || positionId.trim().isEmpty()) {
            return false;
        }

        return favoriteDAO.isFavorited(taId.trim(), positionId.trim());
    }

    /**
     * Returns how many users have favorited the target position.
     *
     * @param positionId positionId value
     * @return operation result
     */
    public int getFavoriteCount(String positionId) {
        if (positionId == null || positionId.trim().isEmpty()) {
            return 0;
        }

        return favoriteDAO.findByPositionId(positionId.trim()).size();
    }

    /**
     * Returns how many favorites a TA currently has.
     *
     * @param taId taId value
     * @return operation result
     */
    public int getTotalFavorites(String taId) {
        if (taId == null || taId.trim().isEmpty()) {
            return 0;
        }

        return favoriteDAO.findByTaId(taId.trim()).size();
    }
}
