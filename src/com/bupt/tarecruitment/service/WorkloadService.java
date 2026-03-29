package com.bupt.tarecruitment.service;

import com.bupt.tarecruitment.dao.ApplicationDAO;
import com.bupt.tarecruitment.dao.PositionDAO;
import com.bupt.tarecruitment.dao.UserDAO;
import com.bupt.tarecruitment.model.Application;
import com.bupt.tarecruitment.model.ApplicationStatus;
import com.bupt.tarecruitment.model.Position;
import com.bupt.tarecruitment.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 工作量服务类
 * 处理工作量统计相关的业务逻辑
 */
public class WorkloadService {
    
    private ApplicationDAO applicationDAO;
    private PositionDAO positionDAO;
    private UserDAO userDAO;
    
    /**
     * 构造函数
     */
    public WorkloadService() {
        this.applicationDAO = new ApplicationDAO();
        this.positionDAO = new PositionDAO();
        this.userDAO = new UserDAO();
    }
    
    /**
     * 计算所有TA的工作量
     * 只计算状态为SELECTED的申请
     * 
     * @return Map<User, Integer> 每个TA及其总工时
     */
    public Map<User, Integer> calculateAllWorkloads() {
        Map<User, Integer> workloads = new HashMap<>();
        
        try {
            // 获取所有申请
            List<Application> allApplications = applicationDAO.loadAll();
            
            // 遍历所有申请
            for (Application application : allApplications) {
                // 只处理状态为SELECTED的申请
                if (application.getStatus() == ApplicationStatus.SELECTED) {
                    // 获取TA用户
                    User ta = userDAO.findById(application.getTaId());
                    if (ta == null) {
                        continue; // 如果TA不存在，跳过
                    }
                    
                    // 获取职位信息
                    Position position = positionDAO.findById(application.getPositionId());
                    if (position == null) {
                        continue; // 如果职位不存在，跳过
                    }
                    
                    // 累加工时
                    int currentHours = workloads.getOrDefault(ta, 0);
                    workloads.put(ta, currentHours + position.getHours());
                }
            }
            
        } catch (Exception e) {
            // 如果发生错误，返回空的工作量映射
            return new HashMap<>();
        }
        
        return workloads;
    }
}
