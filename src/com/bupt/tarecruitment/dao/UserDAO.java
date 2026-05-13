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

public class UserDAO implements CSVDataStore<User> {

    private static final String DATA_FILE = "data/users.csv";
    private static final String HEADER = "userId,name,email,password,role,skills,cvPath,createdAt";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    private static final Object WRITE_LOCK = new Object();

    private List<User> users;

    private String getDataFilePath() {
        String catalinaBase = System.getProperty("catalina.base");
        if (catalinaBase != null && !catalinaBase.trim().isEmpty()) {
            return catalinaBase + "/webapps/TARecruitmentSystem/" + DATA_FILE;
        }
        return "webapps/TARecruitmentSystem/" + DATA_FILE;
    }

    public UserDAO() {
        try {
            this.users = loadAll();
        } catch (IOException e) {
            this.users = new ArrayList<>();
        }
    }

    @Override
    public List<User> loadAll() throws IOException {
        List<User> userList = new ArrayList<>();
        File file = new File(getDataFilePath());

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

            String line = reader.readLine();

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

    @Override
    public void saveAll(List<User> items) throws IOException {
        File file = new File(getDataFilePath());
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

    @Override
    public void add(User item) throws IOException {
        synchronized (WRITE_LOCK) {
            this.users = loadAll();
            users.add(item);
            saveAll(users);
        }
    }

    @Override
    public void update(User item) throws IOException {
        synchronized (WRITE_LOCK) {
            this.users = loadAll();

            for (int i = 0; i < users.size(); i++) {
                if (users.get(i).getUserId().equals(item.getUserId())) {
                    users.set(i, item);
                    saveAll(users);
                    return;
                }
            }
        }
    }

    @Override
    public void delete(String id) throws IOException {
        synchronized (WRITE_LOCK) {
            this.users = loadAll();
            users.removeIf(user -> user.getUserId().equals(id));
            saveAll(users);
        }
    }

    @Override
    public User findById(String id) {
        try {
            this.users = loadAll();
        } catch (IOException e) {
            // use in-memory fallback
        }

        return users.stream()
                .filter(user -> user.getUserId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public User findByEmail(String email) {
        try {
            this.users = loadAll();
        } catch (IOException e) {
            // use in-memory fallback
        }

        return users.stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst()
                .orElse(null);
    }

    public User authenticate(String email, String password) {
        User user = findByEmail(email);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    public boolean emailExists(String email) {
        return findByEmail(email) != null;
    }

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

            if (parts.length >= 8) {
                user.setCvPath(parts[6].isEmpty() ? null : parts[6]);
                user.setCreatedAt(parseDate(parts[7]));
            } else if (parts.length == 7) {
                user.setCvPath(null);
                user.setCreatedAt(parseDate(parts[6]));
            } else {
                return null;
            }

            return user;
        } catch (ParseException | IllegalArgumentException e) {
            System.err.println("Error parsing user from CSV line: " + line);
            e.printStackTrace();
            return null;
        }
    }

    private String formatUserToCSV(User user) {
        return escapeCSV(user.getUserId()) + "," +
               escapeCSV(user.getName()) + "," +
               escapeCSV(user.getEmail()) + "," +
               escapeCSV(user.getPassword()) + "," +
               escapeCSV(user.getRole().toString()) + "," +
               escapeCSV(user.getSkills() != null ? user.getSkills() : "") + "," +
               escapeCSV(user.getCvPath() != null ? user.getCvPath() : "") + "," +
               escapeCSV(formatDate(user.getCreatedAt()));
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
