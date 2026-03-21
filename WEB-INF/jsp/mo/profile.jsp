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
    <title>个人资料 - TA招聘系统</title>
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
            <li><a href="<%= request.getContextPath() %>/positions/create">创建职位</a></li>
            <li><a href="<%= request.getContextPath() %>/mo/profile">个人资料</a></li>
            <li><a href="<%= request.getContextPath() %>/auth/logout">登出</a></li>
        </ul>
    </nav>
    
    <div class="container">
        <div class="card">
            <h2>个人资料</h2>
            
            <% String errorMessage = (String) request.getAttribute("errorMessage"); %>
            <% String successMessage = (String) request.getAttribute("successMessage"); %>
            
            <% if (errorMessage != null && !errorMessage.isEmpty()) { %>
                <div class="error-message">
                    <%= errorMessage %>
                </div>
            <% } %>
            
            <% if (successMessage != null && !successMessage.isEmpty()) { %>
                <div class="success-message">
                    <%= successMessage %>
                </div>
            <% } %>
            
            <form action="<%= request.getContextPath() %>/profile" method="post" class="profile-form">
                <div class="form-group">
                    <label for="name">姓名：</label>
                    <input type="text" id="name" name="name" value="<%= currentUser.getName() %>" required>
                </div>
                
                <div class="form-group">
                    <label for="email">邮箱：</label>
                    <input type="email" id="email" name="email" value="<%= currentUser.getEmail() %>" required>
                </div>
                
                <div class="form-group">
                    <label>角色：</label>
                    <input type="text" value="模块负责人(MO)" readonly disabled>
                </div>
                
                <div class="form-group">
                    <button type="submit" class="btn btn-primary">保存修改</button>
                </div>
            </form>
        </div>
    </div>
    
    <script src="<%= request.getContextPath() %>/js/main.js"></script>
</body>
</html>
