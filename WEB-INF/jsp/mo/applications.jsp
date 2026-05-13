<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.bupt.tarecruitment.model.User" %>
<%@ page import="com.bupt.tarecruitment.model.Position" %>
<%@ page import="com.bupt.tarecruitment.model.Application" %>
<%@ page import="com.bupt.tarecruitment.model.ApplicationStatus" %>
<%@ page import="com.bupt.tarecruitment.dao.UserDAO" %>
<%@ page import="com.bupt.tarecruitment.service.NotificationService" %>
<%@ page import="java.util.List" %>
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
    
    Position position = (Position) request.getAttribute("position");
    @SuppressWarnings("unchecked")
    List<Application> applications = (List<Application>) request.getAttribute("applications");
    
    // Create UserDAO instance to get applicant information
    UserDAO userDAO = new UserDAO();
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Position Applications - TA Recruitment System</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>
    <header>
        <h1><i class="fas fa-graduation-cap"></i> TA Recruitment System</h1>
    </header>
    
    <nav>
        <ul>
            <li><a href="<%= request.getContextPath() %>/mo/dashboard"><i class="fas fa-home"></i>&nbsp;&nbsp;Dashboard</a></li>
            <li><a href="<%= request.getContextPath() %>/mo/profile"><i class="fas fa-user"></i>&nbsp;&nbsp;Profile</a></li>
            <li><a href="<%= request.getContextPath() %>/mo/positions/my"><i class="fas fa-briefcase"></i>&nbsp;&nbsp;My Positions</a></li>
            <li><a href="<%= request.getContextPath() %>/mo/positions/create"><i class="fas fa-plus-circle"></i>&nbsp;&nbsp;Create Position</a></li>
            <li><a href="<%= request.getContextPath() %>/messages/list"><i class="fas fa-comments"></i>&nbsp;&nbsp;Messages</a></li>
            <li>
                <a href="<%= request.getContextPath() %>/mo/notifications">
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
            <h2><i class="fas fa-file-alt"></i>&nbsp;&nbsp;Position Applications</h2>
            <p>Review and manage applications for your positions</p>
        </div>
        
        <% if (position != null) { %>
            <div class="card position-info-card">
                <div class="position-info-header">
                    <h3><%= position.getTitle() %></h3>
                </div>
                <div class="position-info-details">
                    <div class="info-item">
                        <span class="info-label"><i class="fas fa-id-card"></i>&nbsp;&nbsp;Position ID</span>
                        <span class="info-value"><%= position.getPositionId() %></span>
                    </div>
                    <div class="info-item">
                        <span class="info-label"><i class="fas fa-align-left"></i>&nbsp;&nbsp;Description</span>
                        <span class="info-value"><%= position.getDescription() %></span>
                    </div>
                    <div class="info-item">
                        <span class="info-label"><i class="fas fa-clock"></i>&nbsp;&nbsp;Work Hours</span>
                        <span class="info-value"><%= position.getHours() %> hours/week</span>
                    </div>
                </div>
                
                <!-- Status filter buttons -->
                <div class="filter-tabs" style="margin-top: 20px;">
                    <a href="?positionId=<%= position.getPositionId() %>&status=all" 
                       class="filter-tab <%= "all".equals(request.getAttribute("statusFilter")) || request.getAttribute("statusFilter") == null ? "active" : "" %>">
                        <i class="fas fa-list"></i>&nbsp;&nbsp;All
                    </a>
                    <a href="?positionId=<%= position.getPositionId() %>&status=pending" 
                       class="filter-tab <%= "pending".equals(request.getAttribute("statusFilter")) ? "active" : "" %>">
                        <i class="fas fa-hourglass-half"></i>&nbsp;&nbsp;Pending
                    </a>
                    <a href="?positionId=<%= position.getPositionId() %>&status=selected" 
                       class="filter-tab <%= "selected".equals(request.getAttribute("statusFilter")) ? "active" : "" %>">
                        <i class="fas fa-check-circle"></i>&nbsp;&nbsp;Selected
                    </a>
                    <a href="?positionId=<%= position.getPositionId() %>&status=rejected" 
                       class="filter-tab <%= "rejected".equals(request.getAttribute("statusFilter")) ? "active" : "" %>">
                        <i class="fas fa-times-circle"></i>&nbsp;&nbsp;Rejected
                    </a>
                    <a href="?positionId=<%= position.getPositionId() %>&status=withdrawn" 
                       class="filter-tab <%= "withdrawn".equals(request.getAttribute("statusFilter")) ? "active" : "" %>">
                        <i class="fas fa-undo"></i>&nbsp;&nbsp;Withdrawn
                    </a>
                </div>
            </div>
        <% } %>
        
        <% if (applications == null || applications.isEmpty()) { %>
            <div class="card">
                <div class="empty-state">
                    <i class="fas fa-inbox"></i>
                    <h3>No Applications</h3>
                    <p>There are no applications for this position yet.</p>
                    <a href="<%= request.getContextPath() %>/mo/positions/my" class="btn btn-secondary">
                        <i class="fas fa-arrow-left"></i>&nbsp;&nbsp;Back to My Positions
                    </a>
                </div>
            </div>
        <% } else { %>
            <div class="applications-list">
                <% 
                int selectedCount = 0;
                for (Application app : applications) {
                    if (app.getStatus() == ApplicationStatus.SELECTED) {
                        selectedCount++;
                    }
                }
                boolean canSelectMore = selectedCount < position.getMaxPositions();
                
                for (Application app : applications) { 
                    User applicant = userDAO.findById(app.getTaId());
                    if (applicant == null) continue;
                %>
                    <div class="card application-card-enhanced">
                        <div class="application-header-enhanced">
                            <div class="applicant-info">
                                <h3><%= applicant.getName() %></h3>
                                <p class="applicant-email"><i class="fas fa-envelope"></i>&nbsp;&nbsp;<%= applicant.getEmail() %></p>
                            </div>
                            <span class="badge badge-<%= app.getStatus().toString().toLowerCase() %>">
                                <% 
                                String statusText = "";
                                switch (app.getStatus()) {
                                    case PENDING:
                                        statusText = "Pending";
                                        break;
                                    case SELECTED:
                                        statusText = "Selected";
                                        break;
                                    case REJECTED:
                                        statusText = "Rejected";
                                        break;
                                    case WITHDRAWN:
                                        statusText = "Withdrawn";
                                        break;
                                }
                                %>
                                <%= statusText %>
                            </span>
                        </div>
                        
                        <div class="application-details-enhanced">
                            <div class="detail-row">
                                <span class="detail-label"><i class="fas fa-id-card"></i>&nbsp;&nbsp;Application ID:</span>
                                <span class="detail-value"><%= app.getApplicationId() %></span>
                            </div>
                            
                            <% if (applicant.getSkills() != null && !applicant.getSkills().trim().isEmpty()) { %>
                                <div class="detail-row">
                                    <span class="detail-label"><i class="fas fa-star"></i>&nbsp;&nbsp;Skills:</span>
                                    <span class="detail-value"><%= applicant.getSkills() %></span>
                                </div>
                            <% } %>
                            
                            <% 
                            String resumePath = app.getResumePath();
                            boolean hasApplicationResume = resumePath != null && !resumePath.trim().isEmpty();
                            boolean hasUserResume = applicant.getCvPath() != null && !applicant.getCvPath().trim().isEmpty();
                            
                            if (hasApplicationResume) { 
                            %>
                                <div class="detail-row">
                                    <span class="detail-label"><i class="fas fa-file-pdf"></i>&nbsp;&nbsp;Resume:</span>
                                    <span class="detail-value">
                                        <a href="<%= request.getContextPath() %>/cv/download?applicationId=<%= app.getApplicationId() %>" 
                                           class="btn btn-sm btn-secondary" target="_blank">
                                            <i class="fas fa-download"></i>&nbsp;&nbsp;View Application Resume
                                        </a>
                                        <span style="color: #10b981; font-size: 0.9em; margin-left: 10px;">(Submitted with application)</span>
                                    </span>
                                </div>
                            <% } else if (hasUserResume) { %>
                                <div class="detail-row">
                                    <span class="detail-label"><i class="fas fa-file-pdf"></i>&nbsp;&nbsp;Resume:</span>
                                    <span class="detail-value">
                                        <a href="<%= request.getContextPath() %>/cv/download?userId=<%= applicant.getUserId() %>" 
                                           class="btn btn-sm btn-secondary" target="_blank">
                                            <i class="fas fa-download"></i>&nbsp;&nbsp;View Resume
                                        </a>
                                        <span style="color: #64748b; font-size: 0.9em; margin-left: 10px;">(User default resume)</span>
                                    </span>
                                </div>
                            <% } else { %>
                                <div class="detail-row">
                                    <span class="detail-label"><i class="fas fa-file-pdf"></i>&nbsp;&nbsp;Resume:</span>
                                    <span class="detail-value" style="color: #94a3b8;">Not uploaded</span>
                                </div>
                            <% } %>
                        </div>
                        
                        <div class="application-actions-enhanced">
                            <a href="<%= request.getContextPath() %>/messages/conversation?applicationId=<%= app.getApplicationId() %>" 
                               class="btn btn-secondary">
                                <i class="fas fa-comments"></i>&nbsp;&nbsp;Conversation
                            </a>
                            
                            <% if (app.getStatus() == ApplicationStatus.PENDING && canSelectMore) { %>
                                <form method="post" action="<%= request.getContextPath() %>/mo/applications/select" 
                                      style="display: inline;"
                                      onsubmit="return confirm('Are you sure you want to select this applicant? This will reject all other applications.');">
                                    <input type="hidden" name="applicationId" value="<%= app.getApplicationId() %>">
                                    <input type="hidden" name="positionId" value="<%= position.getPositionId() %>">
                                    <button type="submit" class="btn btn-primary">
                                        <i class="fas fa-check"></i>&nbsp;&nbsp;Select This Applicant
                                    </button>
                                </form>
                            <% } else if (app.getStatus() == ApplicationStatus.SELECTED) { %>
                                <div class="success-badge">
                                    <i class="fas fa-check-circle"></i>&nbsp;&nbsp;Selected
                                </div>
                            <% } %>
                        </div>
                    </div>
                <% } %>
            </div>
            
            <div class="card">
                <a href="<%= request.getContextPath() %>/mo/positions/my" class="btn btn-secondary">
                    <i class="fas fa-arrow-left"></i>&nbsp;&nbsp;Back to My Positions
                </a>
            </div>
        <% } %>
    </div>
    
    <script src="<%= request.getContextPath() %>/js/main.js"></script>
</body>
</html>
