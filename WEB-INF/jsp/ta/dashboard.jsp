<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.bupt.tarecruitment.model.User" %>
<%
    User currentUser = (User) session.getAttribute("user");
    if (currentUser == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
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
        
        <div class="dashboard">
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
