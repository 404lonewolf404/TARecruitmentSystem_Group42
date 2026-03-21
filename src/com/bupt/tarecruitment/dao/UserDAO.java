package com.bupt.tarecruitment.dao;

import com.bupt.tarecruitment.model.User;
import com.bupt.tarecruitment.model.UserRole;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 用户数据访问对象
 * 负责用户数据的CSV文件读写操作
 */
public class UserDAO implements CSVDataStore<User> {
    
    private static final String FILE_PATH = "webapps/TARecruitmentSystem/data/users.csv";
    private static final String HEADER = "userId,name,email,password,role,skills,cvPath,createdAt";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    
    private List<User> users;
    
    /**
     * 构造函�?- 初始化时加载数据
     */
    public UserDAO() {
        try {
            this.users = loadAll();
        } catch (IOException e) {
            this.users = new ArrayList<>();
        }
    }
    
    /**
     * 从CSV文件加载所有用�?
     */
    @Override
    public List<User> loadAll() throws IOException {
        List<User> userList = new ArrayList<>();
        File file = new File(FILE_PATH);
        
        // 如果文件不存在，创建带标题的空文�?
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try (BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
                writer.write(HEADER);
                writer.newLine();
            }
            return userList;
        }
        
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            
            String line = reader.readLine(); // 跳过标题�?
            
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                
                User user = parseUserFromCSV(line);
                if (user != null) {
                    userList.add(user);
                }
            }
        }
        
        return userList;
    }
    
    /**
     * 将所有用户保存到CSV文件
     */
    @Override
    public void saveAll(List<User> items) throws IOException {
        File file = new File(FILE_PATH);
        file.getParentFile().mkdirs();
        
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            
            writer.write(HEADER);
            writer.newLine();
            
            for (User user : items) {
                writer.write(formatUserToCSV(user));
                writer.newLine();
            }
        }
        
        this.users = new ArrayList<>(items);
    }
    
    /**
     * 添加新用�?
     */
    @Override
    public void add(User item) throws IOException {
        users.add(item);
        saveAll(users);
    }
    
    /**
     * 更新用户信息
     */
    @Override
    public void update(User item) throws IOException {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUserId().equals(item.getUserId())) {
                users.set(i, item);
                saveAll(users);
                return;
            }
        }
    }
    
    /**
     * 删除用户
     */
    @Override
    public void delete(String id) throws IOException {
        users.removeIf(user -> user.getUserId().equals(id));
        saveAll(users);
    }
    
    /**
     * 根据ID查找用户
     */
    @Override
    public User findById(String id) {
        return users.stream()
                .filter(user -> user.getUserId().equals(id))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * 根据邮箱查找用户
     * 
     * @param email 用户邮箱
     * @return 找到的用户，如果不存在则返回null
     */
    public User findByEmail(String email) {
        return users.stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * 验证用户凭证
     * 
     * @param email 用户邮箱
     * @param password 用户密码
     * @return 如果凭证正确返回用户对象，否则返回null
     */
    public User authenticate(String email, String password) {
        User user = findByEmail(email);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }
    
    /**
     * 检查邮箱是否已存在
     * 
     * @param email 要检查的邮箱
     * @return 如果邮箱已存在返回true，否则返回false
     */
    public boolean emailExists(String email) {
        return findByEmail(email) != null;
    }
    
    /**
     * 从CSV行解析用户对�?
     */
    private User parseUserFromCSV(String line) {
        try {
            String[] parts = splitCSVLine(line);
            if (parts.length < 7) {
                return null;
            }
            
            User user = new User();
            user.setUserId(parts[0]);
            user.setName(parts[1]);
            user.setEmail(parts[2]);
            user.setPassword(parts[3]);
            user.setRole(UserRole.valueOf(parts[4]));
            user.setSkills(parts[5]);
            // 兼容旧数据：如果有第7个字段（cvPath），则读�?
            if (parts.length >= 8) {
                user.setCvPath(parts[6]);
                user.setCreatedAt(DATE_FORMAT.parse(parts[7]));
            } else {
                user.setCvPath(null);
                user.setCreatedAt(DATE_FORMAT.parse(parts[6]));
            }
            
            return user;
        } catch (ParseException | IllegalArgumentException e) {
            return null;
        }
    }
    
    /**
     * 将用户对象格式化为CSV�?
     */
    private String formatUserToCSV(User user) {
        return escapeCSV(user.getUserId()) + "," +
               escapeCSV(user.getName()) + "," +
               escapeCSV(user.getEmail()) + "," +
               escapeCSV(user.getPassword()) + "," +
               escapeCSV(user.getRole().toString()) + "," +
               escapeCSV(user.getSkills() != null ? user.getSkills() : "") + "," +
               escapeCSV(user.getCvPath() != null ? user.getCvPath() : "") + "," +
               escapeCSV(DATE_FORMAT.format(user.getCreatedAt()));
    }
    
    /**
     * 分割CSV行，处理引号内的逗号
     */
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
    
    /**
     * 转义CSV特殊字符
     */
    private String escapeCSV(String value) {
        if (value == null) {
            return "";
        }
        
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        
        return value;
    }
    
    /**
     * 反转义CSV特殊字符
     */
    private String unescapeCSV(String value) {
        if (value.startsWith("\"") && value.endsWith("\"")) {
            value = value.substring(1, value.length() - 1);
            value = value.replace("\"\"", "\"");
        }
        return value;
    }
}
