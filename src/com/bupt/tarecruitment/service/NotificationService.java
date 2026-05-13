package com.bupt.tarecruitment.service;

import com.bupt.tarecruitment.dao.NotificationDAO;
import com.bupt.tarecruitment.model.ApplicationStatus;
import com.bupt.tarecruitment.model.Notification;
import com.bupt.tarecruitment.model.NotificationType;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

public class NotificationService {
    private NotificationDAO notificationDAO;
    
    public NotificationService() {
        this.notificationDAO = new NotificationDAO();
    }
    
    public int getUnreadCount(String userId) throws IOException {
        return notificationDAO.getUnreadCount(userId);
    }
    
    public void sendApplicationStatusNotification(String taId, String positionId, ApplicationStatus status) {
        try {
            System.out.println("=== sendApplicationStatusNotification called ===");
            System.out.println("taId: " + taId);
            System.out.println("positionId: " + positionId);
            System.out.println("status: " + status);

            if (taId == null || taId.trim().isEmpty() || positionId == null || positionId.trim().isEmpty() || status == null) {
                System.out.println("Skip status notification due to invalid parameters");
                return;
            }

            if (status != ApplicationStatus.SELECTED && status != ApplicationStatus.REJECTED) {
                System.out.println("Skip status notification for unsupported status: " + status);
                return;
            }
            
            // 获取职位信息和MO信息
            PositionService positionService = new PositionService();
            com.bupt.tarecruitment.model.Position position = positionService.getPositionById(positionId);
            
            com.bupt.tarecruitment.dao.UserDAO userDAO = new com.bupt.tarecruitment.dao.UserDAO();
            
            Notification notification = new Notification();
            notification.setNotificationId(UUID.randomUUID().toString());
            notification.setUserId(taId);
            notification.setRelatedId(positionId);
            notification.setCreatedAt(new Date());
            notification.setRead(false);
            
            if (status == ApplicationStatus.SELECTED) {
                notification.setType(NotificationType.APPLICATION_SELECTED);
                if (position != null) {
                    com.bupt.tarecruitment.model.User mo = userDAO.findById(position.getMoId());
                    String moInfo = mo != null ? mo.getName() + " (" + mo.getEmail() + ")" : "管理人员";
                    
                    // 获取该职位的总申请人数和选中人数
                    ApplicationService applicationService = new ApplicationService();
                    java.util.List<com.bupt.tarecruitment.model.Application> allApps = 
                        applicationService.getApplicationsByPositionId(positionId);
                    int totalApplicants = allApps.size();
                    long selectedCount = allApps.stream()
                        .filter(app -> app.getStatus() == ApplicationStatus.SELECTED)
                        .count();
                    
                    notification.setMessage(
                        "🎉 Congratulations! Your application has been selected.\n\n" +
                        "Position: [" + position.getTitle() + "] (" + position.getHours() + " hrs/week)\n" +
                        (position.getDescription() != null && !position.getDescription().isEmpty() ? 
                            "Description: " + position.getDescription() + "\n" : "") +
                        "\nOwner: " + (mo != null ? mo.getName() : "Unknown") + "\n" +
                        "Email: " + (mo != null ? mo.getEmail() : "Unknown") + "\n" +
                        "\nYou were selected among " + totalApplicants + " applicants.\n\n" +
                        "💡 Please contact the owner within 3 business days to confirm arrangements."
                    );
                } else {
                    notification.setMessage("🎉 Congratulations! Your application has been selected.\n\nPlease contact the administrators to confirm the details.");
                }
            } else if (status == ApplicationStatus.REJECTED) {
                notification.setType(NotificationType.APPLICATION_REJECTED);
                if (position != null) {
                    // 获取该职位的竞争情况
                    ApplicationService applicationService = new ApplicationService();
                    java.util.List<com.bupt.tarecruitment.model.Application> allApps = 
                        applicationService.getApplicationsByPositionId(positionId);
                    int totalApplicants = allApps.size();
                    long selectedCount = allApps.stream()
                        .filter(app -> app.getStatus() == ApplicationStatus.SELECTED)
                        .count();
                    
                    notification.setMessage(
                        "📋 Application Result\n\n" +
                        "Position: [" + position.getTitle() + "] (" + position.getHours() + " hrs/week)\n" +
                        "Competition: " + totalApplicants + " applicants, " + selectedCount + " selected\n\n" +
                        "We regret to inform you that your application was not selected.\n\n" +
                        "💪 Keep trying and look for other suitable positions!"
                    );
                } else {
                    notification.setMessage("📋 We regret to inform you that your application was not selected.\n\nThank you for applying — please consider other available positions.");
                }
            }
            
            if (notification.getType() == null || notification.getMessage() == null || notification.getMessage().trim().isEmpty()) {
                System.out.println("Skip saving status notification due to incomplete content");
                return;
            }

            System.out.println("Saving notification: " + notification.getNotificationId());
            notificationDAO.save(notification);
            System.out.println("Notification saved successfully");
        } catch (IOException e) {
            System.out.println("ERROR saving notification: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void sendNewApplicationNotification(String moId, String taId, String positionId) {
        try {
            System.out.println("=== sendNewApplicationNotification called ===");
            System.out.println("moId: " + moId);
            System.out.println("taId: " + taId);
            System.out.println("positionId: " + positionId);
            
            // 获取职位和TA信息
            PositionService positionService = new PositionService();
            com.bupt.tarecruitment.model.Position position = positionService.getPositionById(positionId);
            
            com.bupt.tarecruitment.dao.UserDAO userDAO = new com.bupt.tarecruitment.dao.UserDAO();
            com.bupt.tarecruitment.model.User ta = userDAO.findById(taId);
            
            Notification notification = new Notification();
            notification.setNotificationId(UUID.randomUUID().toString());
            notification.setUserId(moId);
            notification.setType(NotificationType.NEW_APPLICATION);
            
            if (position != null && ta != null) {
                // 获取申请统计
                ApplicationService applicationService = new ApplicationService();
                java.util.List<com.bupt.tarecruitment.model.Application> allApps = 
                    applicationService.getApplicationsByPositionId(positionId);
                int totalApplicants = allApps.size();
                
                // 统计各状态的申请数量
                long pendingCount = allApps.stream()
                    .filter(app -> app.getStatus() == ApplicationStatus.PENDING)
                    .count();
                long selectedCount = allApps.stream()
                    .filter(app -> app.getStatus() == ApplicationStatus.SELECTED)
                    .count();
                long rejectedCount = allApps.stream()
                    .filter(app -> app.getStatus() == ApplicationStatus.REJECTED)
                    .count();
                
                // 获取申请人的详细信息
                String taSkills = ta.getSkills() != null && !ta.getSkills().isEmpty() ? 
                    ta.getSkills() : "未填写";
                
                // 计算技能匹配度
                String matchInfo = "";
                
                notification.setMessage(
                    "📬 New application received\n\n" +
                    "Position: [" + position.getTitle() + "] (" + position.getHours() + " hrs/week)\n" +
                    "Applicant: " + ta.getName() + " (" + ta.getEmail() + ")\n" +
                    "Skills: " + taSkills + "\n" +
                    "\nCurrent applications: " + totalApplicants + " (Pending: " + pendingCount + ", Selected: " + selectedCount + ")\n\n" +
                    "💡 Please check \"My Positions\" for details and process the application promptly."
                );
            } else {
                notification.setMessage("📬 You have a new application.\n\nPlease check and process it promptly.");
            }
            
            notification.setRelatedId(positionId);
            notification.setCreatedAt(new Date());
            notification.setRead(false);
            
            System.out.println("Saving notification: " + notification.getNotificationId());
            notificationDAO.save(notification);
            System.out.println("Notification saved successfully");
        } catch (IOException e) {
            System.out.println("ERROR saving notification: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void sendApplicationWithdrawnNotification(String moId, String taId, String positionId) {
        try {
            System.out.println("=== sendApplicationWithdrawnNotification called ===");
            System.out.println("moId: " + moId);
            System.out.println("taId: " + taId);
            System.out.println("positionId: " + positionId);
            
            // 获取职位和TA信息
            PositionService positionService = new PositionService();
            com.bupt.tarecruitment.model.Position position = positionService.getPositionById(positionId);
            
            com.bupt.tarecruitment.dao.UserDAO userDAO = new com.bupt.tarecruitment.dao.UserDAO();
            com.bupt.tarecruitment.model.User ta = userDAO.findById(taId);
            
            Notification notification = new Notification();
            notification.setNotificationId(UUID.randomUUID().toString());
            notification.setUserId(moId);
            notification.setType(NotificationType.APPLICATION_WITHDRAWN);
            
            if (position != null && ta != null) {
                // 获取剩余申请人数和统计
                ApplicationService applicationService = new ApplicationService();
                java.util.List<com.bupt.tarecruitment.model.Application> remainingApps = 
                    applicationService.getApplicationsByPositionId(positionId);
                int remainingCount = remainingApps.size();
                
                // 统计各状态的申请数量
                long pendingCount = remainingApps.stream()
                    .filter(app -> app.getStatus() == ApplicationStatus.PENDING)
                    .count();
                long selectedCount = remainingApps.stream()
                    .filter(app -> app.getStatus() == ApplicationStatus.SELECTED)
                    .count();
                
                notification.setMessage(
                    "⚠️ Application Withdrawn\n\n" +
                    "Position: [" + position.getTitle() + "]\n" +
                    "Withdrawn by: " + ta.getName() + " (" + ta.getEmail() + ")\n\n" +
                    "Remaining applications: " + remainingCount + " (Pending: " + pendingCount + ", Selected: " + selectedCount + ")\n\n" +
                    (remainingCount > 0 ? 
                        "💡 Please review the remaining applications promptly to avoid further loss."
                        :
                        "⚠️ There are no remaining applicants for this position. Consider adjusting requirements or reposting."
                    )
                );
            } else {
                notification.setMessage("⚠️ An applicant has withdrawn their application.\n\nPlease review the position's application status.");
            }
            
            notification.setRelatedId(positionId);
            notification.setCreatedAt(new Date());
            notification.setRead(false);
            
            System.out.println("Saving notification: " + notification.getNotificationId());
            notificationDAO.save(notification);
            System.out.println("Notification saved successfully");
        } catch (IOException e) {
            System.out.println("ERROR saving notification: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 发送职位删除通知给所有申请了该职位的TA
     */
    public void sendPositionDeletedNotification(String positionId, String positionTitle) {
        try {
            System.out.println("=== sendPositionDeletedNotification called ===");
            System.out.println("positionId: " + positionId);
            System.out.println("positionTitle: " + positionTitle);
            
            // 获取该职位的所有申请
            ApplicationService applicationService = new ApplicationService();
            java.util.List<com.bupt.tarecruitment.model.Application> applications = 
                applicationService.getApplicationsByPositionId(positionId);
            
            System.out.println("Found " + applications.size() + " applications for this position");
            
            // 给每个申请者发送通知
            for (com.bupt.tarecruitment.model.Application app : applications) {
                // 获取申请人信息
                com.bupt.tarecruitment.dao.UserDAO userDAO = new com.bupt.tarecruitment.dao.UserDAO();
                com.bupt.tarecruitment.model.User ta = userDAO.findById(app.getTaId());
                
                Notification notification = new Notification();
                notification.setNotificationId(java.util.UUID.randomUUID().toString());
                notification.setUserId(app.getTaId());
                notification.setType(NotificationType.POSITION_DELETED);
                
                String statusText = getStatusText(app.getStatus());
                String statusEmoji = app.getStatus() == ApplicationStatus.SELECTED ? "✅" : 
                                    app.getStatus() == ApplicationStatus.REJECTED ? "❌" : "⏳";
                
                notification.setMessage(
                    "🗑️ Position Deleted\n\n" +
                    "Position: [" + positionTitle + "]\n" +
                    "Your status: " + statusEmoji + " " + statusText + "\n" +
                    "Applied at: " + new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(app.getAppliedAt()) + "\n\n" +
                    "⚠️ This position has been deleted and your application has been cancelled.\n" +
                    (app.getStatus() == ApplicationStatus.SELECTED ? 
                        "\n⚠️ You were previously selected, but the opportunity has been cancelled.\nPlease contact administrators if you have questions.\n" : "") +
                    "\n💡 Please visit the \"Browse Positions\" page to explore other opportunities."
                );
                notification.setRelatedId(positionId);
                notification.setCreatedAt(new java.util.Date());
                notification.setRead(false);
                
                System.out.println("Sending notification to TA: " + app.getTaId());
                notificationDAO.save(notification);
            }
            
            System.out.println("All position deleted notifications sent successfully");
        } catch (Exception e) {
            System.out.println("ERROR sending position deleted notifications: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private String getStatusText(ApplicationStatus status) {
        switch (status) {
            case PENDING: return "Pending";
            case SELECTED: return "Selected";
            case REJECTED: return "Rejected";
            default: return status.toString();
        }
    }

    /**
     * 发送职位关闭通知给所有申请了该职位的TA
     * V3.2 - 职位状态管理
     */
    public void sendPositionClosedNotification(String positionId, String positionTitle) {
        try {
            System.out.println("=== sendPositionClosedNotification called ===");
            System.out.println("positionId: " + positionId);
            System.out.println("positionTitle: " + positionTitle);
            
            // 获取该职位的所有申请
            ApplicationService applicationService = new ApplicationService();
            java.util.List<com.bupt.tarecruitment.model.Application> applications = 
                applicationService.getApplicationsByPositionId(positionId);
            
            System.out.println("Found " + applications.size() + " applications for this position");
            
            // 给每个申请者发送通知
            for (com.bupt.tarecruitment.model.Application app : applications) {
                // 只给待审核的申请者发送通知
                if (app.getStatus() != ApplicationStatus.PENDING) {
                    continue;
                }
                
                Notification notification = new Notification();
                notification.setNotificationId(java.util.UUID.randomUUID().toString());
                notification.setUserId(app.getTaId());
                notification.setType(NotificationType.POSITION_CLOSED);
                
                notification.setMessage(
                    "🔒 Position Closed\n\n" +
                    "Position: [" + positionTitle + "]\n" +
                    "Your status: ⏳ Pending\n" +
                    "Applied at: " + new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(app.getAppliedAt()) + "\n\n" +
                    "⚠️ This position is closed and is temporarily not accepting new applications.\n" +
                    "Your application remains in the system; if the position reopens, your application will still be considered.\n\n" +
                    "💡 Please visit the \"Browse Positions\" page to explore other opportunities."
                );
                notification.setRelatedId(positionId);
                notification.setCreatedAt(new java.util.Date());
                notification.setRead(false);
                
                System.out.println("Sending notification to TA: " + app.getTaId());
                
                // 保存通知
                notificationDAO.save(notification);
            }
            
            System.out.println("Position closed notifications sent successfully");
            
        } catch (Exception e) {
            System.out.println("ERROR sending position closed notifications: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 创建通用通知
     * V3.5 - 招聘对话系统
     */
    public void createNotification(String userId, NotificationType type, String message) throws IOException {
        Notification notification = new Notification();
        notification.setNotificationId(UUID.randomUUID().toString());
        notification.setUserId(userId);
        notification.setType(type);
        notification.setMessage(message);
        notification.setCreatedAt(new Date());
        notification.setRead(false);
        
        notificationDAO.save(notification);
    }
}
