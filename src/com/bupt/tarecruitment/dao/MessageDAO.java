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

/**
 * 消息数据访问对象
 * V3.5 - 招聘对话系统
 */
public class MessageDAO implements CSVDataStore<Message> {
    
    private static final String FILE_PATH = "webapps/TARecruitmentSystem/data/messages.csv";
    private static final String HEADER = "messageId,applicationId,senderId,senderRole,content,sentAt,isRead";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    
    private List<Message> messages;
    
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
        File file = new File(FILE_PATH);
        
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
            
            String line = reader.readLine(); // 跳过标题行
            
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
        File file = new File(FILE_PATH);
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
        messages.add(item);
        saveAll(messages);
    }
    
    @Override
    public void update(Message item) throws IOException {
        for (int i = 0; i < messages.size(); i++) {
            if (messages.get(i).getMessageId().equals(item.getMessageId())) {
                messages.set(i, item);
                saveAll(messages);
                return;
            }
        }
    }
    
    @Override
    public void delete(String id) throws IOException {
        messages.removeIf(message -> message.getMessageId().equals(id));
        saveAll(messages);
    }
    
    @Override
    public Message findById(String id) {
        try {
            this.messages = loadAll();
        } catch (IOException e) {
            // 使用当前内存中的数据
        }
        
        return messages.stream()
                .filter(message -> message.getMessageId().equals(id))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * 根据申请ID查找所有消息
     */
    public List<Message> findByApplicationId(String applicationId) {
        try {
            this.messages = loadAll();
        } catch (IOException e) {
            // 使用当前内存中的数据
        }
        
        return messages.stream()
                .filter(message -> message.getApplicationId().equals(applicationId))
                .sorted((m1, m2) -> m1.getSentAt().compareTo(m2.getSentAt()))
                .collect(Collectors.toList());
    }
    
    /**
     * 获取未读消息数量
     */
    public int getUnreadCount(String userId, String applicationId) {
        try {
            this.messages = loadAll();
        } catch (IOException e) {
            // 使用当前内存中的数据
        }
        
        return (int) messages.stream()
                .filter(message -> message.getApplicationId().equals(applicationId))
                .filter(message -> !message.getSenderId().equals(userId))
                .filter(message -> !message.isRead())
                .count();
    }
    
    /**
     * 标记所有消息为已读
     */
    public void markAllAsRead(String applicationId, String userId) throws IOException {
        try {
            this.messages = loadAll();
        } catch (IOException e) {
            // 使用当前内存中的数据
        }
        
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
            message.setSentAt(DATE_FORMAT.parse(parts[5]));
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
               escapeCSV(DATE_FORMAT.format(message.getSentAt())) + "," +
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
}
