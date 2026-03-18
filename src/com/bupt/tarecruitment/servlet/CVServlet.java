package com.bupt.tarecruitment.servlet;

import com.bupt.tarecruitment.dao.UserDAO;
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
    
    @Override
    public void init() throws ServletException {
        super.init();
        this.userDAO = new UserDAO();
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
        
        // 获取要下载的用户ID
        String userId = request.getParameter("userId");
        if (userId == null || userId.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "用户ID不能为空");
            return;
        }
        
        // 获取用户信息
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
        String cvPath = targetUser.getCvPath();
        if (cvPath == null || cvPath.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "该用户未上传简历");
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
