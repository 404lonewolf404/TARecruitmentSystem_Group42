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
import java.time.LocalDate;
import java.time.ZoneId;

/**
 */
public class PositionService {
    
    private PositionDAO positionDAO;
    private ApplicationDAO applicationDAO;
    
    /**
     */
    public PositionService() {
        this.positionDAO = new PositionDAO();
        this.applicationDAO = new ApplicationDAO();
    }
    
    /**
     * 
     */
    public Position createPosition(String moId, String title, String description, 
                                   String requirements, int hours, int maxPositions) 
            throws IllegalArgumentException, IOException {
        
        if (moId == null || moId.trim().isEmpty()) {
            throw new IllegalArgumentException("MO ID cannot be empty");
        }
        
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Position title cannot be empty");
        }
        
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Position description cannot be empty");
        }
        
        if (hours <= 0) {
            throw new IllegalArgumentException("Work hours must be greater than 0");
        }
        
        if (maxPositions <= 0) {
            throw new IllegalArgumentException("Max positions must be greater than 0");
        }
        
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
        
        positionDAO.add(position);
        
        return position;
    }
    
    /**
     * 
     */
    public List<Position> getPositionsByMO(String moId) {
        if (moId == null || moId.trim().isEmpty()) {
            throw new IllegalArgumentException("MO ID cannot be empty");
        }
        
        return positionDAO.findByMoId(moId.trim());
    }
    
    /**
     * 
     */
    public List<Position> getAllOpenPositions() {
        return positionDAO.findAllOpen();
    }
    
    /**
     * 
     */
    public Position getPositionById(String positionId) {
        if (positionId == null || positionId.trim().isEmpty()) {
            return null;
        }
        
        return positionDAO.findById(positionId.trim());
    }
    
    /**
     * 
     */
    public void deletePosition(String positionId) throws IllegalArgumentException, IOException {
        
        if (positionId == null || positionId.trim().isEmpty()) {
            throw new IllegalArgumentException("Position ID cannot be empty");
        }
        
        Position position = positionDAO.findById(positionId.trim());
        if (position == null) {
            throw new IllegalArgumentException("Position not found");
        }
        
        List<Application> applications = applicationDAO.findByPositionId(positionId.trim());
        for (Application application : applications) {
            applicationDAO.delete(application.getApplicationId());
        }
        
        positionDAO.delete(positionId.trim());
    }

    /**
     * 
     */
    public Position createPositionWithDeadline(String moId, String title, String description, 
                                               String requirements, int hours, int maxPositions, Date deadline) 
            throws IllegalArgumentException, IOException {
        
        if (moId == null || moId.trim().isEmpty()) {
            throw new IllegalArgumentException("MO ID cannot be empty");
        }
        
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Position title cannot be empty");
        }
        
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Position description cannot be empty");
        }
        
        if (hours <= 0) {
            throw new IllegalArgumentException("Work hours must be greater than 0");
        }
        
        if (maxPositions <= 0) {
            throw new IllegalArgumentException("Max positions must be greater than 0");
        }
        
        if (deadline != null) {
            LocalDate deadlineDate = deadline.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            if (deadlineDate.isBefore(LocalDate.now())) {
                throw new IllegalArgumentException("Deadline cannot be earlier than today");
            }
        }
        
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
        position.setDeadline(deadline);
        
        positionDAO.add(position);
        
        return position;
    }
    
    /**
     * 
     */
    public Position updatePosition(String positionId, String title, String description,
                                   String requirements, int hours, int maxPositions, Date deadline)
            throws IllegalArgumentException, IOException {
        
        if (positionId == null || positionId.trim().isEmpty()) {
            throw new IllegalArgumentException("Position ID cannot be empty");
        }
        
        Position position = positionDAO.findById(positionId.trim());
        if (position == null) {
            throw new IllegalArgumentException("Position not found");
        }
        
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Position title cannot be empty");
        }
        
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Position description cannot be empty");
        }
        
        if (hours <= 0) {
            throw new IllegalArgumentException("Work hours must be greater than 0");
        }
        
        if (maxPositions <= 0) {
            throw new IllegalArgumentException("Max positions must be greater than 0");
        }
        
        if (deadline != null) {
            LocalDate deadlineDate = deadline.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            if (deadlineDate.isBefore(LocalDate.now())) {
                throw new IllegalArgumentException("Deadline cannot be earlier than today");
            }
        }
        
        int selectedCount = applicationDAO.countSelectedByPositionId(positionId.trim());
        
        if (maxPositions < selectedCount) {
            throw new IllegalArgumentException(
                "Cannot reduce max positions to " + maxPositions + " because " + selectedCount + 
                " applications are already selected. Please cancel some selected applications first, or set max positions to at least " + selectedCount + "."
            );
        }
        
        if (selectedCount > 0 && position.getHours() != hours) {
            throw new IllegalArgumentException(
                "Cannot modify work hours because " + selectedCount + " applications are already selected. " +
                "Work hours are part of the work contract and cannot be changed after applications are selected. Please cancel all selected applications first, or contact the selected TAs to negotiate."
            );
        }
        
        
        position.setTitle(title.trim());
        position.setDescription(description.trim());
        position.setRequirements(requirements != null ? requirements.trim() : "");
        position.setHours(hours);
        position.setMaxPositions(maxPositions);
        position.setDeadline(deadline);
        
        List<Position> allPositions = positionDAO.loadAll();
        for (int i = 0; i < allPositions.size(); i++) {
            if (allPositions.get(i).getPositionId().equals(positionId.trim())) {
                allPositions.set(i, position);
                positionDAO.saveAll(allPositions);
                return position;
            }
        }
        
        throw new IllegalArgumentException("Position not found");
    }
}
