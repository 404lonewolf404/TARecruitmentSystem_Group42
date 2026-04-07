<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.bupt.tarecruitment.model.User" %>
<%@ page import="java.util.Map" %>
<%
    User currentUser = (User) session.getAttribute("user");
    if (currentUser == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
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
    <title>管理员仪表板 - TA招聘系统</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
</head>
<body>
    <header>
        <h1>TA招聘系统</h1>
    </header>
    
    <nav>
        <ul>
            <li><a href="<%= request.getContextPath() %>/admin/dashboard">仪表板</a></li>
            <li><a href="<%= request.getContextPath() %>/admin/users">用户管理</a></li>
            <li><a href="<%= request.getContextPath() %>/admin/positions">职位管理</a></li>
            <li><a href="<%= request.getContextPath() %>/admin/applications">申请管理</a></li>
            <li><a href="<%= request.getContextPath() %>/admin/workload">工作量报告</a></li>
            <li>
                <a href="<%= request.getContextPath() %>/admin/notifications">
                    通知
                    <% 
                        Integer unreadCount = (Integer) request.getAttribute("unreadNotificationCount");
                        if (unreadCount != null && unreadCount > 0) { 
                    %>
                        <span class="notification-badge"><%= unreadCount %></span>
                    <% } %>
                </a>
            </li>
            <li><a href="<%= request.getContextPath() %>/auth/logout">登出</a></li>
        </ul>
    </nav>
    
    <div class="container">
        <div class="card">
            <h2>欢迎，<%= currentUser.getName() %>！</h2>
            <p>您已登录为系统管理员（Admin）。</p>
        </div>
        
        <% if (stats != null) { %>
        <div class="stats-container">
            <div class="stat-card total">
                <div class="stat-number"><%= stats.get("totalUsers") %></div>
                <div class="stat-label">总用户数</div>
            </div>
            <div class="stat-card">
                <div class="stat-number"><%= stats.get("totalTAs") %></div>
                <div class="stat-label">助教(TA)</div>
            </div>
            <div class="stat-card">
                <div class="stat-number"><%= stats.get("totalMOs") %></div>
                <div class="stat-label">招聘官(MO)</div>
            </div>
            <div class="stat-card">
                <div class="stat-number"><%= stats.get("totalPositions") %></div>
                <div class="stat-label">总职位数</div>
            </div>
            <div class="stat-card">
                <div class="stat-number"><%= stats.get("openPositions") %></div>
                <div class="stat-label">开放职位</div>
            </div>
            <div class="stat-card">
                <div class="stat-number"><%= stats.get("totalApplications") %></div>
                <div class="stat-label">总申请数</div>
            </div>
            <div class="stat-card pending">
                <div class="stat-number"><%= stats.get("pendingApplications") %></div>
                <div class="stat-label">待处理申请</div>
            </div>
            <div class="stat-card selected">
                <div class="stat-number"><%= stats.get("selectedApplications") %></div>
                <div class="stat-label">已选中申请</div>
            </div>
            <div class="stat-card hours">
                <div class="stat-number"><%= stats.get("totalHours") %></div>
                <div class="stat-label">总工时</div>
            </div>
            <div class="stat-card">
                <div class="stat-number"><%= stats.get("avgHours") %></div>
                <div class="stat-label">平均工时</div>
            </div>
        </div>
        <% } %>
        
        <!-- V3.3 图表可视化 -->
        <div class="card">
            <h3>📊 数据可视化</h3>
            <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 20px; margin-top: 20px;">
                <div>
                    <h4 style="text-align: center; color: #2c3e50;">TA工作量分布</h4>
                    <canvas id="workloadChart" style="max-height: 300px;"></canvas>
                </div>
                <div>
                    <h4 style="text-align: center; color: #2c3e50;">申请状态分布</h4>
                    <canvas id="statusChart" style="max-height: 300px;"></canvas>
                </div>
            </div>
        </div>
        
        <div class="dashboard">
            <div class="dashboard-card">
                <h3>👥 用户管理</h3>
                <p>查看和管理系统中的所有用户</p>
                <a href="<%= request.getContextPath() %>/admin/users" class="btn btn-primary">查看用户列表</a>
            </div>
            
            <div class="dashboard-card">
                <h3>⏰ 工作量报告</h3>
                <p>查看所有助教的工作量统计信息</p>
                <a href="<%= request.getContextPath() %>/admin/workload" class="btn btn-primary">查看工作量报告</a>
            </div>
        </div>
    </div>
    
    <script src="<%= request.getContextPath() %>/js/main.js"></script>
    <script>
        // V3.3 - 图表可视化
        <%
            String workloadChartData = (String) request.getAttribute("workloadChartData");
            String statusChartData = (String) request.getAttribute("statusChartData");
            if (workloadChartData != null && statusChartData != null) {
        %>
        // TA工作量分布柱状图
        const workloadData = <%= workloadChartData %>;
        const workloadCtx = document.getElementById('workloadChart').getContext('2d');
        new Chart(workloadCtx, {
            type: 'bar',
            data: {
                labels: workloadData.labels,
                datasets: [{
                    label: '工作时数',
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
                                return context.parsed.y + ' 小时';
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
                            text: '工作时数'
                        }
                    },
                    x: {
                        title: {
                            display: true,
                            text: 'TA姓名'
                        }
                    }
                }
            }
        });
        
        // 申请状态饼图
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
