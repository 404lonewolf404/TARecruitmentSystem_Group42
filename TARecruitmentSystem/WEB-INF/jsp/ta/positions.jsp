<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.bupt.tarecruitment.model.User" %>
<%@ page import="com.bupt.tarecruitment.model.Position" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Set" %>
<%
    User currentUser = (User) session.getAttribute("user");
    if (currentUser == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    
    @SuppressWarnings("unchecked")
    List<Position> positions = (List<Position>) request.getAttribute("positions");
    @SuppressWarnings("unchecked")
    Set<String> appliedPositionIds = (Set<String>) request.getAttribute("appliedPositionIds");
    String errorMessage = (String) request.getAttribute("errorMessage");
    
    // 从session获取错误消息
    String sessionErrorMessage = (String) session.getAttribute("errorMessage");
    if (sessionErrorMessage != null) {
        session.removeAttribute("errorMessage");
        if (errorMessage == null) {
            errorMessage = sessionErrorMessage;
        }
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>浏览职位 - TA招聘系统</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>
    <header>
        <h1>TA招聘系统</h1>
    </header>
    
    <nav>
        <ul>
            <li><a href="<%= request.getContextPath() %>/ta/dashboard">仪表板</a></li>
            <li><a href="<%= request.getContextPath() %>/ta/profile">个人资料</a></li>
            <li><a href="<%= request.getContextPath() %>/ta/positions">浏览职位</a></li>
            <li><a href="<%= request.getContextPath() %>/ta/applications/my">我的申请</a></li>
            <li><a href="<%= request.getContextPath() %>/auth/logout">登出</a></li>
        </ul>
    </nav>
    
    <div class="container">
        <h2>可申请的职位</h2>
        
        <% if (errorMessage != null && !errorMessage.isEmpty()) { %>
            <div class="alert alert-error">
                <%= errorMessage %>
            </div>
        <% } %>
        
        <% if (positions == null || positions.isEmpty()) { %>
            <div class="card">
                <p class="text-center">暂无可申请的职位。</p>
            </div>
        <% } else { %>
            <% for (Position position : positions) { %>
                <div class="card">
                    <h3><%= position.getTitle() %></h3>
                    
                    <div style="margin: 15px 0;">
                        <p><strong>描述：</strong></p>
                        <p><%= position.getDescription() != null ? position.getDescription() : "无" %></p>
                    </div>
                    
                    <div style="margin: 15px 0;">
                        <p><strong>要求：</strong></p>
                        <p><%= position.getRequirements() != null && !position.getRequirements().isEmpty() ? position.getRequirements() : "无特殊要求" %></p>
                    </div>
                    
                    <div style="margin: 15px 0;">
                        <p><strong>工作时长：</strong> <%= position.getHours() %> 小时/周</p>
                        <p><strong>状态：</strong> 
                            <span class="badge badge-<%= position.getStatus().toString().toLowerCase() %>">
                                <%= position.getStatus() == com.bupt.tarecruitment.model.PositionStatus.OPEN ? "开放" : "关闭" %>
                            </span>
                        </p>
                    </div>
                    
                    <% 
                    boolean hasApplied = appliedPositionIds != null && appliedPositionIds.contains(position.getPositionId());
                    if (hasApplied) { 
                    %>
                        <button type="button" class="btn btn-secondary" disabled>
                            已申请
                        </button>
                    <% } else { %>
                        <form action="<%= request.getContextPath() %>/ta/applications/apply" method="post" style="display: inline;">
                            <input type="hidden" name="positionId" value="<%= position.getPositionId() %>">
                            <button type="submit" class="btn btn-success" 
                                    onclick="return confirm('确定要申请这个职位吗？');">
                                申请此职位
                            </button>
                        </form>
                    <% } %>
                </div>
            <% } %>
        <% } %>
        
        <div class="text-center mt-20">
            <a href="<%= request.getContextPath() %>/ta/dashboard" class="btn btn-secondary">返回仪表板</a>
        </div>
    </div>
    
    <script src="<%= request.getContextPath() %>/js/main.js"></script>
</body>
</html>
