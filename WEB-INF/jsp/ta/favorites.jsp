<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.bupt.tarecruitment.model.User" %>
<%@ page import="com.bupt.tarecruitment.model.Position" %>
<%@ page import="com.bupt.tarecruitment.service.NotificationService" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Set" %>
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
    List<Position> favoritePositions = (List<Position>) request.getAttribute("favoritePositions");
    @SuppressWarnings("unchecked")
    Set<String> appliedPositionIds = (Set<String>) request.getAttribute("appliedPositionIds");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Favorites - TA Recruitment System</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        .favorite-icon {
            color: #ffc107;
            font-size: 1.2em;
            margin-right: 5px;
        }
        
        .empty-favorites {
            text-align: center;
            padding: 60px 20px;
            color: #6c757d;
        }
        
        .empty-favorites-icon {
            font-size: 4em;
            color: #dee2e6;
            margin-bottom: 20px;
        }
    </style>
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
            <h1><i class="fas fa-star"></i> My Favorites</h1>
            <p>Your saved positions for quick access</p>
        </div>
        
        <% if (favoritePositions == null || favoritePositions.isEmpty()) { %>
            <div class="card" style="text-align: center; padding: 80px 40px;">
                <div style="font-size: 64px; color: #bbb; margin-bottom: 20px;">
                    <i class="fas fa-star"></i>
                </div>
                <h3 style="font-size: 24px; margin-bottom: 15px;">No Favorites Yet</h3>
                <p style="color: #666; font-size: 16px; margin-bottom: 30px;">
                    Click the star icon when browsing positions to save them to your favorites
                </p>
                <a href="<%= request.getContextPath() %>/ta/positions" class="btn btn-primary" style="padding: 12px 30px; font-size: 16px;">
                    <i class="fas fa-search"></i> Browse Positions
                </a>
            </div>
        <% } else { %>
            <div style="display: flex; flex-direction: column; gap: 25px; margin-bottom: 40px;">
            <% for (Position position : favoritePositions) { %>
                <div class="card">
                    <div style="display: flex; justify-content: space-between; align-items: start; margin-bottom: 20px;">
                        <h3 style="margin: 0; flex: 1;"><%= position.getTitle() %></h3>
                        <div style="display: flex; gap: 8px;">
                            <span class="badge badge-<%= position.getStatus().toString().toLowerCase() %>">
                                <%= position.getStatus() == com.bupt.tarecruitment.model.PositionStatus.OPEN ? "Open" : "Closed" %>
                            </span>
                            <% if (position.isExpired()) { %>
                                <span class="badge" style="background-color: #dc3545;">Expired</span>
                            <% } %>
                        </div>
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
                            <p style="margin: 0; font-size: 12px; color: #999; text-transform: uppercase;">Openings</p>
                            <p style="margin: 8px 0 0 0; font-size: 20px; font-weight: bold; color: #2563eb;">
                                <%= position.getMaxPositions() %>
                            </p>
                        </div>
                        <div>
                            <p style="margin: 0; font-size: 12px; color: #999; text-transform: uppercase;">Status</p>
                            <p style="margin: 8px 0 0 0; font-size: 16px; font-weight: 600; color: #2563eb;">
                                <%= position.getStatus() == com.bupt.tarecruitment.model.PositionStatus.OPEN ? "Open" : "Closed" %>
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
                    
                    <% if (position.getDeadline() != null) { %>
                        <div style="margin-bottom: 20px; padding: 15px; background: #f5f5f5; border-radius: 4px;">
                            <p style="margin: 0; font-size: 13px; color: #666;">
                                <i class="fas fa-calendar"></i> Deadline: <%= new java.text.SimpleDateFormat("MMM dd, yyyy").format(position.getDeadline()) %>
                            </p>
                            <% 
                            int daysRemaining = position.getDaysRemaining();
                            if (daysRemaining > 0) { 
                                String urgencyColor = daysRemaining <= 3 ? "#dc3545" : (daysRemaining <= 7 ? "#ffc107" : "#28a745");
                            %>
                                <p style="margin: 8px 0 0 0; font-size: 13px; color: <%= urgencyColor %>; font-weight: bold;">
                                    <%= daysRemaining %> day(s) remaining
                                </p>
                            <% } else if (position.isExpired()) { %>
                                <p style="margin: 8px 0 0 0; font-size: 13px; color: #dc3545; font-weight: bold;">
                                    <i class="fas fa-times-circle"></i> Expired
                                </p>
                            <% } %>
                        </div>
                    <% } %>
                    
                    <div style="display: flex; gap: 12px; margin-top: 20px; padding-top: 20px; border-top: 1px solid #eee;">
                        <% 
                        boolean hasApplied = appliedPositionIds != null && appliedPositionIds.contains(position.getPositionId());
                        boolean canApply = position.canAcceptApplications();
                        %>
                        
                        <% if (hasApplied) { %>
                            <button type="button" class="btn btn-secondary" disabled style="flex: 1;">
                                <i class="fas fa-check"></i> Applied
                            </button>
                        <% } else if (!canApply) { %>
                            <button type="button" class="btn btn-secondary" disabled style="flex: 1;">
                                <i class="fas fa-lock"></i> Cannot Apply
                            </button>
                        <% } else { %>
                            <button type="button" class="btn btn-primary" style="flex: 1;" 
                                    onclick="openApplyModal('<%= position.getPositionId() %>')">
                                <i class="fas fa-paper-plane"></i> Apply
                            </button>
                        <% } %>
                        
                        <form method="post" 
                              action="<%= request.getContextPath() %>/ta/favorites/remove" 
                              style="display: inline; flex: 1;"
                              onsubmit="return confirm('Remove from favorites?');">
                            <input type="hidden" name="positionId" value="<%= position.getPositionId() %>">
                            <input type="hidden" name="returnUrl" value="<%= request.getContextPath() %>/ta/favorites">
                            <button type="submit" class="btn btn-secondary" style="width: 100%;">
                                <i class="fas fa-trash"></i> Remove
                            </button>
                        </form>
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
    <script>
        // Toggle resume upload option
        document.querySelectorAll('input[name="resumeChoice"]').forEach(radio => {
            radio.addEventListener('change', function() {
                const positionId = this.closest('form').querySelector('input[name="positionId"]').value;
                const uploadDiv = document.getElementById('newResumeUpload_' + positionId);
                if (this.value === 'new') {
                    uploadDiv.style.display = 'block';
                } else {
                    uploadDiv.style.display = 'none';
                }
            });
        });
    </script>
</body>
</html>
