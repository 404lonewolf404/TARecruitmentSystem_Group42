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
                        "🎉 恭喜！您的申请已被选中\n\n" +
                        "职位：【" + position.getTitle() + "】（" + position.getHours() + "小时/周）\n" +
                        (position.getDescription() != null && !position.getDescription().isEmpty() ? 
                            "描述：" + position.getDescription() + "\n" : "") +
                        "\n负责人：" + (mo != null ? mo.getName() : "未知") + "\n" +
                        "邮箱：" + (mo != null ? mo.getEmail() : "未知") + "\n" +
                        "\n您在" + totalApplicants + "位申请者中脱颖而出！\n\n" +
                        "💡 请在3个工作日内联系负责人确认工作安排"
                    );
                } else {
                    notification.setMessage("🎉 恭喜！您的申请已被选中\n\n请及时联系管理人员确认相关事宜。");
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
                        "📋 申请结果通知\n\n" +
                        "职位：【" + position.getTitle() + "】（" + position.getHours() + "小时/周）\n" +
                        "竞争：" + totalApplicants + "人申请，" + selectedCount + "人被选中\n\n" +
                        "很遗憾，您的申请未被选中。\n\n" +
                        "💪 继续加油，寻找其他合适的职位！"
                    );
                } else {
                    notification.setMessage("📋 很遗憾，您的申请未被选中\n\n感谢您的申请，欢迎继续关注其他职位。");
                }
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
                    "📬 收到新的职位申请\n\n" +
                    "职位：【" + position.getTitle() + "】（" + position.getHours() + "小时/周）\n" +
                    "申请人：" + ta.getName() + "（" + ta.getEmail() + "）\n" +
                    "技能：" + taSkills + "\n" +
                    "\n当前申请：" + totalApplicants + "人（待审核" + pendingCount + "，已选中" + selectedCount + "）\n\n" +
                    "💡 请前往\"我的职位\"查看详情并及时处理"
                );
            } else {
                notification.setMessage("📬 您有新的职位申请\n\n请及时查看并处理。");
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
                    "⚠️ 申请撤回通知\n\n" +
                    "职位：【" + position.getTitle() + "】\n" +
                    "撤回人：" + ta.getName() + "（" + ta.getEmail() + "）\n\n" +
                    "剩余申请：" + remainingCount + "人（待审核" + pendingCount + "，已选中" + selectedCount + "）\n\n" +
                    (remainingCount > 0 ? 
                        "💡 请及时审核剩余申请，避免更多流失"
                        :
                        "⚠️ 该职位已无申请人！建议调整要求或重新发布"
                    )
                );
            } else {
                notification.setMessage("⚠️ 有申请者撤回了申请\n\n请查看职位申请情况。");
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
                    "🗑️ 职位删除通知\n\n" +
                    "职位：【" + positionTitle + "】\n" +
                    "您的状态：" + statusEmoji + " " + statusText + "\n" +
                    "申请时间：" + new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(app.getAppliedAt()) + "\n\n" +
                    "⚠️ 该职位已被删除，您的申请已自动取消。\n" +
                    (app.getStatus() == ApplicationStatus.SELECTED ? 
                        "\n⚠️ 您之前已被选中，但工作机会已取消。\n如有疑问请联系管理人员。\n" : "") +
                    "\n💡 请前往\"浏览职位\"页面查看其他机会"
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
            case PENDING: return "待审核";
            case SELECTED: return "已选中";
            case REJECTED: return "未选中";
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
                    "🔒 职位关闭通知\n\n" +
                    "职位：【" + positionTitle + "】\n" +
                    "您的状态：⏳ 待审核\n" +
                    "申请时间：" + new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(app.getAppliedAt()) + "\n\n" +
                    "⚠️ 该职位已关闭，暂时不再接受新申请。\n" +
                    "您的申请仍在系统中，如果职位重新开放，您的申请将继续有效。\n\n" +
                    "💡 请前往\"浏览职位\"页面查看其他机会"
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
}
