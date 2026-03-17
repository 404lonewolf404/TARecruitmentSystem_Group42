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
    <title>MO仪表板 - TA招聘系统</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>
    <header>
        <h1>TA招聘系统</h1>
    </header>
    
    <nav>
        <ul>
            <li><a href="<%= request.getContextPath() %>/mo/dashboard">仪表板</a></li>
            <li><a href="<%= request.getContextPath() %>/mo/positions">我的职位</a></li>
            <li><a href="<%= request.getContextPath() %>/mo/applications">查看申请</a></li>
            <li><a href="<%= request.getContextPath() %>/auth/logout">登出</a></li>
        </ul>
    </nav>
    
    <div class="container">
        <div class="card">
            <h2>欢迎，<%= currentUser.getName() %>！</h2>
            <p>您已登录为模块负责人（MO）。</p>
        </div>
        
        <div class="dashboard">
            <div class="dashboard-card">
                <h3>我的职位</h3>
                <p>查看和管理您发布的助教职位</p>
                <a href="<%= request.getContextPath() %>/mo/positions" class="btn btn-primary">查看我的职位</a>
            </div>
            
            <div class="dashboard-card">
                <h3>查看申请</h3>
                <p>查看和处理助教申请</p>
                <a href="<%= request.getContextPath() %>/mo/applications" class="btn btn-primary">查看申请</a>
            </div>
        </div>
    </div>
    
    <script src="<%= request.getContextPath() %>/js/main.js"></script>
</body>
</html>
