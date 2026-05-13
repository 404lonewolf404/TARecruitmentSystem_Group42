<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.bupt.tarecruitment.model.User" %>
<%@ page import="com.bupt.tarecruitment.model.Position" %>
<%@ page import="com.bupt.tarecruitment.service.NotificationService" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Set" %>
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
    
    @SuppressWarnings("unchecked")
    List<Position> positions = (List<Position>) request.getAttribute("positions");
    @SuppressWarnings("unchecked")
    Set<String> appliedPositionIds = (Set<String>) request.getAttribute("appliedPositionIds");
    @SuppressWarnings("unchecked")
    Set<String> favoritedPositionIds = (Set<String>) request.getAttribute("favoritedPositionIds");
    @SuppressWarnings("unchecked")
    java.util.Map<String, Integer> selectedCountMap = (java.util.Map<String, Integer>) request.getAttribute("selectedCountMap");
    String errorMessage = (String) request.getAttribute("errorMessage");
    
    // Get error message from session
    String sessionErrorMessage = (String) session.getAttribute("errorMessage");
    if (sessionErrorMessage != null) {
        session.removeAttribute("errorMessage");
        if (errorMessage == null) {
            errorMessage = sessionErrorMessage;
        }
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Browse Positions - TA Recruitment System</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>
    <header>
        <h1><i class="fas fa-graduation-cap"></i> TA Recruitment System</h1>
    </header>
    
    <nav>
        <ul>
            <li><a href="<%= request.getContextPath() %>/ta/dashboard"><i class="fas fa-home"></i>&nbsp;&nbsp;Dashboard</a></li>
            <li><a href="<%= request.getContextPath() %>/ta/profile"><i class="fas fa-user"></i>&nbsp;&nbsp;Profile</a></li>
            <li><a href="<%= request.getContextPath() %>/ta/positions"><i class="fas fa-briefcase"></i>&nbsp;&nbsp;Browse Positions</a></li>
            <li><a href="<%= request.getContextPath() %>/ta/applications/my"><i class="fas fa-file-alt"></i>&nbsp;&nbsp;My Applications</a></li>
            <li><a href="<%= request.getContextPath() %>/ta/favorites"><i class="fas fa-star"></i>&nbsp;&nbsp;Favorites</a></li>
            <li><a href="<%= request.getContextPath() %>/messages/list"><i class="fas fa-comments"></i>&nbsp;&nbsp;Messages</a></li>
            <li>
                <a href="<%= request.getContextPath() %>/ta/notifications">
                    <i class="fas fa-bell"></i>&nbsp;&nbsp;Notifications
                    <% if (unreadCount > 0) { %>
                        <span class="notification-badge"><%= unreadCount %></span>
                    <% } %>
                </a>
            </li>
            <li><a href="<%= request.getContextPath() %>/auth/logout"><i class="fas fa-sign-out-alt"></i>&nbsp;&nbsp;Logout</a></li>
        </ul>
    </nav>
    
    <div class="container">
        <div class="page-header">
            <h1><i class="fas fa-briefcase"></i> Browse Positions</h1>
            <p>Explore available TA positions and apply for the ones that interest you</p>
        </div>
        
        <!-- Search Panel -->
        <div class="card search-card">
            <h3 style="margin-top: 0;"><i class="fas fa-search"></i> Search & Filter</h3>
            <form method="get" action="<%= request.getContextPath() %>/ta/positions">
                <div class="form-group">
                    <input type="text" name="keyword" value="<%= request.getAttribute("keyword") != null ? request.getAttribute("keyword") : "" %>" 
                           placeholder="Search position title, description or requirements..." class="search-input">
                </div>
                
                <div style="display: grid; grid-template-columns: 1fr 1fr 1fr auto; gap: 15px; align-items: end;">
                    <div class="form-group">
                        <label>Min Hours</label>
                        <input type="number" name="minHours" value="<%= request.getAttribute("minHours") != null ? request.getAttribute("minHours") : "" %>" 
                               min="0" max="40" placeholder="0">
                    </div>
                    
                    <div class="form-group">
                        <label>Max Hours</label>
                        <input type="number" name="maxHours" value="<%= request.getAttribute("maxHours") != null ? request.getAttribute("maxHours") : "" %>" 
                               min="0" max="40" placeholder="40">
                    </div>
                    
                    <div class="form-group">
                        <label>Sort By</label>
                        <select name="sortBy">
                            <option value="newest" <%= "newest".equals(request.getAttribute("sortBy")) ? "selected" : "" %>>Newest First</option>
                            <option value="hours_asc" <%= "hours_asc".equals(request.getAttribute("sortBy")) ? "selected" : "" %>>Hours: Low to High</option>
                            <option value="hours_desc" <%= "hours_desc".equals(request.getAttribute("sortBy")) ? "selected" : "" %>>Hours: High to Low</option>
                        </select>
                    </div>
                    
                    <div>
                        <button type="submit" class="btn btn-primary" style="width: 100%;">Search</button>
                    </div>
                </div>
                
                <div style="margin-top: 10px;">
                    <a href="<%= request.getContextPath() %>/ta/positions" class="btn btn-secondary">Clear Filters</a>
                </div>
            </form>
            
            <% 
            String keyword = (String) request.getAttribute("keyword");
            Integer minHours = (Integer) request.getAttribute("minHours");
            Integer maxHours = (Integer) request.getAttribute("maxHours");
            boolean hasFilters = (keyword != null && !keyword.trim().isEmpty()) || minHours != null || maxHours != null;
            
            if (hasFilters && positions != null) { 
            %>
                <div style="margin-top: 15px; padding: 10px; background: #e3f2fd; border-left: 4px solid #2196F3; border-radius: 4px;">
                    <strong>Found <%= positions.size() %> position(s)</strong>
                    <% if (keyword != null && !keyword.trim().isEmpty()) { %>
                        | Keyword: "<%= keyword %>"
                    <% } %>
                    <% if (minHours != null || maxHours != null) { %>
                        | Hours: <%= minHours != null ? minHours : "0" %>-<%= maxHours != null ? maxHours : "N/A" %>
                    <% } %>
                </div>
            <% } %>
        </div>
        <% if (positions == null || positions.isEmpty()) { %>
            <div class="card" style="text-align: center; padding: 80px 40px;">
                <div style="font-size: 64px; color: #bbb; margin-bottom: 20px;">
                    <i class="fas fa-inbox"></i>
                </div>
                <h3 style="font-size: 24px; margin-bottom: 15px;">No Positions Available</h3>
                <p style="color: #666; font-size: 16px; margin-bottom: 30px;">
                    Try adjusting your search filters or check back later
                </p>
            </div>
        <% } else { %>
            <div style="display: flex; flex-direction: column; gap: 25px; margin-top: 20px; margin-bottom: 40px;">
            <% for (Position position : positions) { %>
                <div class="card position-card" style="display: flex; flex-direction: column;">
                    <div style="display: flex; justify-content: space-between; align-items: start; margin-bottom: 20px;">
                        <h3 style="margin: 0; flex: 1;"><%= position.getTitle() %></h3>
                        <span class="badge badge-<%= position.getStatus().toString().toLowerCase() %>" style="white-space: nowrap; margin-left: 10px;">
                            <%= position.getStatus() == com.bupt.tarecruitment.model.PositionStatus.OPEN ? "Open" : "Closed" %>
                        </span>
                    </div>
                    
                    <div style="flex: 1;">
                        <div style="margin-bottom: 20px; padding-bottom: 20px; border-bottom: 1px solid #eee;">
                            <p style="margin: 0; color: #666; font-size: 15px; line-height: 1.6;">
                                <%= position.getDescription() != null ? position.getDescription() : "No description" %>
                            </p>
                        </div>
                        
                        <div style="display: grid; grid-template-columns: 1fr 1fr 1fr; gap: 20px; margin-bottom: 20px;">
                            <div>
                                <p style="margin: 0; font-size: 12px; color: #999; text-transform: uppercase;">Hours/Week</p>
                                <p style="margin: 8px 0 0 0; font-size: 20px; font-weight: bold; color: #2563eb;">
                                    <%= position.getHours() %>h
                                </p>
                            </div>
                            <div>
                                <p style="margin: 0; font-size: 12px; color: #999; text-transform: uppercase;">Remaining Positions</p>
                                <% 
                                int selectedCount = selectedCountMap != null ? selectedCountMap.getOrDefault(position.getPositionId(), 0) : 0;
                                int remainingPositions = position.getMaxPositions() - selectedCount;
                                String remainingColor = remainingPositions > 0 ? "#27ae60" : "#e74c3c";
                                %>
                                <p style="margin: 8px 0 0 0; font-size: 20px; font-weight: bold; color: <%= remainingColor %>;">
                                    <%= remainingPositions %> / <%= position.getMaxPositions() %>
                                </p>
                            </div>
                            <div>
                                <p style="margin: 0; font-size: 12px; color: #999; text-transform: uppercase;">Status</p>
                                <p style="margin: 8px 0 0 0; font-size: 16px; font-weight: 600; color: #2563eb;">
                                    <%= position.getStatus() == com.bupt.tarecruitment.model.PositionStatus.OPEN ? "Open" : "Closed" %>
                                </p>
                            </div>
                        </div>
                        
                        <% if (position.getRequirements() != null && !position.getRequirements().isEmpty()) { %>
                            <div style="margin-bottom: 20px; padding: 15px; background: #f5f5f5; border-radius: 4px;">
                                <p style="margin: 0 0 8px 0; font-size: 12px; color: #999; text-transform: uppercase; font-weight: 600;">Requirements</p>
                                <p style="margin: 0; font-size: 14px; color: #555; line-height: 1.6;">
                                    <%= position.getRequirements() %>
                                </p>
                            </div>
                        <% } %>
                        
                        <%-- Deadline --%>
                        <% if (position.getDeadline() != null) { %>
                            <div style="margin-bottom: 20px; padding: 15px; background: #f5f5f5; border-radius: 4px;">
                                <p style="margin: 0; font-size: 13px; color: #666;">
                                    <i class="fas fa-calendar"></i> Deadline: <%= new java.text.SimpleDateFormat("MMM dd, yyyy").format(position.getDeadline()) %>
                                </p>
                                <% 
                                int daysRemaining = position.getDaysRemaining();
                                if (daysRemaining > 0) { 
                                    String urgencyColor = daysRemaining <= 3 ? "#dc3545" : (daysRemaining <= 7 ? "#ffc107" : "#28a745");
                                    String urgencyText = daysRemaining <= 3 ? "Urgent" : (daysRemaining <= 7 ? "Soon" : "");
                                %>
                                    <p style="margin: 8px 0 0 0; font-size: 13px; color: <%= urgencyColor %>; font-weight: bold;">
                                        <%= daysRemaining %> day(s) remaining <%= urgencyText %>
                                    </p>
                                <% } else if (position.isExpired()) { %>
                                    <p style="margin: 8px 0 0 0; font-size: 13px; color: #dc3545; font-weight: bold;">
                                        <i class="fas fa-times-circle"></i> Expired
                                    </p>
                                <% } %>
                            </div>
                        <% } %>
                    </div>
                    
                    <% 
                    boolean hasApplied = appliedPositionIds != null && appliedPositionIds.contains(position.getPositionId());
                    boolean isFavorited = favoritedPositionIds != null && favoritedPositionIds.contains(position.getPositionId());
                    boolean canApply = position.canAcceptApplications();
                    %>
                    
                    <div style="display: flex; gap: 12px; margin-top: auto; padding-top: 20px; border-top: 1px solid #eee;">
                        <% if (hasApplied) { %>
                            <button type="button" class="btn btn-secondary" disabled style="flex: 1;">
                                <i class="fas fa-check"></i> Applied
                            </button>
                        <% } else if (!canApply) { %>
                            <button type="button" class="btn btn-secondary" disabled style="flex: 1;">
                                <i class="fas fa-lock"></i> Cannot Apply
                            </button>
                        <% } else { %>
                            <button type="button" class="btn btn-primary" style="flex: 1;" 
                                    onclick="openApplyModal('<%= position.getPositionId() %>')">
                                <i class="fas fa-paper-plane"></i> Apply
                            </button>
                        <% } %>
                        
                        <button type="button" class="btn btn-secondary" 
                                id="favoriteBtn_<%= position.getPositionId() %>"
                                onclick="toggleFavorite('<%= position.getPositionId() %>', <%= isFavorited %>)"
                                style="width: 50px; padding: 0;">
                            <i class="fas fa-<%= isFavorited ? "star" : "star" %>" style="color: <%= isFavorited ? "#ffc107" : "#ccc" %>;"></i>
                        </button>
                    </div>
                </div>
            <% } %>
            </div>
        <% } %>
        
        <div style="text-align: center; margin-top: 40px;">
            <a href="<%= request.getContextPath() %>/ta/dashboard" class="btn btn-secondary">
                <i class="fas fa-arrow-left"></i> Back to Dashboard
            </a>
        </div>
    </div>
    
    <!-- Apply Modal -->
    <div id="applyModal" style="display: none; position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.5); z-index: 1000; align-items: center; justify-content: center;">
        <div class="card" style="width: 90%; max-width: 500px; max-height: 80vh; overflow-y: auto;">
            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
                <h2 style="margin: 0;">Apply for Position</h2>
                <button type="button" onclick="closeApplyModal()" style="background: none; border: none; font-size: 24px; cursor: pointer;">&times;</button>
            </div>
            
            <form id="applyForm" action="<%= request.getContextPath() %>/ta/applications/apply" method="post" enctype="multipart/form-data">
                <input type="hidden" name="positionId" id="modalPositionId">
                
                <div class="form-group">
                    <label><strong>Select Resume</strong></label>
                    <div style="margin-top: 10px;">
                        <label style="display: flex; align-items: center; margin-bottom: 10px; cursor: pointer;">
                            <input type="radio" name="resumeChoice" value="existing" checked onchange="toggleResumeUpload(this)">
                            <span style="margin-left: 10px;">Use uploaded resume</span>
                            <% if (currentUser.getCvPath() == null || currentUser.getCvPath().trim().isEmpty()) { %>
                                <span style="color: #e74c3c; font-size: 0.9em; margin-left: 10px;">(Not uploaded)</span>
                            <% } %>
                        </label>
                        
                        <label style="display: flex; align-items: center; cursor: pointer;">
                            <input type="radio" name="resumeChoice" value="new" onchange="toggleResumeUpload(this)">
                            <span style="margin-left: 10px;">Upload new resume</span>
                        </label>
                    </div>
                </div>
                
                <div id="newResumeUploadModal" style="display: none; margin-top: 15px; padding: 15px; background: #f5f5f5; border-radius: 4px;">
                    <label style="display: block; margin-bottom: 10px;"><strong>Select PDF file</strong></label>
                    <input type="file" name="newResume" accept=".pdf" style="display: block; width: 100%;">
                    <small style="color: #666; display: block; margin-top: 5px;">PDF format only, max 10MB</small>
                </div>
                
                <div style="display: flex; gap: 10px; margin-top: 20px;">
                    <button type="submit" class="btn btn-primary" style="flex: 1;" onclick="return validateResumeSelection(document.getElementById('applyForm'));">
                        <i class="fas fa-check"></i> Apply
                    </button>
                    <button type="button" class="btn btn-secondary" style="flex: 1;" onclick="closeApplyModal();">
                        Cancel
                    </button>
                </div>
            </form>
        </div>
    </div>
    
    <script>
    function openApplyModal(positionId) {
        document.getElementById('modalPositionId').value = positionId;
        document.getElementById('applyModal').style.display = 'flex';
        document.getElementById('newResumeUploadModal').style.display = 'none';
        document.querySelector('input[name="resumeChoice"][value="existing"]').checked = true;
    }
    
    function closeApplyModal() {
        document.getElementById('applyModal').style.display = 'none';
    }
    
    function toggleResumeUpload(radio) {
        var uploadDiv = document.getElementById('newResumeUploadModal');
        if (radio.value === 'new') {
            uploadDiv.style.display = 'block';
        } else {
            uploadDiv.style.display = 'none';
        }
    }
    
    function validateResumeSelection(form) {
        var resumeChoice = form.querySelector('input[name="resumeChoice"]:checked').value;
        
        if (resumeChoice === 'existing') {
            <% if (currentUser.getCvPath() == null || currentUser.getCvPath().trim().isEmpty()) { %>
                alert('You have not uploaded a resume yet. Please upload one in your profile or select to upload a new resume.');
                return false;
            <% } %>
        } else if (resumeChoice === 'new') {
            var fileInput = form.querySelector('input[name="newResume"]');
            if (!fileInput.files || fileInput.files.length === 0) {
                alert('Please select a resume file to upload');
                return false;
            }
            
            var file = fileInput.files[0];
            if (!file.name.toLowerCase().endsWith('.pdf')) {
                alert('Only PDF format is supported for resume files');
                return false;
            }
            
            if (file.size > 10 * 1024 * 1024) {
                alert('File size cannot exceed 10MB');
                return false;
            }
        }
        
        return confirm('Are you sure you want to apply for this position?');
    }
    
    function toggleFavorite(positionId, isFavorited) {
        var btn = document.getElementById('favoriteBtn_' + positionId);
        var action = isFavorited ? 'remove' : 'add';
        var url = '<%= request.getContextPath() %>/ta/favorites/' + action;
        
        btn.disabled = true;
        
        var params = new URLSearchParams();
        params.append('positionId', positionId);
        
        fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: params.toString()
        })
        .then(response => {
            if (response.ok) {
                if (isFavorited) {
                    btn.innerHTML = '<i class="fas fa-star" style="color: #ccc;"></i>';
                    btn.onclick = function() { toggleFavorite(positionId, false); };
                } else {
                    btn.innerHTML = '<i class="fas fa-star" style="color: #ffc107;"></i>';
                    btn.onclick = function() { toggleFavorite(positionId, true); };
                }
            } else {
                alert('Operation failed, please try again');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('Operation failed, please try again');
        })
        .finally(() => {
            btn.disabled = false;
        });
    }
    </script>
    
    <script src="<%= request.getContextPath() %>/js/main.js"></script>
</body>
</html>
