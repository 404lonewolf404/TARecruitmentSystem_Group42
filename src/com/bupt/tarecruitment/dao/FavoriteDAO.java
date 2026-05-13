package com.bupt.tarecruitment.dao;

import com.bupt.tarecruitment.model.Favorite;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class FavoriteDAO implements CSVDataStore<Favorite> {

    private static final String DATA_FILE = "data/favorites.csv";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final Object WRITE_LOCK = new Object();

    private List<Favorite> favorites;

    private String getDataFilePath() {
        String contextPath = System.getProperty("catalina.base");
        if (contextPath != null) {
            return contextPath + "/webapps/TARecruitmentSystem/" + DATA_FILE;
        }
        return DATA_FILE;
    }

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
        File file = new File(getDataFilePath());

        if (!file.exists()) {
            return items;
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line = reader.readLine();

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
        File file = new File(getDataFilePath());
        file.getParentFile().mkdirs();

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            writer.write("favoriteId,taId,positionId,createdAt");
            writer.newLine();

            for (Favorite favorite : items) {
                writer.write(toCSVLine(favorite));
                writer.newLine();
            }
        }

        this.favorites = new ArrayList<>(items);
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
            favorite.setCreatedAt(parseDate(parts[3].trim()));
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
            formatDate(favorite.getCreatedAt())
        );
    }

    @Override
    public void add(Favorite favorite) throws IOException {
        synchronized (WRITE_LOCK) {
            this.favorites = loadAll();
            favorites.add(favorite);
            saveAll(favorites);
        }
    }

    @Override
    public void update(Favorite favorite) throws IOException {
        synchronized (WRITE_LOCK) {
            this.favorites = loadAll();

            for (int i = 0; i < favorites.size(); i++) {
                if (favorites.get(i).getFavoriteId().equals(favorite.getFavoriteId())) {
                    favorites.set(i, favorite);
                    saveAll(favorites);
                    return;
                }
            }
        }
    }

    @Override
    public void delete(String favoriteId) throws IOException {
        synchronized (WRITE_LOCK) {
            this.favorites = loadAll();
            favorites.removeIf(f -> f.getFavoriteId().equals(favoriteId));
            saveAll(favorites);
        }
    }

    @Override
    public Favorite findById(String favoriteId) {
        try {
            this.favorites = loadAll();
        } catch (IOException e) {
            // use in-memory fallback
        }

        return favorites.stream()
                .filter(f -> f.getFavoriteId().equals(favoriteId))
                .findFirst()
                .orElse(null);
    }

    public List<Favorite> findByTaId(String taId) {
        try {
            this.favorites = loadAll();
        } catch (IOException e) {
            // use in-memory fallback
        }

        return favorites.stream()
                .filter(f -> f.getTaId().equals(taId))
                .collect(Collectors.toList());
    }

    public List<Favorite> findByPositionId(String positionId) {
        try {
            this.favorites = loadAll();
        } catch (IOException e) {
            // use in-memory fallback
        }

        return favorites.stream()
                .filter(f -> f.getPositionId().equals(positionId))
                .collect(Collectors.toList());
    }

    public Favorite findByTaAndPosition(String taId, String positionId) {
        try {
            this.favorites = loadAll();
        } catch (IOException e) {
            // use in-memory fallback
        }

        return favorites.stream()
                .filter(f -> f.getTaId().equals(taId) && f.getPositionId().equals(positionId))
                .findFirst()
                .orElse(null);
    }

    public boolean isFavorited(String taId, String positionId) {
        return findByTaAndPosition(taId, positionId) != null;
    }

    public void deleteByPositionId(String positionId) throws IOException {
        synchronized (WRITE_LOCK) {
            this.favorites = loadAll();
            favorites.removeIf(f -> f.getPositionId().equals(positionId));
            saveAll(favorites);
        }
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
