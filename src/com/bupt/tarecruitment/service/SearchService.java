package com.bupt.tarecruitment.service;

import com.bupt.tarecruitment.dao.PositionDAO;
import com.bupt.tarecruitment.model.Position;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Search service for open positions.
 */
public class SearchService {

    private PositionDAO positionDAO;

    public SearchService() {
        this.positionDAO = new PositionDAO();
    }

    /**
     * Searches open positions by keyword, hour range, and sort rule.
     *
     * @param keyword keyword value
     * @return operation result
     */
    public List<Position> searchPositions(String keyword, Integer minHours, Integer maxHours, String sortBy) {
        System.out.println("=== SearchService.searchPositions ===");
        System.out.println("keyword: " + keyword);
        System.out.println("minHours: " + minHours);
        System.out.println("maxHours: " + maxHours);
        System.out.println("sortBy: " + sortBy);

        List<Position> positions = positionDAO.findAllOpen();
        System.out.println("Initial positions count: " + positions.size());

        // 中文说明：过滤掉已过期岗位。
        positions = positions.stream()
            .filter(p -> !p.isExpired())
            .collect(Collectors.toList());
        System.out.println("After expiration filter: " + positions.size());

        // 中文说明：根据关键词匹配标题、描述和要求。
        if (keyword != null && !keyword.trim().isEmpty()) {
            String lowerKeyword = keyword.toLowerCase();
            System.out.println("Filtering by keyword: " + lowerKeyword);
            positions = positions.stream()
                .filter(p ->
                    p.getTitle().toLowerCase().contains(lowerKeyword) ||
                    p.getDescription().toLowerCase().contains(lowerKeyword) ||
                    (p.getRequirements() != null && p.getRequirements().toLowerCase().contains(lowerKeyword))
                )
                .collect(Collectors.toList());
            System.out.println("After keyword filter: " + positions.size());
        }

        // 中文说明：按工时区间过滤岗位。
        if (minHours != null) {
            System.out.println("Filtering by minHours: " + minHours);
            positions = positions.stream()
                .filter(p -> p.getHours() >= minHours)
                .collect(Collectors.toList());
            System.out.println("After minHours filter: " + positions.size());
        }
        if (maxHours != null) {
            System.out.println("Filtering by maxHours: " + maxHours);
            positions = positions.stream()
                .filter(p -> p.getHours() <= maxHours)
                .collect(Collectors.toList());
            System.out.println("After maxHours filter: " + positions.size());
        }

        // 中文说明：根据前端传入的规则排序结果。
        if ("hours_asc".equals(sortBy)) {
            System.out.println("Sorting by hours ascending");
            positions.sort(Comparator.comparingInt(Position::getHours));
        } else if ("hours_desc".equals(sortBy)) {
            System.out.println("Sorting by hours descending");
            positions.sort(Comparator.comparingInt(Position::getHours).reversed());
        } else {
            System.out.println("Sorting by creation date (newest first)");
            // 中文说明：默认按创建时间倒序排列。
            positions.sort((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()));
        }

        System.out.println("Final positions count: " + positions.size());
        return positions;
    }
}
