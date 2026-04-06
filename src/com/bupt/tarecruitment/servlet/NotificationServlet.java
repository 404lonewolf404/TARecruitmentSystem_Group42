package com.bupt.tarecruitment.servlet;

import com.bupt.tarecruitment.dao.NotificationDAO;
import com.bupt.tarecruitment.service.NotificationService;
import com.bupt.tarecruitment.model.Notification;
import com.bupt.tarecruitment.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

public class NotificationServlet extends HttpServlet {
    private NotificationDAO notificationDAO;
    private NotificationService notificationService;
    
    @Override
    public void init() throws ServletException {
        this.notificationDAO = new NotificationDAO();
        this.notificationService = new NotificationService();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }
        
        User user = (User) session.getAttribute("user");
        
        // 处理通知列表页面
        handleViewNotifications(request, response, user);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }
        
        User user = (User) session.getAttribute("user");
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        
        if (pathInfo.equals("/markRead")) {
            handleMarkAsRead(request, response, user);
        } else if (pathInfo.equals("/markAllRead")) {
            handleMarkAllAsRead(request, response, user);
        } else if (pathInfo.equals("/delete")) {
            handleDelete(request, response, user);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
    
    private void handleViewNotifications(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException {
        try {
            List<Notification> notifications = notificationDAO.findByUserId(user.getUserId());
            int unreadCount = notificationDAO.getUnreadCount(user.getUserId());
            
            request.setAttribute("notifications", notifications);
            request.setAttribute("unreadCount", unreadCount);
            
            String jspPath = "/WEB-INF/jsp/" + user.getRole().toString().toLowerCase() + "/notifications.jsp";
            request.getRequestDispatcher(jspPath).forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "获取通知失败：" + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(request, response);
        }
    }
    
    private void handleMarkAsRead(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException {
        try {
            String notificationId = request.getParameter("notificationId");
            if (notificationId != null) {
                notificationDAO.markAsRead(notificationId);
            }
            // 重定向回通知页面
            String role = user.getRole().toString().toLowerCase();
            response.sendRedirect(request.getContextPath() + "/" + role + "/notifications");
        } catch (Exception e) {
            e.printStackTrace();
            String role = user.getRole().toString().toLowerCase();
            response.sendRedirect(request.getContextPath() + "/" + role + "/notifications");
        }
    }
    
    private void handleMarkAllAsRead(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException {
        try {
            notificationDAO.markAllAsRead(user.getUserId());
            // 重定向回通知页面
            String role = user.getRole().toString().toLowerCase();
            response.sendRedirect(request.getContextPath() + "/" + role + "/notifications");
        } catch (Exception e) {
            e.printStackTrace();
            String role = user.getRole().toString().toLowerCase();
            response.sendRedirect(request.getContextPath() + "/" + role + "/notifications");
        }
    }
    
    private void handleDelete(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException {
        try {
            String notificationId = request.getParameter("notificationId");
            if (notificationId != null) {
                notificationDAO.delete(notificationId);
            }
            // 重定向回通知页面
            String role = user.getRole().toString().toLowerCase();
            response.sendRedirect(request.getContextPath() + "/" + role + "/notifications");
        } catch (Exception e) {
            e.printStackTrace();
            String role = user.getRole().toString().toLowerCase();
            response.sendRedirect(request.getContextPath() + "/" + role + "/notifications");
        }
    }
}
