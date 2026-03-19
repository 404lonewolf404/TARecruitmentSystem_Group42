package com.bupt.tarecruitment.service;

import com.bupt.tarecruitment.dao.ApplicationDAO;
import com.bupt.tarecruitment.dao.PositionDAO;
import com.bupt.tarecruitment.dao.UserDAO;
import com.bupt.tarecruitment.model.Application;
import com.bupt.tarecruitment.model.ApplicationStatus;
import com.bupt.tarecruitment.model.Position;
import com.bupt.tarecruitment.model.User;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 申请服务类
 * 处理申请相关的业务逻辑
 */
public class ApplicationService {
    
    private ApplicationDAO applicationDAO;
    private PositionDAO positionDAO;
    private UserDAO userDAO;
    
    /**
     * 构造函数
     */
    public ApplicationService() {
        this.applicationDAO = new ApplicationDAO();
        this.positionDAO = new PositionDAO();
        this.userDAO = new UserDAO();
    }
    
    /**
     * 申请职位
     * 
     * @param taId TA的用户ID
     * @param positionId 职位ID
     * @param resumePath 简历文件路径（可选，如果为null则使用用户默认简历）
     * @return 创建的申请对象
     * @throws IllegalArgumentException 如果参数无效或已申请过
     * @throws IOException 如果数据保存失败
     */
    public Application applyForPosition(String taId, String positionId, String resumePath) 
            throws IllegalArgumentException, IOException {
        
        // 验证参数
        if (taId == null || taId.trim().isEmpty()) {
            throw new IllegalArgumentException("TA ID不能为空");
        }
        
        if (positionId == null || positionId.trim().isEmpty()) {
            throw new IllegalArgumentException("职位ID不能为空");
        }
        
        // 检查TA是否存在
        User ta = userDAO.findById(taId.trim());
        if (ta == null) {
            throw new IllegalArgumentException("TA不存在");
        }
        
        // 检查职位是否存在
        Position position = positionDAO.findById(positionId.trim());
        if (position == null) {
            throw new IllegalArgumentException("职位不存在");
        }
        
        // 检查是否已申请过（重复申请检查）
        if (applicationDAO.hasApplied(taId.trim(), positionId.trim())) {
            throw new IllegalArgumentException("您已经申请过该职位");
        }
        
        // 创建新申请
        Application application = new Application();
        application.setApplicationId(UUID.randomUUID().toString());
        application.setTaId(taId.trim());
        application.setPositionId(positionId.trim());
        application.setStatus(ApplicationStatus.PENDING);
        application.setAppliedAt(new Date());
        application.setResumePath(resumePath);
        
        // 保存申请
        applicationDAO.add(application);
        
        return application;
    }
    
    /**
     * 撤回申请
     * 
     * @param applicationId 申请ID
     * @throws IllegalArgumentException 如果申请不存在
     * @throws IOException 如果数据删除失败
     */
    public void withdrawApplication(String applicationId) 
            throws IllegalArgumentException, IOException {
        
        if (applicationId == null || applicationId.trim().isEmpty()) {
            throw new IllegalArgumentException("申请ID不能为空");
        }
        
        // 检查申请是否存在
        Application application = applicationDAO.findById(applicationId.trim());
        if (application == null) {
            throw new IllegalArgumentException("申请不存在");
        }
        
        // 删除申请
        applicationDAO.delete(applicationId.trim());
    }
    
    /**
     * 获取TA的所有申请
     * 
     * @param taId TA的用户ID
     * @return 该TA提交的所有申请列表
     */
    public List<Application> getApplicationsByTA(String taId) {
        if (taId == null || taId.trim().isEmpty()) {
            throw new IllegalArgumentException("TA ID不能为空");
        }
        
        return applicationDAO.findByTaId(taId.trim());
    }
    
    /**
     * 获取职位的所有申请
     * 
     * @param positionId 职位ID
     * @return 该职位的所有申请列表
     */
    public List<Application> getApplicationsByPosition(String positionId) {
        if (positionId == null || positionId.trim().isEmpty()) {
            throw new IllegalArgumentException("职位ID不能为空");
        }
        
        return applicationDAO.findByPositionId(positionId.trim());
    }
    
    /**
     * 选择申请者
     * 将指定申请状态更新为SELECTED，同一职位的其他申请更新为REJECTED
     * 
     * @param applicationId 要选择的申请ID
     * @throws IllegalArgumentException 如果申请不存在
     * @throws IOException 如果数据更新失败
     */
    public void selectApplicant(String applicationId) 
            throws IllegalArgumentException, IOException {
        
        if (applicationId == null || applicationId.trim().isEmpty()) {
            throw new IllegalArgumentException("申请ID不能为空");
        }
        
        // 检查申请是否存在
        Application selectedApplication = applicationDAO.findById(applicationId.trim());
        if (selectedApplication == null) {
            throw new IllegalArgumentException("申请不存在");
        }
        
        // 获取同一职位的所有申请
        List<Application> applications = applicationDAO.findByPositionId(
                selectedApplication.getPositionId());
        
        // 更新申请状态
        for (Application application : applications) {
            if (application.getApplicationId().equals(applicationId.trim())) {
                // 选中的申请设置为SELECTED
                application.setStatus(ApplicationStatus.SELECTED);
            } else {
                // 其他申请设置为REJECTED
                application.setStatus(ApplicationStatus.REJECTED);
            }
            applicationDAO.update(application);
        }
    }
}
