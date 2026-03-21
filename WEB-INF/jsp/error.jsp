<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>错误 - TA招聘系统</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>
    <div class="container">
        <div class="error-container">
            <div class="error-icon">⚠️</div>
            <h1>出错�?/h1>
            
            <% 
                String errorMessage = (String) request.getAttribute("errorMessage");
                if (errorMessage == null || errorMessage.isEmpty()) {
                    errorMessage = (String) request.getAttribute("error");
                }
                if (errorMessage == null || errorMessage.isEmpty()) {
                    errorMessage = "发生了一个未知错误，请稍后重试�?;
                }
            %>
            
            <div class="error-message-box">
                <p><%= errorMessage %></p>
            </div>
            
            <% 
                Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
                if (statusCode != null) {
            %>
                <div class="error-details">
                    <p>错误代码: <%= statusCode %></p>
                </div>
            <% } %>
            
            <div class="error-actions">
                <button onclick="history.back()" class="btn btn-secondary">返回上一�?/button>
                <a href="<%= request.getContextPath() %>/" class="btn btn-primary">返回首页</a>
            </div>
        </div>
    </div>
    
    <script src="<%= request.getContextPath() %>/js/main.js"></script>
</body>
</html>
