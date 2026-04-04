package com.bupt.tarecruitment.service;

import com.bupt.tarecruitment.dao.ApplicationDAO;
import com.bupt.tarecruitment.dao.PositionDAO;
import com.bupt.tarecruitment.dao.UserDAO;
import com.bupt.tarecruitment.model.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 统计服务
 * 提供各种统计数据计算功能
 */
public class StatisticsService {
    
    private UserDAO userDAO;
    private PositionDAO positionDAO;
    private ApplicationDAO applicationDAO;
    
    public StatisticsService() {
        this.userDAO = new UserDAO();
        this.positionDAO = new PositionDAO();
        this.applicationDAO = new ApplicationDAO();
    }
    
    /**
     * 获取TA的统计数据
     */
    public Map<String, Integer> getTAStats(String userId) {
        Map<String, Integer> stats = new HashMap<>();
        List<Application> apps = applicationDAO.findByTaId(userId);
        
        stats.put("total", apps.size());
        stats.put("pending", (int) apps.stream()
            .filter(a -> a.getStatus() == ApplicationStatus.PENDING).count());
        stats.put("selected", (int) apps.stream()
            .filter(a -> a.getStatus() == ApplicationStatus.SELECTED).count());
        
        // 计算总工时
        int totalHours = apps.stream()
            .filter(a -> a.getStatus() == ApplicationStatus.SELECTED)
            .mapToInt(a -> {
                Position pos = positionDAO.findById(a.getPositionId());
                return pos != null ? pos.getHours() : 0;
            })
            .sum();
        stats.put("hours", totalHours);
        
        return stats;
    }
    
    /**
     * 获取MO的统计数据
     */
    public Map<String, Integer> getMOStats(String userId) {
        Map<String, Integer> stats = new HashMap<>();
        List<Position> positions = positionDAO.findByMoId(userId);
        
        stats.put("totalPositions", positions.size());
        stats.put("openPositions", (int) positions.stream()
            .filter(p -> p.getStatus() == PositionStatus.OPEN).count());
        
        int totalApps = 0;
        int pendingApps = 0;
        for (Position pos : positions) {
            List<Application> apps = applicationDAO.findByPositionId(pos.getPositionId());
            totalApps += apps.size();
            pendingApps += (int) apps.stream()
                .filter(a -> a.getStatus() == ApplicationStatus.PENDING).count();
        }
        stats.put("totalApplications", totalApps);
        stats.put("pendingApplications", pendingApps);
        
        return stats;
    }
    
    /**
     * 获取Admin的统计数据
     */
    public Map<String, Object> getAdminStats() {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            List<User> allUsers = userDAO.loadAll();
            stats.put("totalUsers", allUsers.size());
            stats.put("totalTAs", (int) allUsers.stream()
                .filter(u -> u.getRole() == UserRole.TA).count());
            stats.put("totalMOs", (int) allUsers.stream()
                .filter(u -> u.getRole() == UserRole.MO).count());
            
            stats.put("totalPositions", positionDAO.loadAll().size());
            stats.put("totalApplications", applicationDAO.loadAll().size());
            
            // 平均工时
            List<User> tas = allUsers.stream()
                .filter(u -> u.getRole() == UserRole.TA)
                .collect(Collectors.toList());
            
            if (!tas.isEmpty()) {
                double avgHours = tas.stream()
                    .mapToInt(ta -> getTAStats(ta.getUserId()).get("hours"))
                    .average()
                    .orElse(0.0);
                stats.put("avgHours", String.format("%.1f", avgHours));
            } else {
                stats.put("avgHours", "0.0");
            }
            
        } catch (Exception e) {
            // 如果出错，返回默认值
            stats.put("totalUsers", 0);
            stats.put("totalTAs", 0);
            stats.put("totalMOs", 0);
            stats.put("totalPositions", 0);
            stats.put("totalApplications", 0);
            stats.put("avgHours", "0.0");
        }
        
        return stats;
    }
}
