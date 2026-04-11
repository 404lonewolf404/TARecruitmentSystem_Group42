<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.bupt.tarecruitment.model.Position" %>
<%@ page import="com.bupt.tarecruitment.model.User" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>职位管理 - TA招聘系统</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>
    <header>
        <h1>TA招聘系统</h1>
    </header>
    
    <nav>
        <ul>
            <li><a href="<%= request.getContextPath() %>/admin/dashboard">仪表板</a></li>
<<<<<<< HEAD
            <li><a href="<%= request.getContextPath() %>/admin/profile">个人资料</a></li>
=======
>>>>>>> de384c5c4bd4c5f2a574b2f75792ffc83db5658c
            <li><a href="<%= request.getContextPath() %>/admin/users">用户管理</a></li>
            <li><a href="<%= request.getContextPath() %>/admin/positions">职位管理</a></li>
            <li><a href="<%= request.getContextPath() %>/admin/applications">申请管理</a></li>
            <li><a href="<%= request.getContextPath() %>/admin/workload">工作量报告</a></li>
<<<<<<< HEAD
            <li><a href="<%= request.getContextPath() %>/admin/notifications">通知</a></li>
=======
>>>>>>> de384c5c4bd4c5f2a574b2f75792ffc83db5658c
            <li><a href="<%= request.getContextPath() %>/auth/logout">登出</a></li>
        </ul>
    </nav>

    <div class="container">
        <h1 style="text-align: center; margin-bottom: 30px;">职位管理</h1>

        <%
            List<Position> positions = (List<Position>) request.getAttribute("positions");
            List<User> users = (List<User>) request.getAttribute("users");
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            
            // 创建一个Map来快速查找用户
            java.util.Map<String, User> userMap = new java.util.HashMap<>();
            if (users != null) {
                for (User u : users) {
                    userMap.put(u.getUserId(), u);
                }
            }
        %>

        <div style="text-align: center; margin-bottom: 20px;">
            <p>共 <%= positions != null ? positions.size() : 0 %> 个职位</p>
        </div>

        <% if (positions != null && !positions.isEmpty()) { %>
            <table class="data-table" style="margin: 0 auto;">
                <thead>
                    <tr>
                        <th>职位名称</th>
                        <th>描述</th>
                        <th>工作时长</th>
                        <th>招聘名额</th>
                        <th>要求</th>
                        <th>状态</th>
                        <th>发布者</th>
                        <th>创建时间</th>
                    </tr>
                </thead>
                <tbody>
                    <% for (Position position : positions) { 
                        User publisher = userMap.get(position.getMoId());
                        String publisherName = (publisher != null) ? publisher.getName() + " (" + publisher.getEmail() + ")" : position.getMoId();
                    %>
                        <tr>
                            <td><%= position.getTitle() %></td>
                            <td><%= position.getDescription() != null && !position.getDescription().isEmpty() ? position.getDescription() : "-" %></td>
                            <td><%= position.getHours() %> 小时/周</td>
                            <td><%= position.getMaxPositions() %></td>
                            <td><%= position.getRequirements() != null && !position.getRequirements().isEmpty() ? position.getRequirements() : "-" %></td>
                            <td>
                                <span class="status-badge status-<%= position.getStatus() %>">
                                    <%= position.getStatus() != null && position.getStatus().toString().equals("OPEN") ? "开放" : "关闭" %>
                                </span>
                            </td>
                            <td><%= publisherName %></td>
                            <td><%= position.getCreatedAt() != null ? formatter.format(position.getCreatedAt()) : "-" %></td>
                        </tr>
                    <% } %>
                </tbody>
            </table>
        <% } else { %>
            <p style="text-align: center; color: #666;">暂无职位数据</p>
        <% } %>
    </div>
</body>
</html>
