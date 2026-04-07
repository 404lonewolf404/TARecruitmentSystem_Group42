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
    Map<String, Integer> stats = (Map<String, Integer>) request.getAttribute("stats");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>MO仪表板 - TA招聘系统</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
</head>
<body>
    <header>
        <h1>TA招聘系统</h1>
    </header>
    
    <nav>
        <ul>
            <li><a href="<%= request.getContextPath() %>/mo/dashboard">仪表板</a></li>
            <li><a href="<%= request.getContextPath() %>/mo/profile">个人资料</a></li>
            <li><a href="<%= request.getContextPath() %>/mo/positions/my">我的职位</a></li>
            <li><a href="<%= request.getContextPath() %>/mo/positions/create">创建职位</a></li>
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
            <li><a href="<%= request.getContextPath() %>/auth/logout">登出</a></li>
        </ul>
    </nav>
    
    <div class="container">
        <div class="card">
            <h2>欢迎，<%= currentUser.getName() %>！</h2>
            <p>您已登录为模块负责人（MO）。</p>
        </div>
        
        <% if (stats != null) { %>
        <div class="stats-container">
            <div class="stat-card total">
                <div class="stat-number"><%= stats.get("totalPositions") %></div>
                <div class="stat-label">我的职位</div>
            </div>
            <div class="stat-card open">
                <div class="stat-number"><%= stats.get("openPositions") %></div>
                <div class="stat-label">开放职位</div>
            </div>
            <div class="stat-card">
                <div class="stat-number"><%= stats.get("totalApplications") %></div>
                <div class="stat-label">收到申请</div>
            </div>
            <div class="stat-card pending">
                <div class="stat-number"><%= stats.get("pendingApplications") %></div>
                <div class="stat-label">待处理</div>
            </div>
        </div>
        <% } %>
        
        <!-- V3.3 图表可视化 -->
        <div class="card">
            <h3>📊 职位申请数对比</h3>
            <div style="margin-top: 20px;">
                <canvas id="positionChart" style="max-height: 400px;"></canvas>
            </div>
        </div>
        
        <div class="dashboard">
            <div class="dashboard-card">
                <h3>我的职位</h3>
                <p>查看和管理您发布的助教职位</p>
                <a href="<%= request.getContextPath() %>/mo/positions/my" class="btn btn-primary">查看我的职位</a>
            </div>
            
            <div class="dashboard-card">
                <h3>创建职位</h3>
                <p>发布新的助教职位招聘信息</p>
                <a href="<%= request.getContextPath() %>/mo/positions/create" class="btn btn-primary">创建职位</a>
            </div>
        </div>
    </div>
    
    <script src="<%= request.getContextPath() %>/js/main.js"></script>
    <script>
        // V3.3 - 图表可视化
        <%
            String chartData = (String) request.getAttribute("chartData");
            if (chartData != null) {
        %>
        // 职位申请数对比横向柱状图
        const positionData = <%= chartData %>;
        const ctx = document.getElementById('positionChart').getContext('2d');
        new Chart(ctx, {
            type: 'bar',
            data: {
                labels: positionData.labels,
                datasets: [{
                    label: '申请数量',
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
                                return '申请数: ' + context.parsed.x;
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
                            text: '申请数量'
                        }
                    },
                    y: {
                        title: {
                            display: true,
                            text: '职位名称'
                        }
                    }
                }
            }
        });
        <% } %>
    </script>
</body>
</html>
