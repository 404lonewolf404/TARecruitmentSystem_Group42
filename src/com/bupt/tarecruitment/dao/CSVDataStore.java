package com.bupt.tarecruitment.dao;

import java.io.IOException;
import java.util.List;

/**
 * CSV数据存储接口
 * 定义所有DAO类的通用CRUD操作
 * 
 * @param <T> 数据实体类型
 */
public interface CSVDataStore<T> {
    
    /**
     * 从CSV文件加载所有数�?
     * 
     * @return 所有数据实体的列表
     * @throws IOException 如果文件读取失败
     */
    List<T> loadAll() throws IOException;
    
    /**
     * 将所有数据保存到CSV文件
     * 
     * @param items 要保存的数据实体列表
     * @throws IOException 如果文件写入失败
     */
    void saveAll(List<T> items) throws IOException;
    
    /**
     * 添加新的数据实体
     * 
     * @param item 要添加的数据实体
     * @throws IOException 如果文件操作失败
     */
    void add(T item) throws IOException;
    
    /**
     * 更新现有的数据实�?
     * 
     * @param item 要更新的数据实体
     * @throws IOException 如果文件操作失败
     */
    void update(T item) throws IOException;
    
    /**
     * 根据ID删除数据实体
     * 
     * @param id 要删除的实体ID
     * @throws IOException 如果文件操作失败
     */
    void delete(String id) throws IOException;
    
    /**
     * 根据ID查找数据实体
     * 
     * @param id 要查找的实体ID
     * @return 找到的数据实体，如果不存在则返回null
     */
    T findById(String id);
}
