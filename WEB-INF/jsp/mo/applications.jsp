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
    
    Position position = (Position) request.getAttribute("position");
    @SuppressWarnings("unchecked")
    List<Application> applications = (List<Application>) request.getAttribute("applications");
    
    // 创建UserDAO实例用于获取申请者信息
    UserDAO userDAO = new UserDAO();
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>职位申请列表 - TA招聘系统</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>
    <header>
        <h1>TA招聘系统</h1>
    </header>
    
    <nav>
        <ul>
            <li><a href="<%= request.getContextPath() %>/mo/dashboard">仪表板</a></li>
            <li><a href="<%= request.getContextPath() %>/mo/profile">个人资料</a></li>
            <li><a href="<%= request.getContextPath() %>/mo/positions/my">我的职位</a></li>
            <li><a href="<%= request.getContextPath() %>/mo/positions/create">创建职位</a></li>
            <li><a href="<%= request.getContextPath() %>/messages/list">💬 消息</a></li>
            <li>
                <a href="<%= request.getContextPath() %>/mo/notifications">
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
            <h2>职位申请列表</h2>
            <% if (position != null) { %>
                <div class="position-info">
                    <h3><%= position.getTitle() %></h3>
                    <p><strong>职位ID：</strong><%= position.getPositionId() %></p>
                    <p><strong>描述：</strong><%= position.getDescription() %></p>
                    <p><strong>工作时长：</strong><%= position.getHours() %> 小时/周</p>
                </div>
                
                <!-- 状态过滤按钮 -->
                <div class="filter-tabs">
                    <a href="?positionId=<%= position.getPositionId() %>&status=all" 
                       class="filter-tab <%= "all".equals(request.getAttribute("statusFilter")) || request.getAttribute("statusFilter") == null ? "active" : "" %>">
                        全部
                    </a>
                    <a href="?positionId=<%= position.getPositionId() %>&status=pending" 
                       class="filter-tab <%= "pending".equals(request.getAttribute("statusFilter")) ? "active" : "" %>">
                        待处理
                    </a>
                    <a href="?positionId=<%= position.getPositionId() %>&status=selected" 
                       class="filter-tab <%= "selected".equals(request.getAttribute("statusFilter")) ? "active" : "" %>">
                        已选中
                    </a>
                    <a href="?positionId=<%= position.getPositionId() %>&status=rejected" 
                       class="filter-tab <%= "rejected".equals(request.getAttribute("statusFilter")) ? "active" : "" %>">
                        已拒绝
                    </a>
                </div>
            <% } %>
        </div>
        
        <% if (applications == null || applications.isEmpty()) { %>
            <div class="card">
                <p class="info-message">此职位暂无申请。</p>
                <a href="<%= request.getContextPath() %>/mo/positions/my" class="btn btn-secondary">返回我的职位</a>
            </div>
        <% } else { %>
            <div class="applications-list">
                <% 
                boolean hasSelected = false;
                for (Application app : applications) {
                    if (app.getStatus() == ApplicationStatus.SELECTED) {
                        hasSelected = true;
                        break;
                    }
                }
                
                for (Application app : applications) { 
                    User applicant = userDAO.findById(app.getTaId());
                    if (applicant == null) continue;
                %>
                    <div class="application-card">
                        <div class="application-header">
                            <h3><%= applicant.getName() %></h3>
                            <span class="badge badge-<%= app.getStatus().toString().toLowerCase() %>">
                                <% 
                                String statusText = "";
                                switch (app.getStatus()) {
                                    case PENDING:
                                        statusText = "待处理";
                                        break;
                                    case SELECTED:
                                        statusText = "已选中";
                                        break;
                                    case REJECTED:
                                        statusText = "已拒绝";
                                        break;
                                }
                                %>
                                <%= statusText %>
                            </span>
                        </div>
                        
                        <div class="application-details">
                            <p><strong>申请ID：</strong><%= app.getApplicationId() %></p>
                            <p><strong>申请者邮箱：</strong><%= applicant.getEmail() %></p>
                            <% if (applicant.getSkills() != null && !applicant.getSkills().trim().isEmpty()) { %>
                                <p><strong>技能：</strong><%= applicant.getSkills() %></p>
                            <% } %>
                            <% 
                            // 优先显示申请时提交的简历，如果没有则显示用户默认简历
                            String resumePath = app.getResumePath();
                            boolean hasApplicationResume = resumePath != null && !resumePath.trim().isEmpty();
                            boolean hasUserResume = applicant.getCvPath() != null && !applicant.getCvPath().trim().isEmpty();
                            
                            if (hasApplicationResume) { 
                            %>
                                <p><strong>简历：</strong> 
                                    <a href="<%= request.getContextPath() %>/cv/download?applicationId=<%= app.getApplicationId() %>" 
                                       class="btn btn-sm btn-secondary" target="_blank">
                                        下载申请简历
                                    </a>
                                    <span style="color: #27ae60; font-size: 0.9em;">(申请时提交)</span>
                                </p>
                            <% } else if (hasUserResume) { %>
                                <p><strong>简历：</strong> 
                                    <a href="<%= request.getContextPath() %>/cv/download?userId=<%= applicant.getUserId() %>" 
                                       class="btn btn-sm btn-secondary" target="_blank">
                                        下载简历
                                    </a>
                                    <span style="color: #7f8c8d; font-size: 0.9em;">(用户默认简历)</span>
                                </p>
                            <% } else { %>
                                <p><strong>简历：</strong> <span style="color: #95a5a6;">未上传</span></p>
                            <% } %>
                        </div>
                        
                        <div class="application-actions" style="margin-top: 15px;">
                            <a href="<%= request.getContextPath() %>/messages/conversation?applicationId=<%= app.getApplicationId() %>" 
                               class="btn btn-primary" style="margin-right: 10px;">
                                💬 对话
                            </a>
                            
                            <% if (app.getStatus() == ApplicationStatus.PENDING && !hasSelected) { %>
                                <form method="post" action="<%= request.getContextPath() %>/mo/applications/select" 
                                      style="display: inline;"
                                      onsubmit="return confirm('确定要选择此申请者吗？这将拒绝其他所有申请。');">
                                    <input type="hidden" name="applicationId" value="<%= app.getApplicationId() %>">
                                    <input type="hidden" name="positionId" value="<%= position.getPositionId() %>">
                                    <button type="submit" class="btn btn-primary">选择此申请者</button>
                                </form>
                            <% } else if (app.getStatus() == ApplicationStatus.SELECTED) { %>
                                <span class="success-message">✓ 已选中此申请者</span>
                            <% } %>
                        </div>
                    </div>
                <% } %>
            </div>
            
            <div class="card">
                <a href="<%= request.getContextPath() %>/mo/positions/my" class="btn btn-secondary">返回我的职位</a>
            </div>
        <% } %>
    </div>
    
    <script src="<%= request.getContextPath() %>/js/main.js"></script>
</body>
</html>
