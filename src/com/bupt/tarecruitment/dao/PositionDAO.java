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

public class PositionDAO implements CSVDataStore<Position> {

    private static final String DATA_FILE = "data/positions.csv";
    private static final String HEADER = "positionId,moId,title,description,requirements,hours,maxPositions,status,createdAt,deadline";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    private static final Object WRITE_LOCK = new Object();

    private List<Position> positions;

    private String getDataFilePath() {
        String catalinaBase = System.getProperty("catalina.base");
        if (catalinaBase != null && !catalinaBase.trim().isEmpty()) {
            return catalinaBase + "/webapps/TARecruitmentSystem/" + DATA_FILE;
        }
        return "webapps/TARecruitmentSystem/" + DATA_FILE;
    }

    public PositionDAO() {
        try {
            this.positions = loadAll();
        } catch (IOException e) {
            this.positions = new ArrayList<>();
        }
    }

    @Override
    public List<Position> loadAll() throws IOException {
        List<Position> positionList = new ArrayList<>();
        File file = new File(getDataFilePath());

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

            String line = reader.readLine();

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

    @Override
    public void saveAll(List<Position> items) throws IOException {
        File file = new File(getDataFilePath());
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

    @Override
    public void add(Position item) throws IOException {
        synchronized (WRITE_LOCK) {
            this.positions = loadAll();
            positions.add(item);
            saveAll(positions);
        }
    }

    @Override
    public void update(Position item) throws IOException {
        synchronized (WRITE_LOCK) {
            this.positions = loadAll();

            for (int i = 0; i < positions.size(); i++) {
                if (positions.get(i).getPositionId().equals(item.getPositionId())) {
                    positions.set(i, item);
                    saveAll(positions);
                    return;
                }
            }
        }
    }

    @Override
    public void delete(String id) throws IOException {
        synchronized (WRITE_LOCK) {
            this.positions = loadAll();
            positions.removeIf(position -> position.getPositionId().equals(id));
            saveAll(positions);
        }
    }

    @Override
    public Position findById(String id) {
        try {
            this.positions = loadAll();
        } catch (IOException e) {
            // 中文说明：读取失败时保留当前内存数据并返回查找结果。
        }

        return positions.stream()
                .filter(position -> position.getPositionId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public List<Position> findByMoId(String moId) {
        try {
            this.positions = loadAll();
        } catch (IOException e) {
            // 中文说明：读取失败时退回当前内存缓存。
        }

        return positions.stream()
                .filter(position -> position.getMoId().equals(moId))
                .collect(Collectors.toList());
    }

    public List<Position> findAllOpen() {
        try {
            this.positions = loadAll();
        } catch (IOException e) {
            // 中文说明：读取失败时退回当前内存缓存。
        }

        return positions.stream()
                .filter(position -> position.getStatus() == PositionStatus.OPEN)
                .collect(Collectors.toList());
    }

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

            if (parts.length >= 9) {
                position.setMaxPositions(Integer.parseInt(parts[6]));
                position.setStatus(PositionStatus.valueOf(parts[7]));
                position.setCreatedAt(parseDate(parts[8]));

                if (parts.length >= 10 && !parts[9].isEmpty()) {
                    position.setDeadline(parseDate(parts[9]));
                }
            } else {
                position.setMaxPositions(1);
                position.setStatus(PositionStatus.valueOf(parts[6]));
                position.setCreatedAt(parseDate(parts[7]));
            }

            return position;
        } catch (ParseException | IllegalArgumentException e) {
            return null;
        }
    }

    private String formatPositionToCSV(Position position) {
        return escapeCSV(position.getPositionId()) + "," +
               escapeCSV(position.getMoId()) + "," +
               escapeCSV(position.getTitle()) + "," +
               escapeCSV(position.getDescription()) + "," +
               escapeCSV(position.getRequirements()) + "," +
               escapeCSV(String.valueOf(position.getHours())) + "," +
               escapeCSV(String.valueOf(position.getMaxPositions())) + "," +
               escapeCSV(position.getStatus().toString()) + "," +
               escapeCSV(formatDate(position.getCreatedAt())) + "," +
               escapeCSV(position.getDeadline() != null ? formatDate(position.getDeadline()) : "");
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
