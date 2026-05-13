<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.bupt.tarecruitment.model.Application" %>
<%@ page import="com.bupt.tarecruitment.model.ApplicationStatus" %>
<%@ page import="com.bupt.tarecruitment.model.User" %>
<%@ page import="com.bupt.tarecruitment.model.Position" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Application Management - TA Recruitment System</title>
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
            <h2><i class="fas fa-file-alt"></i>&nbsp;&nbsp;Application Management</h2>
            <p>View and manage all applications in the system</p>
        </div>

        <%
            List<Application> applications = (List<Application>) request.getAttribute("applications");
            List<User> users = (List<User>) request.getAttribute("users");
            List<Position> positions = (List<Position>) request.getAttribute("positions");
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            
            java.util.Map<String, User> userMap = new java.util.HashMap<>();
            if (users != null) {
                for (User u : users) {
                    userMap.put(u.getUserId(), u);
                }
            }
            
            java.util.Map<String, Position> positionMap = new java.util.HashMap<>();
            if (positions != null) {
                for (Position p : positions) {
                    positionMap.put(p.getPositionId(), p);
                }
            }
        %>

        <div class="card">
            <div class="stats-info">
                <p style="font-size: 1.1rem; color: #64748b;">
                    <i class="fas fa-chart-bar"></i>&nbsp;&nbsp;Total Applications: <strong style="color: #2563eb; font-size: 1.3rem;"><%= applications != null ? applications.size() : 0 %></strong>
                </p>
            </div>
        </div>

        <% if (applications != null && !applications.isEmpty()) { %>
            <div class="card">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th><i class="fas fa-user"></i>&nbsp;&nbsp;Applicant</th>
                            <th><i class="fas fa-briefcase"></i>&nbsp;&nbsp;Position</th>
                            <th><i class="fas fa-file-pdf"></i>&nbsp;&nbsp;Resume</th>
                            <th><i class="fas fa-toggle-on"></i>&nbsp;&nbsp;Status</th>
                            <th><i class="fas fa-calendar"></i>&nbsp;&nbsp;Applied</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (Application app : applications) { 
                            User applicant = userMap.get(app.getTaId());
                            String applicantName = (applicant != null) ? applicant.getName() + " (" + applicant.getEmail() + ")" : app.getTaId();
                            
                            Position position = positionMap.get(app.getPositionId());
                            String positionTitle = (position != null) ? position.getTitle() : app.getPositionId();
                        %>
                            <tr>
                                <td><strong><%= applicantName %></strong></td>
                                <td><%= positionTitle %></td>
                                <td>
                                    <% if (app.getResumePath() != null && !app.getResumePath().isEmpty()) { %>
                                        <span style="color: #10b981; font-weight: 600;"><i class="fas fa-check-circle"></i>&nbsp;&nbsp;Uploaded</span>
                                    <% } else { %>
                                        <span style="color: #94a3b8;">Not uploaded</span>
                                    <% } %>
                                </td>
                                <td>
                                    <span class="badge badge-<%= app.getStatus() %>">
                                        <% 
                                            String statusText = "";
                                            if (app.getStatus() == ApplicationStatus.PENDING) {
                                                statusText = "Pending";
                                            } else if (app.getStatus() == ApplicationStatus.SELECTED) {
                                                statusText = "Selected";
                                            } else if (app.getStatus() == ApplicationStatus.REJECTED) {
                                                statusText = "Rejected";
                                            } else if (app.getStatus() == ApplicationStatus.WITHDRAWN) {
                                                statusText = "Withdrawn";
                                            } else {
                                                statusText = app.getStatus().toString();
                                            }
                                        %>
                                        <%= statusText %>
                                    </span>
                                </td>
                                <td><%= app.getAppliedAt() != null ? formatter.format(app.getAppliedAt()) : "-" %></td>
                            </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
        <% } else { %>
            <div class="card">
                <div class="empty-state">
                    <i class="fas fa-inbox"></i>
                    <h3>No Applications</h3>
                    <p>There are no applications in the system yet.</p>
                </div>
            </div>
        <% } %>
    </div>
</body>
</html>
