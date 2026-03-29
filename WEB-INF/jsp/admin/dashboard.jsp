<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.bupt.tarecruitment.model.User" %>
<%@ page import="java.util.Map" %>
<%
    User currentUser = (User) session.getAttribute("user");
    if (currentUser == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    
    @SuppressWarnings("unchecked")
    Map<String, Object> stats = (Map<String, Object>) request.getAttribute("stats");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>管理员仪表板 - TA招聘系统</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>
    <header>
        <h1>TA招聘系统</h1>
    </header>
    
    <nav>
        <ul>
            <li><a href="<%= request.getContextPath() %>/admin/dashboard">仪表板</a></li>
            <li><a href="<%= request.getContextPath() %>/admin/workload">工作量报告</a></li>
            <li><a href="<%= request.getContextPath() %>/auth/logout">登出</a></li>
        </ul>
    </nav>
    
    <div class="container">
        <div class="card">
            <h2>欢迎，<%= currentUser.getName() %>！</h2>
            <p>您已登录为系统管理员（Admin）。</p>
        </div>
        
        <% if (stats != null) { %>
        <div class="stats-container">
            <div class="stat-card total">
                <div class="stat-number"><%= stats.get("totalUsers") %></div>
                <div class="stat-label">总用户</div>
            </div>
            <div class="stat-card">
                <div class="stat-number"><%= stats.get("totalPositions") %></div>
                <div class="stat-label">总职位</div>
            </div>
            <div class="stat-card">
                <div class="stat-number"><%= stats.get("totalApplications") %></div>
                <div class="stat-label">总申请</div>
            </div>
            <div class="stat-card hours">
                <div class="stat-number"><%= stats.get("avgHours") %></div>
                <div class="stat-label">平均工时</div>
            </div>
        </div>
        <% } %>
        
        <div class="dashboard">
            <div class="dashboard-card">
                <h3>工作量报告</h3>
                <p>查看所有助教的工作量统计信息</p>
                <a href="<%= request.getContextPath() %>/admin/workload" class="btn btn-primary">查看工作量报告</a>
            </div>
        </div>
    </div>
    
    <script src="<%= request.getContextPath() %>/js/main.js"></script>
</body>
</html>
