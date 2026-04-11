<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.bupt.tarecruitment.model.Application" %>
<%@ page import="com.bupt.tarecruitment.model.ApplicationStatus" %>
<%@ page import="com.bupt.tarecruitment.model.User" %>
<%@ page import="com.bupt.tarecruitment.model.Position" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>申请管理 - TA招聘系统</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>
    <header>
        <h1>TA招聘系统</h1>
    </header>
    
    <nav>
        <ul>
            <li><a href="<%= request.getContextPath() %>/admin/dashboard">仪表板</a></li>
            <li><a href="<%= request.getContextPath() %>/admin/profile">个人资料</a></li>
            <li><a href="<%= request.getContextPath() %>/admin/users">用户管理</a></li>
            <li><a href="<%= request.getContextPath() %>/admin/positions">职位管理</a></li>
            <li><a href="<%= request.getContextPath() %>/admin/applications">申请管理</a></li>
            <li><a href="<%= request.getContextPath() %>/admin/workload">工作量报告</a></li>
            <li><a href="<%= request.getContextPath() %>/admin/notifications">通知</a></li>
            <li><a href="<%= request.getContextPath() %>/auth/logout">登出</a></li>
        </ul>
    </nav>

    <div class="container">
        <h1 style="text-align: center; margin-bottom: 30px;">申请管理</h1>

        <%
            List<Application> applications = (List<Application>) request.getAttribute("applications");
            List<User> users = (List<User>) request.getAttribute("users");
            List<Position> positions = (List<Position>) request.getAttribute("positions");
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            
            // 创建Map来快速查找
            java.util.Map<String, User> userMap = new java.util.HashMap<>();
            if (users != null) {
                for (User u : users) {
                    userMap.put(u.getUserId(), u);
                }
            }
            
            java.util.Map<String, Position> positionMap = new java.util.HashMap<>();
            if (positions != null) {
                for (Position p : positions) {
                    positionMap.put(p.getPositionId(), p);
                }
            }
        %>

        <div style="text-align: center; margin-bottom: 20px;">
            <p>共 <%= applications != null ? applications.size() : 0 %> 个申请</p>
        </div>

        <% if (applications != null && !applications.isEmpty()) { %>
            <table class="data-table" style="margin: 0 auto;">
                <thead>
                    <tr>
                        <th>申请人</th>
                        <th>职位名称</th>
                        <th>简历</th>
                        <th>状态</th>
                        <th>申请时间</th>
                    </tr>
                </thead>
                <tbody>
                    <% for (Application app : applications) { 
                        User applicant = userMap.get(app.getTaId());
                        String applicantName = (applicant != null) ? applicant.getName() + " (" + applicant.getEmail() + ")" : app.getTaId();
                        
                        Position position = positionMap.get(app.getPositionId());
                        String positionTitle = (position != null) ? position.getTitle() : app.getPositionId();
                    %>
                        <tr>
                            <td><%= applicantName %></td>
                            <td><%= positionTitle %></td>
                            <td><%= app.getResumePath() != null && !app.getResumePath().isEmpty() ? "已上传" : "未上传" %></td>
                            <td>
                                <span class="status-badge status-<%= app.getStatus() %>">
                                    <% 
                                        String statusText = "";
                                        if (app.getStatus() == ApplicationStatus.PENDING) {
                                            statusText = "待处理";
                                        } else if (app.getStatus() == ApplicationStatus.SELECTED) {
                                            statusText = "已选中";
                                        } else if (app.getStatus() == ApplicationStatus.REJECTED) {
                                            statusText = "已拒绝";
                                        } else if (app.getStatus() == ApplicationStatus.WITHDRAWN) {
                                            statusText = "已撤回";
                                        } else {
                                            statusText = app.getStatus().toString();
                                        }
                                    %>
                                    <%= statusText %>
                                </span>
                            </td>
                            <td><%= app.getAppliedAt() != null ? formatter.format(app.getAppliedAt()) : "-" %></td>
                        </tr>
                    <% } %>
                </tbody>
            </table>
        <% } else { %>
            <p style="text-align: center; color: #666;">暂无申请数据</p>
        <% } %>
    </div>
</body>
</html>
