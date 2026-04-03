package com.bupt.tarecruitment.service;

import com.bupt.tarecruitment.dao.PositionDAO;
import com.bupt.tarecruitment.model.Position;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 搜索服务类
 * 处理职位搜索、过滤和排序功能
 */
public class SearchService {
    
    private PositionDAO positionDAO;
    
    public SearchService() {
        this.positionDAO = new PositionDAO();
    }
    
    /**
     * 搜索职位
     * @param keyword 关键词
     * @param minHours 最小工时
     * @param maxHours 最大工时
     * @param sortBy 排序方式
     * @return 搜索结果
     */
    public List<Position> searchPositions(String keyword, Integer minHours, Integer maxHours, String sortBy) {
        System.out.println("=== SearchService.searchPositions ===");
        System.out.println("keyword: " + keyword);
        System.out.println("minHours: " + minHours);
        System.out.println("maxHours: " + maxHours);
        System.out.println("sortBy: " + sortBy);
        
        List<Position> positions = positionDAO.findAllOpen();
        System.out.println("Initial positions count: " + positions.size());
        
        // 关键词搜索
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
        
        // 工时过滤
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
        
        // 排序
        if ("hours_asc".equals(sortBy)) {
            System.out.println("Sorting by hours ascending");
            positions.sort(Comparator.comparingInt(Position::getHours));
        } else if ("hours_desc".equals(sortBy)) {
            System.out.println("Sorting by hours descending");
            positions.sort(Comparator.comparingInt(Position::getHours).reversed());
        } else {
            System.out.println("Sorting by creation date (newest first)");
            // 默认按创建时间排序（最新的在前）
            positions.sort((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()));
        }
        
        System.out.println("Final positions count: " + positions.size());
        return positions;
    }
}