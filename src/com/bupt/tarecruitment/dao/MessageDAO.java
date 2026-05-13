package com.bupt.tarecruitment.dao;

import com.bupt.tarecruitment.model.Message;
import com.bupt.tarecruitment.model.UserRole;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class MessageDAO implements CSVDataStore<Message> {

    private static final String DATA_FILE = "data/messages.csv";
    private static final String HEADER = "messageId,applicationId,senderId,senderRole,content,sentAt,isRead";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    private static final Object WRITE_LOCK = new Object();

    private List<Message> messages;

    private String getDataFilePath() {
        String catalinaBase = System.getProperty("catalina.base");
        if (catalinaBase != null && !catalinaBase.trim().isEmpty()) {
            return catalinaBase + "/webapps/TARecruitmentSystem/" + DATA_FILE;
        }
        return "webapps/TARecruitmentSystem/" + DATA_FILE;
    }

    public MessageDAO() {
        try {
            this.messages = loadAll();
        } catch (IOException e) {
            this.messages = new ArrayList<>();
        }
    }

    @Override
    public List<Message> loadAll() throws IOException {
        List<Message> messageList = new ArrayList<>();
        File file = new File(getDataFilePath());

        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try (BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
                writer.write(HEADER);
                writer.newLine();
            }
            return messageList;
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {

            String line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                Message message = parseMessageFromCSV(line);
                if (message != null) {
                    messageList.add(message);
                }
            }
        }

        return messageList;
    }

    @Override
    public void saveAll(List<Message> items) throws IOException {
        File file = new File(getDataFilePath());
        file.getParentFile().mkdirs();

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {

            writer.write(HEADER);
            writer.newLine();

            for (Message message : items) {
                writer.write(formatMessageToCSV(message));
                writer.newLine();
            }
        }

        this.messages = new ArrayList<>(items);
    }

    @Override
    public void add(Message item) throws IOException {
        synchronized (WRITE_LOCK) {
            this.messages = loadAll();
            messages.add(item);
            saveAll(messages);
        }
    }

    @Override
    public void update(Message item) throws IOException {
        synchronized (WRITE_LOCK) {
            this.messages = loadAll();

            for (int i = 0; i < messages.size(); i++) {
                if (messages.get(i).getMessageId().equals(item.getMessageId())) {
                    messages.set(i, item);
                    saveAll(messages);
                    return;
                }
            }
        }
    }

    @Override
    public void delete(String id) throws IOException {
        synchronized (WRITE_LOCK) {
            this.messages = loadAll();
            messages.removeIf(message -> message.getMessageId().equals(id));
            saveAll(messages);
        }
    }

    @Override
    public Message findById(String id) {
        try {
            this.messages = loadAll();
        } catch (IOException e) {
            // use in-memory fallback
        }

        return messages.stream()
                .filter(message -> message.getMessageId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public List<Message> findByApplicationId(String applicationId) {
        try {
            this.messages = loadAll();
        } catch (IOException e) {
            // use in-memory fallback
        }

        return messages.stream()
                .filter(message -> message.getApplicationId().equals(applicationId))
                .sorted((m1, m2) -> m1.getSentAt().compareTo(m2.getSentAt()))
                .collect(Collectors.toList());
    }

    public int getUnreadCount(String userId, String applicationId) {
        try {
            this.messages = loadAll();
        } catch (IOException e) {
            // use in-memory fallback
        }

        return (int) messages.stream()
                .filter(message -> message.getApplicationId().equals(applicationId))
                .filter(message -> !message.getSenderId().equals(userId))
                .filter(message -> !message.isRead())
                .count();
    }

    public void markAllAsRead(String applicationId, String userId) throws IOException {
        synchronized (WRITE_LOCK) {
            this.messages = loadAll();

            boolean updated = false;
            for (Message message : messages) {
                if (message.getApplicationId().equals(applicationId)
                        && !message.getSenderId().equals(userId)
                        && !message.isRead()) {
                    message.setRead(true);
                    updated = true;
                }
            }

            if (updated) {
                saveAll(messages);
            }
        }
    }

    private Message parseMessageFromCSV(String line) {
        try {
            String[] parts = splitCSVLine(line);
            if (parts.length < 7) {
                return null;
            }

            Message message = new Message();
            message.setMessageId(parts[0]);
            message.setApplicationId(parts[1]);
            message.setSenderId(parts[2]);
            message.setSenderRole(UserRole.valueOf(parts[3]));
            message.setContent(parts[4]);
            message.setSentAt(parseDate(parts[5]));
            message.setRead(Boolean.parseBoolean(parts[6]));
            return message;
        } catch (ParseException | IllegalArgumentException e) {
            return null;
        }
    }

    private String formatMessageToCSV(Message message) {
        return escapeCSV(message.getMessageId()) + "," +
               escapeCSV(message.getApplicationId()) + "," +
               escapeCSV(message.getSenderId()) + "," +
               escapeCSV(message.getSenderRole().toString()) + "," +
               escapeCSV(message.getContent()) + "," +
               escapeCSV(formatDate(message.getSentAt())) + "," +
               escapeCSV(String.valueOf(message.isRead()));
    }

    private String[] splitCSVLine(String line) {
        List<String> result = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder current = new StringBuilder();

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                result.add(unescapeCSV(current.toString()));
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }

        result.add(unescapeCSV(current.toString()));
        return result.toArray(new String[0]);
    }

    private String escapeCSV(String value) {
        if (value == null) {
            return "";
        }

        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }

        return value;
    }

    private String unescapeCSV(String value) {
        if (value.startsWith("\"") && value.endsWith("\"")) {
            value = value.substring(1, value.length() - 1);
            value = value.replace("\"\"", "\"");
        }
        return value;
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
