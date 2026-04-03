<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.bupt.tarecruitment.model.User" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Collections" %>
<%@ page import="java.util.Comparator" %>
<%
    User currentUser = (User) session.getAttribute("user");
    if (currentUser == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    
    // 获取工作量数据
    @SuppressWarnings("unchecked")
    Map<User, Integer> workloads = (Map<User, Integer>) request.getAttribute("workloads");
    
    // 将Map转换为List以便排序
    List<Map.Entry<User, Integer>> workloadList = new ArrayList<>();
    if (workloads != null) {
        workloadList.addAll(workloads.entrySet());
        // 按工时降序排序
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
    <title>工作量报告 - TA招聘系统</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
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
            <li><a href="<%= request.getContextPath() %>/auth/logout">登出</a></li>
        </ul>
    </nav>
    
    <div class="container">
        <div class="card">
            <h2>助教工作量报告</h2>
            <p>显示所有助教的工作量统计，按总工时降序排列</p>
        </div>
        
        <div class="card">
            <% if (workloadList != null && !workloadList.isEmpty()) { %>
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>序号</th>
                            <th>姓名</th>
                            <th>邮箱</th>
                            <th>总工时</th>
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
                            <td><%= index++ %></td>
                            <td><%= ta.getName() %></td>
                            <td><%= ta.getEmail() %></td>
                            <td><%= hours %> 小时</td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
            <% } else { %>
                <p class="no-data">暂无工作量数据</p>
            <% } %>
        </div>
        
        <div class="actions">
            <a href="<%= request.getContextPath() %>/admin/dashboard" class="btn btn-secondary">返回仪表板</a>
        </div>
    </div>
    
    <script src="<%= request.getContextPath() %>/js/main.js"></script>
</body>
</html>
