package com.bupt.tarecruitment.dao;

import com.bupt.tarecruitment.model.Notification;
import com.bupt.tarecruitment.model.NotificationType;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class NotificationDAO {
    private static final String DATA_FILE = "data/notifications.csv";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    private String getDataFilePath() {
        String contextPath = System.getProperty("catalina.base");
        if (contextPath != null) {
            return contextPath + "/webapps/TARecruitmentSystem/" + DATA_FILE;
        }
        return DATA_FILE;
    }
    
    public List<Notification> findByUserId(String userId) throws IOException {
        List<Notification> notifications = new ArrayList<>();
        File file = new File(getDataFilePath());
        
        if (!file.exists()) {
            return notifications;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine(); // Skip header
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 7 && parts[1].equals(userId)) {
                    notifications.add(parseNotification(parts));
                }
            }
        } catch (ParseException e) {
            throw new IOException("Error parsing notification data", e);
        }
        
        notifications.sort((n1, n2) -> n2.getCreatedAt().compareTo(n1.getCreatedAt()));
        return notifications;
    }
    
    public int getUnreadCount(String userId) throws IOException {
        int count = 0;
        File file = new File(getDataFilePath());
        
        if (!file.exists()) {
            return 0;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine(); // Skip header
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 7 && parts[1].equals(userId) && parts[5].equals("false")) {
                    count++;
                }
            }
        }
        
        return count;
    }
    
    public void save(Notification notification) throws IOException {
        File file = new File(getDataFilePath());
        File parentDir = file.getParentFile();
        
        if (!parentDir.exists()) {
            parentDir.mkdirs();
        }
        
        boolean isNewFile = !file.exists();
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            if (isNewFile) {
                writer.write("notificationId,userId,type,message,relatedId,isRead,createdAt");
                writer.newLine();
            }
            
            // 转义消息中的换行符和逗号
            String escapedMessage = notification.getMessage()
                .replace("\\", "\\\\")  // 先转义反斜杠
                .replace("\n", "\\n")   // 转义换行符
                .replace(",", "\\,");   // 转义逗号
            
            writer.write(String.format("%s,%s,%s,%s,%s,%s,%s",
                notification.getNotificationId(),
                notification.getUserId(),
                notification.getType(),
                escapedMessage,
                notification.getRelatedId(),
                notification.isRead(),
                DATE_FORMAT.format(notification.getCreatedAt())
            ));
            writer.newLine();
        }
    }
    
    public void markAsRead(String notificationId) throws IOException {
        updateNotificationField(notificationId, 5, "true");
    }
    
    public void markAllAsRead(String userId) throws IOException {
        File file = new File(getDataFilePath());
        if (!file.exists()) {
            return;
        }
        
        List<String> lines = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            lines.add(line); // Header
            
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 7 && parts[1].equals(userId)) {
                    parts[5] = "true";
                    lines.add(String.join(",", parts));
                } else {
                    lines.add(line);
                }
            }
        }
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        }
    }
    
    public void delete(String notificationId) throws IOException {
        File file = new File(getDataFilePath());
        if (!file.exists()) {
            return;
        }
        
        List<String> lines = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            lines.add(line); // Header
            
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (!parts[0].equals(notificationId)) {
                    lines.add(line);
                }
            }
        }
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        }
    }
    
    private void updateNotificationField(String notificationId, int fieldIndex, String newValue) throws IOException {
        File file = new File(getDataFilePath());
        if (!file.exists()) {
            return;
        }
        
        List<String> lines = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            lines.add(line); // Header
            
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(notificationId)) {
                    parts[fieldIndex] = newValue;
                    lines.add(String.join(",", parts));
                } else {
                    lines.add(line);
                }
            }
        }
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        }
    }
    
    private Notification parseNotification(String[] parts) throws ParseException {
        Notification notification = new Notification();
        notification.setNotificationId(parts[0]);
        notification.setUserId(parts[1]);
        notification.setType(NotificationType.valueOf(parts[2]));
        
        // 反转义消息
        String message = parts[3]
            .replace("\\,", ",")    // 反转义逗号
            .replace("\\n", "\n")   // 反转义换行符
            .replace("\\\\", "\\"); // 反转义反斜杠
        notification.setMessage(message);
        
        notification.setRelatedId(parts[4]);
        notification.setRead(Boolean.parseBoolean(parts[5]));
        notification.setCreatedAt(DATE_FORMAT.parse(parts[6]));
        return notification;
    }
    
}
