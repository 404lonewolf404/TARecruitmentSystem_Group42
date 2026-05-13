package com.bupt.tarecruitment.servlet;

import com.bupt.tarecruitment.dao.UserDAO;
import com.bupt.tarecruitment.model.Application;
import com.bupt.tarecruitment.model.Message;
import com.bupt.tarecruitment.model.NotificationType;
import com.bupt.tarecruitment.model.Position;
import com.bupt.tarecruitment.model.User;
import com.bupt.tarecruitment.model.UserRole;
import com.bupt.tarecruitment.service.ApplicationService;
import com.bupt.tarecruitment.service.MessageService;
import com.bupt.tarecruitment.service.NotificationService;
import com.bupt.tarecruitment.service.PositionService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Messaging servlet.
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
        if (pathInfo == null || "/".equals(pathInfo)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request path");
            return;
        }

        switch (pathInfo) {
            case "/conversation":
                handleViewConversation(request, response);
                break;
            case "/list":
                handleListConversations(request, response);
                break;
            case "/inbox":
                handleMessageList(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource not found");
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
            case "/send":
                handleSendMessage(request, response);
                break;
            case "/mark-read":
                handleMarkAsRead(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource not found");
                break;
        }
    }

    private void handleMessageList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }

        User currentUser = (User) session.getAttribute("user");

        try {
            List<MessageService.ConversationInfo> conversations =
                    messageService.getUserConversations(currentUser.getUserId());
            int unreadCount = notificationService.getUnreadCount(currentUser.getUserId());

            request.setAttribute("conversations", conversations);
            request.setAttribute("unreadNotificationCount", unreadCount);

            if (currentUser.getRole() == UserRole.TA) {
                request.getRequestDispatcher("/WEB-INF/jsp/ta/messages.jsp").forward(request, response);
            } else if (currentUser.getRole() == UserRole.MO) {
                request.getRequestDispatcher("/WEB-INF/jsp/mo/messages.jsp").forward(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Admin cannot access messaging");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Failed to load message list: " + e.getMessage());
        }
    }

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
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Application ID is required");
            return;
        }

        try {
            Application application = applicationService.getApplicationById(applicationId);
            if (application == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Application not found");
                return;
            }

            Position position = positionService.getPositionById(application.getPositionId());
            if (position == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Position not found");
                return;
            }

            // Existing permission behavior retained.
            if (!application.getTaId().equals(currentUser.getUserId())
                    && !position.getMoId().equals(currentUser.getUserId())) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "No permission to view this conversation");
                return;
            }

            List<Message> messages = messageService.getConversation(applicationId);
            User ta = userDAO.findById(application.getTaId());
            User mo = userDAO.findById(position.getMoId());
            messageService.markAllAsRead(applicationId, currentUser.getUserId());
            int unreadCount = notificationService.getUnreadCount(currentUser.getUserId());

            request.setAttribute("application", application);
            request.setAttribute("position", position);
            request.setAttribute("messages", messages);
            request.setAttribute("ta", ta);
            request.setAttribute("mo", mo);
            request.setAttribute("unreadNotificationCount", unreadCount);

            request.getRequestDispatcher("/WEB-INF/jsp/conversation-simple.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Failed to load conversation: " + e.getMessage());
        }
    }

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
            messageService.sendMessage(applicationId, currentUser.getUserId(), content);

            Application application = applicationService.getApplicationById(applicationId);
            if (application != null) {
                Position position = positionService.getPositionById(application.getPositionId());
                if (position != null) {
                    String receiverId = currentUser.getUserId().equals(application.getTaId())
                            ? position.getMoId()
                            : application.getTaId();

                    try {
                        notificationService.createNotification(
                                receiverId,
                                NotificationType.MESSAGE,
                                "You received a new message from " + currentUser.getName()
                                        + " about position '" + position.getTitle() + "'."
                        );
                    } catch (Exception notificationError) {
                        notificationError.printStackTrace();
                    }
                }
            }

            response.sendRedirect(request.getContextPath() + "/messages/conversation?applicationId=" + applicationId);
        } catch (IllegalArgumentException e) {
            session.setAttribute("errorMessage", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/messages/conversation?applicationId=" + applicationId);
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("errorMessage", "Failed to send message: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/messages/conversation?applicationId=" + applicationId);
        }
    }

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
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to mark messages as read");
        }
    }

    private void handleListConversations(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }

        User currentUser = (User) session.getAttribute("user");

        try {
            List<Application> applications;
            if (currentUser.getRole() == UserRole.TA) {
                applications = applicationService.getApplicationsByTA(currentUser.getUserId());
            } else {
                List<Position> positions = positionService.getPositionsByMO(currentUser.getUserId());
                applications = new ArrayList<>();
                for (Position pos : positions) {
                    applications.addAll(applicationService.getApplicationsByPositionId(pos.getPositionId()));
                }
            }

            List<Map<String, Object>> conversationList = new ArrayList<>();
            for (Application app : applications) {
                Position position = positionService.getPositionById(app.getPositionId());
                if (position == null) {
                    continue;
                }

                User ta = userDAO.findById(app.getTaId());
                User mo = userDAO.findById(position.getMoId());
                List<Message> messages = messageService.getConversation(app.getApplicationId());
                int unreadCount = messageService.getUnreadCount(currentUser.getUserId(), app.getApplicationId());

                Map<String, Object> conversation = new HashMap<>();
                conversation.put("application", app);
                conversation.put("position", position);
                conversation.put("ta", ta);
                conversation.put("mo", mo);
                conversation.put("unreadCount", unreadCount);
                conversation.put("messageCount", messages.size());
                conversation.put("lastMessage", messages.isEmpty() ? null : messages.get(messages.size() - 1));
                conversationList.add(conversation);
            }

            conversationList.sort((a, b) -> {
                Message msgA = (Message) a.get("lastMessage");
                Message msgB = (Message) b.get("lastMessage");
                if (msgA == null && msgB == null) {
                    return 0;
                }
                if (msgA == null) {
                    return 1;
                }
                if (msgB == null) {
                    return -1;
                }
                return msgB.getSentAt().compareTo(msgA.getSentAt());
            });

            int unreadNotificationCount = notificationService.getUnreadCount(currentUser.getUserId());
            request.setAttribute("conversations", conversationList);
            request.setAttribute("unreadNotificationCount", unreadNotificationCount);
            request.getRequestDispatcher("/WEB-INF/jsp/messages-list.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Failed to load conversations: " + e.getMessage());
        }
    }
}
