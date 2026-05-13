<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.bupt.tarecruitment.model.User" %>
<%@ page import="com.bupt.tarecruitment.model.Application" %>
<%@ page import="com.bupt.tarecruitment.model.ApplicationStatus" %>
<%@ page import="com.bupt.tarecruitment.model.Position" %>
<%@ page import="com.bupt.tarecruitment.dao.PositionDAO" %>
<%@ page import="com.bupt.tarecruitment.service.NotificationService" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%
    User currentUser = (User) session.getAttribute("user");
    if (currentUser == null) {
        response.sendRedirect(request.getContextPath() + "/auth/login");
        return;
    }
    
    // Get unread notification count
    int unreadCount = 0;
    try {
        NotificationService notificationService = new NotificationService();
        unreadCount = notificationService.getUnreadCount(currentUser.getUserId());
    } catch (Exception e) {
        e.printStackTrace();
    }
    
    @SuppressWarnings("unchecked")
    List<Application> applications = (List<Application>) request.getAttribute("applications");
    String errorMessage = (String) request.getAttribute("errorMessage");
    
    // Get messages from session
    String successMessage = (String) session.getAttribute("successMessage");
    String sessionErrorMessage = (String) session.getAttribute("errorMessage");
    
    // Clear messages from session (remove after displaying once)
    if (successMessage != null) {
        session.removeAttribute("successMessage");
    }
    if (sessionErrorMessage != null) {
        session.removeAttribute("errorMessage");
        if (errorMessage == null) {
            errorMessage = sessionErrorMessage;
        }
    }
    
    // 鍒涘缓PositionDAO瀹炰緥鐢ㄤ簬鏌ヨ鑱屼綅淇℃伅
    PositionDAO positionDAO = new PositionDAO();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Applications - TA Recruitment System</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>
    <header>
        <h1><i class="fas fa-graduation-cap"></i> TA Recruitment System</h1>
    </header>
    
    <nav>
        <ul>
            <li><a href="<%= request.getContextPath() %>/ta/dashboard"><i class="fas fa-home"></i>&nbsp;&nbsp;Dashboard</a></li>
            <li><a href="<%= request.getContextPath() %>/ta/profile"><i class="fas fa-user"></i>&nbsp;&nbsp;Profile</a></li>
            <li><a href="<%= request.getContextPath() %>/ta/positions"><i class="fas fa-briefcase"></i>&nbsp;&nbsp;Browse Positions</a></li>
            <li><a href="<%= request.getContextPath() %>/ta/applications/my"><i class="fas fa-file-alt"></i>&nbsp;&nbsp;My Applications</a></li>
            <li><a href="<%= request.getContextPath() %>/ta/favorites"><i class="fas fa-star"></i>&nbsp;&nbsp;Favorites</a></li>
            <li><a href="<%= request.getContextPath() %>/messages/list"><i class="fas fa-comments"></i>&nbsp;&nbsp;Messages</a></li>
            <li>
                <a href="<%= request.getContextPath() %>/ta/notifications">
                    <i class="fas fa-bell"></i>&nbsp;&nbsp;Notifications
                    <% if (unreadCount > 0) { %>
                        <span class="notification-badge"><%= unreadCount %></span>
                    <% } %>
                </a>
            </li>
            <li><a href="<%= request.getContextPath() %>/auth/logout"><i class="fas fa-sign-out-alt"></i>&nbsp;&nbsp;Logout</a></li>
        </ul>
    </nav>
    
    <div class="container">
        <div class="page-header">
            <h1><i class="fas fa-file-alt"></i> My Applications</h1>
            <p>Track the status of your TA position applications</p>
        </div>
        
        <% if (successMessage != null && !successMessage.isEmpty()) { %>
            <div class="alert alert-success" style="margin-bottom: 30px;">
                <i class="fas fa-check-circle"></i> <%= successMessage %>
            </div>
        <% } %>
        
        <% if (errorMessage != null && !errorMessage.isEmpty()) { %>
            <div class="alert alert-error" style="margin-bottom: 30px;">
                <i class="fas fa-exclamation-circle"></i> <%= errorMessage %>
            </div>
        <% } %>
        
        <% if (applications == null || applications.isEmpty()) { %>
            <div class="card" style="text-align: center; padding: 80px 40px;">
                <div style="font-size: 64px; color: #bbb; margin-bottom: 20px;">
                    <i class="fas fa-inbox"></i>
                </div>
                <h3 style="font-size: 24px; margin-bottom: 15px;">No Applications Yet</h3>
                <p style="color: #666; font-size: 16px; margin-bottom: 30px;">
                    You haven't submitted any applications. Start exploring available positions!
                </p>
                <a href="<%= request.getContextPath() %>/ta/positions" class="btn btn-primary" style="padding: 12px 30px; font-size: 16px;">
                    <i class="fas fa-search"></i> Browse Positions
                </a>
            </div>
        <% } else { %>
            <div style="display: flex; flex-direction: column; gap: 25px; margin-bottom: 40px;">
            <% for (Application app : applications) { %>
                <%
                    Position position = positionDAO.findById(app.getPositionId());
                    if (position == null) continue;
                    
                    String statusText = "";
                    String statusClass = "";
                    String statusIcon = "";
                    switch (app.getStatus()) {
                        case PENDING:
                            statusText = "Pending";
                            statusClass = "badge-pending";
                            statusIcon = "fa-hourglass-half";
                            break;
                        case SELECTED:
                            statusText = "Selected";
                            statusClass = "badge-selected";
                            statusIcon = "fa-check-circle";
                            break;
                        case REJECTED:
                            statusText = "Rejected";
                            statusClass = "badge-rejected";
                            statusIcon = "fa-times-circle";
                            break;
                        case WITHDRAWN:
                            statusText = "Withdrawn";
                            statusClass = "badge-closed";
                            statusIcon = "fa-ban";
                            break;
                    }
                %>
                <div class="card">
                    <div style="display: flex; justify-content: space-between; align-items: start; margin-bottom: 20px;">
                        <h3 style="margin: 0; flex: 1;"><%= position.getTitle() %></h3>
                        <span class="badge <%= statusClass %>" style="white-space: nowrap; margin-left: 10px;">
                            <i class="fas <%= statusIcon %>"></i> <%= statusText %>
                        </span>
                    </div>
                    
                    <div style="margin-bottom: 20px; padding-bottom: 20px; border-bottom: 1px solid #eee;">
                        <p style="color: #666; line-height: 1.6; margin: 0;">
                            <%= position.getDescription() != null ? position.getDescription() : "No description" %>
                        </p>
                    </div>
                    
                    <div style="display: grid; grid-template-columns: 1fr 1fr 1fr; gap: 20px; margin-bottom: 20px;">
                        <div>
                            <p style="margin: 0; font-size: 12px; color: #999; text-transform: uppercase;">Hours/Week</p>
                            <p style="margin: 8px 0 0 0; font-size: 20px; font-weight: bold; color: #2563eb;">
                                <%= position.getHours() %>h
                            </p>
                        </div>
                        <div>
                            <p style="margin: 0; font-size: 12px; color: #999; text-transform: uppercase;">Applied</p>
                            <p style="margin: 8px 0 0 0; font-size: 14px; color: #555;">
                                <%= app.getAppliedAt() != null ? dateFormat.format(app.getAppliedAt()) : "Unknown" %>
                            </p>
                        </div>
                        <div>
                            <p style="margin: 0; font-size: 12px; color: #999; text-transform: uppercase;">Status</p>
                            <p style="margin: 8px 0 0 0; font-size: 14px; font-weight: 600; color: #2563eb;">
                                <%= statusText %>
                            </p>
                        </div>
                    </div>
                    
                    <% if (position.getRequirements() != null && !position.getRequirements().isEmpty()) { %>
                        <div style="margin-bottom: 20px; padding: 15px; background: #f5f5f5; border-radius: 4px;">
                            <p style="margin: 0 0 8px 0; font-size: 12px; color: #999; text-transform: uppercase; font-weight: 600;">Requirements</p>
                            <p style="margin: 0; font-size: 14px; color: #555; line-height: 1.6;">
                                <%= position.getRequirements() %>
                            </p>
                        </div>
                    <% } %>
                    
                    <div style="display: flex; gap: 12px; margin-top: 20px; padding-top: 20px; border-top: 1px solid #eee;">
                        <% if (app.getResumePath() != null && !app.getResumePath().trim().isEmpty()) { %>
                            <a href="<%= request.getContextPath() %>/<%= app.getResumePath() %>" 
                               target="_blank" class="btn btn-secondary" style="flex: 1;">
                                <i class="fas fa-file-pdf"></i> View Submitted Resume
                            </a>
                        <% } else { %>
                            <span class="btn btn-secondary" style="flex: 1; opacity: 0.5; cursor: not-allowed;">
                                <i class="fas fa-file-pdf"></i> No Resume Submitted
                            </span>
                        <% } %>
                        
                        <a href="<%= request.getContextPath() %>/messages/conversation?applicationId=<%= app.getApplicationId() %>" 
                           class="btn btn-secondary" style="flex: 1;">
                            <i class="fas fa-comments"></i> Messages
                        </a>
                        
                        <% if (app.getStatus() == ApplicationStatus.PENDING) { %>
                            <form action="<%= request.getContextPath() %>/ta/applications/withdraw" method="post" style="display: inline; flex: 1;">
                                <input type="hidden" name="applicationId" value="<%= app.getApplicationId() %>">
                                <button type="submit" class="btn btn-danger" 
                                        onclick="return confirm('Are you sure you want to withdraw this application?');"
                                        style="width: 100%;">
                                    <i class="fas fa-trash"></i> Withdraw
                                </button>
                            </form>
                        <% } %>
                    </div>
                </div>
            <% } %>
            </div>
        <% } %>
        
        <div style="text-align: center; margin-top: 50px; margin-bottom: 40px;">
            <a href="<%= request.getContextPath() %>/ta/dashboard" class="btn btn-secondary" style="padding: 12px 30px; font-size: 16px;">
                <i class="fas fa-arrow-left"></i> Back to Dashboard
            </a>
        </div>
    </div>
    
    <script src="<%= request.getContextPath() %>/js/main.js"></script>
</body>
</html>
