<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.bupt.tarecruitment.model.User" %>
<%@ page import="com.bupt.tarecruitment.model.UserRole" %>
<%@ page import="com.bupt.tarecruitment.service.NotificationService" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%
    User currentUser = (User) session.getAttribute("user");
    if (currentUser == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    
    // 获取未读通知数量
    int unreadCount = 0;
    try {
        NotificationService notificationService = new NotificationService();
        unreadCount = notificationService.getUnreadCount(currentUser.getUserId());
    } catch (Exception e) {
        e.printStackTrace();
    }
    
    @SuppressWarnings("unchecked")
    List<User> users = (List<User>) request.getAttribute("users");
    
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>用户管理 - TA招聘系统</title>
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
            <li>
                <a href="<%= request.getContextPath() %>/admin/notifications">
                    通知
                    <% if (unreadCount > 0) { %>
                        <span class="notification-badge"><%= unreadCount %></span>
                    <% } %>
                </a>
            </li>
>>>>>>> de384c5c4bd4c5f2a574b2f75792ffc83db5658c
            <li><a href="<%= request.getContextPath() %>/auth/logout">登出</a></li>
        </ul>
    </nav>
    
    <div class="container">
        <div class="card">
            <h2>用户管理</h2>
            <p>查看系统中所有用户的详细信息</p>
        </div>
        
        <% if (users != null && !users.isEmpty()) { %>
            <div class="card">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>姓名</th>
                            <th>邮箱</th>
                            <th>角色</th>
                            <th>技能</th>
                            <th>简历状态</th>
                            <th>注册时间</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (User user : users) { %>
                        <tr>
                            <td><strong><%= user.getName() %></strong></td>
                            <td><%= user.getEmail() %></td>
                            <td>
                                <% 
                                String roleText = "";
                                String roleClass = "";
                                if (user.getRole() == UserRole.TA) {
                                    roleText = "助教(TA)";
                                    roleClass = "badge-selected";
                                } else if (user.getRole() == UserRole.MO) {
                                    roleText = "招聘官(MO)";
                                    roleClass = "badge-pending";
                                } else if (user.getRole() == UserRole.ADMIN) {
                                    roleText = "管理员";
                                    roleClass = "badge-rejected";
                                }
                                %>
                                <span class="badge <%= roleClass %>"><%= roleText %></span>
                            </td>
                            <td>
                                <% if (user.getRole() == UserRole.TA) { %>
                                    <%= user.getSkills() != null && !user.getSkills().isEmpty() ? user.getSkills() : "未填写" %>
                                <% } else { %>
                                    <span style="color: #7f8c8d;">-</span>
                                <% } %>
                            </td>
                            <td>
                                <% if (user.getRole() == UserRole.TA) { %>
                                    <% if (user.getCvPath() != null && !user.getCvPath().isEmpty()) { %>
                                        <span style="color: #27ae60;">✓ 已上传</span>
                                    <% } else { %>
                                        <span style="color: #e74c3c;">✗ 未上传</span>
                                    <% } %>
                                <% } else { %>
                                    <span style="color: #7f8c8d;">-</span>
                                <% } %>
                            </td>
                            <td><%= user.getCreatedAt() != null ? dateFormat.format(user.getCreatedAt()) : "-" %></td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
                
                <div style="margin-top: 20px; padding: 15px; background-color: #f8f9fa; border-radius: 4px;">
                    <p style="margin: 0; color: #666; font-size: 14px;">
                        <strong>说明：</strong>
                        技能列：仅TA需要填写 | 
                        简历状态列：仅TA需要上传简历
                    </p>
                </div>
            </div>
        <% } else { %>
            <div class="card">
                <p class="no-data">暂无用户数据</p>
            </div>
        <% } %>
        
        <div class="actions">
            <a href="<%= request.getContextPath() %>/admin/dashboard" class="btn btn-secondary">返回仪表板</a>
        </div>
    </div>
    
    <script src="<%= request.getContextPath() %>/js/main.js"></script>
</body>
</html>
