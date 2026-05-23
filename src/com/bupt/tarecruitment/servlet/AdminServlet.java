package com.bupt.tarecruitment.servlet;

import com.bupt.tarecruitment.dao.ApplicationDAO;
import com.bupt.tarecruitment.dao.PositionDAO;
import com.bupt.tarecruitment.dao.UserDAO;
import com.bupt.tarecruitment.model.Application;
import com.bupt.tarecruitment.model.Position;
import com.bupt.tarecruitment.model.User;
import com.bupt.tarecruitment.model.UserRole;
import com.bupt.tarecruitment.service.WorkloadService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * Administrator servlet for reports and management pages.
 */
public class AdminServlet extends HttpServlet {

    private static final String ADMIN_KEY_FILE = "data/admin_register_key.txt";

    private WorkloadService workloadService;
    private UserDAO userDAO;
    private PositionDAO positionDAO;
    private ApplicationDAO applicationDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        this.workloadService = new WorkloadService();
        this.userDAO = new UserDAO();
        this.positionDAO = new PositionDAO();
        this.applicationDAO = new ApplicationDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();

        if (pathInfo == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request path");
            return;
        }

        switch (pathInfo) {
            case "/workload":
                handleWorkloadReport(request, response);
                break;
            case "/users":
                handleUsersList(request, response);
                break;
            case "/positions":
                handlePositionsList(request, response);
                break;
            case "/applications":
                handleApplicationsList(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Endpoint not found");
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String pathInfo = request.getPathInfo();

        if (pathInfo == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request path");
            return;
        }

        switch (pathInfo) {
            case "/admin-key":
                handleUpdateAdminRegisterKey(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Endpoint not found");
                break;
        }
    }

    /**
     * Renders the TA workload report page.
     */
    private void handleWorkloadReport(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // 中文说明：计算全部 TA 的工时负载统计。
            Map<User, Integer> workloads = workloadService.calculateAllWorkloads();

            // 中文说明：写入页面渲染所需数据。
            request.setAttribute("workloads", workloads);

            // 中文说明：转发到管理员工时报表页。
            request.getRequestDispatcher("/WEB-INF/jsp/admin/workload.jsp").forward(request, response);
        } catch (Exception e) {
            // 中文说明：异常时转发到统一错误页。
            request.setAttribute("errorMessage", "Operation failed: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(request, response);
        }
    }

    /**
     * Renders the administrator user list page.
     */
    private void handleUsersList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // 中文说明：加载全部用户。
            List<User> allUsers = userDAO.loadAll();

            // 中文说明：写入页面渲染所需数据。
            request.setAttribute("users", allUsers);

            // 中文说明：转发到管理员用户列表页。
            request.getRequestDispatcher("/WEB-INF/jsp/admin/users.jsp").forward(request, response);
        } catch (Exception e) {
            // 中文说明：异常时转发到统一错误页。
            request.setAttribute("errorMessage", "Operation failed: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(request, response);
        }
    }

    /**
     * Renders the administrator position list page.
     */
    private void handlePositionsList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            List<Position> allPositions = positionDAO.loadAll();
            List<User> allUsers = userDAO.loadAll();

            request.setAttribute("positions", allPositions);
            request.setAttribute("users", allUsers);
            request.getRequestDispatcher("/WEB-INF/jsp/admin/positions.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Operation failed: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(request, response);
        }
    }

    /**
     * Renders the administrator application list page.
     */
    private void handleApplicationsList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            List<Application> allApplications = applicationDAO.loadAll();
            List<User> allUsers = userDAO.loadAll();
            List<Position> allPositions = positionDAO.loadAll();

            request.setAttribute("applications", allApplications);
            request.setAttribute("users", allUsers);
            request.setAttribute("positions", allPositions);
            request.getRequestDispatcher("/WEB-INF/jsp/admin/applications.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Operation failed: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(request, response);
        }
    }

    /**
     * Updates the administrator registration key.
     */
    private void handleUpdateAdminRegisterKey(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }

        User currentUser = (User) session.getAttribute("user");
        if (currentUser.getRole() != UserRole.ADMIN) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Only admins can update admin registration key");
            return;
        }

        String newKey = request.getParameter("newAdminKey");
        String confirmNewKey = request.getParameter("confirmNewAdminKey");

        if (newKey == null || newKey.trim().isEmpty()
                || confirmNewKey == null || confirmNewKey.trim().isEmpty()) {
            session.setAttribute("adminKeyErrorMessage", "Please fill in both new key fields");
            response.sendRedirect(request.getContextPath() + "/admin/profile");
            return;
        }

        if (newKey.trim().length() < 8) {
            session.setAttribute("adminKeyErrorMessage", "New key must be at least 8 characters");
            response.sendRedirect(request.getContextPath() + "/admin/profile");
            return;
        }

        if (!newKey.trim().equals(confirmNewKey.trim())) {
            session.setAttribute("adminKeyErrorMessage", "New key and confirmation do not match");
            response.sendRedirect(request.getContextPath() + "/admin/profile");
            return;
        }

        saveAdminRegisterKey(newKey.trim());
        session.setAttribute("adminKeySuccessMessage", "Admin registration key updated successfully");
        response.sendRedirect(request.getContextPath() + "/admin/profile");
    }

    private String getWebAppRootPath() {
        String catalinaBase = System.getProperty("catalina.base");
        if (catalinaBase != null && !catalinaBase.trim().isEmpty()) {
            return catalinaBase + "/webapps/TARecruitmentSystem";
        }
        return "webapps/TARecruitmentSystem";
    }

    private String loadAdminRegisterKey() throws IOException {
        File keyFile = new File(getWebAppRootPath() + "/" + ADMIN_KEY_FILE);
        File parent = keyFile.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        if (!keyFile.exists()) {
            try (BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(keyFile), StandardCharsets.UTF_8))) {
                writer.write("CHANGE_ME_ADMIN_KEY");
                writer.newLine();
            }
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(keyFile), StandardCharsets.UTF_8))) {
            String line = reader.readLine();
            return line == null ? "" : line.trim();
        }
    }

    private void saveAdminRegisterKey(String newKey) throws IOException {
        File keyFile = new File(getWebAppRootPath() + "/" + ADMIN_KEY_FILE);
        File parent = keyFile.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(keyFile), StandardCharsets.UTF_8))) {
            writer.write(newKey);
            writer.newLine();
        }
    }
}
