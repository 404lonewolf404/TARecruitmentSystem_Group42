<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>注册 - TA招聘系统</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>
    <div class="container">
        <div class="form-container">
            <h1>TA招聘系统</h1>
            <h2>注册新账号</h2>
            
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
            
            <form action="<%= request.getContextPath() %>/auth/register" method="post" class="register-form">
                <div class="form-group">
                    <label for="name">姓名：<span class="required">*</span></label>
                    <input type="text" id="name" name="name" required 
                           placeholder="请输入姓名"
                           value="<%= request.getParameter("name") != null ? request.getParameter("name") : "" %>">
                </div>
                
                <div class="form-group">
                    <label for="email">邮箱：<span class="required">*</span></label>
                    <input type="email" id="email" name="email" required 
                           placeholder="请输入邮箱地址"
                           value="<%= request.getParameter("email") != null ? request.getParameter("email") : "" %>">
                </div>
                
                <div class="form-group">
                    <label for="password">密码：<span class="required">*</span></label>
                    <input type="password" id="password" name="password" required 
                           placeholder="请输入密码（至少6位）"
                           minlength="6">
                </div>
                
                <div class="form-group">
                    <label for="confirmPassword">确认密码：<span class="required">*</span></label>
                    <input type="password" id="confirmPassword" name="confirmPassword" required 
                           placeholder="请再次输入密码"
                           minlength="6">
                </div>
                
                <div class="form-group">
                    <label for="role">角色：<span class="required">*</span></label>
                    <select id="role" name="role" required>
                        <option value="">请选择角色</option>
                        <option value="TA" <%= "TA".equals(request.getParameter("role")) ? "selected" : "" %>>助教 (TA)</option>
                        <option value="MO" <%= "MO".equals(request.getParameter("role")) ? "selected" : "" %>>模块负责人(MO)</option>
                        <option value="ADMIN" <%= "ADMIN".equals(request.getParameter("role")) ? "selected" : "" %>>管理员(Admin)</option>
                    </select>
                </div>
                
                <div class="form-group">
                    <label for="skills">技能：</label>
                    <textarea id="skills" name="skills" rows="4" 
                              placeholder="请输入您的技能和经验（选填）"><%= request.getParameter("skills") != null ? request.getParameter("skills") : "" %></textarea>
                    <small>例如：Java编程、数据库管理、Web开发等</small>
                </div>
                
                <div class="form-group">
                    <button type="submit" class="btn btn-primary">注册</button>
                </div>
            </form>
            
            <div class="form-footer">
                <p>已有账号？<a href="<%= request.getContextPath() %>/auth/login">立即登录</a></p>
            </div>
        </div>
    </div>
    
    <script src="<%= request.getContextPath() %>/js/main.js"></script>
    <script>
        // 客户端密码确认验�?
        document.querySelector('.register-form').addEventListener('submit', function(e) {
            var password = document.getElementById('password').value;
            var confirmPassword = document.getElementById('confirmPassword').value;
            
            if (password !== confirmPassword) {
                e.preventDefault();
                alert('两次输入的密码不一致，请重新输入！');
                return false;
            }
        });
    </script>
</body>
</html>
