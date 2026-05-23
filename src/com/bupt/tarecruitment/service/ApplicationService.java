package com.bupt.tarecruitment.service;

import com.bupt.tarecruitment.dao.ApplicationDAO;
import com.bupt.tarecruitment.dao.PositionDAO;
import com.bupt.tarecruitment.dao.UserDAO;
import com.bupt.tarecruitment.model.Application;
import com.bupt.tarecruitment.model.ApplicationStatus;
import com.bupt.tarecruitment.model.Position;
import com.bupt.tarecruitment.model.PositionStatus;
import com.bupt.tarecruitment.model.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Service for application lifecycle operations.
 */
public class ApplicationService {

    private ApplicationDAO applicationDAO;
    private PositionDAO positionDAO;
    private UserDAO userDAO;

    /**
     * Creates the application service.
     */
    public ApplicationService() {
        this.applicationDAO = new ApplicationDAO();
        this.positionDAO = new PositionDAO();
        this.userDAO = new UserDAO();
    }

    /**
     * Creates a new application for a TA.
     */
    public Application applyForPosition(String taId, String positionId, String resumePath)
            throws IllegalArgumentException, IOException {

        if (taId == null || taId.trim().isEmpty()) {
            throw new IllegalArgumentException("TA ID cannot be empty");
        }

        if (positionId == null || positionId.trim().isEmpty()) {
            throw new IllegalArgumentException("Position ID cannot be empty");
        }

        User ta = userDAO.findById(taId.trim());
        if (ta == null) {
            throw new IllegalArgumentException("TA not found");
        }

        Position position = positionDAO.findById(positionId.trim());
        if (position == null) {
            throw new IllegalArgumentException("Position not found");
        }

        if (!position.canAcceptApplications()) {
            if (position.isExpired()) {
                throw new IllegalArgumentException("This position deadline has passed");
            }
            if (position.getStatus() == PositionStatus.CLOSED) {
                throw new IllegalArgumentException("This position is closed");
            }
            throw new IllegalArgumentException("This position is not accepting applications currently");
        }

        if (applicationDAO.hasApplied(taId.trim(), positionId.trim())) {
            throw new IllegalArgumentException("You have already applied for this position. Please wait for the review.");
        }

        int selectedCount = applicationDAO.countSelectedByPositionId(positionId.trim());
        if (selectedCount >= position.getMaxPositions()) {
            throw new IllegalArgumentException("This position is full and cannot accept more applications.");
        }

        Application application = new Application();
        application.setApplicationId(UUID.randomUUID().toString());
        application.setTaId(taId.trim());
        application.setPositionId(positionId.trim());
        application.setStatus(ApplicationStatus.PENDING);
        application.setAppliedAt(new Date());
        application.setResumePath(resumePath);

        applicationDAO.add(application);

        return application;
    }

    /**
     * Withdraws a pending application.
     */
    public void withdrawApplication(String applicationId)
            throws IllegalArgumentException, IOException {

        if (applicationId == null || applicationId.trim().isEmpty()) {
            throw new IllegalArgumentException("Application ID cannot be empty");
        }

        Application application = applicationDAO.findById(applicationId.trim());
        if (application == null) {
            throw new IllegalArgumentException("Application not found");
        }

        if (application.getStatus() != ApplicationStatus.PENDING) {
            String statusMsg = "";
            switch (application.getStatus()) {
                case SELECTED:
                    statusMsg = "Selected applications cannot be withdrawn (contract already signed).";
                    break;
                case REJECTED:
                    statusMsg = "Rejected applications cannot be withdrawn.";
                    break;
                case WITHDRAWN:
                    statusMsg = "Application has already been withdrawn.";
                    break;
                default:
                    statusMsg = "This application status does not allow withdrawal.";
            }
            throw new IllegalArgumentException(statusMsg);
        }

        application.setStatus(ApplicationStatus.WITHDRAWN);

        List<Application> allApplications = applicationDAO.loadAll();
        for (int i = 0; i < allApplications.size(); i++) {
            if (allApplications.get(i).getApplicationId().equals(applicationId.trim())) {
                allApplications.set(i, application);
                applicationDAO.saveAll(allApplications);
                return;
            }
        }
    }

    /**
     * Returns all applications submitted by one TA.
     */
    public List<Application> getApplicationsByTA(String taId) {
        if (taId == null || taId.trim().isEmpty()) {
            throw new IllegalArgumentException("TA ID cannot be empty");
        }

        return applicationDAO.findByTaId(taId.trim());
    }

    /**
     * Returns all applications for one position.
     */
    public List<Application> getApplicationsByPosition(String positionId) {
        if (positionId == null || positionId.trim().isEmpty()) {
            throw new IllegalArgumentException("Position ID cannot be empty");
        }

        return applicationDAO.findByPositionId(positionId.trim());
    }

    /**
     * Selects an applicant for a position.
     */
    public void selectApplicant(String applicationId)
            throws IllegalArgumentException, IOException {

        if (applicationId == null || applicationId.trim().isEmpty()) {
            throw new IllegalArgumentException("Application ID cannot be empty");
        }

        String targetApplicationId = applicationId.trim();
        List<Application> allApplications = applicationDAO.loadAll();

        Application selectedApplication = null;
        for (Application application : allApplications) {
            if (targetApplicationId.equals(application.getApplicationId())) {
                selectedApplication = application;
                break;
            }
        }
        if (selectedApplication == null) {
            throw new IllegalArgumentException("Application not found");
        }

        if (selectedApplication.getStatus() != ApplicationStatus.PENDING) {
            throw new IllegalArgumentException("Only pending applications can be selected");
        }

        String targetPositionId = selectedApplication.getPositionId();
        Position targetPosition = positionDAO.findById(targetPositionId);
        if (targetPosition == null) {
            throw new IllegalArgumentException("Position not found");
        }

        int selectedCount = (int) allApplications.stream()
            .filter(app -> targetPositionId.equals(app.getPositionId()))
            .filter(app -> app.getStatus() == ApplicationStatus.SELECTED)
            .count();
        if (selectedCount >= targetPosition.getMaxPositions()) {
            throw new IllegalArgumentException("This position has reached its maximum number of selected applicants");
        }

        // 中文说明：先把目标申请标记为录用。
        for (Application application : allApplications) {
            if (!targetPositionId.equals(application.getPositionId())) {
                continue;
            }
            if (targetApplicationId.equals(application.getApplicationId())) {
                application.setStatus(ApplicationStatus.SELECTED);
            }
        }

        int newSelectedCount = selectedCount + 1;
        if (newSelectedCount >= targetPosition.getMaxPositions()) {
            // 中文说明：岗位名额满后，剩余待处理申请统一转为拒绝。
            for (Application application : allApplications) {
                if (!targetPositionId.equals(application.getPositionId())) {
                    continue;
                }
                if (!targetApplicationId.equals(application.getApplicationId())
                        && application.getStatus() == ApplicationStatus.PENDING) {
                    application.setStatus(ApplicationStatus.REJECTED);
                }
            }
        }

        applicationDAO.saveAll(allApplications);
    }

    /**
     * Returns one application by id, or null on failure.
     */
    public Application getApplicationById(String applicationId) {
        try {
            return applicationDAO.findById(applicationId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Returns all applications for one position id, or an empty list on failure.
     */
    public List<Application> getApplicationsByPositionId(String positionId) {
        try {
            return applicationDAO.findByPositionId(positionId);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Rejects pending applications whose positions have expired.
     */
    public void cleanupExpiredPositionApplications() throws IOException {
        List<Application> allApplications = applicationDAO.loadAll();
        boolean hasChanges = false;

        for (Application application : allApplications) {
            if (application.getStatus() != ApplicationStatus.PENDING) {
                continue;
            }

            Position position = positionDAO.findById(application.getPositionId());
            if (position != null && position.isExpired()) {
                application.setStatus(ApplicationStatus.REJECTED);
                hasChanges = true;
            }
        }

        if (hasChanges) {
            applicationDAO.saveAll(allApplications);
        }
    }
}
