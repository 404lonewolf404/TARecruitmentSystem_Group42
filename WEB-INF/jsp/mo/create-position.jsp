<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.bupt.tarecruitment.model.User" %>
<%@ page import="com.bupt.tarecruitment.service.NotificationService" %>
<%
    User currentUser = (User) session.getAttribute("user");
    if (currentUser == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    
    String errorMessage = (String) request.getAttribute("errorMessage");
    
    // 获取未读通知数量
    int unreadCount = 0;
    try {
        NotificationService notificationService = new NotificationService();
        unreadCount = notificationService.getUnreadCount(currentUser.getUserId());
    } catch (Exception e) {
        e.printStackTrace();
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>创建职位 - TA招聘系统</title>
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
            <li><a href="<%= request.getContextPath() %>/mo/positions/create" class="active">创建职位</a></li>
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
            <h2>创建新职位</h2>
            <p>填写以下信息发布助教职位招聘</p>
        </div>
        
        <% if (errorMessage != null) { %>
            <div class="alert alert-error">
                <%= errorMessage %>
            </div>
        <% } %>
        
        <div class="card">
            <form method="post" action="<%= request.getContextPath() %>/mo/positions/create" class="form">
                <div class="form-group">
                    <label for="title">职位标题 <span class="required">*</span></label>
                    <input type="text" 
                           id="title" 
                           name="title" 
                           required 
                           maxlength="200"
                           placeholder="例如：数据结构课程助教"
                           value="<%= request.getParameter("title") != null ? request.getParameter("title") : "" %>">
                </div>
                
                <div class="form-group">
                    <label for="description">职位描述 <span class="required">*</span></label>
                    <textarea id="description" 
                              name="description" 
                              required 
                              rows="5"
                              placeholder="详细描述职位职责和工作内容"><%= request.getParameter("description") != null ? request.getParameter("description") : "" %></textarea>
                </div>
                
                <div class="form-group">
                    <label for="requirements">职位要求</label>
                    <textarea id="requirements" 
                              name="requirements" 
                              rows="4"
                              placeholder="描述对申请者的技能和经验要求（可选）"><%= request.getParameter("requirements") != null ? request.getParameter("requirements") : "" %></textarea>
                </div>
                
                <div class="form-group">
                    <label for="hours">工作时长（小时/周）<span class="required">*</span></label>
                    <input type="number" 
                           id="hours" 
                           name="hours" 
                           required 
                           min="1" 
                           max="40"
                           placeholder="例如：10"
                           value="<%= request.getParameter("hours") != null ? request.getParameter("hours") : "" %>">
                    <small>请输入每周工作小时数（1-40小时）</small>
                </div>
                
                <div class="form-group">
                    <label for="maxPositions">招聘名额 <span class="required">*</span></label>
                    <input type="number" 
                           id="maxPositions" 
                           name="maxPositions" 
                           required 
                           min="1" 
                           max="100"
                           placeholder="例如：2"
                           value="<%= request.getParameter("maxPositions") != null ? request.getParameter("maxPositions") : "1" %>">
                    <small>请输入需要招聘的TA数量（1-100人）</small>
                </div>
                
                <div class="form-actions">
                    <button type="submit" class="btn btn-primary">创建职位</button>
                    <a href="<%= request.getContextPath() %>/mo/positions/my" class="btn btn-secondary">取消</a>
                </div>
            </form>
        </div>
    </div>
    
    <script src="<%= request.getContextPath() %>/js/main.js"></script>
</body>
</html>
