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
    Map<String, Integer> stats = (Map<String, Integer>) request.getAttribute("stats");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>TA仪表板 - TA招聘系统</title>
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
            <li><a href="<%= request.getContextPath() %>/auth/logout">登出</a></li>
        </ul>
    </nav>
    
    <div class="container">
        <div class="card">
            <h2>欢迎，<%= currentUser.getName() %>！</h2>
            <p>您已登录为助教（TA）。</p>
        </div>
        
        <% if (stats != null) { %>
        <div class="stats-container">
            <div class="stat-card total">
                <div class="stat-number"><%= stats.get("total") %></div>
                <div class="stat-label">我的申请</div>
            </div>
            <div class="stat-card pending">
                <div class="stat-number"><%= stats.get("pending") %></div>
                <div class="stat-label">待审核</div>
            </div>
            <div class="stat-card selected">
                <div class="stat-number"><%= stats.get("selected") %></div>
                <div class="stat-label">已选中</div>
            </div>
            <div class="stat-card hours">
                <div class="stat-number"><%= stats.get("hours") %></div>
                <div class="stat-label">当前工时</div>
            </div>
        </div>
        <% } %>
        
        <div class="dashboard">
            <div class="dashboard-card">
                <h3>个人资料</h3>
                <p>查看和编辑您的个人信息和技能</p>
                <a href="<%= request.getContextPath() %>/ta/profile" class="btn btn-primary">查看个人资料</a>
            </div>
            
            <div class="dashboard-card">
                <h3>浏览职位</h3>
                <p>查看所有可申请的助教职位</p>
                <a href="<%= request.getContextPath() %>/ta/positions" class="btn btn-primary">浏览职位</a>
            </div>
            
            <div class="dashboard-card">
                <h3>我的申请</h3>
                <p>查看您已提交的申请及其状态</p>
                <a href="<%= request.getContextPath() %>/ta/applications/my" class="btn btn-primary">查看申请</a>
            </div>
        </div>
    </div>
    
    <script src="<%= request.getContextPath() %>/js/main.js"></script>
</body>
</html>
