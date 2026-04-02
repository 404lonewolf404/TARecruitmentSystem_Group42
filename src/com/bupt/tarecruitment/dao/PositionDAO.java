package com.bupt.tarecruitment.dao;

import com.bupt.tarecruitment.model.Position;
import com.bupt.tarecruitment.model.PositionStatus;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 职位数据访问对象
 * 负责职位数据的CSV文件读写操作
 */
public class PositionDAO implements CSVDataStore<Position> {
    
    private static final String FILE_PATH = "webapps/TARecruitmentSystem/data/positions.csv";
    private static final String HEADER = "positionId,moId,title,description,requirements,hours,maxPositions,status,createdAt";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    
    private List<Position> positions;
    
    /**
     * 构造函数 - 初始化时加载数据
     */
    public PositionDAO() {
        try {
            this.positions = loadAll();
        } catch (IOException e) {
            this.positions = new ArrayList<>();
        }
    }
    
    /**
     * 从CSV文件加载所有职位
     */
    @Override
    public List<Position> loadAll() throws IOException {
        List<Position> positionList = new ArrayList<>();
        File file = new File(FILE_PATH);
        
        // 如果文件不存在，创建带标题的空文件
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try (BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
                writer.write(HEADER);
                writer.newLine();
            }
            return positionList;
        }
        
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            
            String line = reader.readLine(); // 跳过标题行
            
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                
                Position position = parsePositionFromCSV(line);
                if (position != null) {
                    positionList.add(position);
                }
            }
        }
        
        return positionList;
    }
    
    /**
     * 将所有职位保存到CSV文件
     */
    @Override
    public void saveAll(List<Position> items) throws IOException {
        File file = new File(FILE_PATH);
        file.getParentFile().mkdirs();
        
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            
            writer.write(HEADER);
            writer.newLine();
            
            for (Position position : items) {
                writer.write(formatPositionToCSV(position));
                writer.newLine();
            }
        }
        
        this.positions = new ArrayList<>(items);
    }
    
    /**
     * 添加新职位
     */
    @Override
    public void add(Position item) throws IOException {
        positions.add(item);
        saveAll(positions);
    }
    
    /**
     * 更新职位信息
     */
    @Override
    public void update(Position item) throws IOException {
        for (int i = 0; i < positions.size(); i++) {
            if (positions.get(i).getPositionId().equals(item.getPositionId())) {
                positions.set(i, item);
                saveAll(positions);
                return;
            }
        }
    }
    
    /**
     * 删除职位
     */
    @Override
    public void delete(String id) throws IOException {
        positions.removeIf(position -> position.getPositionId().equals(id));
        saveAll(positions);
    }
    
    /**
     * 根据ID查找职位
     */
    @Override
    public Position findById(String id) {
        // 重新加载数据以确保获取最新数据
        try {
            this.positions = loadAll();
        } catch (IOException e) {
            // 如果加载失败，使用当前内存中的数据
        }
        
        return positions.stream()
                .filter(position -> position.getPositionId().equals(id))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * 根据MO ID查找职位列表
     * 
     * @param moId MO的用户ID
     * @return 该MO创建的所有职位列表
     */
    public List<Position> findByMoId(String moId) {
        // 重新加载数据以确保获取最新数据
        try {
            this.positions = loadAll();
        } catch (IOException e) {
            // 如果加载失败，使用当前内存中的数据
        }
        
        return positions.stream()
                .filter(position -> position.getMoId().equals(moId))
                .collect(Collectors.toList());
    }
    
    /**
     * 查找所有开放的职位
     * 
     * @return 所有状态为OPEN的职位列表
     */
    public List<Position> findAllOpen() {
        // 重新加载数据以确保获取最新数据
        try {
            this.positions = loadAll();
        } catch (IOException e) {
            // 如果加载失败，使用当前内存中的数据
        }
        
        return positions.stream()
                .filter(position -> position.getStatus() == PositionStatus.OPEN)
                .collect(Collectors.toList());
    }
    
    /**
     * 从CSV行解析职位对象
     */
    private Position parsePositionFromCSV(String line) {
        try {
            String[] parts = splitCSVLine(line);
            if (parts.length < 8) {
                return null;
            }
            
            Position position = new Position();
            position.setPositionId(parts[0]);
            position.setMoId(parts[1]);
            position.setTitle(parts[2]);
            position.setDescription(parts[3]);
            position.setRequirements(parts[4]);
            position.setHours(Integer.parseInt(parts[5]));
            
            // 兼容旧数据：如果有第7个字段（maxPositions），则读取，否则默认为1
            if (parts.length >= 9) {
                position.setMaxPositions(Integer.parseInt(parts[6]));
                position.setStatus(PositionStatus.valueOf(parts[7]));
                position.setCreatedAt(DATE_FORMAT.parse(parts[8]));
            } else {
                position.setMaxPositions(1); // 默认名额为1
                position.setStatus(PositionStatus.valueOf(parts[6]));
                position.setCreatedAt(DATE_FORMAT.parse(parts[7]));
            }
            
            return position;
        } catch (ParseException | IllegalArgumentException e) {
            return null;
        }
    }
    
    /**
     * 将职位对象格式化为CSV行
     */
    private String formatPositionToCSV(Position position) {
        return escapeCSV(position.getPositionId()) + "," +
               escapeCSV(position.getMoId()) + "," +
               escapeCSV(position.getTitle()) + "," +
               escapeCSV(position.getDescription()) + "," +
               escapeCSV(position.getRequirements()) + "," +
               escapeCSV(String.valueOf(position.getHours())) + "," +
               escapeCSV(String.valueOf(position.getMaxPositions())) + "," +
               escapeCSV(position.getStatus().toString()) + "," +
               escapeCSV(DATE_FORMAT.format(position.getCreatedAt()));
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
