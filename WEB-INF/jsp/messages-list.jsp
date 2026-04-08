<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.bupt.tarecruitment.model.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%
    User currentUser = (User) session.getAttribute("user");
    if (currentUser == null) {
        response.sendRedirect(request.getContextPath() + "/auth/login");
        return;
    }
    
    List conversations = (List) request.getAttribute("conversations");
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>消息列表 - TA招聘系统</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
    <style>
        .conversation-list {
            max-width: 900px;
            margin: 20px auto;
        }
        
        .conversation-item {
            background: white;
            border: 1px solid #ddd;
            border-radius: 8px;
            padding: 15px;
            margin-bottom: 15px;
            display: flex;
            align-items: center;
            cursor: pointer;
            transition: all 0.3s;
        }
        
        .conversation-item:hover {
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
            transform: translateY(-2px);
        }
        
        .conversation-avatar {
            width: 50px;
            height: 50px;
            border-radius: 50%;
            background: #3498db;
            color: white;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 20px;
            margin-right: 15px;
            flex-shrink: 0;
        }
        
        .conversation-content {
            flex: 1;
            min-width: 0;
        }
        
        .conversation-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 5px;
        }
        
        .conversation-name {
            font-weight: bold;
            font-size: 16px;
            color: #2c3e50;
        }
        
        .conversation-time {
            font-size: 12px;
            color: #999;
        }
        
        .conversation-preview {
            color: #666;
            font-size: 14px;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
        }
        
        .conversation-meta {
            display: flex;
            align-items: center;
            gap: 10px;
            margin-top: 5px;
        }
        
        .conversation-badge {
            background: #e74c3c;
            color: white;
            border-radius: 10px;
            padding: 2px 8px;
            font-size: 12px;
            font-weight: bold;
        }
        
        .conversation-status {
            font-size: 12px;
            padding: 2px 8px;
            border-radius: 4px;
        }
        
        .status-pending { background: #f39c12; color: white; }
        .status-selected { background: #27ae60; color: white; }
        .status-rejected { background: #e74c3c; color: white; }
        
        .empty-state {
            text-align: center;
            padding: 60px 20px;
            color: #999;
        }
        
        .empty-state-icon {
            font-size: 64px;
            margin-bottom: 20px;
        }
    </style>
</head>
<body>
    <header>
        <h1>TA招聘系统</h1>
    </header>
    
    <nav>
        <ul>
            <% if (currentUser.getRole() == UserRole.TA) { %>
                <li><a href="<%= request.getContextPath() %>/ta/dashboard">仪表板</a></li>
                <li><a href="<%= request.getContextPath() %>/ta/profile">个人资料</a></li>
                <li><a href="<%= request.getContextPath() %>/ta/positions">浏览职位</a></li>
                <li><a href="<%= request.getContextPath() %>/ta/applications/my">我的申请</a></li>
                <li><a href="<%= request.getContextPath() %>/messages/list" class="active">💬 消息</a></li>
                <li><a href="<%= request.getContextPath() %>/ta/notifications">通知</a></li>
            <% } else { %>
                <li><a href="<%= request.getContextPath() %>/mo/dashboard">仪表板</a></li>
                <li><a href="<%= request.getContextPath() %>/mo/profile">个人资料</a></li>
                <li><a href="<%= request.getContextPath() %>/mo/positions/my">我的职位</a></li>
                <li><a href="<%= request.getContextPath() %>/mo/positions/create">创建职位</a></li>
                <li><a href="<%= request.getContextPath() %>/messages/list" class="active">💬 消息</a></li>
                <li><a href="<%= request.getContextPath() %>/mo/notifications">通知</a></li>
            <% } %>
            <li><a href="<%= request.getContextPath() %>/auth/logout">登出</a></li>
        </ul>
    </nav>
    
    <div class="container">
        <h2>💬 消息列表</h2>
        
        <div class="conversation-list">
            <% if (conversations == null || conversations.isEmpty()) { %>
                <div class="empty-state">
                    <div class="empty-state-icon">💬</div>
                    <p>暂无消息</p>
                    <p style="font-size: 14px;">申请职位后可以与MO进行对话</p>
                </div>
            <% } else { %>
                <% for (Object obj : conversations) {
                    Map conv = (Map) obj;
                    Application app = (Application) conv.get("application");
                    Position position = (Position) conv.get("position");
                    User ta = (User) conv.get("ta");
                    User mo = (User) conv.get("mo");
                    Integer unreadCount = (Integer) conv.get("unreadCount");
                    Integer messageCount = (Integer) conv.get("messageCount");
                    Message lastMessage = (Message) conv.get("lastMessage");
                    
                    String otherUserName = currentUser.getRole() == UserRole.TA ? mo.getName() : ta.getName();
                    String statusClass = "";
                    String statusText = "";
                    
                    if (app.getStatus() == ApplicationStatus.PENDING) {
                        statusClass = "status-pending";
                        statusText = "待审核";
                    } else if (app.getStatus() == ApplicationStatus.SELECTED) {
                        statusClass = "status-selected";
                        statusText = "已选中";
                    } else if (app.getStatus() == ApplicationStatus.REJECTED) {
                        statusClass = "status-rejected";
                        statusText = "已拒绝";
                    }
                %>
                    <div class="conversation-item" onclick="window.location.href='<%= request.getContextPath() %>/messages/conversation?applicationId=<%= app.getApplicationId() %>'">
                        <div class="conversation-avatar">
                            <%= otherUserName.substring(0, 1) %>
                        </div>
                        <div class="conversation-content">
                            <div class="conversation-header">
                                <span class="conversation-name"><%= otherUserName %> - <%= position.getTitle() %></span>
                                <span class="conversation-time">
                                    <%= lastMessage != null ? dateFormat.format(lastMessage.getSentAt()) : "" %>
                                </span>
                            </div>
                            <div class="conversation-preview">
                                <%= lastMessage != null ? lastMessage.getContent() : "暂无消息" %>
                            </div>
                            <div class="conversation-meta">
                                <span class="conversation-status <%= statusClass %>"><%= statusText %></span>
                                <% if (messageCount != null && messageCount > 0) { %>
                                    <span style="font-size: 12px; color: #999;"><%= messageCount %>条消息</span>
                                <% } %>
                                <% if (unreadCount != null && unreadCount > 0) { %>
                                    <span class="conversation-badge"><%= unreadCount %>条未读</span>
                                <% } %>
                            </div>
                        </div>
                    </div>
                <% } %>
            <% } %>
        </div>
    </div>
</body>
</html>
