<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Error - TA Recruitment System</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>
    <div class="container">
        <div class="error-container">
            <div class="error-icon">⚠️</div>
            <h1>Something Went Wrong</h1>
            
            <% 
                String errorMessage = (String) request.getAttribute("errorMessage");
                if (errorMessage == null || errorMessage.isEmpty()) {
                    errorMessage = (String) request.getAttribute("error");
                }
                if (errorMessage == null || errorMessage.isEmpty()) {
                    errorMessage = "An unknown error occurred. Please try again later.";
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
                    <p>Error Code: <%= statusCode %></p>
                </div>
            <% } %>
            
            <div class="error-actions">
                <button onclick="history.back()" class="btn btn-secondary">Go Back</button>
                <a href="<%= request.getContextPath() %>/" class="btn btn-primary">Go to Home</a>
            </div>
        </div>
    </div>
    
    <script src="<%= request.getContextPath() %>/js/main.js"></script>
</body>
</html>
