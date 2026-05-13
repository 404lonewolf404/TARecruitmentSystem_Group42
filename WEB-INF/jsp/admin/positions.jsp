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
    <title>Position Management - TA Recruitment System</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>
    <header>
        <h1><i class="fas fa-graduation-cap"></i> TA Recruitment System</h1>
    </header>
    
    <nav>
        <ul>
            <li><a href="<%= request.getContextPath() %>/admin/dashboard"><i class="fas fa-home"></i>&nbsp;&nbsp;Dashboard</a></li>
            <li><a href="<%= request.getContextPath() %>/admin/profile"><i class="fas fa-user"></i>&nbsp;&nbsp;Profile</a></li>
            <li><a href="<%= request.getContextPath() %>/admin/users"><i class="fas fa-users"></i>&nbsp;&nbsp;User Management</a></li>
            <li><a href="<%= request.getContextPath() %>/admin/positions"><i class="fas fa-briefcase"></i>&nbsp;&nbsp;Position Management</a></li>
            <li><a href="<%= request.getContextPath() %>/admin/applications"><i class="fas fa-file-alt"></i>&nbsp;&nbsp;Application Management</a></li>
            <li><a href="<%= request.getContextPath() %>/admin/workload"><i class="fas fa-chart-bar"></i>&nbsp;&nbsp;Workload Report</a></li>
            <li><a href="<%= request.getContextPath() %>/admin/notifications"><i class="fas fa-bell"></i>&nbsp;&nbsp;Notifications</a></li>
            <li><a href="<%= request.getContextPath() %>/auth/logout"><i class="fas fa-sign-out-alt"></i>&nbsp;&nbsp;Logout</a></li>
        </ul>
    </nav>

    <div class="container">
        <div class="page-header">
            <h2><i class="fas fa-briefcase"></i>&nbsp;&nbsp;Position Management</h2>
            <p>View and manage all TA positions in the system</p>
        </div>

        <%
            List<Position> positions = (List<Position>) request.getAttribute("positions");
            List<User> users = (List<User>) request.getAttribute("users");
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            
            java.util.Map<String, User> userMap = new java.util.HashMap<>();
            if (users != null) {
                for (User u : users) {
                    userMap.put(u.getUserId(), u);
                }
            }
        %>

        <div class="card">
            <div class="stats-info">
                <p style="font-size: 1.1rem; color: #64748b;">
                    <i class="fas fa-chart-bar"></i>&nbsp;&nbsp;Total Positions: <strong style="color: #2563eb; font-size: 1.3rem;"><%= positions != null ? positions.size() : 0 %></strong>
                </p>
            </div>
        </div>

        <% if (positions != null && !positions.isEmpty()) { %>
            <div class="card">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th><i class="fas fa-heading"></i>&nbsp;&nbsp;Title</th>
                            <th><i class="fas fa-align-left"></i>&nbsp;&nbsp;Description</th>
                            <th><i class="fas fa-clock"></i>&nbsp;&nbsp;Hours</th>
                            <th><i class="fas fa-users"></i>&nbsp;&nbsp;Max</th>
                            <th><i class="fas fa-list-check"></i>&nbsp;&nbsp;Requirements</th>
                            <th><i class="fas fa-toggle-on"></i>&nbsp;&nbsp;Status</th>
                            <th><i class="fas fa-user-tie"></i>&nbsp;&nbsp;Publisher</th>
                            <th><i class="fas fa-calendar"></i>&nbsp;&nbsp;Created</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (Position position : positions) { 
                            User publisher = userMap.get(position.getMoId());
                            String publisherName = (publisher != null) ? publisher.getName() + " (" + publisher.getEmail() + ")" : position.getMoId();
                        %>
                            <tr>
                                <td><strong><%= position.getTitle() %></strong></td>
                                <td><%= position.getDescription() != null && !position.getDescription().isEmpty() ? position.getDescription() : "-" %></td>
                                <td><%= position.getHours() %>/week</td>
                                <td><%= position.getMaxPositions() %></td>
                                <td><%= position.getRequirements() != null && !position.getRequirements().isEmpty() ? position.getRequirements() : "-" %></td>
                                <td>
                                    <span class="badge badge-<%= position.getStatus() %>">
                                        <%= position.getStatus() != null && position.getStatus().toString().equals("OPEN") ? "Open" : "Closed" %>
                                    </span>
                                </td>
                                <td><%= publisherName %></td>
                                <td><%= position.getCreatedAt() != null ? formatter.format(position.getCreatedAt()) : "-" %></td>
                            </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
        <% } else { %>
            <div class="card">
                <div class="empty-state">
                    <i class="fas fa-inbox"></i>
                    <h3>No Positions</h3>
                    <p>There are no positions in the system yet.</p>
                </div>
            </div>
        <% } %>
    </div>
</body>
</html>
