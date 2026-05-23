package com.bupt.tarecruitment.servlet;

import com.bupt.tarecruitment.dao.UserDAO;
import com.bupt.tarecruitment.model.User;
import com.bupt.tarecruitment.model.UserRole;
import com.bupt.tarecruitment.service.AuthService;
import com.bupt.tarecruitment.util.ValidationUtil;
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
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Handles login, registration, logout, and password reset flows.
 */
public class AuthServlet extends HttpServlet {

    private static final String ADMIN_KEY_FILE = "data/admin_register_key.txt";
    private static final String PASSWORD_RESET_FILE = "data/password_resets.csv";

    private AuthService authService;
    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        this.authService = new AuthService();
        this.userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();

        if (pathInfo == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Operation failed");
            return;
        }

        switch (pathInfo) {
            case "/login":
                // 中文说明：展示登录页。
                request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response);
                break;
            case "/register":
                // 中文说明：展示注册页。
                request.getRequestDispatcher("/WEB-INF/jsp/register.jsp").forward(request, response);
                break;
            case "/forgot-password":
                request.getRequestDispatcher("/WEB-INF/jsp/forgot-password.jsp").forward(request, response);
                break;
            case "/logout":
                handleLogout(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Operation failed");
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 中文说明：统一使用 UTF-8 处理表单提交。
        request.setCharacterEncoding("UTF-8");

        String pathInfo = request.getPathInfo();

        if (pathInfo == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Operation failed");
            return;
        }

        switch (pathInfo) {
            case "/register":
                handleRegister(request, response);
                break;
            case "/login":
                handleLogin(request, response);
                break;
            case "/forgot-password":
                handleSendResetCode(request, response);
                break;
            case "/send-reset-code":
                handleSendResetCodeApi(request, response);
                break;
            case "/reset-password":
                handleResetPassword(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Operation failed");
                break;
        }
    }

    /**
     * Handles new account registration.
     */
    private void handleRegister(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // 中文说明：读取注册表单字段。
            String name = request.getParameter("name");
            String email = request.getParameter("email");
            String password = request.getParameter("password");
            String roleStr = request.getParameter("role");
            String skills = request.getParameter("skills");

            // 中文说明：校验基础必填参数。
            if (name == null || email == null || password == null || roleStr == null) {
                request.setAttribute("errorMessage", "Operation failed");
                request.getRequestDispatcher("/WEB-INF/jsp/register.jsp").forward(request, response);
                return;
            }

            // 中文说明：执行输入格式校验。
            if (!ValidationUtil.isValidLength(name, 2, 50)) {
                request.setAttribute("errorMessage", "Operation failed");
                request.getRequestDispatcher("/WEB-INF/jsp/register.jsp").forward(request, response);
                return;
            }

            if (!ValidationUtil.isValidEmail(email)) {
                request.setAttribute("errorMessage", "Operation failed");
                request.getRequestDispatcher("/WEB-INF/jsp/register.jsp").forward(request, response);
                return;
            }

            if (!ValidationUtil.isStrongPassword(password)) {
                request.setAttribute("errorMessage", "Operation failed");
                request.getRequestDispatcher("/WEB-INF/jsp/register.jsp").forward(request, response);
                return;
            }

            // 中文说明：对可显示字段做 HTML 转义。
            name = ValidationUtil.escapeHtml(name);
            if (skills != null) {
                skills = ValidationUtil.escapeHtml(skills);
            }

            // 中文说明：解析用户角色。
            UserRole role;
            try {
                role = UserRole.valueOf(roleStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                request.setAttribute("errorMessage", "Operation failed");
                request.getRequestDispatcher("/WEB-INF/jsp/register.jsp").forward(request, response);
                return;
            }

            // 中文说明：管理员注册必须校验注册码。
            if (role == UserRole.ADMIN) {
                String adminRegisterKey = request.getParameter("adminRegisterKey");
                if (adminRegisterKey == null || adminRegisterKey.trim().isEmpty()) {
                    request.setAttribute("errorMessage", "Operation failed");
                    request.getRequestDispatcher("/WEB-INF/jsp/register.jsp").forward(request, response);
                    return;
                }

                String expectedKey = loadAdminRegisterKey();
                if (expectedKey.isEmpty() || !expectedKey.equals(adminRegisterKey.trim())) {
                    request.setAttribute("errorMessage", "Operation failed");
                    request.getRequestDispatcher("/WEB-INF/jsp/register.jsp").forward(request, response);
                    return;
                }
            }

            // 中文说明：调用认证服务完成注册。
            authService.register(name.trim(), email.trim(), password, role, skills);

            // 中文说明：注册成功后返回登录页。
            request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response);
        } catch (IllegalArgumentException e) {
            // 中文说明：业务校验失败时回显错误信息。
            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/register.jsp").forward(request, response);
        } catch (IOException e) {
            // 中文说明：持久化失败时展示错误页信息。
            request.setAttribute("errorMessage", "Operation failed: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/register.jsp").forward(request, response);
        }
    }

    /**
     * Handles user login.
     */
    private void handleLogin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // 中文说明：读取登录凭据。
            String email = request.getParameter("email");
            String password = request.getParameter("password");

            // 中文说明：校验基础参数。
            if (email == null || password == null) {
                request.setAttribute("errorMessage", "Operation failed");
                request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response);
                return;
            }

            // 中文说明：先校验邮箱格式，减少无效查询。
            if (!ValidationUtil.isValidEmail(email)) {
                request.setAttribute("errorMessage", "Operation failed");
                request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response);
                return;
            }

            // 中文说明：认证成功后写入会话。
            User user = authService.login(email.trim(), password);
            HttpSession session = request.getSession(true);
            session.setAttribute("user", user);

            // 中文说明：按角色跳转到对应首页。
            String redirectUrl = getDashboardUrl(user.getRole());
            response.sendRedirect(request.getContextPath() + redirectUrl);
        } catch (IllegalArgumentException e) {
            // 中文说明：登录失败时回显错误信息。
            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response);
        }
    }

    /**
     * Handles logout.
     */
    private void handleLogout(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 中文说明：读取当前会话。
        HttpSession session = request.getSession(false);

        // 中文说明：注销时清理当前会话。
        authService.logout(session);

        // 中文说明：注销后返回登录页。
        response.sendRedirect(request.getContextPath() + "/auth/login");
    }

    /**
     * Returns the dashboard path for the given role.
     *
     * @param role role value
     * @return operation result
     */
    private String getDashboardUrl(UserRole role) {
        switch (role) {
            case TA:
                return "/ta/dashboard";
            case MO:
                return "/mo/dashboard";
            case ADMIN:
                return "/admin/dashboard";
            default:
                return "/auth/login";
        }
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

        // 中文说明：第一次读取时自动生成默认管理员注册码文件。
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

    private void handleSendResetCode(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        if (email == null || email.trim().isEmpty() || !ValidationUtil.isValidEmail(email)) {
            request.setAttribute("errorMessage", "Please enter a valid email address.");
            request.getRequestDispatcher("/WEB-INF/jsp/forgot-password.jsp").forward(request, response);
            return;
        }

        String normalizedEmail = email.trim();
        if (userDAO.findByEmail(normalizedEmail) == null) {
            request.setAttribute("errorMessage", "No account is associated with this email.");
            request.getRequestDispatcher("/WEB-INF/jsp/forgot-password.jsp").forward(request, response);
            return;
        }
        long now = System.currentTimeMillis();
        long expireAt = now + 60_000L;

        List<String[]> records = loadResetRecords();
        long latestNotExpiredForEmail = 0L;
        for (String[] r : records) {
            if (r.length < 5) {
                continue;
            }
            if (normalizedEmail.equalsIgnoreCase(r[0]) && "false".equalsIgnoreCase(r[3])) {
                try {
                    long sentAt = Long.parseLong(r[4]);
                    long oldExpireAt = Long.parseLong(r[2]);
                    if (oldExpireAt > now && sentAt > latestNotExpiredForEmail) {
                        latestNotExpiredForEmail = sentAt;
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        }

        if (latestNotExpiredForEmail > 0 && now - latestNotExpiredForEmail < 60_000L) {
            long remain = 60 - ((now - latestNotExpiredForEmail) / 1000);
            if (remain < 1) {
                remain = 1;
            }
            request.setAttribute("errorMessage", "Please wait " + remain + " seconds before requesting a new code.");
            request.setAttribute("email", normalizedEmail);
            request.setAttribute("showResetForm", true);
            request.getRequestDispatcher("/WEB-INF/jsp/forgot-password.jsp").forward(request, response);
            return;
        }

        // 中文说明：同邮箱之前未使用的验证码全部作废。
        for (String[] r : records) {
            if (r.length >= 5 && normalizedEmail.equalsIgnoreCase(r[0])) {
                r[3] = "true";
            }
        }

        String code = String.format("%06d", new Random().nextInt(1_000_000));
        System.out.println("[RESET CODE] email=" + normalizedEmail + " code=" + code);
        records.add(new String[]{normalizedEmail, code, String.valueOf(expireAt), "false", String.valueOf(now)});
        saveResetRecords(records);

        request.setAttribute("successMessage", "Verification code generated. Demo mode: check server console.");
        request.setAttribute("email", normalizedEmail);
        request.setAttribute("showResetForm", true);
        request.setAttribute("expiresInSeconds", 60);
        request.getRequestDispatcher("/WEB-INF/jsp/forgot-password.jsp").forward(request, response);
    }

    private void handleSendResetCodeApi(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");

        String email = request.getParameter("email");
        if (email == null || email.trim().isEmpty() || !ValidationUtil.isValidEmail(email)) {
            writeJson(response, false, "Please enter a valid email address.", 0);
            return;
        }

        String normalizedEmail = email.trim();
        if (userDAO.findByEmail(normalizedEmail) == null) {
            writeJson(response, false, "No account is associated with this email.", 0);
            return;
        }

        long now = System.currentTimeMillis();
        long expireAt = now + 60_000L;
        List<String[]> records = loadResetRecords();
        long latestNotExpiredForEmail = 0L;
        for (String[] r : records) {
            if (r.length < 5) {
                continue;
            }
            if (normalizedEmail.equalsIgnoreCase(r[0]) && "false".equalsIgnoreCase(r[3])) {
                try {
                    long sentAt = Long.parseLong(r[4]);
                    long oldExpireAt = Long.parseLong(r[2]);
                    if (oldExpireAt > now && sentAt > latestNotExpiredForEmail) {
                        latestNotExpiredForEmail = sentAt;
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        }

        if (latestNotExpiredForEmail > 0 && now - latestNotExpiredForEmail < 60_000L) {
            long remain = 60 - ((now - latestNotExpiredForEmail) / 1000);
            if (remain < 1) {
                remain = 1;
            }
            writeJson(response, false, "Please wait " + remain + " seconds before requesting a new code.", (int) remain);
            return;
        }

        for (String[] r : records) {
            if (r.length >= 5 && normalizedEmail.equalsIgnoreCase(r[0])) {
                r[3] = "true";
            }
        }

        String code = String.format("%06d", new Random().nextInt(1_000_000));
        System.out.println("[RESET CODE] email=" + normalizedEmail + " code=" + code);
        records.add(new String[]{normalizedEmail, code, String.valueOf(expireAt), "false", String.valueOf(now)});
        saveResetRecords(records);

        writeJson(response, true, "Verification code generated. Demo mode: check server console.", 60);
    }

    private void handleResetPassword(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String code = request.getParameter("code");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        if (email == null || code == null || newPassword == null || confirmPassword == null
                || email.trim().isEmpty() || code.trim().isEmpty()
                || newPassword.trim().isEmpty() || confirmPassword.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Please fill in all fields.");
            request.setAttribute("email", email);
            request.setAttribute("showResetForm", true);
            request.getRequestDispatcher("/WEB-INF/jsp/forgot-password.jsp").forward(request, response);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            request.setAttribute("errorMessage", "New password and confirmation do not match.");
            request.setAttribute("email", email.trim());
            request.setAttribute("showResetForm", true);
            request.getRequestDispatcher("/WEB-INF/jsp/forgot-password.jsp").forward(request, response);
            return;
        }

        if (!ValidationUtil.isStrongPassword(newPassword)) {
            request.setAttribute("errorMessage", "Password must be at least 8 characters and contain letters and numbers.");
            request.setAttribute("email", email.trim());
            request.setAttribute("showResetForm", true);
            request.getRequestDispatcher("/WEB-INF/jsp/forgot-password.jsp").forward(request, response);
            return;
        }

        String normalizedEmail = email.trim();
        long now = System.currentTimeMillis();
        List<String[]> records = loadResetRecords();
        String[] matched = null;
        for (int i = records.size() - 1; i >= 0; i--) {
            String[] r = records.get(i);
            if (r.length < 5) {
                continue;
            }
            if (!normalizedEmail.equalsIgnoreCase(r[0])) {
                continue;
            }
            if (!code.trim().equals(r[1])) {
                continue;
            }
            if ("true".equalsIgnoreCase(r[3])) {
                continue;
            }
            matched = r;
            break;
        }

        if (matched == null) {
            request.setAttribute("errorMessage", "Invalid verification code.");
            request.setAttribute("email", normalizedEmail);
            request.setAttribute("showResetForm", true);
            request.getRequestDispatcher("/WEB-INF/jsp/forgot-password.jsp").forward(request, response);
            return;
        }

        long expireAt;
        try {
            expireAt = Long.parseLong(matched[2]);
        } catch (NumberFormatException e) {
            expireAt = 0L;
        }
        if (expireAt <= now) {
            matched[3] = "true";
            saveResetRecords(records);
            request.setAttribute("errorMessage", "Verification code has expired. Please request a new one.");
            request.setAttribute("email", normalizedEmail);
            request.setAttribute("showResetForm", true);
            request.getRequestDispatcher("/WEB-INF/jsp/forgot-password.jsp").forward(request, response);
            return;
        }

        try {
            authService.resetPasswordByEmail(normalizedEmail, newPassword);
            matched[3] = "true";
            saveResetRecords(records);
            request.setAttribute("successMessage", "Password reset successful. Please log in.");
            request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response);
        } catch (IllegalArgumentException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("email", normalizedEmail);
            request.setAttribute("showResetForm", true);
            request.getRequestDispatcher("/WEB-INF/jsp/forgot-password.jsp").forward(request, response);
        }
    }

    private List<String[]> loadResetRecords() throws IOException {
        File file = new File(getWebAppRootPath() + "/" + PASSWORD_RESET_FILE);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        List<String[]> records = new ArrayList<>();
        if (!file.exists()) {
            try (BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
                writer.write("email,code,expireAt,used,sentAt");
                writer.newLine();
            }
            return records;
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] parts = line.split(",", -1);
                if (parts.length >= 5) {
                    records.add(parts);
                }
            }
        }
        return records;
    }

    private void saveResetRecords(List<String[]> records) throws IOException {
        File file = new File(getWebAppRootPath() + "/" + PASSWORD_RESET_FILE);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            writer.write("email,code,expireAt,used,sentAt");
            writer.newLine();
            for (String[] r : records) {
                writer.write(r[0] + "," + r[1] + "," + r[2] + "," + r[3] + "," + r[4]);
                writer.newLine();
            }
        }
    }

    private void writeJson(HttpServletResponse response, boolean ok, String message, int expiresInSeconds)
            throws IOException {
        String safeMessage = message == null ? "" : message.replace("\\", "\\\\").replace("\"", "\\\"");
        try (PrintWriter out = response.getWriter()) {
            out.write("{\"ok\":" + ok + ",\"message\":\"" + safeMessage + "\",\"expiresInSeconds\":" + expiresInSeconds + "}");
        }
    }
}
