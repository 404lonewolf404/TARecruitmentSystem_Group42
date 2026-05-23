package com.bupt.tarecruitment.service;

import com.bupt.tarecruitment.dao.NotificationDAO;
import com.bupt.tarecruitment.dao.UserDAO;
import com.bupt.tarecruitment.model.Application;
import com.bupt.tarecruitment.model.ApplicationStatus;
import com.bupt.tarecruitment.model.Notification;
import com.bupt.tarecruitment.model.NotificationType;
import com.bupt.tarecruitment.model.Position;
import com.bupt.tarecruitment.model.User;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Service for creating and dispatching system notifications.
 */
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

            // 中文说明：读取岗位和相关用户信息，拼出更完整的通知内容。
            PositionService positionService = new PositionService();
            Position position = positionService.getPositionById(positionId);
            UserDAO userDAO = new UserDAO();

            Notification notification = new Notification();
            notification.setNotificationId(UUID.randomUUID().toString());
            notification.setUserId(taId);
            notification.setRelatedId(positionId);
            notification.setCreatedAt(new Date());
            notification.setRead(false);

            if (status == ApplicationStatus.SELECTED) {
                notification.setType(NotificationType.APPLICATION_SELECTED);
                if (position != null) {
                    User mo = userDAO.findById(position.getMoId());

                    // 中文说明：统计该岗位当前申请情况，用于增强录用说明。
                    ApplicationService applicationService = new ApplicationService();
                    List<Application> allApps = applicationService.getApplicationsByPositionId(positionId);
                    int totalApplicants = allApps.size();

                    notification.setMessage(
                        "Congratulations! Your application has been selected.\n\n"
                        + "Position: [" + position.getTitle() + "] (" + position.getHours() + " hrs/week)\n"
                        + (position.getDescription() != null && !position.getDescription().isEmpty()
                            ? "Description: " + position.getDescription() + "\n" : "")
                        + "\nOwner: " + (mo != null ? mo.getName() : "Unknown") + "\n"
                        + "Email: " + (mo != null ? mo.getEmail() : "Unknown") + "\n"
                        + "\nYou were selected among " + totalApplicants + " applicants.\n\n"
                        + "Please contact the owner within 3 business days to confirm arrangements."
                    );
                } else {
                    notification.setMessage(
                        "Congratulations! Your application has been selected.\n\n"
                        + "Please contact the administrators to confirm the details."
                    );
                }
            } else {
                notification.setType(NotificationType.APPLICATION_REJECTED);
                if (position != null) {
                    // 中文说明：统计竞争情况，用于拒绝通知展示。
                    ApplicationService applicationService = new ApplicationService();
                    List<Application> allApps = applicationService.getApplicationsByPositionId(positionId);
                    int totalApplicants = allApps.size();
                    long selectedCount = allApps.stream()
                        .filter(app -> app.getStatus() == ApplicationStatus.SELECTED)
                        .count();

                    notification.setMessage(
                        "Application Result\n\n"
                        + "Position: [" + position.getTitle() + "] (" + position.getHours() + " hrs/week)\n"
                        + "Competition: " + totalApplicants + " applicants, " + selectedCount + " selected\n\n"
                        + "We regret to inform you that your application was not selected.\n\n"
                        + "Keep trying and look for other suitable positions."
                    );
                } else {
                    notification.setMessage(
                        "We regret to inform you that your application was not selected.\n\n"
                        + "Thank you for applying. Please consider other available positions."
                    );
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

            // 中文说明：读取岗位和申请人信息，供通知文案使用。
            PositionService positionService = new PositionService();
            Position position = positionService.getPositionById(positionId);
            UserDAO userDAO = new UserDAO();
            User ta = userDAO.findById(taId);

            Notification notification = new Notification();
            notification.setNotificationId(UUID.randomUUID().toString());
            notification.setUserId(moId);
            notification.setType(NotificationType.NEW_APPLICATION);

            if (position != null && ta != null) {
                // 中文说明：统计该岗位当前申请概况。
                ApplicationService applicationService = new ApplicationService();
                List<Application> allApps = applicationService.getApplicationsByPositionId(positionId);
                int totalApplicants = allApps.size();

                long pendingCount = allApps.stream()
                    .filter(app -> app.getStatus() == ApplicationStatus.PENDING)
                    .count();
                long selectedCount = allApps.stream()
                    .filter(app -> app.getStatus() == ApplicationStatus.SELECTED)
                    .count();

                String taSkills = ta.getSkills() != null && !ta.getSkills().isEmpty()
                    ? ta.getSkills()
                    : "No skills provided";

                notification.setMessage(
                    "New application received\n\n"
                    + "Position: [" + position.getTitle() + "] (" + position.getHours() + " hrs/week)\n"
                    + "Applicant: " + ta.getName() + " (" + ta.getEmail() + ")\n"
                    + "Skills: " + taSkills + "\n"
                    + "\nCurrent applications: " + totalApplicants
                    + " (Pending: " + pendingCount + ", Selected: " + selectedCount + ")\n\n"
                    + "Please check \"My Positions\" for details and process the application promptly."
                );
            } else {
                notification.setMessage(
                    "You have a new application.\n\nPlease check and process it promptly."
                );
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

            // 中文说明：读取岗位和申请人信息，供通知文案使用。
            PositionService positionService = new PositionService();
            Position position = positionService.getPositionById(positionId);
            UserDAO userDAO = new UserDAO();
            User ta = userDAO.findById(taId);

            Notification notification = new Notification();
            notification.setNotificationId(UUID.randomUUID().toString());
            notification.setUserId(moId);
            notification.setType(NotificationType.APPLICATION_WITHDRAWN);

            if (position != null && ta != null) {
                // 中文说明：统计剩余申请数量，便于 MO 判断下一步操作。
                ApplicationService applicationService = new ApplicationService();
                List<Application> remainingApps = applicationService.getApplicationsByPositionId(positionId);
                int remainingCount = remainingApps.size();
                long pendingCount = remainingApps.stream()
                    .filter(app -> app.getStatus() == ApplicationStatus.PENDING)
                    .count();
                long selectedCount = remainingApps.stream()
                    .filter(app -> app.getStatus() == ApplicationStatus.SELECTED)
                    .count();

                notification.setMessage(
                    "Application Withdrawn\n\n"
                    + "Position: [" + position.getTitle() + "]\n"
                    + "Withdrawn by: " + ta.getName() + " (" + ta.getEmail() + ")\n\n"
                    + "Remaining applications: " + remainingCount
                    + " (Pending: " + pendingCount + ", Selected: " + selectedCount + ")\n\n"
                    + (remainingCount > 0
                        ? "Please review the remaining applications promptly to avoid further loss."
                        : "There are no remaining applicants for this position. Consider adjusting requirements or reposting.")
                );
            } else {
                notification.setMessage(
                    "An applicant has withdrawn their application.\n\n"
                    + "Please review the position's application status."
                );
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
     * Sends a notification to every applicant when a position is deleted.
     */
    public void sendPositionDeletedNotification(String positionId, String positionTitle) {
        try {
            System.out.println("=== sendPositionDeletedNotification called ===");
            System.out.println("positionId: " + positionId);
            System.out.println("positionTitle: " + positionTitle);

            // 中文说明：读取该岗位的全部申请，逐个通知受影响 TA。
            ApplicationService applicationService = new ApplicationService();
            List<Application> applications = applicationService.getApplicationsByPositionId(positionId);

            System.out.println("Found " + applications.size() + " applications for this position");

            for (Application app : applications) {
                Notification notification = new Notification();
                notification.setNotificationId(UUID.randomUUID().toString());
                notification.setUserId(app.getTaId());
                notification.setType(NotificationType.POSITION_DELETED);

                String statusText = getStatusText(app.getStatus());
                String statusFlag = app.getStatus() == ApplicationStatus.SELECTED
                    ? "SELECTED"
                    : (app.getStatus() == ApplicationStatus.REJECTED ? "REJECTED" : "PENDING");

                notification.setMessage(
                    "Position Deleted\n\n"
                    + "Position: [" + positionTitle + "]\n"
                    + "Your status: " + statusFlag + " " + statusText + "\n"
                    + "Applied at: " + new SimpleDateFormat("yyyy-MM-dd HH:mm").format(app.getAppliedAt()) + "\n\n"
                    + "This position has been deleted and your application has been cancelled.\n"
                    + (app.getStatus() == ApplicationStatus.SELECTED
                        ? "\nYou were previously selected, but the opportunity has been cancelled.\nPlease contact administrators if you have questions.\n"
                        : "")
                    + "\nPlease visit the \"Browse Positions\" page to explore other opportunities."
                );
                notification.setRelatedId(positionId);
                notification.setCreatedAt(new Date());
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
            case PENDING:
                return "Pending";
            case SELECTED:
                return "Selected";
            case REJECTED:
                return "Rejected";
            default:
                return status.toString();
        }
    }

    /**
     * Sends a notification to pending applicants when a position is closed.
     */
    public void sendPositionClosedNotification(String positionId, String positionTitle) {
        try {
            System.out.println("=== sendPositionClosedNotification called ===");
            System.out.println("positionId: " + positionId);
            System.out.println("positionTitle: " + positionTitle);

            // 中文说明：仅读取并通知仍处于待处理状态的申请人。
            ApplicationService applicationService = new ApplicationService();
            List<Application> applications = applicationService.getApplicationsByPositionId(positionId);

            System.out.println("Found " + applications.size() + " applications for this position");

            for (Application app : applications) {
                if (app.getStatus() != ApplicationStatus.PENDING) {
                    continue;
                }

                Notification notification = new Notification();
                notification.setNotificationId(UUID.randomUUID().toString());
                notification.setUserId(app.getTaId());
                notification.setType(NotificationType.POSITION_CLOSED);

                notification.setMessage(
                    "Position Closed\n\n"
                    + "Position: [" + positionTitle + "]\n"
                    + "Your status: Pending\n"
                    + "Applied at: " + new SimpleDateFormat("yyyy-MM-dd HH:mm").format(app.getAppliedAt()) + "\n\n"
                    + "This position is closed and is temporarily not accepting new applications.\n"
                    + "Your application remains in the system; if the position reopens, your application will still be considered.\n\n"
                    + "Please visit the \"Browse Positions\" page to explore other opportunities."
                );
                notification.setRelatedId(positionId);
                notification.setCreatedAt(new Date());
                notification.setRead(false);

                System.out.println("Sending notification to TA: " + app.getTaId());

                // 中文说明：逐条保存关闭岗位通知。
                notificationDAO.save(notification);
            }

            System.out.println("Position closed notifications sent successfully");
        } catch (Exception e) {
            System.out.println("ERROR sending position closed notifications: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Creates a generic notification with custom content.
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
