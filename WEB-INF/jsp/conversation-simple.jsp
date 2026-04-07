<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.bupt.tarecruitment.model.User" %>
<%@ page import="com.bupt.tarecruitment.model.Application" %>
<%@ page import="com.bupt.tarecruitment.model.Position" %>
<%@ page import="com.bupt.tarecruitment.model.Message" %>
<%@ page import="com.bupt.tarecruitment.model.UserRole" %>
<%@ page import="com.bupt.tarecruitment.model.ApplicationStatus" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%
    User currentUser = (User) session.getAttribute("user");
    if (currentUser == null) {
        response.sendRedirect(request.getContextPath() + "/auth/login");
        return;
    }
    
    Application app = (Application) request.getAttribute("application");
    Position position = (Position) request.getAttribute("position");
    List messages = (List) request.getAttribute("messages");
    User ta = (User) request.getAttribute("ta");
    User mo = (User) request.getAttribute("mo");
    
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>对话 - TA招聘系统</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>
    <header>
        <h1>TA招聘系统 - 对话</h1>
    </header>
    
    <div class="container">
        <h2>职位：<%= position != null ? position.getTitle() : "未知" %></h2>
        <p>TA: <%= ta != null ? ta.getName() : "未知" %></p>
        <p>MO: <%= mo != null ? mo.getName() : "未知" %></p>
        
        <div style="border: 1px solid #ccc; padding: 20px; margin: 20px 0;">
            <h3>消息列表</h3>
            <% if (messages == null || messages.isEmpty()) { %>
                <p>还没有消息</p>
            <% } else { %>
                <% for (Object obj : messages) {
                    Message msg = (Message) obj;
                %>
                    <div style="margin: 10px 0; padding: 10px; background: #f5f5f5;">
                        <strong><%= msg.getSenderRole() == UserRole.TA ? ta.getName() : mo.getName() %></strong>
                        <p><%= msg.getContent() %></p>
                        <small><%= dateFormat.format(msg.getSentAt()) %></small>
                    </div>
                <% } %>
            <% } %>
        </div>
        
        <form action="<%= request.getContextPath() %>/messages/send" method="post">
            <input type="hidden" name="applicationId" value="<%= app.getApplicationId() %>">
            <textarea name="content" rows="3" style="width: 100%;" required></textarea>
            <button type="submit">发送</button>
        </form>
        
        <% if (currentUser.getRole() == UserRole.TA) { %>
            <p><a href="<%= request.getContextPath() %>/ta/applications/my">返回我的申请</a></p>
        <% } else if (currentUser.getRole() == UserRole.MO) { %>
            <p><a href="<%= request.getContextPath() %>/mo/applications/position?positionId=<%= position.getPositionId() %>">返回申请列表</a></p>
        <% } %>
    </div>
</body>
</html>
