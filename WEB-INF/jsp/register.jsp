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
                           placeholder="请输入密码（至少8位，包含字母和数字）"
                           minlength="8">
                    <div id="password-strength" class="password-strength"></div>
                    <small>密码必须至少8位，包含字母和数字</small>
                </div>
                
                <div class="form-group">
                    <label for="confirmPassword">确认密码：<span class="required">*</span></label>
                    <input type="password" id="confirmPassword" name="confirmPassword" required 
                           placeholder="请再次输入密码"
                           minlength="8">
                </div>
                
                <div class="form-group">
                    <label for="role">角色：<span class="required">*</span></label>
                    <select id="role" name="role" required>
                        <option value="">请选择角色</option>
                        <option value="TA" <%= "TA".equals(request.getParameter("role")) ? "selected" : "" %>>助教 (TA)</option>
                        <option value="MO" <%= "MO".equals(request.getParameter("role")) ? "selected" : "" %>>模块负责人 (MO)</option>
                        <option value="ADMIN" <%= "ADMIN".equals(request.getParameter("role")) ? "selected" : "" %>>管理员 (Admin)</option>
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
                <p>已有账号？ <a href="<%= request.getContextPath() %>/auth/login">立即登录</a></p>
            </div>
        </div>
    </div>
    
    <script src="<%= request.getContextPath() %>/js/main.js"></script>
    <script>
        // 密码强度检查
        document.getElementById('password').addEventListener('input', function() {
            var password = this.value;
            var strengthDiv = document.getElementById('password-strength');
            
            if (password.length === 0) {
                strengthDiv.innerHTML = '';
                return;
            }
            
            var strength = checkPasswordStrength(password);
            var strengthText = '';
            var strengthClass = '';
            
            switch(strength) {
                case '太弱':
                    strengthText = '密码强度：太弱';
                    strengthClass = 'strength-very-weak';
                    break;
                case '弱':
                    strengthText = '密码强度：弱';
                    strengthClass = 'strength-weak';
                    break;
                case '中':
                    strengthText = '密码强度：中等';
                    strengthClass = 'strength-medium';
                    break;
                case '强':
                    strengthText = '密码强度：强';
                    strengthClass = 'strength-strong';
                    break;
            }
            
            strengthDiv.innerHTML = '<span class="' + strengthClass + '">' + strengthText + '</span>';
        });
        
        // 密码强度检查函数
        function checkPasswordStrength(password) {
            if (password.length < 6) {
                return '太弱';
            }
            if (password.length < 8) {
                return '弱';
            }
            
            var score = 0;
            if (/[a-z]/.test(password)) score++;
            if (/[A-Z]/.test(password)) score++;
            if (/\d/.test(password)) score++;
            if (/[!@#$%^&*]/.test(password)) score++;
            
            if (score < 2) return '弱';
            if (score < 3) return '中';
            return '强';
        }
        
        // 客户端密码确认验证
        document.querySelector('.register-form').addEventListener('submit', function(e) {
            var password = document.getElementById('password').value;
            var confirmPassword = document.getElementById('confirmPassword').value;
            
            // 密码强度检查
            if (password.length < 8) {
                e.preventDefault();
                alert('密码必须至少8位！');
                return false;
            }
            
            if (!/[a-zA-Z]/.test(password) || !/\d/.test(password)) {
                e.preventDefault();
                alert('密码必须包含字母和数字！');
                return false;
            }
            
            // 密码确认检查
            if (password !== confirmPassword) {
                e.preventDefault();
                alert('两次输入的密码不一致，请重新输入！');
                return false;
            }
        });
    </script>
</body>
</html>
