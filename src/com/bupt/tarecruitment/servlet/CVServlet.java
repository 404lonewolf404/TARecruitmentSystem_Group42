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
 * CV下载Servlet
 * 处理简历文件的下载请求
 */
public class CVServlet extends HttpServlet {
    
    private UserDAO userDAO;
    private ApplicationDAO applicationDAO;
    
    @Override
    public void init() throws ServletException {
        super.init();
        this.userDAO = new UserDAO();
        this.applicationDAO = new ApplicationDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 获取当前登录用户
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "请先登录");
            return;
        }
        
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "请先登录");
            return;
        }
        
        // 检查是通过userId还是applicationId下载
        String userId = request.getParameter("userId");
        String applicationId = request.getParameter("applicationId");
        
        String cvPath = null;
        String targetUserId = null;
        
        if (applicationId != null && !applicationId.trim().isEmpty()) {
            // 通过applicationId下载
            Application application = applicationDAO.findById(applicationId.trim());
            if (application == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "申请不存在");
                return;
            }
            
            // 检查权限：只有MO可以通过applicationId下载
            boolean isMO = currentUser.getRole().toString().equals("MO");
            if (!isMO) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "您没有权限下载此简历");
                return;
            }
            
            // 获取申请中的简历路径
            cvPath = application.getResumePath();
            targetUserId = application.getTaId();
            
            if (cvPath == null || cvPath.trim().isEmpty()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "该申请未关联简历");
                return;
            }
            
        } else if (userId != null && !userId.trim().isEmpty()) {
            // 通过userId下载
            User targetUser = userDAO.findById(userId.trim());
            if (targetUser == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "用户不存在");
                return;
            }
            
            // 检查权限：只有本人或MO可以下载简历
            boolean isOwner = currentUser.getUserId().equals(targetUser.getUserId());
            boolean isMO = currentUser.getRole().toString().equals("MO");
            
            if (!isOwner && !isMO) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "您没有权限下载此简历");
                return;
            }
            
            // 检查用户是否有简历
            cvPath = targetUser.getCvPath();
            targetUserId = targetUser.getUserId();
            
            if (cvPath == null || cvPath.trim().isEmpty()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "该用户未上传简历");
                return;
            }
            
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "必须提供userId或applicationId参数");
            return;
        }
        
        // 构建完整的文件路径
        String fullPath = "webapps/TARecruitmentSystem/" + cvPath;
        File cvFile = new File(fullPath);
        
        if (!cvFile.exists() || !cvFile.isFile()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "简历文件不存在");
            return;
        }
        
        // 设置响应头
        String fileName = cvFile.getName();
        String mimeType = getServletContext().getMimeType(fileName);
        if (mimeType == null) {
            mimeType = "application/octet-stream";
        }
        
        response.setContentType(mimeType);
        response.setContentLength((int) cvFile.length());
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        
        // 读取文件并写入响应
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
