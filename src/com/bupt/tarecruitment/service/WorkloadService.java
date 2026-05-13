package com.bupt.tarecruitment.service;

import com.bupt.tarecruitment.dao.ApplicationDAO;
import com.bupt.tarecruitment.dao.PositionDAO;
import com.bupt.tarecruitment.dao.UserDAO;
import com.bupt.tarecruitment.model.Application;
import com.bupt.tarecruitment.model.ApplicationStatus;
import com.bupt.tarecruitment.model.Position;
import com.bupt.tarecruitment.model.User;
import com.bupt.tarecruitment.model.UserRole;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class WorkloadService {

    private ApplicationDAO applicationDAO;
    private PositionDAO positionDAO;
    private UserDAO userDAO;

    public WorkloadService() {
        this.applicationDAO = new ApplicationDAO();
        this.positionDAO = new PositionDAO();
        this.userDAO = new UserDAO();
    }

    public Map<User, Integer> calculateAllWorkloads() {
        Map<User, Integer> result = new LinkedHashMap<>();

        try {
            List<User> allUsers = userDAO.loadAll();
            Map<String, User> taById = new LinkedHashMap<>();
            Map<String, Integer> hoursByTaId = new HashMap<>();

            for (User user : allUsers) {
                if (user.getRole() == UserRole.TA) {
                    taById.put(user.getUserId(), user);
                    hoursByTaId.put(user.getUserId(), 0);
                }
            }

            List<Application> allApplications = applicationDAO.loadAll();
            for (Application application : allApplications) {
                if (application.getStatus() != ApplicationStatus.SELECTED) {
                    continue;
                }

                String taId = application.getTaId();
                if (!hoursByTaId.containsKey(taId)) {
                    continue;
                }

                Position position = positionDAO.findById(application.getPositionId());
                if (position == null) {
                    continue;
                }

                int current = hoursByTaId.get(taId);
                hoursByTaId.put(taId, current + position.getHours());
            }

            for (Map.Entry<String, User> entry : taById.entrySet()) {
                result.put(entry.getValue(), hoursByTaId.getOrDefault(entry.getKey(), 0));
            }
        } catch (Exception e) {
            return new LinkedHashMap<>();
        }

        return result;
    }
}
