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
    <title>Conversation - TA Recruitment System</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        .conversation-container {
            max-width: 900px;
            margin: 30px auto;
            display: flex;
            flex-direction: column;
            height: calc(100vh - 200px);
        }
        
        .conversation-header {
            background: white;
            padding: 25px;
            border-radius: 12px;
            box-shadow: 0 4px 15px rgba(0,0,0,0.08);
            margin-bottom: 20px;
            border-left: 5px solid #3498db;
        }
        
        .conversation-header h2 {
            margin: 0 0 15px 0;
            color: #1e3c72;
            font-size: 1.5rem;
            display: flex;
            align-items: center;
            gap: 10px;
        }
        
        .conversation-header p {
            margin: 8px 0;
            color: #555;
            font-size: 0.95rem;
        }
        
        .conversation-header .info-row {
            display: flex;
            gap: 30px;
            margin-top: 12px;
        }
        
        .conversation-header .info-item {
            display: flex;
            align-items: center;
            gap: 8px;
        }
        
        .conversation-header .info-item i {
            color: #3498db;
            font-size: 1.1rem;
        }
        
        .messages-container {
            background: white;
            border-radius: 12px;
            box-shadow: 0 4px 15px rgba(0,0,0,0.08);
            flex: 1;
            overflow-y: auto;
            padding: 20px;
            margin-bottom: 20px;
        }
        
        .message {
            margin-bottom: 20px;
            display: flex;
            gap: 15px;
            animation: slideIn 0.3s ease-out;
        }
        
        @keyframes slideIn {
            from {
                opacity: 0;
                transform: translateY(10px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }
        
        .message-avatar {
            width: 45px;
            height: 45px;
            border-radius: 50%;
            background: linear-gradient(135deg, #3498db 0%, #2980b9 100%);
            color: white;
            display: flex;
            align-items: center;
            justify-content: center;
            font-weight: bold;
            flex-shrink: 0;
        }
        
        .message-content {
            flex: 1;
        }
        
        .message-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 8px;
        }
        
        .message-sender {
            font-weight: 600;
            color: #1e3c72;
            font-size: 0.95rem;
        }
        
        .message-time {
            font-size: 0.85rem;
            color: #999;
        }
        
        .message-text {
            background: #f8f9fa;
            padding: 12px 15px;
            border-radius: 8px;
            color: #333;
            line-height: 1.5;
            word-wrap: break-word;
        }
        
        .empty-state {
            text-align: center;
            padding: 40px 20px;
            color: #999;
        }
        
        .empty-state-icon {
            font-size: 48px;
            margin-bottom: 15px;
            opacity: 0.5;
        }
        
        .input-container {
            background: white;
            padding: 20px;
            border-radius: 12px;
            box-shadow: 0 4px 15px rgba(0,0,0,0.08);
            margin-bottom: 20px;
        }
        
        .input-container form {
            display: flex;
            gap: 10px;
        }
        
        .input-container textarea {
            flex: 1;
            padding: 12px 15px;
            border: 1px solid #ddd;
            border-radius: 8px;
            font-family: inherit;
            font-size: 0.95rem;
            resize: vertical;
            min-height: 60px;
            transition: border-color 0.3s;
        }
        
        .input-container textarea:focus {
            outline: none;
            border-color: #3498db;
            box-shadow: 0 0 0 3px rgba(52, 152, 219, 0.1);
        }
        
        .input-container button {
            padding: 12px 25px;
            background: linear-gradient(135deg, #3498db 0%, #2980b9 100%);
            color: white;
            border: none;
            border-radius: 8px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s;
            align-self: flex-end;
        }
        
        .input-container button:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(52, 152, 219, 0.3);
        }
        
        .back-link {
            display: inline-flex;
            align-items: center;
            gap: 8px;
            color: #3498db;
            text-decoration: none;
            font-weight: 500;
            transition: all 0.3s;
        }
        
        .back-link:hover {
            gap: 12px;
            color: #2980b9;
        }
    </style>
</head>
<body>
    <header>
        <h1><i class="fas fa-graduation-cap"></i> TA Recruitment System</h1>
    </header>
    
    <div class="container">
        <div class="conversation-container">
            <div class="conversation-header">
                <h2><i class="fas fa-comments"></i> Conversation</h2>
                <div class="info-row">
                    <div class="info-item">
                        <i class="fas fa-briefcase"></i>
                        <span><strong>Position:</strong> <%= position != null ? position.getTitle() : "Unknown" %></span>
                    </div>
                    <div class="info-item">
                        <i class="fas fa-user"></i>
                        <span><strong>TA:</strong> <%= ta != null ? ta.getName() : "Unknown" %></span>
                    </div>
                    <div class="info-item">
                        <i class="fas fa-user-tie"></i>
                        <span><strong>MO:</strong> <%= mo != null ? mo.getName() : "Unknown" %></span>
                    </div>
                </div>
            </div>
            
            <div class="messages-container">
                <% if (messages == null || messages.isEmpty()) { %>
                    <div class="empty-state">
                        <div class="empty-state-icon">💬</div>
                        <p>No messages yet. Start the conversation!</p>
                    </div>
                <% } else { %>
                    <% for (Object obj : messages) {
                        Message msg = (Message) obj;
                        String senderName = msg.getSenderRole() == UserRole.TA ? ta.getName() : mo.getName();
                        String senderInitial = senderName.substring(0, 1).toUpperCase();
                    %>
                        <div class="message">
                            <div class="message-avatar"><%= senderInitial %></div>
                            <div class="message-content">
                                <div class="message-header">
                                    <span class="message-sender"><%= senderName %></span>
                                    <span class="message-time"><%= dateFormat.format(msg.getSentAt()) %></span>
                                </div>
                                <div class="message-text"><%= msg.getContent() %></div>
                            </div>
                        </div>
                    <% } %>
                <% } %>
            </div>
            
            <div class="input-container">
                <form action="<%= request.getContextPath() %>/messages/send" method="post">
                    <input type="hidden" name="applicationId" value="<%= app.getApplicationId() %>">
                    <textarea name="content" placeholder="Type your message here..." required></textarea>
                    <button type="submit"><i class="fas fa-paper-plane"></i> Send</button>
                </form>
            </div>
            
            <div>
                <% if (currentUser.getRole() == UserRole.TA) { %>
                    <a href="<%= request.getContextPath() %>/ta/applications/my" class="back-link">
                        <i class="fas fa-arrow-left"></i> Back to My Applications
                    </a>
                <% } else if (currentUser.getRole() == UserRole.MO) { %>
                    <a href="<%= request.getContextPath() %>/mo/applications/position?positionId=<%= position.getPositionId() %>" class="back-link">
                        <i class="fas fa-arrow-left"></i> Back to Application List
                    </a>
                <% } %>
            </div>
        </div>
    </div>
</body>
</html>
