package com.bupt.tarecruitment.dao;

import com.bupt.tarecruitment.model.Favorite;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 收藏数据访问对象
 * 负责收藏数据的持久化操作
 */
public class FavoriteDAO implements CSVDataStore<Favorite> {
    
    private static final String FILE_PATH = "data/favorites.csv";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    private List<Favorite> favorites;
    
    /**
     * 构造函数，加载数据
     */
    public FavoriteDAO() {
        this.favorites = new ArrayList<>();
        try {
            this.favorites = loadAll();
        } catch (IOException e) {
            System.err.println("加载收藏数据失败: " + e.getMessage());
        }
    }
    
    @Override
    public List<Favorite> loadAll() throws IOException {
        List<Favorite> items = new ArrayList<>();
        File file = new File(FILE_PATH);
        
        if (!file.exists()) {
            return items;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine(); // 跳过标题行
            
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                
                Favorite favorite = fromCSVLine(line);
                if (favorite != null) {
                    items.add(favorite);
                }
            }
        }
        
        return items;
    }
    
    @Override
    public void saveAll(List<Favorite> items) throws IOException {
        File file = new File(FILE_PATH);
        file.getParentFile().mkdirs();
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            // 写入标题行
            writer.write("favoriteId,taId,positionId,createdAt");
            writer.newLine();
            
            // 写入数据行
            for (Favorite favorite : items) {
                writer.write(toCSVLine(favorite));
                writer.newLine();
            }
        }
    }
    
    private Favorite fromCSVLine(String line) {
        String[] parts = line.split(",", -1);
        if (parts.length < 4) {
            return null;
        }
        
        try {
            Favorite favorite = new Favorite();
            favorite.setFavoriteId(parts[0].trim());
            favorite.setTaId(parts[1].trim());
            favorite.setPositionId(parts[2].trim());
            favorite.setCreatedAt(DATE_FORMAT.parse(parts[3].trim()));
            return favorite;
        } catch (ParseException e) {
            System.err.println("解析收藏数据失败: " + e.getMessage());
            return null;
        }
    }
    
    private String toCSVLine(Favorite favorite) {
        return String.join(",",
            favorite.getFavoriteId(),
            favorite.getTaId(),
            favorite.getPositionId(),
            DATE_FORMAT.format(favorite.getCreatedAt())
        );
    }
    
    @Override
    public void add(Favorite favorite) throws IOException {
        favorites.add(favorite);
        saveAll(favorites);
    }
    
    @Override
    public void update(Favorite favorite) throws IOException {
        for (int i = 0; i < favorites.size(); i++) {
            if (favorites.get(i).getFavoriteId().equals(favorite.getFavoriteId())) {
                favorites.set(i, favorite);
                saveAll(favorites);
                return;
            }
        }
    }
    
    @Override
    public void delete(String favoriteId) throws IOException {
        favorites.removeIf(f -> f.getFavoriteId().equals(favoriteId));
        saveAll(favorites);
    }
    
    @Override
    public Favorite findById(String favoriteId) {
        return favorites.stream()
                .filter(f -> f.getFavoriteId().equals(favoriteId))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * 根据TA ID查找所有收藏
     * 
     * @param taId TA用户ID
     * @return 该TA的所有收藏列表
     */
    public List<Favorite> findByTaId(String taId) {
        return favorites.stream()
                .filter(f -> f.getTaId().equals(taId))
                .collect(Collectors.toList());
    }
    
    /**
     * 根据职位ID查找所有收藏
     * 
     * @param positionId 职位ID
     * @return 收藏该职位的所有记录
     */
    public List<Favorite> findByPositionId(String positionId) {
        return favorites.stream()
                .filter(f -> f.getPositionId().equals(positionId))
                .collect(Collectors.toList());
    }
    
    /**
     * 查找特定TA对特定职位的收藏
     * 
     * @param taId TA用户ID
     * @param positionId 职位ID
     * @return 收藏记录，如果不存在则返回null
     */
    public Favorite findByTaAndPosition(String taId, String positionId) {
        return favorites.stream()
                .filter(f -> f.getTaId().equals(taId) && f.getPositionId().equals(positionId))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * 检查TA是否已收藏某职位
     * 
     * @param taId TA用户ID
     * @param positionId 职位ID
     * @return 如果已收藏返回true，否则返回false
     */
    public boolean isFavorited(String taId, String positionId) {
        return findByTaAndPosition(taId, positionId) != null;
    }
    
    /**
     * 删除某职位的所有收藏（级联删除）
     * 
     * @param positionId 职位ID
     * @throws IOException 如果保存失败
     */
    public void deleteByPositionId(String positionId) throws IOException {
        favorites.removeIf(f -> f.getPositionId().equals(positionId));
        saveAll(favorites);
    }
}
