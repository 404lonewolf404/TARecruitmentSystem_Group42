package com.bupt.tarecruitment.service;

import com.bupt.tarecruitment.dao.ApplicationDAO;
import com.bupt.tarecruitment.dao.PositionDAO;
import com.bupt.tarecruitment.dao.UserDAO;
import com.bupt.tarecruitment.model.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 图表服务
 * V3.3 - 图表可视化功能
 * 提供各种图表数据的JSON格式输出
 */
public class ChartService {
    
    private UserDAO userDAO;
    private PositionDAO positionDAO;
    private ApplicationDAO applicationDAO;
    
    public ChartService() {
        this.userDAO = new UserDAO();
        this.positionDAO = new PositionDAO();
        this.applicationDAO = new ApplicationDAO();
    }
    
    /**
     * 转义JSON字符串
     */
    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
    
    /**
     * 获取TA工作量分布柱状图数据（Admin视图）
     * 返回JSON格式：{ labels: [...], data: [...], colors: [...] }
     */
    public String getWorkloadChartData() {
        try {
            List<User> allUsers = userDAO.loadAll();
            List<User> tas = allUsers.stream()
                .filter(u -> u.getRole() == UserRole.TA)
                .collect(Collectors.toList());
            
            StringBuilder labels = new StringBuilder("[");
            StringBuilder data = new StringBuilder("[");
            StringBuilder colors = new StringBuilder("[");
            
            boolean first = true;
            for (User ta : tas) {
                List<Application> apps = applicationDAO.findByTaId(ta.getUserId());
                int totalHours = apps.stream()
                    .filter(a -> a.getStatus() == ApplicationStatus.SELECTED)
                    .mapToInt(a -> {
                        Position pos = positionDAO.findById(a.getPositionId());
                        return pos != null ? pos.getHours() : 0;
                    })
                    .sum();
                
                if (!first) {
                    labels.append(",");
                    data.append(",");
                    colors.append(",");
                }
                first = false;
                
                labels.append("\"").append(escapeJson(ta.getName())).append("\"");
                data.append(totalHours);
                
                // 颜色编码：>30小时(红)，20-30(橙)，<20(绿)
                if (totalHours > 30) {
                    colors.append("\"#e74c3c\""); // 红色
                } else if (totalHours >= 20) {
                    colors.append("\"#f39c12\""); // 橙色
                } else {
                    colors.append("\"#27ae60\""); // 绿色
                }
            }
            
            labels.append("]");
            data.append("]");
            colors.append("]");
            
            return "{\"labels\":" + labels + ",\"data\":" + data + ",\"colors\":" + colors + "}";
            
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"labels\":[],\"data\":[],\"colors\":[]}";
        }
    }
    
    /**
     * 获取申请状态饼图数据（Admin视图）
     * 返回JSON格式：{ labels: [...], data: [...], colors: [...] }
     */
    public String getApplicationStatusData() {
        try {
            List<Application> allApplications = applicationDAO.loadAll();
            
            int pending = (int) allApplications.stream()
                .filter(a -> a.getStatus() == ApplicationStatus.PENDING).count();
            int selected = (int) allApplications.stream()
                .filter(a -> a.getStatus() == ApplicationStatus.SELECTED).count();
            int rejected = (int) allApplications.stream()
                .filter(a -> a.getStatus() == ApplicationStatus.REJECTED).count();
            int withdrawn = (int) allApplications.stream()
                .filter(a -> a.getStatus() == ApplicationStatus.WITHDRAWN).count();
            
            return "{\"labels\":[\"待审核\",\"已选中\",\"已拒绝\",\"已撤回\"]," +
                   "\"data\":[" + pending + "," + selected + "," + rejected + "," + withdrawn + "]," +
                   "\"colors\":[\"#f39c12\",\"#27ae60\",\"#e74c3c\",\"#95a5a6\"]}";
            
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"labels\":[],\"data\":[],\"colors\":[]}";
        }
    }
    
    /**
     * 获取职位申请数对比图数据（MO视图）
     * 返回JSON格式：{ labels: [...], data: [...] }
     */
    public String getPositionApplicationsData(String moId) {
        try {
            List<Position> positions = positionDAO.findByMoId(moId);
            
            StringBuilder labels = new StringBuilder("[");
            StringBuilder data = new StringBuilder("[");
            
            boolean first = true;
            for (Position pos : positions) {
                List<Application> apps = applicationDAO.findByPositionId(pos.getPositionId());
                
                if (!first) {
                    labels.append(",");
                    data.append(",");
                }
                first = false;
                
                labels.append("\"").append(escapeJson(pos.getTitle())).append("\"");
                data.append(apps.size());
            }
            
            labels.append("]");
            data.append("]");
            
            return "{\"labels\":" + labels + ",\"data\":" + data + "}";
            
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"labels\":[],\"data\":[]}";
        }
    }
    
    /**
     * 获取申请状态分布环形图数据（TA视图）
     * 返回JSON格式：{ labels: [...], data: [...], colors: [...] }
     */
    public String getTAApplicationStatusData(String taId) {
        try {
            List<Application> apps = applicationDAO.findByTaId(taId);
            
            int pending = (int) apps.stream()
                .filter(a -> a.getStatus() == ApplicationStatus.PENDING).count();
            int selected = (int) apps.stream()
                .filter(a -> a.getStatus() == ApplicationStatus.SELECTED).count();
            int rejected = (int) apps.stream()
                .filter(a -> a.getStatus() == ApplicationStatus.REJECTED).count();
            int withdrawn = (int) apps.stream()
                .filter(a -> a.getStatus() == ApplicationStatus.WITHDRAWN).count();
            
            return "{\"labels\":[\"待审核\",\"已选中\",\"已拒绝\",\"已撤回\"]," +
                   "\"data\":[" + pending + "," + selected + "," + rejected + "," + withdrawn + "]," +
                   "\"colors\":[\"#f39c12\",\"#27ae60\",\"#e74c3c\",\"#95a5a6\"]}";
            
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"labels\":[],\"data\":[],\"colors\":[]}";
        }
    }
}
