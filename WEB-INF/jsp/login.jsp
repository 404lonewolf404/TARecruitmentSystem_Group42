<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>登录 - TA招聘系统</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>
    <div class="container">
        <div class="form-container">
            <h1>TA招聘系统</h1>
            <h2>登录</h2>
            
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
            
            <form action="<%= request.getContextPath() %>/auth/login" method="post" class="login-form">
                <div class="form-group">
                    <label for="email">邮箱：</label>
                    <input type="email" id="email" name="email" required 
                           placeholder="请输入邮箱地址"
                           value="<%= request.getParameter("email") != null ? request.getParameter("email") : "" %>">
                </div>
                
                <div class="form-group">
                    <label for="password">密码：</label>
                    <input type="password" id="password" name="password" required 
                           placeholder="请输入密码">
                </div>
                
                <div class="form-group">
                    <button type="submit" class="btn btn-primary">登录</button>
                </div>
            </form>
            
            <div class="form-footer">
                <p>还没有账号？ <a href="<%= request.getContextPath() %>/auth/register">立即注册</a></p>
            </div>
        </div>
    </div>
    
    <script src="<%= request.getContextPath() %>/js/main.js"></script>
</body>
</html>
