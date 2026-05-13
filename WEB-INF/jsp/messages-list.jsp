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
    <title>Messages - TA Recruitment System</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
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
        
        .filter-toolbar {
            background: white;
            border: 1px solid #ddd;
            border-radius: 8px;
            padding: 15px;
            margin-bottom: 20px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            flex-wrap: wrap;
            gap: 15px;
        }
        
        .filter-group, .sort-group {
            display: flex;
            align-items: center;
            gap: 10px;
        }
        
        .filter-group label, .sort-group label {
            font-weight: bold;
            color: #2c3e50;
        }
        
        .filter-toolbar select {
            padding: 8px 12px;
            border: 1px solid #ddd;
            border-radius: 4px;
            background: white;
            color: #2c3e50;
            font-size: 14px;
            cursor: pointer;
            transition: border-color 0.3s;
        }
        
        .filter-toolbar select:hover {
            border-color: #3498db;
        }
        
        .filter-toolbar select:focus {
            outline: none;
            border-color: #3498db;
            box-shadow: 0 0 0 2px rgba(52, 152, 219, 0.1);
        }
        
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
        <h1><i class="fas fa-graduation-cap"></i> TA Recruitment System</h1>
    </header>
    
    <nav>
        <ul>
            <% if (currentUser.getRole() == UserRole.TA) { %>
                <li><a href="<%= request.getContextPath() %>/ta/dashboard"><i class="fas fa-home"></i>&nbsp;&nbsp;Dashboard</a></li>
                <li><a href="<%= request.getContextPath() %>/ta/profile"><i class="fas fa-user"></i>&nbsp;&nbsp;Profile</a></li>
                <li><a href="<%= request.getContextPath() %>/ta/positions"><i class="fas fa-briefcase"></i>&nbsp;&nbsp;Browse Positions</a></li>
                <li><a href="<%= request.getContextPath() %>/ta/applications/my"><i class="fas fa-file-alt"></i>&nbsp;&nbsp;My Applications</a></li>
                <li><a href="<%= request.getContextPath() %>/ta/favorites"><i class="fas fa-star"></i>&nbsp;&nbsp;Favorites</a></li>
                <li><a href="<%= request.getContextPath() %>/messages/list"><i class="fas fa-comments"></i>&nbsp;&nbsp;Messages</a></li>
                <li>
                    <a href="<%= request.getContextPath() %>/ta/notifications">
                        <i class="fas fa-bell"></i>&nbsp;&nbsp;Notifications
                        <% 
                            Integer unreadCount = (Integer) request.getAttribute("unreadNotificationCount");
                            if (unreadCount != null && unreadCount > 0) { 
                        %>
                            <span class="notification-badge"><%= unreadCount %></span>
                        <% } %>
                    </a>
                </li>
            <% } else { %>
                <li><a href="<%= request.getContextPath() %>/mo/dashboard"><i class="fas fa-home"></i>&nbsp;&nbsp;Dashboard</a></li>
                <li><a href="<%= request.getContextPath() %>/mo/profile"><i class="fas fa-user"></i>&nbsp;&nbsp;Profile</a></li>
                <li><a href="<%= request.getContextPath() %>/mo/positions/my"><i class="fas fa-briefcase"></i>&nbsp;&nbsp;My Positions</a></li>
                <li><a href="<%= request.getContextPath() %>/mo/positions/create"><i class="fas fa-plus-circle"></i>&nbsp;&nbsp;Create Position</a></li>
                <li><a href="<%= request.getContextPath() %>/messages/list"><i class="fas fa-comments"></i>&nbsp;&nbsp;Messages</a></li>
                <li>
                    <a href="<%= request.getContextPath() %>/mo/notifications">
                        <i class="fas fa-bell"></i>&nbsp;&nbsp;Notifications
                        <% 
                            Integer unreadCount = (Integer) request.getAttribute("unreadNotificationCount");
                            if (unreadCount != null && unreadCount > 0) { 
                        %>
                            <span class="notification-badge"><%= unreadCount %></span>
                        <% } %>
                    </a>
                </li>
            <% } %>
            <li><a href="<%= request.getContextPath() %>/auth/logout"><i class="fas fa-sign-out-alt"></i>&nbsp;&nbsp;Logout</a></li>
        </ul>
    </nav>
    
    <div class="container">
        <h2>💬 Message List</h2>
        
        <!-- Filter and sort toolbar -->
        <div class="filter-toolbar">
            <div class="filter-group">
                <label>Filter:</label>
                <select id="filterUnread" onchange="applyFilters()">
                    <option value="all">All Messages</option>
                    <option value="unread">Unread Only</option>
                    <option value="read">Read Only</option>
                </select>
                
                <select id="filterStatus" onchange="applyFilters()">
                    <option value="all">All Status</option>
                    <option value="PENDING">Pending</option>
                    <option value="SELECTED">Selected</option>
                    <option value="REJECTED">Rejected</option>
                </select>
            </div>
            
            <div class="sort-group">
                <label>Sort By:</label>
                <select id="sortBy" onchange="applyFilters()">
                    <option value="time">By Time</option>
                    <option value="unread">By Unread Count</option>
                    <option value="name">By Name</option>
                </select>
            </div>
        </div>
        
        <div class="conversation-list">
            <% if (conversations == null || conversations.isEmpty()) { %>
                <div class="empty-state">
                    <div class="empty-state-icon">💬</div>
                    <p>No messages yet</p>
                    <p style="font-size: 14px;">You can chat with MO after applying for positions</p>
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
                    String statusValue = "";
                    
                    if (app.getStatus() == ApplicationStatus.PENDING) {
                        statusClass = "status-pending";
                        statusText = "Pending";
                        statusValue = "PENDING";
                    } else if (app.getStatus() == ApplicationStatus.SELECTED) {
                        statusClass = "status-selected";
                        statusText = "Selected";
                        statusValue = "SELECTED";
                    } else if (app.getStatus() == ApplicationStatus.REJECTED) {
                        statusClass = "status-rejected";
                        statusText = "Rejected";
                        statusValue = "REJECTED";
                    }
                %>
                    <div class="conversation-item" data-status="<%= statusValue %>" onclick="window.location.href='<%= request.getContextPath() %>/messages/conversation?applicationId=<%= app.getApplicationId() %>'">
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
                                <%= lastMessage != null ? lastMessage.getContent() : "No messages yet" %>
                            </div>
                            <div class="conversation-meta">
                                <span class="conversation-status <%= statusClass %>"><%= statusText %></span>
                                <% if (messageCount != null && messageCount > 0) { %>
                                    <span style="font-size: 12px; color: #999;"><%= messageCount %> message(s)</span>
                                <% } %>
                                <% if (unreadCount != null && unreadCount > 0) { %>
                                    <span class="conversation-badge"><%= unreadCount %> unread</span>
                                <% } %>
                            </div>
                        </div>
                    </div>
                <% } %>
            <% } %>
        </div>
    </div>
    
    <script>
        // Store all conversation data
        let allConversations = [];
        
        // Initialize data on page load
        window.addEventListener('DOMContentLoaded', function() {
            // Get all conversation items
            const conversationItems = document.querySelectorAll('.conversation-item');
            conversationItems.forEach(item => {
                const unreadBadge = item.querySelector('.conversation-badge');
                const statusSpan = item.querySelector('.conversation-status');
                const nameSpan = item.querySelector('.conversation-name');
                const timeSpan = item.querySelector('.conversation-time');
                
                allConversations.push({
                    element: item,
                    unreadCount: unreadBadge ? parseInt(unreadBadge.textContent) : 0,
                    status: statusSpan ? statusSpan.textContent.trim() : '',
                    statusValue: item.dataset.status || '',
                    name: nameSpan ? nameSpan.textContent.trim() : '',
                    time: timeSpan ? timeSpan.textContent.trim() : ''
                });
            });
        });
        
        function applyFilters() {
            const filterUnread = document.getElementById('filterUnread').value;
            const filterStatus = document.getElementById('filterStatus').value;
            const sortBy = document.getElementById('sortBy').value;
            
            // Filter
            let filtered = allConversations.filter(conv => {
                // Unread filter
                if (filterUnread === 'unread' && conv.unreadCount === 0) return false;
                if (filterUnread === 'read' && conv.unreadCount > 0) return false;
                
                // Status filter
                if (filterStatus !== 'all' && conv.statusValue !== filterStatus) return false;
                
                return true;
            });
            
            // Sort
            filtered.sort((a, b) => {
                if (sortBy === 'time') {
                    // Sort by time (newest first)
                    return b.time.localeCompare(a.time);
                } else if (sortBy === 'unread') {
                    // Sort by unread count (most first)
                    return b.unreadCount - a.unreadCount;
                } else if (sortBy === 'name') {
                    // Sort by name
                    return a.name.localeCompare(b.name, 'zh-CN');
                }
                return 0;
            });
            
            // Re-render list
            const conversationList = document.querySelector('.conversation-list');
            conversationList.innerHTML = '';
            
            if (filtered.length === 0) {
                conversationList.innerHTML = `
                    <div class="empty-state">
                        <div class="empty-state-icon">💬</div>
                        <p>No messages match the criteria</p>
                    </div>
                `;
            } else {
                filtered.forEach(conv => {
                    conversationList.appendChild(conv.element.cloneNode(true));
                });
            }
        }
    </script>
</body>
</html>
