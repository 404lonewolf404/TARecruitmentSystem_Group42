package com.bupt.tarecruitment.dao;

import com.bupt.tarecruitment.model.Application;
import com.bupt.tarecruitment.model.ApplicationStatus;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 申请数据访问对象
 * 负责申请数据的CSV文件读写操作
 */
public class ApplicationDAO implements CSVDataStore<Application> {
    
    private static final String FILE_PATH = "webapps/TARecruitmentSystem/data/applications.csv";
    private static final String HEADER = "applicationId,taId,positionId,status,appliedAt,resumePath";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    
    private List<Application> applications;
    
    /**
     * 构造函�?- 初始化时加载数据
     */
    public ApplicationDAO() {
        try {
            this.applications = loadAll();
        } catch (IOException e) {
            this.applications = new ArrayList<>();
        }
    }
    
    /**
     * 从CSV文件加载所有申�?
     */
    @Override
    public List<Application> loadAll() throws IOException {
        List<Application> applicationList = new ArrayList<>();
        File file = new File(FILE_PATH);
        
        // 如果文件不存在，创建带标题的空文�?
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try (BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
                writer.write(HEADER);
                writer.newLine();
            }
            return applicationList;
        }
        
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            
            String line = reader.readLine(); // 跳过标题�?
            
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                
                Application application = parseApplicationFromCSV(line);
                if (application != null) {
                    applicationList.add(application);
                }
            }
        }
        
        return applicationList;
    }
    
    /**
     * 将所有申请保存到CSV文件
     */
    @Override
    public void saveAll(List<Application> items) throws IOException {
        File file = new File(FILE_PATH);
        file.getParentFile().mkdirs();
        
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            
            writer.write(HEADER);
            writer.newLine();
            
            for (Application application : items) {
                writer.write(formatApplicationToCSV(application));
                writer.newLine();
            }
        }
        
        this.applications = new ArrayList<>(items);
    }
    
    /**
     * 添加新申�?
     */
    @Override
    public void add(Application item) throws IOException {
        applications.add(item);
        saveAll(applications);
    }
    
    /**
     * 更新申请信息
     */
    @Override
    public void update(Application item) throws IOException {
        for (int i = 0; i < applications.size(); i++) {
            if (applications.get(i).getApplicationId().equals(item.getApplicationId())) {
                applications.set(i, item);
                saveAll(applications);
                return;
            }
        }
    }
    
    /**
     * 删除申请
     */
    @Override
    public void delete(String id) throws IOException {
        applications.removeIf(application -> application.getApplicationId().equals(id));
        saveAll(applications);
    }
    
    /**
     * 根据ID查找申请
     */
    @Override
    public Application findById(String id) {
        // 重新加载数据以确保获取最新数�?
        try {
            this.applications = loadAll();
        } catch (IOException e) {
            // 如果加载失败，使用当前内存中的数�?
        }
        
        return applications.stream()
                .filter(application -> application.getApplicationId().equals(id))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * 根据TA ID查找申请列表
     * 
     * @param taId TA的用户ID
     * @return 该TA提交的所有申请列�?
     */
    public List<Application> findByTaId(String taId) {
        // 重新加载数据以确保获取最新数�?
        try {
            this.applications = loadAll();
        } catch (IOException e) {
            // 如果加载失败，使用当前内存中的数�?
        }
        
        return applications.stream()
                .filter(application -> application.getTaId().equals(taId))
                .collect(Collectors.toList());
    }
    
    /**
     * 根据职位ID查找申请列表
     * 
     * @param positionId 职位ID
     * @return 该职位的所有申请列�?
     */
    public List<Application> findByPositionId(String positionId) {
        // 重新加载数据以确保获取最新数�?
        try {
            this.applications = loadAll();
        } catch (IOException e) {
            // 如果加载失败，使用当前内存中的数�?
        }
        
        return applications.stream()
                .filter(application -> application.getPositionId().equals(positionId))
                .collect(Collectors.toList());
    }
    
    /**
     * 根据TA ID和职位ID查找申请
     * 
     * @param taId TA的用户ID
     * @param positionId 职位ID
     * @return 找到的申请，如果不存在则返回null
     */
    public Application findByTaAndPosition(String taId, String positionId) {
        // 重新加载数据以确保获取最新数�?
        try {
            this.applications = loadAll();
        } catch (IOException e) {
            // 如果加载失败，使用当前内存中的数�?
        }
        
        return applications.stream()
                .filter(application -> application.getTaId().equals(taId) 
                        && application.getPositionId().equals(positionId))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * 检查TA是否已申请某职位
     * 
     * @param taId TA的用户ID
     * @param positionId 职位ID
     * @return 如果已申请返回true，否则返回false
     */
    public boolean hasApplied(String taId, String positionId) {
        return findByTaAndPosition(taId, positionId) != null;
    }
    
    /**
     * 从CSV行解析申请对�?
     */
    private Application parseApplicationFromCSV(String line) {
        try {
            String[] parts = splitCSVLine(line);
            if (parts.length < 5) {
                return null;
            }
            
            Application application = new Application();
            application.setApplicationId(parts[0]);
            application.setTaId(parts[1]);
            application.setPositionId(parts[2]);
            application.setStatus(ApplicationStatus.valueOf(parts[3]));
            application.setAppliedAt(DATE_FORMAT.parse(parts[4]));
            
            // 处理resumePath字段（可能不存在于旧数据�?
            if (parts.length >= 6) {
                application.setResumePath(parts[5]);
            }
            
            return application;
        } catch (ParseException | IllegalArgumentException e) {
            return null;
        }
    }
    
    /**
     * 将申请对象格式化为CSV�?
     */
    private String formatApplicationToCSV(Application application) {
        return escapeCSV(application.getApplicationId()) + "," +
               escapeCSV(application.getTaId()) + "," +
               escapeCSV(application.getPositionId()) + "," +
               escapeCSV(application.getStatus().toString()) + "," +
               escapeCSV(DATE_FORMAT.format(application.getAppliedAt())) + "," +
               escapeCSV(application.getResumePath() != null ? application.getResumePath() : "");
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
