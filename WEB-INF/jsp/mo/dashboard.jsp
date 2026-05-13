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
    Map<String, Integer> stats = (Map<String, Integer>) request.getAttribute("stats");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>MO Dashboard - TA Recruitment System</title>
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
                        <i class="fas fa-building"></i> Module Owner Dashboard
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
                    <i class="fas fa-briefcase" style="font-size: 2.5rem; opacity: 0.9;"></i>
                    <div class="stat-number"><%= stats.get("totalPositions") %></div>
                </div>
                <div class="stat-label"><i class="fas fa-list"></i> My Positions</div>
            </div>
            <div class="stat-card open">
                <div style="display: flex; align-items: center; justify-content: space-between; margin-bottom: 15px;">
                    <i class="fas fa-unlock" style="font-size: 2.5rem; opacity: 0.9;"></i>
                    <div class="stat-number"><%= stats.get("openPositions") %></div>
                </div>
                <div class="stat-label"><i class="fas fa-door-open"></i> Open Positions</div>
            </div>
            <div class="stat-card" style="border-left: 4px solid #9b59b6;">
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
                <div class="stat-label"><i class="fas fa-clock"></i> Pending Review</div>
            </div>
        </div>
        <% } %>
        
        <!-- Chart Visualization -->
        <div class="card">
            <div style="display: flex; align-items: center; gap: 10px; margin-bottom: 20px;">
                <i class="fas fa-chart-bar" style="font-size: 1.5rem; color: #2563eb;"></i>
                <h3 style="margin: 0;">Position Application Comparison</h3>
            </div>
            <div style="margin-top: 30px; padding: 20px;">
                <canvas id="positionChart" style="max-height: 400px;"></canvas>
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
                        <i class="fas fa-briefcase" style="font-size: 3rem; color: #2563eb;"></i>
                    </div>
                    <h3 style="text-align: center;">My Positions</h3>
                    <p style="text-align: center;">View and manage your posted TA positions</p>
                    <a href="<%= request.getContextPath() %>/mo/positions/my" class="btn btn-primary btn-full">
                        <i class="fas fa-arrow-right"></i> View Positions
                    </a>
                </div>
                
                <div class="dashboard-card" style="background: linear-gradient(135deg, rgba(16, 185, 129, 0.05) 0%, rgba(52, 211, 153, 0.05) 100%); border: 2px solid #e2e8f0;">
                    <div style="text-align: center; margin-bottom: 15px;">
                        <i class="fas fa-plus-circle" style="font-size: 3rem; color: #10b981;"></i>
                    </div>
                    <h3 style="text-align: center;">Create Position</h3>
                    <p style="text-align: center;">Post a new TA position recruitment</p>
                    <a href="<%= request.getContextPath() %>/mo/positions/create" class="btn btn-success btn-full">
                        <i class="fas fa-arrow-right"></i> Create Now
                    </a>
                </div>
                
                <div class="dashboard-card" style="background: linear-gradient(135deg, rgba(245, 158, 11, 0.05) 0%, rgba(251, 191, 36, 0.05) 100%); border: 2px solid #e2e8f0;">
                    <div style="text-align: center; margin-bottom: 15px;">
                        <i class="fas fa-user-circle" style="font-size: 3rem; color: #f59e0b;"></i>
                    </div>
                    <h3 style="text-align: center;">My Profile</h3>
                    <p style="text-align: center;">View and update your personal information</p>
                    <a href="<%= request.getContextPath() %>/mo/profile" class="btn btn-primary btn-full">
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
            String chartData = (String) request.getAttribute("chartData");
            if (chartData != null) {
        %>
        // Position application comparison horizontal bar chart
        const positionData = <%= chartData %>;
        const ctx = document.getElementById('positionChart').getContext('2d');
        new Chart(ctx, {
            type: 'bar',
            data: {
                labels: positionData.labels,
                datasets: [{
                    label: 'Application Count',
                    data: positionData.data,
                    backgroundColor: '#3498db',
                    borderColor: '#2980b9',
                    borderWidth: 1
                }]
            },
            options: {
                indexAxis: 'y',
                responsive: true,
                maintainAspectRatio: true,
                plugins: {
                    legend: {
                        display: false
                    },
                    tooltip: {
                        callbacks: {
                            label: function(context) {
                                return 'Applications: ' + context.parsed.x;
                            }
                        }
                    }
                },
                scales: {
                    x: {
                        beginAtZero: true,
                        ticks: {
                            stepSize: 1
                        },
                        title: {
                            display: true,
                            text: 'Application Count'
                        }
                    },
                    y: {
                        title: {
                            display: true,
                            text: 'Position Name'
                        }
                    }
                }
            }
        });
        <% } %>
    </script>
</body>
</html>
