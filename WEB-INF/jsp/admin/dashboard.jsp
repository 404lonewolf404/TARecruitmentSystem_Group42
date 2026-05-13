<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.bupt.tarecruitment.model.User" %>
<%@ page import="java.util.Map" %>
<%
    User currentUser = (User) session.getAttribute("user");
    if (currentUser == null) {
        response.sendRedirect(request.getContextPath() + "/auth/login");
        return;
    }
    
    @SuppressWarnings("unchecked")
    Map<String, Object> stats = (Map<String, Object>) request.getAttribute("stats");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Dashboard - TA Recruitment System</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
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
            <li>
                <a href="<%= request.getContextPath() %>/admin/notifications">
                    <i class="fas fa-bell"></i>&nbsp;&nbsp;Notifications
                    <% 
                        Integer unreadCount = (Integer) request.getAttribute("unreadNotificationCount");
                        if (unreadCount != null && unreadCount > 0) { 
                    %>
                        <span class="notification-badge"><%= unreadCount %></span>
                    <% } %>
                </a>
            </li>
            <li><a href="<%= request.getContextPath() %>/auth/logout"><i class="fas fa-sign-out-alt"></i>&nbsp;&nbsp;Logout</a></li>
        </ul>
    </nav>
    
    <div class="container">
        <!-- Welcome Banner -->
        <div class="card" style="background: linear-gradient(135deg, #2563eb 0%, #3b82f6 100%); color: white; border: none;">
            <div style="display: flex; align-items: center; justify-content: space-between; flex-wrap: wrap; gap: 20px;">
                <div>
                    <h2 style="color: white; font-size: 2rem; margin-bottom: 10px;">
                        <i class="fas fa-hand-wave"></i> Welcome back, <%= currentUser.getName() %>!
                    </h2>
                    <p style="font-size: 1.1rem; opacity: 0.95; margin: 0;">
                        <i class="fas fa-shield-alt"></i> System Administrator Dashboard
                    </p>
                </div>
                <div style="text-align: right;">
                    <div style="font-size: 0.9rem; opacity: 0.9;">
                        <i class="fas fa-calendar-day"></i> <%= new java.text.SimpleDateFormat("EEEE, MMMM dd, yyyy", java.util.Locale.ENGLISH).format(new java.util.Date()) %>
                    </div>
                </div>
            </div>
        </div>
        
        <!-- Statistics Cards -->
        <% if (stats != null) { %>
        <div class="stats-container">
            <div class="stat-card total">
                <div style="display: flex; align-items: center; justify-content: space-between; margin-bottom: 15px;">
                    <i class="fas fa-users" style="font-size: 2.5rem; opacity: 0.9;"></i>
                    <div class="stat-number"><%= stats.get("totalUsers") %></div>
                </div>
                <div class="stat-label"><i class="fas fa-people-group"></i> Total Users</div>
            </div>
            <div class="stat-card" style="border-left: 4px solid #8b5cf6;">
                <div style="display: flex; align-items: center; justify-content: space-between; margin-bottom: 15px;">
                    <i class="fas fa-graduation-cap" style="font-size: 2.5rem; opacity: 0.9;"></i>
                    <div class="stat-number"><%= stats.get("totalTAs") %></div>
                </div>
                <div class="stat-label"><i class="fas fa-chalkboard-user"></i> Teaching Assistants</div>
            </div>
            <div class="stat-card" style="border-left: 4px solid #06b6d4;">
                <div style="display: flex; align-items: center; justify-content: space-between; margin-bottom: 15px;">
                    <i class="fas fa-building" style="font-size: 2.5rem; opacity: 0.9;"></i>
                    <div class="stat-number"><%= stats.get("totalMOs") %></div>
                </div>
                <div class="stat-label"><i class="fas fa-sitemap"></i> Module Owners</div>
            </div>
            <div class="stat-card" style="border-left: 4px solid #f59e0b;">
                <div style="display: flex; align-items: center; justify-content: space-between; margin-bottom: 15px;">
                    <i class="fas fa-briefcase" style="font-size: 2.5rem; opacity: 0.9;"></i>
                    <div class="stat-number"><%= stats.get("totalPositions") %></div>
                </div>
                <div class="stat-label"><i class="fas fa-list"></i> Total Positions</div>
            </div>
            <div class="stat-card open">
                <div style="display: flex; align-items: center; justify-content: space-between; margin-bottom: 15px;">
                    <i class="fas fa-unlock" style="font-size: 2.5rem; opacity: 0.9;"></i>
                    <div class="stat-number"><%= stats.get("openPositions") %></div>
                </div>
                <div class="stat-label"><i class="fas fa-door-open"></i> Open Positions</div>
            </div>
            <div class="stat-card" style="border-left: 4px solid #ec4899;">
                <div style="display: flex; align-items: center; justify-content: space-between; margin-bottom: 15px;">
                    <i class="fas fa-file-alt" style="font-size: 2.5rem; opacity: 0.9;"></i>
                    <div class="stat-number"><%= stats.get("totalApplications") %></div>
                </div>
                <div class="stat-label"><i class="fas fa-inbox"></i> Total Applications</div>
            </div>
            <div class="stat-card pending">
                <div style="display: flex; align-items: center; justify-content: space-between; margin-bottom: 15px;">
                    <i class="fas fa-hourglass-half" style="font-size: 2.5rem; opacity: 0.9;"></i>
                    <div class="stat-number"><%= stats.get("pendingApplications") %></div>
                </div>
                <div class="stat-label"><i class="fas fa-clock"></i> Pending</div>
            </div>
            <div class="stat-card selected">
                <div style="display: flex; align-items: center; justify-content: space-between; margin-bottom: 15px;">
                    <i class="fas fa-check-circle" style="font-size: 2.5rem; opacity: 0.9;"></i>
                    <div class="stat-number"><%= stats.get("selectedApplications") %></div>
                </div>
                <div class="stat-label"><i class="fas fa-thumbs-up"></i> Selected</div>
            </div>
            <div class="stat-card hours">
                <div style="display: flex; align-items: center; justify-content: space-between; margin-bottom: 15px;">
                    <i class="fas fa-business-time" style="font-size: 2.5rem; opacity: 0.9;"></i>
                    <div class="stat-number"><%= stats.get("totalHours") %></div>
                </div>
                <div class="stat-label"><i class="fas fa-calendar-week"></i> Total Hours</div>
            </div>
        </div>
        <% } %>
        
        <!-- Chart Visualization -->
        <div class="card">
            <div style="display: flex; align-items: center; gap: 10px; margin-bottom: 20px;">
                <i class="fas fa-chart-line" style="font-size: 1.5rem; color: #2563eb;"></i>
                <h3 style="margin: 0;">Data Visualization</h3>
            </div>
            <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 20px; margin-top: 30px;">
                <div>
                    <h4 style="text-align: center; color: #2c3e50; margin-bottom: 15px;">TA Workload Distribution</h4>
                    <canvas id="workloadChart" style="max-height: 300px;"></canvas>
                </div>
                <div>
                    <h4 style="text-align: center; color: #2c3e50; margin-bottom: 15px;">Application Status Distribution</h4>
                    <canvas id="statusChart" style="max-height: 300px;"></canvas>
                </div>
            </div>
        </div>
        
        <!-- Quick Actions -->
        <div class="card">
            <div style="display: flex; align-items: center; gap: 10px; margin-bottom: 25px;">
                <i class="fas fa-bolt" style="font-size: 1.5rem; color: #2563eb;"></i>
                <h3 style="margin: 0;">Quick Actions</h3>
            </div>
            <div class="dashboard">
                <div class="dashboard-card" style="background: linear-gradient(135deg, rgba(37, 99, 235, 0.05) 0%, rgba(59, 130, 246, 0.05) 100%); border: 2px solid #e2e8f0;">
                    <div style="text-align: center; margin-bottom: 15px;">
                        <i class="fas fa-users" style="font-size: 3rem; color: #2563eb;"></i>
                    </div>
                    <h3 style="text-align: center;">User Management</h3>
                    <p style="text-align: center;">View and manage all users in the system</p>
                    <a href="<%= request.getContextPath() %>/admin/users" class="btn btn-primary btn-full">
                        <i class="fas fa-arrow-right"></i> View Users
                    </a>
                </div>
                
                <div class="dashboard-card" style="background: linear-gradient(135deg, rgba(16, 185, 129, 0.05) 0%, rgba(52, 211, 153, 0.05) 100%); border: 2px solid #e2e8f0;">
                    <div style="text-align: center; margin-bottom: 15px;">
                        <i class="fas fa-chart-bar" style="font-size: 3rem; color: #10b981;"></i>
                    </div>
                    <h3 style="text-align: center;">Workload Report</h3>
                    <p style="text-align: center;">View workload statistics for all teaching assistants</p>
                    <a href="<%= request.getContextPath() %>/admin/workload" class="btn btn-success btn-full">
                        <i class="fas fa-arrow-right"></i> View Report
                    </a>
                </div>
                
                <div class="dashboard-card" style="background: linear-gradient(135deg, rgba(245, 158, 11, 0.05) 0%, rgba(251, 191, 36, 0.05) 100%); border: 2px solid #e2e8f0;">
                    <div style="text-align: center; margin-bottom: 15px;">
                        <i class="fas fa-user-circle" style="font-size: 3rem; color: #f59e0b;"></i>
                    </div>
                    <h3 style="text-align: center;">My Profile</h3>
                    <p style="text-align: center;">View and update your personal information</p>
                    <a href="<%= request.getContextPath() %>/admin/profile" class="btn btn-primary btn-full">
                        <i class="fas fa-arrow-right"></i> View Profile
                    </a>
                </div>
            </div>
        </div>
    </div>
    
    <script src="<%= request.getContextPath() %>/js/main.js"></script>
    <script>
        // V3.3 - Chart Visualization
        <%
            String workloadChartData = (String) request.getAttribute("workloadChartData");
            String statusChartData = (String) request.getAttribute("statusChartData");
            if (workloadChartData != null && statusChartData != null) {
        %>
        // TA Workload Distribution Bar Chart
        const workloadData = <%= workloadChartData %>;
        const workloadCtx = document.getElementById('workloadChart').getContext('2d');
        new Chart(workloadCtx, {
            type: 'bar',
            data: {
                labels: workloadData.labels,
                datasets: [{
                    label: 'Working Hours',
                    data: workloadData.data,
                    backgroundColor: workloadData.colors,
                    borderColor: workloadData.colors,
                    borderWidth: 1
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: true,
                plugins: {
                    legend: {
                        display: false
                    },
                    tooltip: {
                        callbacks: {
                            label: function(context) {
                                return context.parsed.y + ' hours';
                            }
                        }
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: {
                            stepSize: 5
                        },
                        title: {
                            display: true,
                            text: 'Working Hours'
                        }
                    },
                    x: {
                        title: {
                            display: true,
                            text: 'TA Name'
                        }
                    }
                }
            }
        });
        
        // Application Status Pie Chart
        const statusData = <%= statusChartData %>;
        const statusCtx = document.getElementById('statusChart').getContext('2d');
        new Chart(statusCtx, {
            type: 'pie',
            data: {
                labels: statusData.labels,
                datasets: [{
                    data: statusData.data,
                    backgroundColor: statusData.colors,
                    borderWidth: 2,
                    borderColor: '#fff'
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: true,
                plugins: {
                    legend: {
                        position: 'bottom'
                    },
                    tooltip: {
                        callbacks: {
                            label: function(context) {
                                const total = context.dataset.data.reduce((a, b) => a + b, 0);
                                const percentage = ((context.parsed / total) * 100).toFixed(1);
                                return context.label + ': ' + context.parsed + ' (' + percentage + '%)';
                            }
                        }
                    }
                }
            }
        });
        <% } %>
    </script>
</body>
</html>
