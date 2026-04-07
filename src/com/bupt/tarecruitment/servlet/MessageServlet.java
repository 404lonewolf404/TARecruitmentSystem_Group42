package com.bupt.tarecruitment.servlet;

import com.bupt.tarecruitment.model.User;
import com.bupt.tarecruitment.model.Application;
import com.bupt.tarecruitment.model.Position;
import com.bupt.tarecruitment.model.Message;
import com.bupt.tarecruitment.service.MessageService;
import com.bupt.tarecruitment.service.ApplicationService;
import com.bupt.tarecruitment.service.PositionService;
import com.bupt.tarecruitment.service.NotificationService;
import com.bupt.tarecruitment.dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

/**
 * 消息Servlet
 * V3.5 - 招聘对话系统
 */
public class MessageServlet extends HttpServlet {
    
    private MessageService messageService;
    private ApplicationService applicationService;
    private PositionService positionService;
    private NotificationService notificationService;
    private UserDAO userDAO;
    
    @Override
    public void init() {
        this.messageService = new MessageService();
        this.applicationService = new ApplicationService();
        this.positionService = new PositionService();
        this.notificationService = new NotificationService();
        this.userDAO = new UserDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "无效的请求路径");
            return;
        }
        
        switch (pathInfo) {
            case "/conversation":
                handleViewConversation(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "请求的资源不存在");
                break;
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "无效的请求路径");
            return;
        }
        
        switch (pathInfo) {
            case "/send":
                handleSendMessage(request, response);
                break;
            case "/mark-read":
                handleMarkAsRead(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "请求的资源不存在");
                break;
        }
    }
    
    /**
     * 查看对话
     */
    private void handleViewConversation(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }
        
        User currentUser = (User) session.getAttribute("user");
        String applicationId = request.getParameter("applicationId");
        
        if (applicationId == null || applicationId.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "申请ID不能为空");
            return;
        }
        
        try {
            // 获取申请信息
            Application application = applicationService.getApplicationById(applicationId);
            if (application == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "申请不存在");
                return;
            }
            
            // 获取职位信息
            Position position = positionService.getPositionById(application.getPositionId());
            if (position == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "职位不存在");
                return;
            }
            
            // 验证权限（必须是申请的TA或职位的MO）
            if (!application.getTaId().equals(currentUser.getUserId()) 
                && !position.getMoId().equals(currentUser.getUserId())) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "您没有权限查看此对话");
                return;
            }
            
            // 获取对话历史
            List<Message> messages = messageService.getConversation(applicationId);
            
            // 获取对话双方的用户信息
            User ta = userDAO.findById(application.getTaId());
            User mo = userDAO.findById(position.getMoId());
            
            // 标记消息为已读
            messageService.markAllAsRead(applicationId, currentUser.getUserId());
            
            // 获取未读通知数量
            int unreadCount = notificationService.getUnreadCount(currentUser.getUserId());
            
            // 设置属性
            request.setAttribute("application", application);
            request.setAttribute("position", position);
            request.setAttribute("messages", messages);
            request.setAttribute("ta", ta);
            request.setAttribute("mo", mo);
            request.setAttribute("unreadNotificationCount", unreadCount);
            
            // 转发到对话页面
            request.getRequestDispatcher("/WEB-INF/jsp/conversation-simple.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "获取对话失败：" + e.getMessage());
        }
    }
    
    /**
     * 发送消息
     */
    private void handleSendMessage(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }
        
        User currentUser = (User) session.getAttribute("user");
        String applicationId = request.getParameter("applicationId");
        String content = request.getParameter("content");
        
        try {
            // 发送消息
            messageService.sendMessage(applicationId, currentUser.getUserId(), content);
            
            // 获取申请信息用于发送通知
            Application application = applicationService.getApplicationById(applicationId);
            if (application != null) {
                Position position = positionService.getPositionById(application.getPositionId());
                if (position != null) {
                    // 确定接收者
                    String receiverId;
                    if (currentUser.getUserId().equals(application.getTaId())) {
                        // 当前用户是TA，接收者是MO
                        receiverId = position.getMoId();
                    } else {
                        // 当前用户是MO，接收者是TA
                        receiverId = application.getTaId();
                    }
                    
                    // 发送通知
                    try {
                        notificationService.createNotification(
                            receiverId,
                            com.bupt.tarecruitment.model.NotificationType.MESSAGE,
                            "您收到了来自 " + currentUser.getName() + " 关于职位「" + position.getTitle() + "」的新消息"
                        );
                    } catch (Exception e) {
                        // 通知发送失败不影响主流程
                        e.printStackTrace();
                    }
                }
            }
            
            // 重定向回对话页面
            response.sendRedirect(request.getContextPath() + "/messages/conversation?applicationId=" + applicationId);
            
        } catch (IllegalArgumentException e) {
            session.setAttribute("errorMessage", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/messages/conversation?applicationId=" + applicationId);
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("errorMessage", "发送消息失败：" + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/messages/conversation?applicationId=" + applicationId);
        }
    }
    
    /**
     * 标记消息为已读
     */
    private void handleMarkAsRead(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }
        
        User currentUser = (User) session.getAttribute("user");
        String applicationId = request.getParameter("applicationId");
        
        try {
            messageService.markAllAsRead(applicationId, currentUser.getUserId());
            response.sendRedirect(request.getContextPath() + "/messages/conversation?applicationId=" + applicationId);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "标记已读失败");
        }
    }
}
