package com.bupt.tarecruitment.service;

import com.bupt.tarecruitment.dao.ApplicationDAO;
import com.bupt.tarecruitment.dao.PositionDAO;
import com.bupt.tarecruitment.model.Application;
import com.bupt.tarecruitment.model.Position;
import com.bupt.tarecruitment.model.PositionStatus;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 职位服务�?
 * 处理职位相关的业务逻辑
 */
public class PositionService {
    
    private PositionDAO positionDAO;
    private ApplicationDAO applicationDAO;
    
    /**
     * 构造函�?
     */
    public PositionService() {
        this.positionDAO = new PositionDAO();
        this.applicationDAO = new ApplicationDAO();
    }
    
    /**
     * 创建新职�?
     * 
     * @param moId MO的用户ID
     * @param title 职位标题
     * @param description 职位描述
     * @param requirements 职位要求
     * @param hours 工作时长（小�?周）
     * @param maxPositions 招聘名额
     * @return 创建的职位对�?
     * @throws IllegalArgumentException 如果参数无效
     * @throws IOException 如果数据保存失败
     */
    public Position createPosition(String moId, String title, String description, 
                                   String requirements, int hours, int maxPositions) 
            throws IllegalArgumentException, IOException {
        
        // 验证必填字段
        if (moId == null || moId.trim().isEmpty()) {
            throw new IllegalArgumentException("MO ID不能为空");
        }
        
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("职位标题不能为空");
        }
        
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("职位描述不能为空");
        }
        
        if (hours <= 0) {
            throw new IllegalArgumentException("工作时长必须大于0");
        }
        
        if (maxPositions <= 0) {
            throw new IllegalArgumentException("招聘名额必须大于0");
        }
        
        // 创建新职�?
        Position position = new Position();
        position.setPositionId(UUID.randomUUID().toString());
        position.setMoId(moId.trim());
        position.setTitle(title.trim());
        position.setDescription(description.trim());
        position.setRequirements(requirements != null ? requirements.trim() : "");
        position.setHours(hours);
        position.setMaxPositions(maxPositions);
        position.setStatus(PositionStatus.OPEN);
        position.setCreatedAt(new Date());
        
        // 保存职位
        positionDAO.add(position);
        
        return position;
    }
    
    /**
     * 获取MO创建的所有职�?
     * 
     * @param moId MO的用户ID
     * @return 该MO创建的所有职位列�?
     */
    public List<Position> getPositionsByMO(String moId) {
        if (moId == null || moId.trim().isEmpty()) {
            throw new IllegalArgumentException("MO ID不能为空");
        }
        
        return positionDAO.findByMoId(moId.trim());
    }
    
    /**
     * 获取所有开放的职位
     * 
     * @return 所有状态为OPEN的职位列�?
     */
    public List<Position> getAllOpenPositions() {
        return positionDAO.findAllOpen();
    }
    
    /**
     * 根据ID获取职位
     * 
     * @param positionId 职位ID
     * @return 职位对象，如果不存在则返回null
     */
    public Position getPositionById(String positionId) {
        if (positionId == null || positionId.trim().isEmpty()) {
            return null;
        }
        
        return positionDAO.findById(positionId.trim());
    }
    
    /**
     * 删除职位（级联删除相关申请）
     * 
     * @param positionId 职位ID
     * @throws IllegalArgumentException 如果职位不存�?
     * @throws IOException 如果数据删除失败
     */
    public void deletePosition(String positionId) throws IllegalArgumentException, IOException {
        
        if (positionId == null || positionId.trim().isEmpty()) {
            throw new IllegalArgumentException("职位ID不能为空");
        }
        
        // 检查职位是否存�?
        Position position = positionDAO.findById(positionId.trim());
        if (position == null) {
            throw new IllegalArgumentException("职位不存�?);
        }
        
        // 级联删除：先删除所有相关申�?
        List<Application> applications = applicationDAO.findByPositionId(positionId.trim());
        for (Application application : applications) {
            applicationDAO.delete(application.getApplicationId());
        }
        
        // 删除职位
        positionDAO.delete(positionId.trim());
    }
}
