package com.bupt.tarecruitment.dao;

import java.io.IOException;
import java.util.List;

/**
 * Generic CSV-based persistence contract.
 */
public interface CSVDataStore<T> {
    
    /**
     * Loads all records from storage.
     *
     * @return operation result
     * @throws IOException if operation fails
     */
    List<T> loadAll() throws IOException;
    
    /**
     * Persists the full record list to storage.
     *
     * @param items items value
     * @throws IOException if operation fails
     */
    void saveAll(List<T> items) throws IOException;
    
    /**
     * Appends a single record to storage.
     *
     * @param item item value
     * @throws IOException if operation fails
     */
    void add(T item) throws IOException;
    
    /**
     * Updates an existing record in storage.
     *
     * @param item item value
     * @throws IOException if operation fails
     */
    void update(T item) throws IOException;
    
    /**
     * Deletes a record by primary key.
     *
     * @param id id value
     * @throws IOException if operation fails
     */
    void delete(String id) throws IOException;
    
    /**
     * Finds a record by primary key.
     *
     * @param id id value
     * @return operation result
     */
    T findById(String id);
}
