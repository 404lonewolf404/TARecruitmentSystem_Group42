<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.bupt.tarecruitment.model.User" %>
<%@ page import="com.bupt.tarecruitment.service.NotificationService" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Collections" %>
<%@ page import="java.util.Comparator" %>
<%
    User currentUser = (User) session.getAttribute("user");
    if (currentUser == null) {
        response.sendRedirect(request.getContextPath() + "/auth/login");
        return;
    }
    
    // Get unread notification count
    int unreadCount = 0;
    try {
        NotificationService notificationService = new NotificationService();
        unreadCount = notificationService.getUnreadCount(currentUser.getUserId());
    } catch (Exception e) {
        e.printStackTrace();
    }
    
    // Get workload data
    @SuppressWarnings("unchecked")
    Map<User, Integer> workloads = (Map<User, Integer>) request.getAttribute("workloads");
    
    // Convert Map to List for sorting
    List<Map.Entry<User, Integer>> workloadList = new ArrayList<>();
    if (workloads != null) {
        workloadList.addAll(workloads.entrySet());
        // Sort by hours in descending order
        Collections.sort(workloadList, new Comparator<Map.Entry<User, Integer>>() {
            @Override
            public int compare(Map.Entry<User, Integer> e1, Map.Entry<User, Integer> e2) {
                return e2.getValue().compareTo(e1.getValue());
            }
        });
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Workload Report - TA Recruitment System</title>
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
            <h2><i class="fas fa-chart-bar"></i>&nbsp;&nbsp;TA Workload Report</h2>
            <p>View workload statistics for all TAs, sorted by total hours</p>
        </div>
        
        <% if (workloadList != null && !workloadList.isEmpty()) { %>
            <div class="card">
                <div class="stats-info">
                    <p style="font-size: 1.1rem; color: #64748b;">
                        <i class="fas fa-users"></i>&nbsp;&nbsp;Total TAs: <strong style="color: #2563eb; font-size: 1.3rem;"><%= workloadList.size() %></strong>
                    </p>
                </div>
            </div>
            
            <div class="card">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th style="width: 60px;"><i class="fas fa-list-ol"></i>&nbsp;&nbsp;No.</th>
                            <th><i class="fas fa-user"></i>&nbsp;&nbsp;Name</th>
                            <th><i class="fas fa-envelope"></i>&nbsp;&nbsp;Email</th>
                            <th style="width: 150px;"><i class="fas fa-hourglass-half"></i>&nbsp;&nbsp;Total Hours</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% 
                        int index = 1;
                        for (Map.Entry<User, Integer> entry : workloadList) { 
                            User ta = entry.getKey();
                            Integer hours = entry.getValue();
                        %>
                        <tr>
                            <td><strong><%= index++ %></strong></td>
                            <td><strong><%= ta.getName() %></strong></td>
                            <td><%= ta.getEmail() %></td>
                            <td>
                                <span style="font-weight: 600; color: #2563eb; font-size: 1.1rem;">
                                    <i class="fas fa-clock"></i>&nbsp;&nbsp;<%= hours %> hours
                                </span>
                            </td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
        <% } else { %>
            <div class="card">
                <div class="empty-state">
                    <i class="fas fa-inbox"></i>
                    <h3>No Workload Data</h3>
                    <p>There are no workload statistics available yet.</p>
                </div>
            </div>
        <% } %>
    </div>
    
    <script src="<%= request.getContextPath() %>/js/main.js"></script>
</body>
</html>
