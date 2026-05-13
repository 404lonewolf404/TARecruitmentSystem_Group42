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

public class ApplicationDAO implements CSVDataStore<Application> {

    private static final String DATA_FILE = "data/applications.csv";
    private static final String HEADER = "applicationId,taId,positionId,status,appliedAt,resumePath";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    private static final Object WRITE_LOCK = new Object();

    private List<Application> applications;

    private String getDataFilePath() {
        String catalinaBase = System.getProperty("catalina.base");
        if (catalinaBase != null && !catalinaBase.trim().isEmpty()) {
            return catalinaBase + "/webapps/TARecruitmentSystem/" + DATA_FILE;
        }
        return "webapps/TARecruitmentSystem/" + DATA_FILE;
    }

    public ApplicationDAO() {
        try {
            this.applications = loadAll();
        } catch (IOException e) {
            this.applications = new ArrayList<>();
        }
    }

    @Override
    public List<Application> loadAll() throws IOException {
        List<Application> applicationList = new ArrayList<>();
        File file = new File(getDataFilePath());

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

            String line = reader.readLine();

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

    @Override
    public void saveAll(List<Application> items) throws IOException {
        File file = new File(getDataFilePath());
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

    @Override
    public void add(Application item) throws IOException {
        synchronized (WRITE_LOCK) {
            this.applications = loadAll();
            applications.add(item);
            saveAll(applications);
        }
    }

    @Override
    public void update(Application item) throws IOException {
        synchronized (WRITE_LOCK) {
            this.applications = loadAll();

            for (int i = 0; i < applications.size(); i++) {
                if (applications.get(i).getApplicationId().equals(item.getApplicationId())) {
                    applications.set(i, item);
                    saveAll(applications);
                    return;
                }
            }
        }
    }

    @Override
    public void delete(String id) throws IOException {
        synchronized (WRITE_LOCK) {
            this.applications = loadAll();
            applications.removeIf(application -> application.getApplicationId().equals(id));
            saveAll(applications);
        }
    }

    @Override
    public Application findById(String id) {
        try {
            this.applications = loadAll();
        } catch (IOException e) {
            // use in-memory fallback
        }

        return applications.stream()
                .filter(application -> application.getApplicationId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public List<Application> findByTaId(String taId) {
        try {
            this.applications = loadAll();
        } catch (IOException e) {
            // use in-memory fallback
        }

        return applications.stream()
                .filter(application -> application.getTaId().equals(taId))
                .collect(Collectors.toList());
    }

    public List<Application> findByPositionId(String positionId) {
        try {
            this.applications = loadAll();
        } catch (IOException e) {
            // use in-memory fallback
        }

        return applications.stream()
                .filter(application -> application.getPositionId().equals(positionId))
                .collect(Collectors.toList());
    }

    public Application findByTaAndPosition(String taId, String positionId) {
        try {
            this.applications = loadAll();
        } catch (IOException e) {
            // use in-memory fallback
        }

        return applications.stream()
                .filter(application -> application.getTaId().equals(taId)
                        && application.getPositionId().equals(positionId))
                .findFirst()
                .orElse(null);
    }

    public boolean hasApplied(String taId, String positionId) {
        try {
            this.applications = loadAll();
        } catch (IOException e) {
            // use in-memory fallback
        }

        return applications.stream()
                .filter(application -> application.getTaId().equals(taId)
                        && application.getPositionId().equals(positionId))
                .anyMatch(application -> application.getStatus() == ApplicationStatus.PENDING
                        || application.getStatus() == ApplicationStatus.SELECTED);
    }

    public List<Application> findByPositionIdAndStatus(String positionId, ApplicationStatus status) {
        try {
            this.applications = loadAll();
        } catch (IOException e) {
            // use in-memory fallback
        }

        return applications.stream()
                .filter(application -> application.getPositionId().equals(positionId)
                        && application.getStatus() == status)
                .collect(Collectors.toList());
    }

    public int countSelectedByPositionId(String positionId) {
        try {
            this.applications = loadAll();
        } catch (IOException e) {
            // use in-memory fallback
        }

        return (int) applications.stream()
                .filter(application -> application.getPositionId().equals(positionId)
                        && application.getStatus() == ApplicationStatus.SELECTED)
                .count();
    }

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
            application.setAppliedAt(parseDate(parts[4]));

            if (parts.length >= 6) {
                application.setResumePath(parts[5]);
            }

            return application;
        } catch (ParseException | IllegalArgumentException e) {
            return null;
        }
    }

    private String formatApplicationToCSV(Application application) {
        return escapeCSV(application.getApplicationId()) + "," +
               escapeCSV(application.getTaId()) + "," +
               escapeCSV(application.getPositionId()) + "," +
               escapeCSV(application.getStatus().toString()) + "," +
               escapeCSV(formatDate(application.getAppliedAt())) + "," +
               escapeCSV(application.getResumePath() != null ? application.getResumePath() : "");
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
