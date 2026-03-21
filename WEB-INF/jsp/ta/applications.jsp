<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.bupt.tarecruitment.model.User" %>
<%@ page import="com.bupt.tarecruitment.model.Application" %>
<%@ page import="com.bupt.tarecruitment.model.ApplicationStatus" %>
<%@ page import="com.bupt.tarecruitment.model.Position" %>
<%@ page import="com.bupt.tarecruitment.dao.PositionDAO" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%
    User currentUser = (User) session.getAttribute("user");
    if (currentUser == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    
    @SuppressWarnings("unchecked")
    List<Application> applications = (List<Application>) request.getAttribute("applications");
    String errorMessage = (String) request.getAttribute("errorMessage");
    
    // 从session获取消息
    String successMessage = (String) session.getAttribute("successMessage");
    String sessionErrorMessage = (String) session.getAttribute("errorMessage");
    
    // 清除session中的消息（显示一次后删除�?
    if (successMessage != null) {
        session.removeAttribute("successMessage");
    }
    if (sessionErrorMessage != null) {
        session.removeAttribute("errorMessage");
        if (errorMessage == null) {
            errorMessage = sessionErrorMessage;
        }
    }
    
    // 创建PositionDAO实例用于查询职位信息
    PositionDAO positionDAO = new PositionDAO();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>我的申请 - TA招聘系统</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>
    <header>
        <h1>TA招聘系统</h1>
    </header>
    
    <nav>
        <ul>
            <li><a href="<%= request.getContextPath() %>/ta/dashboard">仪表�?/a></li>
            <li><a href="<%= request.getContextPath() %>/ta/profile">个人资料</a></li>
            <li><a href="<%= request.getContextPath() %>/ta/positions">浏览职位</a></li>
            <li><a href="<%= request.getContextPath() %>/ta/applications/my">我的申请</a></li>
            <li><a href="<%= request.getContextPath() %>/auth/logout">登出</a></li>
        </ul>
    </nav>
    
    <div class="container">
        <h2>我的申请</h2>
        
        <% if (successMessage != null && !successMessage.isEmpty()) { %>
            <div class="alert alert-success">
                <%= successMessage %>
            </div>
        <% } %>
        
        <% if (errorMessage != null && !errorMessage.isEmpty()) { %>
            <div class="alert alert-error">
                <%= errorMessage %>
            </div>
        <% } %>
        
        <% if (applications == null || applications.isEmpty()) { %>
            <div class="card">
                <p class="text-center">您还没有提交任何申请�?/p>
                <div class="text-center mt-20">
                    <a href="<%= request.getContextPath() %>/ta/positions" class="btn btn-primary">浏览职位</a>
                </div>
            </div>
        <% } else { %>
            <% for (Application app : applications) { %>
                <%
                    // 获取职位信息
                    Position position = positionDAO.findById(app.getPositionId());
                    if (position == null) continue; // 如果职位不存在，跳过
                    
                    // 确定状态的中文显示和样�?
                    String statusText = "";
                    String statusClass = "";
                    switch (app.getStatus()) {
                        case PENDING:
                            statusText = "待审�?;
                            statusClass = "badge-pending";
                            break;
                        case SELECTED:
                            statusText = "已选中";
                            statusClass = "badge-selected";
                            break;
                        case REJECTED:
                            statusText = "已拒�?;
                            statusClass = "badge-rejected";
                            break;
                        case WITHDRAWN:
                            statusText = "已撤�?;
                            statusClass = "badge-closed";
                            break;
                    }
                %>
                <div class="card">
                    <h3><%= position.getTitle() %></h3>
                    
                    <div style="margin: 15px 0;">
                        <p><strong>职位描述�?/strong></p>
                        <p><%= position.getDescription() != null ? position.getDescription() : "�? %></p>
                    </div>
                    
                    <div style="margin: 15px 0;">
                        <p><strong>职位要求�?/strong></p>
                        <p><%= position.getRequirements() != null && !position.getRequirements().isEmpty() ? position.getRequirements() : "无特殊要�? %></p>
                    </div>
                    
                    <div style="margin: 15px 0;">
                        <p><strong>工作时长�?/strong> <%= position.getHours() %> 小时/�?/p>
                        <p><strong>申请时间�?/strong> <%= app.getAppliedAt() != null ? dateFormat.format(app.getAppliedAt()) : "未知" %></p>
                        <p><strong>申请状态：</strong> 
                            <span class="badge <%= statusClass %>">
                                <%= statusText %>
                            </span>
                        </p>
                        <% if (currentUser.getCvPath() != null && !currentUser.getCvPath().trim().isEmpty()) { %>
                            <p><strong>我的简历：</strong> 
                                <a href="<%= request.getContextPath() %>/cv/download?userId=<%= currentUser.getUserId() %>" 
                                   class="btn btn-sm btn-secondary" target="_blank">
                                    下载简�?
                                </a>
                            </p>
                        <% } else { %>
                            <p><strong>我的简历：</strong> 
                                <span style="color: #e74c3c;">未上�?/span>
                                <a href="<%= request.getContextPath() %>/ta/profile" class="btn btn-sm btn-primary">
                                    去上�?
                                </a>
                            </p>
                        <% } %>
                    </div>
                    
                    <% if (app.getStatus() == ApplicationStatus.PENDING) { %>
                        <form action="<%= request.getContextPath() %>/ta/applications/withdraw" method="post" style="display: inline;">
                            <input type="hidden" name="applicationId" value="<%= app.getApplicationId() %>">
                            <button type="submit" class="btn btn-danger" 
                                    onclick="return confirm('确定要撤回这个申请吗？撤回后将无法恢复�?);">
                                撤回申请
                            </button>
                        </form>
                    <% } else { %>
                        <p style="color: #7f8c8d; font-style: italic;">
                            <% if (app.getStatus() == ApplicationStatus.SELECTED) { %>
                                恭喜！您已被选中担任此职位�?
                            <% } else if (app.getStatus() == ApplicationStatus.REJECTED) { %>
                                很遗憾，您的申请未被选中�?
                            <% } else if (app.getStatus() == ApplicationStatus.WITHDRAWN) { %>
                                您已撤回此申请�?
                            <% } %>
                        </p>
                    <% } %>
                </div>
            <% } %>
        <% } %>
        
        <div class="text-center mt-20">
            <a href="<%= request.getContextPath() %>/ta/dashboard" class="btn btn-secondary">返回仪表�?/a>
        </div>
    </div>
    
    <script src="<%= request.getContextPath() %>/js/main.js"></script>
</body>
</html>
