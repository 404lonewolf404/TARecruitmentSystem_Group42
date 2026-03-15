<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.bupt.tarecruitment.model.User" %>
<%@ page import="com.bupt.tarecruitment.model.Position" %>
<%@ page import="java.util.List" %>
<%
    User currentUser = (User) session.getAttribute("user");
    if (currentUser == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    
    @SuppressWarnings("unchecked")
    List<Position> positions = (List<Position>) request.getAttribute("positions");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>我的职位 - TA招聘系统</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>
    <header>
        <h1>TA招聘系统</h1>
    </header>
    
    <nav>
        <ul>
            <li><a href="<%= request.getContextPath() %>/mo/dashboard">仪表板</a></li>
            <li><a href="<%= request.getContextPath() %>/mo/positions/my" class="active">我的职位</a></li>
            <li><a href="<%= request.getContextPath() %>/mo/positions/create">创建职位</a></li>
            <li><a href="<%= request.getContextPath() %>/auth/logout">登出</a></li>
        </ul>
    </nav>
    
    <div class="container">
        <div class="card">
            <h2>我的职位</h2>
            <p>管理您发布的助教职位</p>
        </div>
        
        <% if (positions == null || positions.isEmpty()) { %>
            <div class="card">
                <p class="info-message">您还没有发布任何职位。</p>
                <a href="<%= request.getContextPath() %>/mo/positions/create" class="btn btn-primary">创建第一个职位</a>
            </div>
        <% } else { %>
            <div class="positions-list">
                <% for (Position position : positions) { %>
                    <div class="position-card">
                        <div class="position-header">
                            <h3><%= position.getTitle() %></h3>
                            <span class="badge badge-<%= position.getStatus().toString().toLowerCase() %>">
                                <%= position.getStatus() == com.bupt.tarecruitment.model.PositionStatus.OPEN ? "开放" : "关闭" %>
                            </span>
                        </div>
                        
                        <div class="position-details">
                            <p><strong>职位ID：</strong><%= position.getPositionId() %></p>
                            <p><strong>描述：</strong><%= position.getDescription() %></p>
                            <% if (position.getRequirements() != null && !position.getRequirements().trim().isEmpty()) { %>
                                <p><strong>要求：</strong><%= position.getRequirements() %></p>
                            <% } %>
                            <p><strong>工作时长：</strong><%= position.getHours() %> 小时/周</p>
                        </div>
                        
                        <div class="position-actions">
                            <a href="<%= request.getContextPath() %>/mo/applications/position?positionId=<%= position.getPositionId() %>" 
                               class="btn btn-secondary">查看申请</a>
                            <form method="post" action="<%= request.getContextPath() %>/mo/positions/delete" 
                                  style="display: inline;" 
                                  onsubmit="return confirm('确定要删除此职位吗？这将同时删除所有相关申请。');">
                                <input type="hidden" name="positionId" value="<%= position.getPositionId() %>">
                                <button type="submit" class="btn btn-danger">删除职位</button>
                            </form>
                        </div>
                    </div>
                <% } %>
            </div>
        <% } %>
    </div>
    
    <script src="<%= request.getContextPath() %>/js/main.js"></script>
</body>
</html>
