<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.bupt.tarecruitment.model.User" %>
<%@ page import="com.bupt.tarecruitment.service.NotificationService" %>
<%
    User user = (User) request.getAttribute("user");
    if (user == null) {
        user = (User) session.getAttribute("user");
    }
    if (user == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    
    // 获取未读通知数量
    int unreadCount = 0;
    try {
        NotificationService notificationService = new NotificationService();
        unreadCount = notificationService.getUnreadCount(user.getUserId());
    } catch (Exception e) {
        e.printStackTrace();
    }
    
    String successMessage = (String) request.getAttribute("successMessage");
    String errorMessage = (String) request.getAttribute("errorMessage");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>个人资料 - TA招聘系统</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
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
        <div class="form-container">
            <h2>个人资料</h2>
            
            <% if (successMessage != null && !successMessage.isEmpty()) { %>
                <div class="alert alert-success">
                    <%= successMessage %>
                </div>
            <% } %>
            
            <% if (errorMessage != null && !errorMessage.isEmpty()) { %>
                <div class="alert alert-error">
                    <%= errorMessage %>
                </div>
            <% } %>
            
            <form action="<%= request.getContextPath() %>/ta/profile/update" method="post" class="profile-form" enctype="multipart/form-data">
                <div class="form-group">
                    <label for="name">姓名：<span class="required">*</span></label>
                    <input type="text" id="name" name="name" required 
                           placeholder="请输入姓名"
                           value="<%= user.getName() != null ? user.getName() : "" %>">
                </div>
                
                <div class="form-group">
                    <label for="email">邮箱：<span class="required">*</span></label>
                    <input type="email" id="email" name="email" required 
                           placeholder="请输入邮箱地址"
                           value="<%= user.getEmail() != null ? user.getEmail() : "" %>">
                </div>
                
                <div class="form-group">
                    <label for="role">角色：</label>
                    <input type="text" id="role" name="role" readonly 
                           value="<%= user.getRole() != null ? user.getRole().toString() : "" %>"
                           style="background-color: #f5f5f5;">
                    <small>角色信息不可修改</small>
                </div>
                
                <div class="form-group">
                    <label for="skills">技能：</label>
                    <textarea id="skills" name="skills" rows="4" 
                              placeholder="请输入您的技能和经验"><%= user.getSkills() != null ? user.getSkills() : "" %></textarea>
                    <small>例如：Java编程、数据库管理、Web开发等</small>
                </div>
                
                <div class="form-group">
                    <label for="cv">上传简历（CV）：</label>
                    <% if (user.getCvPath() != null && !user.getCvPath().isEmpty()) { %>
                        <div class="cv-info">
                            <p>当前简历：<a href="<%= request.getContextPath() %>/<%= user.getCvPath() %>" target="_blank">查看简历</a></p>
                        </div>
                    <% } %>
                    <input type="file" id="cv" name="cv" accept=".pdf,.doc,.docx">
                    <small>支持格式：PDF、DOC、DOCX，最大5MB</small>
                </div>
                
                <div class="form-group">
                    <button type="submit" class="btn btn-primary btn-full">保存更改</button>
                </div>
            </form>
            
            <div class="form-footer text-center">
                <a href="<%= request.getContextPath() %>/ta/dashboard" class="btn btn-secondary">返回仪表板</a>
            </div>
        </div>
    </div>
    
    <script src="<%= request.getContextPath() %>/js/main.js"></script>
</body>
</html>
