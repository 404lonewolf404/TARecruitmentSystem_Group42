<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.bupt.tarecruitment.model.*" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%
    User currentUser = (User) session.getAttribute("user");
    if (currentUser == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    
    Application application = (Application) request.getAttribute("application");
    Position position = (Position) request.getAttribute("position");
    @SuppressWarnings("unchecked")
    List<Message> messages = (List<Message>) request.getAttribute("messages");
    User ta = (User) request.getAttribute("ta");
    User mo = (User) request.getAttribute("mo");
    
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    
    String errorMessage = (String) session.getAttribute("errorMessage");
    if (errorMessage != null) {
        session.removeAttribute("errorMessage");
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>对话 - TA招聘系统</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
    <style>
        .conversation-container {
            max-width: 900px;
            margin: 20px auto;
            background: white;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        
        .conversation-header {
            padding: 20px;
            border-bottom: 1px solid #e0e0e0;
            background: #f8f9fa;
            border-radius: 8px 8px 0 0;
        }
        
        .conversation-info {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 10px;
        }
        
        .conversation-title {
            font-size: 20px;
            font-weight: bold;
            color: #2c3e50;
            margin-bottom: 5px;
        }
        
        .conversation-participants {
            font-size: 14px;
            color: #666;
        }
        
        .messages-container {
            padding: 20px;
            max-height: 500px;
            overflow-y: auto;
            background: #fafafa;
        }
        
        .message {
            margin-bottom: 15px;
            display: flex;
            flex-direction: column;
        }
        
        .message.sent {
            align-items: flex-end;
        }
        
        .message.received {
            align-items: flex-start;
        }
        
        .message-header {
            font-size: 12px;
            color: #666;
            margin-bottom: 5px;
        }
        
        .message-bubble {
            max-width: 70%;
            padding: 12px 16px;
            border-radius: 18px;
            word-wrap: break-word;
        }
        
        .message.sent .message-bubble {
            background: #3498db;
            color: white;
        }
        
        .message.received .message-bubble {
            background: white;
            color: #333;
            border: 1px solid #e0e0e0;
        }
        
        .message-time {
            font-size: 11px;
            color: #999;
            margin-top: 5px;
        }
        
        .message-form {
            padding: 20px;
            border-top: 1px solid #e0e0e0;
            background: white;
            border-radius: 0 0 8px 8px;
        }
        
        .message-input-container {
            display: flex;
            gap: 10px;
        }
        
        .message-input {
            flex: 1;
            padding: 12px;
            border: 1px solid #ddd;
            border-radius: 24px;
            font-size: 14px;
            resize: none;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }
        
        .message-input:focus {
            outline: none;
            border-color: #3498db;
        }
        
        .send-button {
            padding: 12px 24px;
            background: #3498db;
            color: white;
            border: none;
            border-radius: 24px;
            cursor: pointer;
            font-size: 14px;
            font-weight: bold;
            transition: background 0.3s;
        }
        
        .send-button:hover {
            background: #2980b9;
        }
        
        .back-link {
            display: inline-block;
            margin-bottom: 20px;
            color: #3498db;
            text-decoration: none;
        }
        
        .back-link:hover {
            text-decoration: underline;
        }
        
        .empty-messages {
            text-align: center;
            padding: 40px;
            color: #999;
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
<<<<<<< HEAD
                <li><a href="<%= request.getContextPath() %>/ta/favorites">⭐ 我的收藏</a></li>
                <li><a href="<%= request.getContextPath() %>/messages/list">💬 消息</a></li>
=======
>>>>>>> de384c5c4bd4c5f2a574b2f75792ffc83db5658c
                <li>
                    <a href="<%= request.getContextPath() %>/ta/notifications">
                        通知
                        <% 
                            Integer unreadCount = (Integer) request.getAttribute("unreadNotificationCount");
                            if (unreadCount != null && unreadCount > 0) { 
                        %>
                            <span class="notification-badge"><%= unreadCount %></span>
                        <% } %>
                    </a>
                </li>
            <% } else if (currentUser.getRole() == UserRole.MO) { %>
                <li><a href="<%= request.getContextPath() %>/mo/dashboard">仪表板</a></li>
                <li><a href="<%= request.getContextPath() %>/mo/profile">个人资料</a></li>
                <li><a href="<%= request.getContextPath() %>/mo/positions/my">我的职位</a></li>
                <li><a href="<%= request.getContextPath() %>/mo/positions/create">创建职位</a></li>
<<<<<<< HEAD
                <li><a href="<%= request.getContextPath() %>/messages/list">💬 消息</a></li>
=======
>>>>>>> de384c5c4bd4c5f2a574b2f75792ffc83db5658c
                <li>
                    <a href="<%= request.getContextPath() %>/mo/notifications">
                        通知
                        <% 
                            Integer unreadCount = (Integer) request.getAttribute("unreadNotificationCount");
                            if (unreadCount != null && unreadCount > 0) { 
                        %>
                            <span class="notification-badge"><%= unreadCount %></span>
                        <% } %>
                    </a>
                </li>
            <% } %>
            <li><a href="<%= request.getContextPath() %>/auth/logout">登出</a></li>
        </ul>
    </nav>
    
    <div class="container">
        <% if (currentUser.getRole() == UserRole.TA) { %>
            <a href="<%= request.getContextPath() %>/ta/applications/my" class="back-link">← 返回我的申请</a>
        <% } else { %>
            <a href="<%= request.getContextPath() %>/mo/applications/position?positionId=<%= position.getPositionId() %>" class="back-link">← 返回申请列表</a>
        <% } %>
        
        <% if (errorMessage != null) { %>
            <div class="error-message"><%= errorMessage %></div>
        <% } %>
        
        <div class="conversation-container">
            <div class="conversation-header">
                <div class="conversation-title">职位：<%= position.getTitle() %></div>
                <div class="conversation-participants">
                    <strong>TA:</strong> <%= ta.getName() %> &nbsp;|&nbsp; 
                    <strong>MO:</strong> <%= mo.getName() %>
                </div>
                <div style="font-size: 12px; color: #999; margin-top: 5px;">
                    申请状态：
                    <% if (application.getStatus() == ApplicationStatus.PENDING) { %>
                        <span style="color: #f39c12;">待审核</span>
                    <% } else if (application.getStatus() == ApplicationStatus.SELECTED) { %>
                        <span style="color: #27ae60;">已选中</span>
                    <% } else if (application.getStatus() == ApplicationStatus.REJECTED) { %>
                        <span style="color: #e74c3c;">已拒绝</span>
                    <% } else { %>
                        <span style="color: #95a5a6;">已撤回</span>
                    <% } %>
                </div>
            </div>
            
            <div class="messages-container" id="messagesContainer">
                <% if (messages == null || messages.isEmpty()) { %>
                    <div class="empty-messages">
                        <p>还没有消息，开始对话吧！</p>
                    </div>
                <% } else { %>
                    <% for (Message message : messages) { %>
                        <div class="message <%= message.getSenderId().equals(currentUser.getUserId()) ? "sent" : "received" %>">
                            <div class="message-header">
                                <%= message.getSenderRole() == UserRole.TA ? ta.getName() : mo.getName() %>
                            </div>
                            <div class="message-bubble">
                                <%= message.getContent() %>
                            </div>
                            <div class="message-time">
                                <%= dateFormat.format(message.getSentAt()) %>
                            </div>
                        </div>
                    <% } %>
                <% } %>
            </div>
            
            <div class="message-form">
                <form action="<%= request.getContextPath() %>/messages/send" method="post" onsubmit="return validateMessage()">
                    <input type="hidden" name="applicationId" value="<%= application.getApplicationId() %>">
                    <div class="message-input-container">
                        <textarea 
                            name="content" 
                            id="messageInput"
                            class="message-input" 
                            placeholder="输入消息..." 
                            rows="2"
                            required></textarea>
                        <button type="submit" class="send-button">发送</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
    
    <script>
        // 滚动到最新消息
        window.onload = function() {
            var container = document.getElementById('messagesContainer');
            container.scrollTop = container.scrollHeight;
        };
        
        // 验证消息
        function validateMessage() {
            var content = document.getElementById('messageInput').value.trim();
            if (content === '') {
                alert('请输入消息内容');
                return false;
            }
            return true;
        }
        
        // Enter键发送（Shift+Enter换行）
        document.getElementById('messageInput').addEventListener('keydown', function(e) {
            if (e.key === 'Enter' && !e.shiftKey) {
                e.preventDefault();
                if (validateMessage()) {
                    this.form.submit();
                }
            }
        });
    </script>
</body>
</html>
