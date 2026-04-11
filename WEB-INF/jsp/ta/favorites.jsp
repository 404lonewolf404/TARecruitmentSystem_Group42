<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.bupt.tarecruitment.model.User" %>
<%@ page import="com.bupt.tarecruitment.model.Position" %>
<%@ page import="com.bupt.tarecruitment.service.NotificationService" %>
<%@ page import="java.util.List" %>
<%
    User currentUser = (User) session.getAttribute("user");
    if (currentUser == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    
    // 获取未读通知数量
    int unreadCount = 0;
    try {
        NotificationService notificationService = new NotificationService();
        unreadCount = notificationService.getUnreadCount(currentUser.getUserId());
    } catch (Exception e) {
        e.printStackTrace();
    }
    
    @SuppressWarnings("unchecked")
    List<Position> favoritePositions = (List<Position>) request.getAttribute("favoritePositions");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>我的收藏 - TA招聘系统</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
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
        <h1>TA招聘系统</h1>
    </header>
    
    <nav>
        <ul>
            <li><a href="<%= request.getContextPath() %>/ta/dashboard">仪表板</a></li>
            <li><a href="<%= request.getContextPath() %>/ta/profile">个人资料</a></li>
            <li><a href="<%= request.getContextPath() %>/ta/positions">浏览职位</a></li>
            <li><a href="<%= request.getContextPath() %>/ta/applications/my">我的申请</a></li>
            <li><a href="<%= request.getContextPath() %>/ta/favorites" class="active">⭐ 我的收藏</a></li>
            <li><a href="<%= request.getContextPath() %>/messages/list">💬 消息</a></li>
            <li>
                <a href="<%= request.getContextPath() %>/ta/notifications">
                    通知
                    <% if (unreadCount > 0) { %>
                        <span class="notification-badge"><%= unreadCount %></span>
                    <% } %>
                </a>
            </li>
            <li><a href="<%= request.getContextPath() %>/auth/logout">登出</a></li>
        </ul>
    </nav>
    
    <div class="container">
        <div class="card">
            <h2><span class="favorite-icon">⭐</span>我的收藏</h2>
            <p>查看您收藏的职位</p>
        </div>
        
        <% if (favoritePositions == null || favoritePositions.isEmpty()) { %>
            <div class="card empty-favorites">
                <div class="empty-favorites-icon">⭐</div>
                <h3>还没有收藏任何职位</h3>
                <p>浏览职位时点击"⭐ 收藏"按钮，将感兴趣的职位添加到收藏夹</p>
                <a href="<%= request.getContextPath() %>/ta/positions" class="btn btn-primary" style="margin-top: 20px;">
                    浏览职位
                </a>
            </div>
        <% } else { %>
            <div class="positions-list">
                <% for (Position position : favoritePositions) { %>
                    <div class="position-card">
                        <div class="position-header">
                            <h3><%= position.getTitle() %></h3>
                            <div>
                                <span class="badge badge-<%= position.getStatus().toString().toLowerCase() %>">
                                    <%= position.getStatus() == com.bupt.tarecruitment.model.PositionStatus.OPEN ? "开放" : "关闭" %>
                                </span>
                                <% if (position.isExpired()) { %>
                                    <span class="badge" style="background-color: #dc3545;">已过期</span>
                                <% } %>
                            </div>
                        </div>
                        
                        <div class="position-details">
                            <p><strong>职位ID：</strong><%= position.getPositionId() %></p>
                            <p><strong>描述：</strong><%= position.getDescription() %></p>
                            <% if (position.getRequirements() != null && !position.getRequirements().trim().isEmpty()) { %>
                                <p><strong>要求：</strong><%= position.getRequirements() %></p>
                            <% } %>
                            <p><strong>工作时长：</strong><%= position.getHours() %> 小时/周</p>
                            <p><strong>招聘名额：</strong><%= position.getMaxPositions() %> 人</p>
                            
                            <% if (position.getDeadline() != null) { %>
                                <p><strong>申请截止：</strong>
                                    <%= new java.text.SimpleDateFormat("yyyy-MM-dd").format(position.getDeadline()) %>
                                    <% 
                                    int daysRemaining = position.getDaysRemaining();
                                    if (daysRemaining > 7) { %>
                                        <span style="color: #28a745;">(还剩 <%= daysRemaining %> 天)</span>
                                    <% } else if (daysRemaining >= 4) { %>
                                        <span style="color: #ffc107;">(还剩 <%= daysRemaining %> 天)</span>
                                    <% } else if (daysRemaining > 0) { %>
                                        <span style="color: #dc3545;">(还剩 <%= daysRemaining %> 天)</span>
                                    <% } else if (position.isExpired()) { %>
                                        <span style="color: #dc3545; font-weight: bold;">(已过期)</span>
                                    <% } %>
                                </p>
                            <% } else { %>
                                <p><strong>申请截止：</strong><span style="color: #6c757d;">无截止日期</span></p>
                            <% } %>
                        </div>
                        
                        <div class="position-actions">
                            <% if (position.getStatus() == com.bupt.tarecruitment.model.PositionStatus.OPEN && !position.isExpired()) { %>
                                <a href="<%= request.getContextPath() %>/ta/applications/apply?positionId=<%= position.getPositionId() %>" 
                                   class="btn btn-primary">申请职位</a>
                            <% } else { %>
                                <button class="btn btn-secondary" disabled>无法申请</button>
                            <% } %>
                            
                            <form method="post" 
                                  action="<%= request.getContextPath() %>/ta/favorites/remove" 
                                  style="display: inline;"
                                  onsubmit="return confirm('确定要取消收藏此职位吗？');">
                                <input type="hidden" name="positionId" value="<%= position.getPositionId() %>">
                                <input type="hidden" name="returnUrl" value="<%= request.getContextPath() %>/ta/favorites">
                                <button type="submit" class="btn btn-secondary">取消收藏</button>
                            </form>
                        </div>
                    </div>
                <% } %>
            </div>
        <% } %>
    </div>
    
    <script src="<%= request.getContextPath() %>/js/main.js"></script>
</body>
</html>
