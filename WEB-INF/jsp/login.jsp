<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - TA Recruitment System</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        body {
            display: flex;
            align-items: center;
            justify-content: center;
            min-height: 100vh;
            margin: 0;
            position: relative;
            overflow: hidden;
        }
        
        /* 背景装饰圆形 */
        body::before,
        body::after {
            content: '';
            position: absolute;
            border-radius: 50%;
            opacity: 0.15;
            z-index: 0;
        }
        
        body::before {
            width: 500px;
            height: 500px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            top: -250px;
            left: -250px;
            animation: float1 20s ease-in-out infinite;
        }
        
        body::after {
            width: 400px;
            height: 400px;
            background: linear-gradient(135deg, #764ba2 0%, #667eea 100%);
            bottom: -200px;
            right: -200px;
            animation: float2 18s ease-in-out infinite;
        }
        
        @keyframes float1 {
            0%, 100% { transform: translate(0, 0) rotate(0deg); }
            33% { transform: translate(30px, -30px) rotate(120deg); }
            66% { transform: translate(-20px, 20px) rotate(240deg); }
        }
        
        @keyframes float2 {
            0%, 100% { transform: translate(0, 0) rotate(0deg); }
            33% { transform: translate(-40px, 30px) rotate(-120deg); }
            66% { transform: translate(30px, -25px) rotate(-240deg); }
        }
        
        .login-wrapper {
            width: 100%;
            max-width: 450px;
            padding: 20px;
            animation: fadeInUp 0.6s ease;
            position: relative;
            z-index: 1;
        }
        
        @keyframes fadeInUp {
            from {
                opacity: 0;
                transform: translateY(30px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }
        
        .form-container {
            position: relative;
            overflow: hidden;
        }
        
        .form-container::before {
            content: '';
            position: absolute;
            top: -50%;
            left: -50%;
            width: 200%;
            height: 200%;
            background: linear-gradient(45deg, transparent, rgba(255,255,255,0.1), transparent);
            transform: rotate(45deg);
            animation: shine 3s infinite;
        }
        
        @keyframes shine {
            0%, 100% { transform: translateX(-100%) translateY(-100%) rotate(45deg); }
            50% { transform: translateX(100%) translateY(100%) rotate(45deg); }
        }
        
        .logo-container {
            text-align: center;
            margin-bottom: 30px;
            animation: fadeIn 0.8s ease 0.2s both;
        }
        
        @keyframes fadeIn {
            from { opacity: 0; }
            to { opacity: 1; }
        }
        
        .logo-icon {
            font-size: 64px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            margin-bottom: 10px;
            display: inline-block;
            animation: bounce 2s ease-in-out infinite;
        }
        
        @keyframes bounce {
            0%, 100% { transform: translateY(0); }
            50% { transform: translateY(-10px); }
        }
        
        .logo-icon:hover {
            animation: bounce 0.5s ease-in-out infinite;
        }
        
        .form-container h1 {
            text-align: center;
            font-size: 28px;
            margin-bottom: 10px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            animation: fadeIn 0.8s ease 0.3s both;
        }
        
        .form-container h2 {
            text-align: center;
            font-size: 18px;
            color: #7f8c8d;
            font-weight: 400;
            margin-bottom: 30px;
            animation: fadeIn 0.8s ease 0.4s both;
        }
        
        .input-group {
            position: relative;
            margin-bottom: 25px;
            animation: fadeIn 0.8s ease both;
        }
        
        .input-group:nth-child(1) {
            animation-delay: 0.5s;
        }
        
        .input-group:nth-child(2) {
            animation-delay: 0.6s;
        }
        
        .input-group i {
            position: absolute;
            left: 15px;
            top: 50%;
            transform: translateY(-50%);
            color: #95a5a6;
            font-size: 18px;
            transition: all 0.3s ease;
        }
        
        .input-group input {
            padding-left: 50px;
            padding-right: 15px;
            height: 50px;
            font-size: 15px;
            width: 100%;
            max-width: 100%;
            transition: all 0.3s ease;
        }
        
        .input-group input:focus {
            border-color: #667eea;
            box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
            transform: translateY(-2px);
        }
        
        .input-group input:focus + i {
            color: #667eea;
            transform: translateY(-50%) scale(1.1);
        }
        
        .form-group {
            animation: fadeIn 0.8s ease 0.7s both;
        }
        
        .btn-primary {
            height: 50px;
            font-size: 16px;
            font-weight: 600;
            text-transform: uppercase;
            letter-spacing: 1px;
            transition: all 0.3s ease;
            position: relative;
            overflow: hidden;
        }
        
        .btn-primary::before {
            content: '';
            position: absolute;
            top: 50%;
            left: 50%;
            width: 0;
            height: 0;
            border-radius: 50%;
            background: rgba(255, 255, 255, 0.3);
            transform: translate(-50%, -50%);
            transition: width 0.6s, height 0.6s;
        }
        
        .btn-primary:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
        }
        
        .btn-primary:active::before {
            width: 300px;
            height: 300px;
        }
        
        .form-footer {
            text-align: center;
            margin-top: 25px;
            animation: fadeIn 0.8s ease 0.8s both;
        }
        
        .form-footer a {
            font-weight: 600;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            transition: all 0.3s ease;
            display: inline-block;
        }
        
        .form-footer a:hover {
            transform: translateX(3px);
        }
        
        /* 错误和成功消息动画 */
        .error-message,
        .success-message {
            animation: slideDown 0.4s ease;
        }
        
        @keyframes slideDown {
            from {
                opacity: 0;
                transform: translateY(-20px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }
    </style>
</head>
<body class="auth-page">
    <div class="login-wrapper">
        <div class="form-container">
            <div class="logo-container">
                <i class="fas fa-graduation-cap logo-icon"></i>
            </div>
            <h1>TA Recruitment System</h1>
            <h2>Welcome Back!</h2>
            
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
                <div class="input-group">
                    <input type="email" id="email" name="email" required 
                           placeholder="Enter your email address"
                           value="<%= request.getParameter("email") != null ? request.getParameter("email") : "" %>">
                    <i class="fas fa-envelope"></i>
                </div>
                
                <div class="input-group">
                    <input type="password" id="password" name="password" required 
                           placeholder="Enter your password">
                    <i class="fas fa-lock"></i>
                </div>
                
                <div class="form-group">
                    <button type="submit" class="btn btn-primary btn-full">
                        <i class="fas fa-sign-in-alt"></i> Login
                    </button>
                </div>
            </form>
            
            <div class="form-footer">
                <p>Don't have an account? <a href="<%= request.getContextPath() %>/auth/register"><i class="fas fa-user-plus"></i> Register Now</a></p>
            </div>
        </div>
    </div>
    
    <script src="<%= request.getContextPath() %>/js/main.js"></script>
</body>
</html>
