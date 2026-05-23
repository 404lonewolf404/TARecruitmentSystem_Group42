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
    private static final Object WRITE_LOCK = new Object();

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

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), java.nio.charset.StandardCharsets.UTF_8))) {
            String line = reader.readLine(); // 中文说明：跳过 CSV 表头。
            while ((line = reader.readLine()) != null) {
                String[] parts = splitCSVLine(line);
                if (parts.length >= 7 && parts[1].equals(userId)) {
                    try {
                        notifications.add(parseNotification(parts));
                    } catch (ParseException e) {
                        System.err.println("Error parsing notification line: " + line);
                        e.printStackTrace();
                    }
                }
            }
        }

        notifications.sort((n1, n2) -> n2.getCreatedAt().compareTo(n1.getCreatedAt()));
        return notifications;
    }

    public Notification findById(String notificationId) throws IOException {
        File file = new File(getDataFilePath());
        if (!file.exists()) {
            return null;
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), java.nio.charset.StandardCharsets.UTF_8))) {
            String line = reader.readLine(); // 中文说明：跳过 CSV 表头。
            while ((line = reader.readLine()) != null) {
                String[] parts = splitCSVLine(line);
                if (parts.length >= 7 && parts[0].equals(notificationId)) {
                    try {
                        return parseNotification(parts);
                    } catch (ParseException e) {
                        System.err.println("Error parsing notification line: " + line);
                        e.printStackTrace();
                        return null;
                    }
                }
            }
        }

        return null;
    }

    public int getUnreadCount(String userId) throws IOException {
        int count = 0;
        File file = new File(getDataFilePath());

        if (!file.exists()) {
            return 0;
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), java.nio.charset.StandardCharsets.UTF_8))) {
            String line = reader.readLine(); // 中文说明：跳过 CSV 表头。
            while ((line = reader.readLine()) != null) {
                String[] parts = splitCSVLine(line);
                if (parts.length >= 7 && parts[1].equals(userId) && !Boolean.parseBoolean(parts[5])) {
                    count++;
                }
            }
        }

        return count;
    }

    public void save(Notification notification) throws IOException {
        synchronized (WRITE_LOCK) {
            File file = new File(getDataFilePath());
            File parentDir = file.getParentFile();

            if (!parentDir.exists()) {
                parentDir.mkdirs();
            }

            boolean isNewFile = !file.exists();

            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), java.nio.charset.StandardCharsets.UTF_8))) {
                if (isNewFile) {
                    writer.write("notificationId,userId,type,message,relatedId,isRead,createdAt");
                    writer.newLine();
                }

                writer.write(formatNotificationToCSV(notification));
                writer.newLine();
            }
        }
    }

    public void markAsRead(String notificationId) throws IOException {
        updateNotificationField(notificationId, 5, "true");
    }

    public void markAllAsRead(String userId) throws IOException {
        synchronized (WRITE_LOCK) {
            File file = new File(getDataFilePath());
            if (!file.exists()) {
                return;
            }

            List<String> lines = new ArrayList<>();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), java.nio.charset.StandardCharsets.UTF_8))) {
                String line = reader.readLine();
                lines.add(line); // 中文说明：保留表头。

                while ((line = reader.readLine()) != null) {
                    String[] parts = splitCSVLine(line);
                    if (parts.length >= 7 && parts[1].equals(userId)) {
                        parts[5] = "true";
                        lines.add(buildLineFromParts(parts));
                    } else {
                        lines.add(line);
                    }
                }
            }

            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), java.nio.charset.StandardCharsets.UTF_8))) {
                for (String line : lines) {
                    writer.write(line);
                    writer.newLine();
                }
            }
        }
    }

    public void delete(String notificationId) throws IOException {
        synchronized (WRITE_LOCK) {
            File file = new File(getDataFilePath());
            if (!file.exists()) {
                return;
            }

            List<String> lines = new ArrayList<>();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), java.nio.charset.StandardCharsets.UTF_8))) {
                String line = reader.readLine();
                lines.add(line); // 中文说明：保留表头。

                while ((line = reader.readLine()) != null) {
                    String[] parts = splitCSVLine(line);
                    if (parts.length >= 1 && !parts[0].equals(notificationId)) {
                        lines.add(line);
                    }
                }
            }

            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), java.nio.charset.StandardCharsets.UTF_8))) {
                for (String line : lines) {
                    writer.write(line);
                    writer.newLine();
                }
            }
        }
    }

    private void updateNotificationField(String notificationId, int fieldIndex, String newValue) throws IOException {
        synchronized (WRITE_LOCK) {
            File file = new File(getDataFilePath());
            if (!file.exists()) {
                return;
            }

            List<String> lines = new ArrayList<>();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), java.nio.charset.StandardCharsets.UTF_8))) {
                String line = reader.readLine();
                lines.add(line); // 中文说明：保留表头。

                while ((line = reader.readLine()) != null) {
                    String[] parts = splitCSVLine(line);
                    if (parts.length > fieldIndex && parts[0].equals(notificationId)) {
                        parts[fieldIndex] = newValue;
                        lines.add(buildLineFromParts(parts));
                    } else {
                        lines.add(line);
                    }
                }
            }

            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), java.nio.charset.StandardCharsets.UTF_8))) {
                for (String line : lines) {
                    writer.write(line);
                    writer.newLine();
                }
            }
        }
    }

    private Notification parseNotification(String[] parts) throws ParseException {
        Notification notification = new Notification();
        notification.setNotificationId(parts[0]);
        notification.setUserId(parts[1]);
        notification.setType(NotificationType.valueOf(parts[2]));
        notification.setMessage(parts[3]);
        notification.setRelatedId(parts[4]);
        notification.setRead(Boolean.parseBoolean(parts[5]));
        notification.setCreatedAt(parseDate(parts[6]));
        return notification;
    }

    private String formatNotificationToCSV(Notification notification) {
        String message = notification.getMessage() != null ? notification.getMessage() : "";
        String relatedId = notification.getRelatedId() != null ? notification.getRelatedId() : "";

        return String.format("%s,%s,%s,%s,%s,%s,%s",
            escapeField(notification.getNotificationId()),
            escapeField(notification.getUserId()),
            escapeField(notification.getType() != null ? notification.getType().toString() : ""),
            escapeField(message),
            escapeField(relatedId),
            escapeField(String.valueOf(notification.isRead())),
            escapeField(formatDate(notification.getCreatedAt()))
        );
    }

    private String buildLineFromParts(String[] parts) {
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) {
                line.append(',');
            }
            line.append(escapeField(parts[i] == null ? "" : parts[i]));
        }
        return line.toString();
    }

    /**
     * Split CSV-like lines and support both quoted values and legacy backslash-escaped commas.
     */
    private String[] splitCSVLine(String line) {
        List<String> result = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder current = new StringBuilder();

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes && !isEscapedComma(line, i)) {
                result.add(unescapeField(unescapeCSV(current.toString())));
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }

        result.add(unescapeField(unescapeCSV(current.toString())));
        return result.toArray(new String[0]);
    }

    private String unescapeCSV(String value) {
        if (value.startsWith("\"") && value.endsWith("\"")) {
            value = value.substring(1, value.length() - 1);
            value = value.replace("\"\"", "\"");
        }
        return value;
    }

    private String escapeField(String value) {
        if (value == null) {
            return "";
        }
        return value
            .replace("\\", "\\\\")
            .replace("\n", "\\n")
            .replace(",", "\\,");
    }

    private String unescapeField(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        return value
            .replace("\\n", "\n")
            .replace("\\,", ",")
            .replace("\\\\", "\\");
    }

    private boolean isEscapedComma(String line, int commaIndex) {
        int backslashCount = 0;
        int cursor = commaIndex - 1;
        while (cursor >= 0 && line.charAt(cursor) == '\\') {
            backslashCount++;
            cursor--;
        }
        return backslashCount % 2 == 1;
    }

    private Date parseDate(String value) throws ParseException {
        synchronized (DATE_FORMAT) {
            return DATE_FORMAT.parse(value);
        }
    }

    private String formatDate(Date value) {
        synchronized (DATE_FORMAT) {
            return DATE_FORMAT.format(value);
        }
    }
}
