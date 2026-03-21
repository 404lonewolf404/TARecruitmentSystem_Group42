package com.bupt.tarecruitment.servlet;

import com.bupt.tarecruitment.dao.UserDAO;
import com.bupt.tarecruitment.model.User;
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
 * 个人资料Servlet
 * 处理用户个人资料的查看和更新请求
 */
@MultipartConfig(
    maxFileSize = 5 * 1024 * 1024,      // 5MB
    maxRequestSize = 10 * 1024 * 1024   // 10MB
)
public class ProfileServlet extends HttpServlet {
    
    private UserDAO userDAO;
    
    @Override
    public void init() throws ServletException {
        super.init();
        this.userDAO = new UserDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        
        // 如果路径�?/profile �?/profile/，显示个人资料页�?
        if (pathInfo == null || pathInfo.equals("/")) {
            handleViewProfile(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "请求的资源不存在");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 设置请求编码为UTF-8
        request.setCharacterEncoding("UTF-8");
        
        String pathInfo = request.getPathInfo();
        
        // 如果pathInfo为null或为"/"，也处理更新请求（兼容不同的URL格式�?
        if (pathInfo == null || pathInfo.equals("/") || pathInfo.equals("/update")) {
            handleUpdateProfile(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "请求的资源不存在");
        }
    }
    
    /**
     * 处理查看个人资料请求
     */
    private void handleViewProfile(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 获取当前登录用户
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        
        // 从数据库重新加载用户信息（确保是最新的�?
        User user = userDAO.findById(currentUser.getUserId());
        if (user == null) {
            request.setAttribute("errorMessage", "用户不存�?);
            request.getRequestDispatcher("/error.jsp").forward(request, response);
            return;
        }
        
        // 将用户信息设置到request中，供JSP页面使用
        request.setAttribute("user", user);
        
        // 根据用户角色转发到相应的个人资料页面
        String profilePage = getProfilePage(user);
        request.getRequestDispatcher(profilePage).forward(request, response);
    }
    
    /**
     * 处理更新个人资料请求
     */
    private void handleUpdateProfile(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            // 获取当前登录用户
            HttpSession session = request.getSession(false);
            if (session == null) {
                response.sendRedirect(request.getContextPath() + "/login.jsp");
                return;
            }
            
            User currentUser = (User) session.getAttribute("user");
            if (currentUser == null) {
                response.sendRedirect(request.getContextPath() + "/login.jsp");
                return;
            }
            
            // 获取表单参数
            String name = request.getParameter("name");
            String email = request.getParameter("email");
            String skills = request.getParameter("skills");
            
            // 验证必填字段不为�?
            if (name == null || name.trim().isEmpty()) {
                request.setAttribute("errorMessage", "姓名不能为空");
                request.setAttribute("user", currentUser);
                String profilePage = getProfilePage(currentUser);
                request.getRequestDispatcher(profilePage).forward(request, response);
                return;
            }
            
            if (email == null || email.trim().isEmpty()) {
                request.setAttribute("errorMessage", "邮箱不能为空");
                request.setAttribute("user", currentUser);
                String profilePage = getProfilePage(currentUser);
                request.getRequestDispatcher(profilePage).forward(request, response);
                return;
            }
            
            // 检查邮箱是否被其他用户使用
            User existingUser = userDAO.findByEmail(email.trim());
            if (existingUser != null && !existingUser.getUserId().equals(currentUser.getUserId())) {
                request.setAttribute("errorMessage", "该邮箱已被其他用户使�?);
                request.setAttribute("user", currentUser);
                String profilePage = getProfilePage(currentUser);
                request.getRequestDispatcher(profilePage).forward(request, response);
                return;
            }
            
            // 从数据库加载完整的用户信�?
            User user = userDAO.findById(currentUser.getUserId());
            if (user == null) {
                request.setAttribute("errorMessage", "用户不存�?);
                request.getRequestDispatcher("/error.jsp").forward(request, response);
                return;
            }
            
            // 更新用户信息
            user.setName(name.trim());
            user.setEmail(email.trim());
            
            // 只有TA角色才更新技能字�?
            if (skills != null) {
                user.setSkills(skills.trim());
            }
            
            // 处理CV文件上传（仅TA角色�?
            if (user.getRole().toString().equals("TA")) {
                Part cvPart = request.getPart("cv");
                if (cvPart != null && cvPart.getSize() > 0) {
                    String cvPath = handleCVUpload(cvPart, user.getUserId());
                    if (cvPath != null) {
                        user.setCvPath(cvPath);
                    } else {
                        request.setAttribute("errorMessage", "CV上传失败，请检查文件格式（支持PDF、DOC、DOCX�?);
                        request.setAttribute("user", currentUser);
                        String profilePage = getProfilePage(currentUser);
                        request.getRequestDispatcher(profilePage).forward(request, response);
                        return;
                    }
                }
            }
            
            // 保存更新到数据库
            userDAO.update(user);
            
            // 更新会话中的用户信息
            session.setAttribute("user", user);
            
            // 设置成功消息
            request.setAttribute("successMessage", "个人资料更新成功");
            request.setAttribute("user", user);
            
            // 转发回个人资料页�?
            String profilePage = getProfilePage(user);
            request.getRequestDispatcher(profilePage).forward(request, response);
            
        } catch (IOException e) {
            // 数据访问错误
            request.setAttribute("errorMessage", "更新失败�? + e.getMessage());
            HttpSession session = request.getSession(false);
            User currentUser = (User) session.getAttribute("user");
            request.setAttribute("user", currentUser);
            String profilePage = getProfilePage(currentUser);
            request.getRequestDispatcher(profilePage).forward(request, response);
        }
    }
    
    /**
     * 根据用户角色获取对应的个人资料页�?
     * 
     * @param user 用户对象
     * @return 个人资料页面路径
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
     * 处理CV文件上传
     * 
     * @param cvPart 上传的文件Part
     * @param userId 用户ID
     * @return CV文件路径，失败返回null
     */
    private String handleCVUpload(Part cvPart, String userId) {
        try {
            // 获取文件�?
            String fileName = getFileName(cvPart);
            if (fileName == null || fileName.isEmpty()) {
                return null;
            }
            
            // 验证文件类型
            String fileExtension = getFileExtension(fileName);
            if (!isValidCVFile(fileExtension)) {
                return null;
            }
            
            // 创建CV存储目录
            String cvDirectory = "webapps/TARecruitmentSystem/data/cv";
            File dir = new File(cvDirectory);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            
            // 生成新文件名：userId_timestamp.extension
            String newFileName = userId + "_" + System.currentTimeMillis() + "." + fileExtension;
            String cvPath = cvDirectory + "/" + newFileName;
            
            // 保存文件
            Path filePath = Paths.get(cvPath);
            Files.copy(cvPart.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            // 返回相对路径
            return "data/cv/" + newFileName;
            
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 从Part中获取文件名
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
     * 获取文件扩展�?
     */
    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(lastDotIndex + 1).toLowerCase();
        }
        return "";
    }
    
    /**
     * 验证是否为有效的CV文件类型
     */
    private boolean isValidCVFile(String extension) {
        return extension.equals("pdf") || 
               extension.equals("doc") || 
               extension.equals("docx");
    }
}
