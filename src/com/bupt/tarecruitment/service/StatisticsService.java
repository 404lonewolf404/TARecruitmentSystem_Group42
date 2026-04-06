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
            // 获取所有用户
            List<User> allUsers = userDAO.loadAll();
            int totalUsers = allUsers.size();
            List<User> tas = allUsers.stream()
                .filter(u -> u.getRole() == UserRole.TA)
                .collect(Collectors.toList());
            List<User> mos = allUsers.stream()
                .filter(u -> u.getRole() == UserRole.MO)
                .collect(Collectors.toList());
            int totalTAs = tas.size();
            int totalMOs = mos.size();
            int totalAdmins = (int) allUsers.stream()
                .filter(u -> u.getRole() == UserRole.ADMIN).count();
            
            // 获取所有职位
            List<Position> allPositions = positionDAO.loadAll();
            int totalPositions = allPositions.size();
            int openPositions = (int) allPositions.stream()
                .filter(p -> p.getStatus() == PositionStatus.OPEN).count();
            int closedPositions = (int) allPositions.stream()
                .filter(p -> p.getStatus() == PositionStatus.CLOSED).count();
            
            // 获取所有申请
            List<Application> allApplications = applicationDAO.loadAll();
            int totalApplications = allApplications.size();
            int pendingApplications = (int) allApplications.stream()
                .filter(a -> a.getStatus() == ApplicationStatus.PENDING).count();
            int selectedApplications = (int) allApplications.stream()
                .filter(a -> a.getStatus() == ApplicationStatus.SELECTED).count();
            int rejectedApplications = (int) allApplications.stream()
                .filter(a -> a.getStatus() == ApplicationStatus.REJECTED).count();
            
            // 计算工时统计
            int totalHours = 0;
            int activeTAs = 0; // 有工作的TA数量
            for (User ta : tas) {
                int taHours = getTAStats(ta.getUserId()).get("hours");
                if (taHours > 0) {
                    activeTAs++;
                }
                totalHours += taHours;
            }
            
            double avgHours = totalTAs > 0 ? (double) totalHours / totalTAs : 0.0;
            double avgHoursActive = activeTAs > 0 ? (double) totalHours / activeTAs : 0.0;
            
            // 设置基本统计数据
            stats.put("totalUsers", totalUsers);
            stats.put("totalTAs", totalTAs);
            stats.put("totalMOs", totalMOs);
            stats.put("totalAdmins", totalAdmins);
            
            // 职位统计
            stats.put("totalPositions", totalPositions);
            stats.put("openPositions", openPositions);
            stats.put("closedPositions", closedPositions);
            
            // 申请统计
            stats.put("totalApplications", totalApplications);
            stats.put("pendingApplications", pendingApplications);
            stats.put("selectedApplications", selectedApplications);
            stats.put("rejectedApplications", rejectedApplications);
            
            // 工时统计
            stats.put("totalHours", totalHours);
            stats.put("activeTAs", activeTAs);
            stats.put("avgHours", String.format("%.1f", avgHours));
            stats.put("avgHoursActive", String.format("%.1f", avgHoursActive));
            
        } catch (Exception e) {
            // 如果出错，返回默认值
            e.printStackTrace();
            stats.put("totalUsers", 0);
            stats.put("totalTAs", 0);
            stats.put("totalMOs", 0);
            stats.put("totalAdmins", 0);
            stats.put("totalPositions", 0);
            stats.put("openPositions", 0);
            stats.put("closedPositions", 0);
            stats.put("totalApplications", 0);
            stats.put("pendingApplications", 0);
            stats.put("selectedApplications", 0);
            stats.put("rejectedApplications", 0);
            stats.put("totalHours", 0);
            stats.put("activeTAs", 0);
            stats.put("avgHours", "0.0");
            stats.put("avgHoursActive", "0.0");
        }
        
        return stats;
    }
}
