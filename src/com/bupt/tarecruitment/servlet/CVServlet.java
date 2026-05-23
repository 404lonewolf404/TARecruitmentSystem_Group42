package com.bupt.tarecruitment.servlet;

import com.bupt.tarecruitment.dao.ApplicationDAO;
import com.bupt.tarecruitment.dao.UserDAO;
import com.bupt.tarecruitment.model.Application;
import com.bupt.tarecruitment.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Streams stored CV files to authorized users.
 */
public class CVServlet extends HttpServlet {
    
    private UserDAO userDAO;
    private ApplicationDAO applicationDAO;

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
        this.applicationDAO = new ApplicationDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 中文说明：必须已登录才能访问简历文件。
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Operation failed");
            return;
        }
        
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Operation failed");
            return;
        }
        
        // 中文说明：支持按用户 ID 或申请 ID 两种方式定位简历。
        String userId = request.getParameter("userId");
        String applicationId = request.getParameter("applicationId");
        
        String cvPath = null;
        String targetUserId = null;
        
        if (applicationId != null && !applicationId.trim().isEmpty()) {
            // 中文说明：按申请记录读取简历。
            Application application = applicationDAO.findById(applicationId.trim());
            if (application == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Operation failed");
                return;
            }
            
            // 中文说明：只有 MO 可以通过申请视图查看 TA 简历。
            boolean isMO = currentUser.getRole().toString().equals("MO");
            if (!isMO) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Operation failed");
                return;
            }
            
            // 中文说明：读取申请记录上的简历路径。
            cvPath = application.getResumePath();
            targetUserId = application.getTaId();
            
            if (cvPath == null || cvPath.trim().isEmpty()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Operation failed");
                return;
            }
            
        } else if (userId != null && !userId.trim().isEmpty()) {
            // 中文说明：按用户资料读取简历。
            User targetUser = userDAO.findById(userId.trim());
            if (targetUser == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Operation failed");
                return;
            }
            
            // 中文说明：本人或 MO 才能查看该简历。
            boolean isOwner = currentUser.getUserId().equals(targetUser.getUserId());
            boolean isMO = currentUser.getRole().toString().equals("MO");
            
            if (!isOwner && !isMO) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Operation failed");
                return;
            }
            
            // 中文说明：读取用户资料中的简历路径。
            cvPath = targetUser.getCvPath();
            targetUserId = targetUser.getUserId();
            
            if (cvPath == null || cvPath.trim().isEmpty()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Operation failed");
                return;
            }
            
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Operation failed");
            return;
        }
        
        // 中文说明：拼接部署目录中的实际文件路径。
        String fullPath = getWebAppRootPath() + "/" + cvPath;
        File cvFile = new File(fullPath);
        
        if (!cvFile.exists() || !cvFile.isFile()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Operation failed");
            return;
        }
        
        // 中文说明：推断 MIME 类型，未知时按二进制下载。
        String fileName = cvFile.getName();
        String mimeType = getServletContext().getMimeType(fileName);
        if (mimeType == null) {
            mimeType = "application/octet-stream";
        }
        
        response.setContentType(mimeType);
        response.setContentLength((int) cvFile.length());
        // 中文说明：以内联形式返回，便于浏览器直接预览。
        response.setHeader("Content-Disposition", "inline; filename=\"" + fileName + "\"");
        
        // 中文说明：把文件内容流式写入响应输出流。
        try (FileInputStream fis = new FileInputStream(cvFile);
             OutputStream os = response.getOutputStream()) {
            
            byte[] buffer = new byte[4096];
            int bytesRead;
            
            while ((bytesRead = fis.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            
            os.flush();
        }
    }
}

