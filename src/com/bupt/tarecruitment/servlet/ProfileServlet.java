package com.bupt.tarecruitment.servlet;

import com.bupt.tarecruitment.dao.UserDAO;
import com.bupt.tarecruitment.model.User;
import com.bupt.tarecruitment.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * 逻辑说明
 * 逻辑说明
 */
@MultipartConfig(
    maxFileSize = 5 * 1024 * 1024,      // 5MB
    maxRequestSize = 10 * 1024 * 1024   // 10MB
)
public class ProfileServlet extends HttpServlet {
    
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
        
        // 逻辑说明
        if (pathInfo == null || pathInfo.equals("/")) {
            handleViewProfile(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Requested resource not found");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 逻辑说明
        request.setCharacterEncoding("UTF-8");
        
        String pathInfo = request.getPathInfo();
        
        // 逻辑说明
        if (pathInfo == null || pathInfo.equals("/") || pathInfo.equals("/update")) {
            handleUpdateProfile(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Requested resource not found");
        }
    }
    
    /**
     * 逻辑说明
     */
    private void handleViewProfile(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 逻辑说明
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
        
        // 逻辑说明
        User user = userDAO.findById(currentUser.getUserId());
        if (user == null) {
            request.setAttribute("errorMessage", "User not found");
            request.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(request, response);
            return;
        }
        
        // 逻辑说明
        request.setAttribute("user", user);
        
        // 逻辑说明
        String profilePage = getProfilePage(user);
        request.getRequestDispatcher(profilePage).forward(request, response);
    }
    
    /**
     * 逻辑说明
     */
    private void handleUpdateProfile(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            // 逻辑说明
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
            
            // 逻辑说明
            String name = request.getParameter("name");
            String email = request.getParameter("email");
            String skills = request.getParameter("skills");
            
            // 逻辑说明
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
            
            // 逻辑说明
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
            
            // 逻辑说明
            User existingUser = userDAO.findByEmail(email.trim());
            if (existingUser != null && !existingUser.getUserId().equals(currentUser.getUserId())) {
                request.setAttribute("errorMessage", "This email is already in use by another user");
                request.setAttribute("user", currentUser);
                String profilePage = getProfilePage(currentUser);
                request.getRequestDispatcher(profilePage).forward(request, response);
                return;
            }
            
            // 逻辑说明
            User user = userDAO.findById(currentUser.getUserId());
            if (user == null) {
                request.setAttribute("errorMessage", "User not found");
                request.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(request, response);
                return;
            }
            
            // 逻辑说明
            user.setName(ValidationUtil.escapeHtml(name.trim()));
            user.setEmail(email.trim()); // 邮箱格式已在前面校验，这里直接保存
            
            // 逻辑说明
            if (skills != null) {
                user.setSkills(ValidationUtil.escapeHtml(skills.trim()));
            }
            
            // 逻辑说明
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
            
            // 逻辑说明
            userDAO.update(user);
            
            // 逻辑说明
            session.setAttribute("user", user);
            
            // 逻辑说明
            request.setAttribute("successMessage", "Profile updated successfully");
            request.setAttribute("user", user);
            
            // 逻辑说明
            String profilePage = getProfilePage(user);
            request.getRequestDispatcher(profilePage).forward(request, response);
            
        } catch (IOException e) {
            // 逻辑说明            request.setAttribute("errorMessage", "Update failed: " + e.getMessage());
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
     * 逻辑说明
     * 逻辑说明
     * @param user 参数
     * @return 返回结果
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
     * 逻辑说明
     * 逻辑说明
     * @param cvPart 参数
     * @param userId 参数
     * @return 返回结果
     */
    private String handleCVUpload(Part cvPart, String userId) {
        try {
            // 逻辑说明
            String fileName = getFileName(cvPart);
            if (fileName == null || fileName.isEmpty()) {
                return null;
            }
            
            // 逻辑说明
            String fileExtension = getFileExtension(fileName);
            if (!isValidCVFile(fileExtension)) {
                return null;
            }
            
            // 保存简历文件到 data/cv 目录
            String cvDirectory = getWebAppRootPath() + "/data/cv";
            File dir = new File(cvDirectory);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            
            // 逻辑说明
            String newFileName = userId + "_" + System.currentTimeMillis() + "." + fileExtension;
            String cvPath = cvDirectory + "/" + newFileName;
            
            // 逻辑说明
            Path filePath = Paths.get(cvPath);
            Files.copy(cvPart.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            // 逻辑说明
            return "data/cv/" + newFileName;
            
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 逻辑说明
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
     * 逻辑说明
     */
    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(lastDotIndex + 1).toLowerCase();
        }
        return "";
    }
    
    /**
     * 逻辑说明
     */
    private boolean isValidCVFile(String extension) {
        return extension.equals("pdf") || 
               extension.equals("doc") || 
               extension.equals("docx");
    }
}

