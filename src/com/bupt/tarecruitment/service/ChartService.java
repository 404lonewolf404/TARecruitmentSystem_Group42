package com.bupt.tarecruitment.service;

import com.bupt.tarecruitment.dao.ApplicationDAO;
import com.bupt.tarecruitment.dao.PositionDAO;
import com.bupt.tarecruitment.dao.UserDAO;
import com.bupt.tarecruitment.model.Application;
import com.bupt.tarecruitment.model.ApplicationStatus;
import com.bupt.tarecruitment.model.Position;
import com.bupt.tarecruitment.model.User;
import com.bupt.tarecruitment.model.UserRole;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Builds chart data payloads for dashboard pages.
 */
public class ChartService {

    private UserDAO userDAO;
    private PositionDAO positionDAO;
    private ApplicationDAO applicationDAO;

    public ChartService() {
        this.userDAO = new UserDAO();
        this.positionDAO = new PositionDAO();
        this.applicationDAO = new ApplicationDAO();
    }

    /**
     * Escapes a string for direct JSON string concatenation.
     */
    private String escapeJson(String str) {
        if (str == null) {
            return "";
        }
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }

    /**
     * Returns workload chart data for all TAs.
     */
    public String getWorkloadChartData() {
        try {
            List<User> allUsers = userDAO.loadAll();
            List<User> tas = allUsers.stream()
                .filter(u -> u.getRole() == UserRole.TA)
                .collect(Collectors.toList());

            StringBuilder labels = new StringBuilder("[");
            StringBuilder data = new StringBuilder("[");
            StringBuilder colors = new StringBuilder("[");

            boolean first = true;
            for (User ta : tas) {
                List<Application> apps = applicationDAO.findByTaId(ta.getUserId());

                // 中文说明：仅统计已录用岗位，并按岗位去重后累加工时。
                int totalHours = apps.stream()
                    .filter(a -> a.getStatus() == ApplicationStatus.SELECTED)
                    .map(Application::getPositionId)
                    .distinct()
                    .mapToInt(posId -> {
                        Position pos = positionDAO.findById(posId);
                        return pos != null ? pos.getHours() : 0;
                    })
                    .sum();

                if (!first) {
                    labels.append(",");
                    data.append(",");
                    colors.append(",");
                }
                first = false;

                labels.append("\"").append(escapeJson(ta.getName())).append("\"");
                data.append(totalHours);

                // 中文说明：使用颜色区分工时负载水平。
                if (totalHours > 30) {
                    colors.append("\"#e74c3c\"");
                } else if (totalHours >= 20) {
                    colors.append("\"#f39c12\"");
                } else {
                    colors.append("\"#27ae60\"");
                }
            }

            labels.append("]");
            data.append("]");
            colors.append("]");

            return "{\"labels\":" + labels + ",\"data\":" + data + ",\"colors\":" + colors + "}";
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"labels\":[],\"data\":[],\"colors\":[]}";
        }
    }

    /**
     * Returns global application status chart data.
     */
    public String getApplicationStatusData() {
        try {
            List<Application> allApplications = applicationDAO.loadAll();

            int pending = (int) allApplications.stream()
                .filter(a -> a.getStatus() == ApplicationStatus.PENDING).count();
            int selected = (int) allApplications.stream()
                .filter(a -> a.getStatus() == ApplicationStatus.SELECTED).count();
            int rejected = (int) allApplications.stream()
                .filter(a -> a.getStatus() == ApplicationStatus.REJECTED).count();
            int withdrawn = (int) allApplications.stream()
                .filter(a -> a.getStatus() == ApplicationStatus.WITHDRAWN).count();

            return "{\"labels\":[\"Pending\",\"Selected\",\"Rejected\",\"Withdrawn\"],"
                   + "\"data\":[" + pending + "," + selected + "," + rejected + "," + withdrawn + "],"
                   + "\"colors\":[\"#f39c12\",\"#27ae60\",\"#e74c3c\",\"#95a5a6\"]}";
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"labels\":[],\"data\":[],\"colors\":[]}";
        }
    }

    /**
     * Returns application count chart data for one MO's positions.
     */
    public String getPositionApplicationsData(String moId) {
        try {
            List<Position> positions = positionDAO.findByMoId(moId);

            StringBuilder labels = new StringBuilder("[");
            StringBuilder data = new StringBuilder("[");

            boolean first = true;
            for (Position pos : positions) {
                List<Application> apps = applicationDAO.findByPositionId(pos.getPositionId());

                if (!first) {
                    labels.append(",");
                    data.append(",");
                }
                first = false;

                labels.append("\"").append(escapeJson(pos.getTitle())).append("\"");
                data.append(apps.size());
            }

            labels.append("]");
            data.append("]");

            return "{\"labels\":" + labels + ",\"data\":" + data + "}";
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"labels\":[],\"data\":[]}";
        }
    }

    /**
     * Returns application status chart data for one TA.
     */
    public String getTAApplicationStatusData(String taId) {
        try {
            List<Application> apps = applicationDAO.findByTaId(taId);

            int pending = (int) apps.stream()
                .filter(a -> a.getStatus() == ApplicationStatus.PENDING).count();
            int selected = (int) apps.stream()
                .filter(a -> a.getStatus() == ApplicationStatus.SELECTED).count();
            int rejected = (int) apps.stream()
                .filter(a -> a.getStatus() == ApplicationStatus.REJECTED).count();
            int withdrawn = (int) apps.stream()
                .filter(a -> a.getStatus() == ApplicationStatus.WITHDRAWN).count();

            return "{\"labels\":[\"Pending\",\"Selected\",\"Rejected\",\"Withdrawn\"],"
                   + "\"data\":[" + pending + "," + selected + "," + rejected + "," + withdrawn + "],"
                   + "\"colors\":[\"#f39c12\",\"#27ae60\",\"#e74c3c\",\"#95a5a6\"]}";
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"labels\":[],\"data\":[],\"colors\":[]}";
        }
    }
}
