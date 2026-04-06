<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.bupt.tarecruitment.model.User" %>
<%@ page import="com.bupt.tarecruitment.model.Position" %>
<%@ page import="com.bupt.tarecruitment.service.NotificationService" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Set" %>
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
            <li>
                <a href="<%= request.getContextPath() %>/ta/notifications">
                    通知
                    <% if (unreadCount > 0) { %>
                        <span class="notification-badge"><%= unreadCount %></span>
                    <% } %>
                </a>
            </li>
            <li><a href="<%= request.getContextPath() %>/auth/logout">登出</a></li>
        </ul>
    </nav>
    
    <div class="container">
        <h2>可申请的职位</h2>
        
        <!-- 搜索面板 -->
        <div class="search-panel">
            <form method="get" action="<%= request.getContextPath() %>/ta/positions">
                <div class="search-row">
                    <input type="text" name="keyword" value="<%= request.getAttribute("keyword") != null ? request.getAttribute("keyword") : "" %>" 
                           placeholder="搜索职位标题、描述或要求..." class="search-input">
                    <button type="submit" class="btn btn-primary">搜索</button>
                </div>
                
                <div class="filter-row">
                    <label>
                        工时范围: 
                        <input type="number" name="minHours" value="<%= request.getAttribute("minHours") != null ? request.getAttribute("minHours") : "" %>" 
                               min="0" max="40" placeholder="最少"> - 
                        <input type="number" name="maxHours" value="<%= request.getAttribute("maxHours") != null ? request.getAttribute("maxHours") : "" %>" 
                               min="0" max="40" placeholder="最多">
                    </label>
                    
                    <label>
                        排序: 
                        <select name="sortBy">
                            <option value="newest" <%= "newest".equals(request.getAttribute("sortBy")) ? "selected" : "" %>>最新发布</option>
                            <option value="hours_asc" <%= "hours_asc".equals(request.getAttribute("sortBy")) ? "selected" : "" %>>工时从低到高</option>
                            <option value="hours_desc" <%= "hours_desc".equals(request.getAttribute("sortBy")) ? "selected" : "" %>>工时从高到低</option>
                        </select>
                    </label>
                    
                    <a href="<%= request.getContextPath() %>/ta/positions" class="btn btn-secondary">清除</a>
                </div>
            </form>
            
            <% 
            String keyword = (String) request.getAttribute("keyword");
            Integer minHours = (Integer) request.getAttribute("minHours");
            Integer maxHours = (Integer) request.getAttribute("maxHours");
            boolean hasFilters = (keyword != null && !keyword.trim().isEmpty()) || minHours != null || maxHours != null;
            
            if (hasFilters && positions != null) { 
            %>
                <p class="search-result">
                    找到 <%= positions.size() %> 个职位
                    <% if (keyword != null && !keyword.trim().isEmpty()) { %>
                        (关键词: "<%= keyword %>")
                    <% } %>
                    <% if (minHours != null || maxHours != null) { %>
                        (工时: <%= minHours != null ? minHours : "0" %>-<%= maxHours != null ? maxHours : "∞" %>)
                    <% } %>
                </p>
            <% } %>
        </div>
        
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
                        <p><strong>招聘名额：</strong> <%= position.getMaxPositions() %> 人</p>
                        <p><strong>状态：</strong> 
                            <span class="badge badge-<%= position.getStatus().toString().toLowerCase() %>">
                                <%= position.getStatus() == com.bupt.tarecruitment.model.PositionStatus.OPEN ? "开放" : "关闭" %>
                            </span>
                        </p>
                        
                        <%-- V3.2: 显示截止日期和剩余天数 --%>
                        <% if (position.getDeadline() != null) { %>
                            <p><strong>申请截止：</strong>
                                <%= new java.text.SimpleDateFormat("yyyy-MM-dd").format(position.getDeadline()) %>
                                <% 
                                int daysRemaining = position.getDaysRemaining();
                                if (daysRemaining > 0) { 
                                    String urgencyColor = daysRemaining <= 3 ? "#dc3545" : (daysRemaining <= 7 ? "#ffc107" : "#28a745");
                                %>
                                    <span style="color: <%= urgencyColor %>; font-weight: bold;">
                                        (还剩 <%= daysRemaining %> 天)
                                    </span>
                                <% } else if (position.isExpired()) { %>
                                    <span style="color: #dc3545; font-weight: bold;">(已过期)</span>
                                <% } %>
                            </p>
                        <% } %>
                    </div>
                    
                    <% 
                    boolean hasApplied = appliedPositionIds != null && appliedPositionIds.contains(position.getPositionId());
                    boolean canApply = position.canAcceptApplications(); // V3.2: 检查是否可以申请
                    
                    if (hasApplied) { 
                    %>
                        <button type="button" class="btn btn-secondary" disabled>
                            已申请
                        </button>
                    <% } else if (!canApply) { %>
                        <%-- V3.2: 职位已关闭或过期 --%>
                        <div style="padding: 10px; background-color: #f8d7da; border-left: 4px solid #dc3545; border-radius: 4px; margin-bottom: 10px;">
                            <p style="margin: 0; color: #721c24;">
                                <strong>⚠️ 此职位暂不接受申请</strong><br>
                                <% if (position.isExpired()) { %>
                                    原因：申请已截止
                                <% } else if (position.getStatus() == com.bupt.tarecruitment.model.PositionStatus.CLOSED) { %>
                                    原因：职位已关闭
                                <% } %>
                            </p>
                        </div>
                        <button type="button" class="btn btn-secondary" disabled>
                            无法申请
                        </button>
                    <% } else { %>
                        <form action="<%= request.getContextPath() %>/ta/applications/apply" method="post" enctype="multipart/form-data" style="margin-top: 15px;">
                            <input type="hidden" name="positionId" value="<%= position.getPositionId() %>">
                            
                            <div style="margin-bottom: 15px;">
                                <label style="display: block; margin-bottom: 5px; font-weight: bold;">选择简历：</label>
                                
                                <div style="margin-bottom: 10px;">
                                    <label style="display: block; cursor: pointer;">
                                        <input type="radio" name="resumeChoice" value="existing" checked onchange="toggleResumeUpload(this)">
                                        使用已上传的简历
                                        <% if (currentUser.getCvPath() == null || currentUser.getCvPath().trim().isEmpty()) { %>
                                            <span style="color: #e74c3c; font-size: 0.9em;">(您还没有上传简历)</span>
                                        <% } %>
                                    </label>
                                </div>
                                
                                <div style="margin-bottom: 10px;">
                                    <label style="display: block; cursor: pointer;">
                                        <input type="radio" name="resumeChoice" value="new" onchange="toggleResumeUpload(this)">
                                        上传新简历
                                    </label>
                                </div>
                                
                                <div id="newResumeUpload_<%= position.getPositionId() %>" style="display: none; margin-top: 10px; padding: 10px; background-color: #f8f9fa; border-radius: 4px;">
                                    <label style="display: block; margin-bottom: 5px;">选择PDF文件：</label>
                                    <input type="file" name="newResume" accept=".pdf" style="display: block;">
                                    <small style="color: #666; display: block; margin-top: 5px;">仅支持PDF格式，最大10MB</small>
                                </div>
                            </div>
                            
                            <button type="submit" class="btn btn-success" 
                                    onclick="return validateResumeSelection(this.form);">
                                申请此职位
                            </button>
                        </form>
                        
                        <script>
                        function toggleResumeUpload(radio) {
                            var form = radio.form;
                            var positionId = form.querySelector('input[name="positionId"]').value;
                            var uploadDiv = document.getElementById('newResumeUpload_' + positionId);
                            
                            if (radio.value === 'new') {
                                uploadDiv.style.display = 'block';
                            } else {
                                uploadDiv.style.display = 'none';
                            }
                        }
                        
                        function validateResumeSelection(form) {
                            var resumeChoice = form.querySelector('input[name="resumeChoice"]:checked').value;
                            
                            if (resumeChoice === 'existing') {
                                <% if (currentUser.getCvPath() == null || currentUser.getCvPath().trim().isEmpty()) { %>
                                    alert('您还没有上传简历，请先在个人资料中上传简历或选择上传新简历');
                                    return false;
                                <% } %>
                            } else if (resumeChoice === 'new') {
                                var fileInput = form.querySelector('input[name="newResume"]');
                                if (!fileInput.files || fileInput.files.length === 0) {
                                    alert('请选择要上传的简历文件');
                                    return false;
                                }
                                
                                var file = fileInput.files[0];
                                if (!file.name.toLowerCase().endsWith('.pdf')) {
                                    alert('只支持PDF格式的简历文件');
                                    return false;
                                }
                                
                                if (file.size > 10 * 1024 * 1024) {
                                    alert('文件大小不能超过10MB');
                                    return false;
                                }
                            }
                            
                            return confirm('确定要申请这个职位吗？');
                        }
                        </script>
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
