package com.bupt.tarecruitment.servlet;

import com.bupt.tarecruitment.dao.UserDAO;
import com.bupt.tarecruitment.model.User;
import com.bupt.tarecruitment.model.UserRole;
import com.bupt.tarecruitment.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Displays and updates the current user's profile.
 */
@MultipartConfig(
    maxFileSize = 5 * 1024 * 1024,      // 中文说明：单个简历文件最大 5MB。
    maxRequestSize = 10 * 1024 * 1024   // 中文说明：整个请求最大 10MB。
)
public class ProfileServlet extends HttpServlet {

    private static final String ADMIN_KEY_FILE = "data/admin_register_key.txt";
    private UserDAO userDAO;

    private String getWebAppRootPath() {
        String catalinaBase = System.getProperty("catalina.base");
        if (catalinaBase != null && !catalinaBase.trim().isEmpty()) {
            return catalinaBase + "/webapps/TARecruitmentSystem";
        }
        return "webapps/TARecruitmentSystem";
    }

    @Override
    public void init() throws ServletException {
        super.init();
        this.userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();

        // 中文说明：根路径默认展示个人资料页。
        if (pathInfo == null || pathInfo.equals("/")) {
            handleViewProfile(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Requested resource not found");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 中文说明：统一使用 UTF-8 处理表单提交。
        request.setCharacterEncoding("UTF-8");

        String pathInfo = request.getPathInfo();

        // 中文说明：根路径和 `/update` 都视为更新资料请求。
        if (pathInfo == null || pathInfo.equals("/") || pathInfo.equals("/update")) {
            handleUpdateProfile(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Requested resource not found");
        }
    }

    /**
     * Renders the current user's profile page.
     */
    private void handleViewProfile(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 中文说明：只有已登录用户才能查看个人资料页。
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }

        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }

        // 中文说明：重新加载最新用户资料，避免会话数据过期。
        User user = userDAO.findById(currentUser.getUserId());
        if (user == null) {
            request.setAttribute("errorMessage", "User not found");
            request.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(request, response);
            return;
        }

        // 中文说明：写入页面渲染所需用户对象。
        request.setAttribute("user", user);

        // 中文说明：仅管理员可以看到管理员注册码预览。
        if (user.getRole() == UserRole.ADMIN) {
            request.setAttribute("adminRegisterKeyPreview", loadAdminRegisterKeyPreview());
        }

        // 中文说明：按角色转发到对应资料页。
        String profilePage = getProfilePage(user);
        request.getRequestDispatcher(profilePage).forward(request, response);
    }

    private String loadAdminRegisterKeyPreview() {
        File keyFile = new File(getWebAppRootPath() + "/" + ADMIN_KEY_FILE);
        if (!keyFile.exists()) {
            return "";
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(keyFile), StandardCharsets.UTF_8))) {
            String line = reader.readLine();
            return line == null ? "" : line.trim();
        } catch (IOException e) {
            return "";
        }
    }

    /**
     * Updates the current user's profile information.
     */
    private void handleUpdateProfile(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // 中文说明：只有已登录用户才能提交资料修改。
            HttpSession session = request.getSession(false);
            if (session == null) {
                response.sendRedirect(request.getContextPath() + "/auth/login");
                return;
            }

            User currentUser = (User) session.getAttribute("user");
            if (currentUser == null) {
                response.sendRedirect(request.getContextPath() + "/auth/login");
                return;
            }

            // 中文说明：读取表单字段。
            String name = request.getParameter("name");
            String email = request.getParameter("email");
            String skills = request.getParameter("skills");

            // 中文说明：校验必填字段。
            if (name == null || name.trim().isEmpty()) {
                request.setAttribute("errorMessage", "Name cannot be empty");
                request.setAttribute("user", currentUser);
                String profilePage = getProfilePage(currentUser);
                request.getRequestDispatcher(profilePage).forward(request, response);
                return;
            }

            if (email == null || email.trim().isEmpty()) {
                request.setAttribute("errorMessage", "Email cannot be empty");
                request.setAttribute("user", currentUser);
                String profilePage = getProfilePage(currentUser);
                request.getRequestDispatcher(profilePage).forward(request, response);
                return;
            }

            // 中文说明：执行格式校验。
            if (!ValidationUtil.isValidLength(name.trim(), 2, 50)) {
                request.setAttribute("errorMessage", "Name length must be between 2-50 characters");
                request.setAttribute("user", currentUser);
                String profilePage = getProfilePage(currentUser);
                request.getRequestDispatcher(profilePage).forward(request, response);
                return;
            }

            if (!ValidationUtil.isValidEmail(email.trim())) {
                request.setAttribute("errorMessage", "Please enter a valid email address");
                request.setAttribute("user", currentUser);
                String profilePage = getProfilePage(currentUser);
                request.getRequestDispatcher(profilePage).forward(request, response);
                return;
            }

            // 中文说明：同一邮箱不能被其他用户占用。
            User existingUser = userDAO.findByEmail(email.trim());
            if (existingUser != null && !existingUser.getUserId().equals(currentUser.getUserId())) {
                request.setAttribute("errorMessage", "This email is already in use by another user");
                request.setAttribute("user", currentUser);
                String profilePage = getProfilePage(currentUser);
                request.getRequestDispatcher(profilePage).forward(request, response);
                return;
            }

            // 中文说明：读取最新用户实体用于更新。
            User user = userDAO.findById(currentUser.getUserId());
            if (user == null) {
                request.setAttribute("errorMessage", "User not found");
                request.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(request, response);
                return;
            }

            // 中文说明：写回经过清理后的用户信息。
            user.setName(ValidationUtil.escapeHtml(name.trim()));
            user.setEmail(email.trim()); // 中文说明：邮箱保留原始格式，不做 HTML 转义。

            // 中文说明：技能字段允许为空，非空时做 HTML 转义。
            if (skills != null) {
                user.setSkills(ValidationUtil.escapeHtml(skills.trim()));
            }

            // 中文说明：TA 可额外上传或替换简历文件。
            if (user.getRole().toString().equals("TA")) {
                Part cvPart = request.getPart("cv");
                if (cvPart != null && cvPart.getSize() > 0) {
                    String cvPath = handleCVUpload(cvPart, user.getUserId());
                    if (cvPath != null) {
                        user.setCvPath(cvPath);
                    } else {
                        request.setAttribute("errorMessage", "CV upload failed, please check file format (PDF, DOC, DOCX supported)");
                        request.setAttribute("user", currentUser);
                        String profilePage = getProfilePage(currentUser);
                        request.getRequestDispatcher(profilePage).forward(request, response);
                        return;
                    }
                }
            }

            // 中文说明：持久化资料更新。
            userDAO.update(user);

            // 中文说明：同步刷新会话中的用户对象。
            session.setAttribute("user", user);

            // 中文说明：返回成功提示并重新渲染页面。
            request.setAttribute("successMessage", "Profile updated successfully");
            request.setAttribute("user", user);

            // 中文说明：按角色返回对应资料页。
            String profilePage = getProfilePage(user);
            request.getRequestDispatcher(profilePage).forward(request, response);
        } catch (IOException e) {
            // 中文说明：IO 异常时回到当前用户资料页。
            HttpSession session = request.getSession(false);
            User currentUser = session != null ? (User) session.getAttribute("user") : null;
            if (currentUser == null) {
                response.sendRedirect(request.getContextPath() + "/auth/login");
                return;
            }
            request.setAttribute("user", currentUser);
            String profilePage = getProfilePage(currentUser);
            request.getRequestDispatcher(profilePage).forward(request, response);
        }
    }

    /**
     * Returns the profile JSP path for the user's role.
     *
     * @param user user value
     * @return operation result
     */
    private String getProfilePage(User user) {
        switch (user.getRole()) {
            case TA:
                return "/WEB-INF/jsp/ta/profile.jsp";
            case MO:
                return "/WEB-INF/jsp/mo/profile.jsp";
            case ADMIN:
                return "/WEB-INF/jsp/admin/profile.jsp";
            default:
                return "/WEB-INF/jsp/error.jsp";
        }
    }

    /**
     * Stores an uploaded CV file and returns its relative path.
     *
     * @param cvPart cvPart value
     * @param userId userId value
     * @return operation result
     */
    private String handleCVUpload(Part cvPart, String userId) {
        try {
            // 中文说明：提取上传文件名。
            String fileName = getFileName(cvPart);
            if (fileName == null || fileName.isEmpty()) {
                return null;
            }

            // 中文说明：校验文件扩展名。
            String fileExtension = getFileExtension(fileName);
            if (!isValidCVFile(fileExtension)) {
                return null;
            }

            // 中文说明：确保简历存储目录存在。
            String cvDirectory = getWebAppRootPath() + "/data/cv";
            File dir = new File(cvDirectory);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 中文说明：生成唯一文件名，避免覆盖旧文件。
            String newFileName = userId + "_" + System.currentTimeMillis() + "." + fileExtension;
            String cvPath = cvDirectory + "/" + newFileName;

            // 中文说明：保存上传文件到目标目录。
            Path filePath = Paths.get(cvPath);
            Files.copy(cvPart.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // 中文说明：返回写入用户资料的相对路径。
            return "data/cv/" + newFileName;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Extracts the original file name from a multipart part.
     */
    private String getFileName(Part part) {
        String contentDisposition = part.getHeader("content-disposition");
        if (contentDisposition == null) {
            return null;
        }

        for (String token : contentDisposition.split(";")) {
            if (token.trim().startsWith("filename")) {
                return token.substring(token.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }

    /**
     * Returns the lower-case file extension of the uploaded file.
     */
    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(lastDotIndex + 1).toLowerCase();
        }
        return "";
    }

    /**
     * Returns whether the uploaded CV extension is allowed.
     */
    private boolean isValidCVFile(String extension) {
        return extension.equals("pdf")
               || extension.equals("doc")
               || extension.equals("docx");
    }
}
