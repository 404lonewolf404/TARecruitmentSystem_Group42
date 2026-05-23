package com.bupt.tarecruitment.servlet;

import com.bupt.tarecruitment.model.Application;
import com.bupt.tarecruitment.model.Position;
import com.bupt.tarecruitment.model.User;
import com.bupt.tarecruitment.model.UserRole;
import com.bupt.tarecruitment.service.ApplicationService;
import com.bupt.tarecruitment.service.PositionService;
import com.bupt.tarecruitment.service.NotificationService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles TA application submission and MO application review flows.
 */
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2,  // 中文说明：超过 2MB 后写入磁盘临时文件。
    maxFileSize = 1024 * 1024 * 10,       // 中文说明：单个上传文件最大 10MB。
    maxRequestSize = 1024 * 1024 * 50     // 中文说明：整个请求最大 50MB。
)
public class ApplicationServlet extends HttpServlet {
    
    private ApplicationService applicationService;
    private PositionService positionService;
    private NotificationService notificationService;
    
    @Override
    public void init() throws ServletException {
        super.init();
        this.applicationService = new ApplicationService();
        this.positionService = new PositionService();
        this.notificationService = new NotificationService();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        
        // 中文说明：输出路径调试信息，便于排查 servlet 映射问题。
        System.out.println("ApplicationServlet.doGet() called");
        System.out.println("Request URI: " + request.getRequestURI());
        System.out.println("Context Path: " + request.getContextPath());
        System.out.println("Servlet Path: " + request.getServletPath());
        System.out.println("Path Info: " + pathInfo);
        
        if (pathInfo == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request path");
            return;
        }
        
        // 中文说明：兼容带尾部斜杠的路径。
        if (pathInfo.endsWith("/") && pathInfo.length() > 1) {
            pathInfo = pathInfo.substring(0, pathInfo.length() - 1);
        }
        
        System.out.println("Processed Path Info: " + pathInfo);
        
        switch (pathInfo) {
            case "/apply":
                handleShowApplyForm(request, response);
                break;
            case "/my":
                // 中文说明：查看当前 TA 的申请列表。
                System.out.println("Handling /my request");
                handleViewMyApplications(request, response);
                break;
            case "/position":
                // 中文说明：查看某个岗位的申请列表。
                System.out.println("Handling /position request");
                handleViewPositionApplications(request, response);
                break;
            default:
                System.out.println("No matching case, returning 404");
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource not found");
                break;
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 中文说明：统一使用 UTF-8 处理表单提交。
        request.setCharacterEncoding("UTF-8");
        
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request path");
            return;
        }
        
        switch (pathInfo) {
            case "/apply":
                // 中文说明：处理提交岗位申请。
                handleApplyForPosition(request, response);
                break;
            case "/withdraw":
                // 中文说明：处理撤回申请。
                handleWithdrawApplication(request, response);
                break;
            case "/select":
                // 中文说明：处理 MO 录用申请人。
                handleSelectApplicant(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource not found");
                break;
        }
    }
    
    /**
     * Shows the application form for a TA.
     */
    private void handleShowApplyForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        
        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }
        
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }
        
        // 中文说明：仅 TA 可以申请岗位。
        if (currentUser.getRole() != UserRole.TA) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Only TAs can apply for positions");
            return;
        }
        
        // 中文说明：读取目标岗位 ID。
        String positionId = request.getParameter("positionId");
        if (positionId == null || positionId.trim().isEmpty()) {
            session.setAttribute("errorMessage", "Position ID cannot be empty");
            response.sendRedirect(request.getContextPath() + "/ta/positions");
            return;
        }
        
        // 中文说明：确认岗位存在。
        Position position = positionService.getPositionById(positionId.trim());
        if (position == null) {
            session.setAttribute("errorMessage", "Position not found");
            response.sendRedirect(request.getContextPath() + "/ta/positions");
            return;
        }
        
        // 中文说明：仅允许申请仍可接收申请的岗位。
        if (!position.canAcceptApplications()) {
            String reason = "";
            if (position.isExpired()) {
                reason = "This position deadline has passed";
            } else if (position.getStatus() == com.bupt.tarecruitment.model.PositionStatus.CLOSED) {
                reason = "This position is closed";
            } else {
                reason = "This position is not accepting applications currently";
            }
            session.setAttribute("errorMessage", reason);
            response.sendRedirect(request.getContextPath() + "/ta/positions");
            return;
        }
        
        // 中文说明：写入岗位信息并转发到申请页。
        request.setAttribute("position", position);
        request.getRequestDispatcher("/WEB-INF/jsp/ta/apply-position.jsp").forward(request, response);
    }
    
    /**
     * Submits a new application for a TA.
     */
    private void handleApplyForPosition(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 中文说明：读取当前会话。
        HttpSession session = request.getSession(false);
        
        try {
            if (session == null) {
                response.sendRedirect(request.getContextPath() + "/auth/login");
                return;
            }
            
            User currentUser = (User) session.getAttribute("user");
            if (currentUser == null) {
                response.sendRedirect(request.getContextPath() + "/auth/login");
                return;
            }
            
            // 中文说明：仅 TA 可以提交岗位申请。
            if (currentUser.getRole() != UserRole.TA) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Only TAs can apply for positions");
                return;
            }
            
            // 中文说明：从 multipart 请求中提取岗位、简历选择和上传文件。
            String positionId = null;
            String resumeChoice = null;
            Part filePart = null;
            
            // 中文说明：遍历表单部件并提取所需值。
            for (Part part : request.getParts()) {
                String partName = part.getName();
                if ("positionId".equals(partName)) {
                    positionId = getValue(part);
                } else if ("resumeChoice".equals(partName)) {
                    resumeChoice = getValue(part);
                } else if ("newResume".equals(partName) && part.getSize() > 0) {
                    filePart = part;
                }
            }
            
            if (positionId == null || positionId.trim().isEmpty()) {
                session.setAttribute("errorMessage", "Position ID cannot be empty");
                response.sendRedirect(request.getContextPath() + "/ta/positions");
                return;
            }
            
            String resumePath = null;
            
            if ("new".equals(resumeChoice)) {
                // 中文说明：用户选择上传新简历文件。
                if (filePart != null && filePart.getSize() > 0) {
                    String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                    
                    // 中文说明：当前仅允许上传 PDF 简历。
                    if (!fileName.toLowerCase().endsWith(".pdf")) {
                        session.setAttribute("errorMessage", "Only PDF resume files are supported");
                        response.sendRedirect(request.getContextPath() + "/ta/positions");
                        return;
                    }
                    
                    // 中文说明：为上传文件生成唯一文件名并确保目录存在。
                    String uniqueFileName = System.currentTimeMillis() + "_" + fileName;
                    String uploadPath = getServletContext().getRealPath("") + File.separator + "uploads";
                    File uploadDir = new File(uploadPath);
                    if (!uploadDir.exists()) {
                        uploadDir.mkdirs();
                    }
                    
                    String filePath = uploadPath + File.separator + uniqueFileName;
                    filePart.write(filePath);
                    
                    // 中文说明：保存相对路径到申请记录中。
                    resumePath = "uploads/" + uniqueFileName;
                } else {
                    session.setAttribute("errorMessage", "Please select a resume file to upload");
                    response.sendRedirect(request.getContextPath() + "/ta/positions");
                    return;
                }
            } else {
                // 中文说明：复用用户资料里已有的简历路径。
                resumePath = currentUser.getCvPath();
                
                if (resumePath == null || resumePath.trim().isEmpty()) {
                    session.setAttribute("errorMessage", "No resume found. Upload one in your profile or upload a new file now.");
                    response.sendRedirect(request.getContextPath() + "/ta/positions");
                    return;
                }
            }
            
            // 中文说明：再次确认岗位存在且仍可申请。
            Position position = positionService.getPositionById(positionId.trim());
            if (position == null) {
                session.setAttribute("errorMessage", "Position not found");
                response.sendRedirect(request.getContextPath() + "/ta/positions");
                return;
            }
            
            if (!position.canAcceptApplications()) {
                String reason = "";
                if (position.isExpired()) {
                    reason = "This position deadline has passed";
                } else if (position.getStatus() == com.bupt.tarecruitment.model.PositionStatus.CLOSED) {
                    reason = "This position is closed";
                } else {
                    reason = "This position is not accepting applications currently";
                }
                session.setAttribute("errorMessage", reason);
                response.sendRedirect(request.getContextPath() + "/ta/positions");
                return;
            }
            
            // 中文说明：创建申请记录。
            applicationService.applyForPosition(currentUser.getUserId(), positionId.trim(), resumePath);
            
            // 中文说明：通知岗位所属 MO 有新申请。
            try {
                if (position != null) {
                    notificationService.sendNewApplicationNotification(
                        position.getMoId(),
                        currentUser.getUserId(),
                        positionId.trim()
                    );
                }
            } catch (Exception e) {
                // 中文说明：通知失败不影响主流程。
                e.printStackTrace();
            }
            
            // 中文说明：写入成功提示。
            session.setAttribute("successMessage", "Application submitted successfully");
            
            // 中文说明：成功后跳到我的申请列表。
            response.sendRedirect(request.getContextPath() + "/ta/applications/my");
            
        } catch (IllegalArgumentException e) {
            // 中文说明：业务校验失败时回到岗位列表。
            if (session != null) {
                session.setAttribute("errorMessage", e.getMessage());
            }
            response.sendRedirect(request.getContextPath() + "/ta/positions");
            
        } catch (IOException e) {
            // 中文说明：IO 失败时回到岗位列表。
            if (session != null) {
                session.setAttribute("errorMessage", "Failed to apply for position: " + e.getMessage());
            }
            response.sendRedirect(request.getContextPath() + "/ta/positions");
        }
    }
    
    /**
     * Reads a plain text value from a multipart part.
     */
    private String getValue(Part part) throws IOException {
        java.io.BufferedReader reader = new java.io.BufferedReader(
            new java.io.InputStreamReader(part.getInputStream(), "UTF-8"));
        StringBuilder value = new StringBuilder();
        char[] buffer = new char[1024];
        int length;
        while ((length = reader.read(buffer)) > 0) {
            value.append(buffer, 0, length);
        }
        return value.toString();
    }
    
    /**
     * Withdraws an existing application.
     */
    private void handleWithdrawApplication(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            // 中文说明：只有已登录用户才能撤回申请。
            HttpSession session = request.getSession(false);
            if (session == null) {
                response.sendRedirect(request.getContextPath() + "/auth/login");
                return;
            }
            
            User currentUser = (User) session.getAttribute("user");
            if (currentUser == null) {
                response.sendRedirect(request.getContextPath() + "/auth/login");
                return;
            }
            
            // 中文说明：撤回申请仅对 TA 角色开放。
            if (currentUser.getRole() != UserRole.TA) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Only TAs can withdraw applications");
                return;
            }
            
            // 中文说明：读取目标申请 ID。
            String applicationId = request.getParameter("applicationId");
            
            if (applicationId == null || applicationId.trim().isEmpty()) {
                session.setAttribute("errorMessage", "Application ID cannot be empty");
                response.sendRedirect(request.getContextPath() + "/ta/applications/my");
                return;
            }
            
            // 中文说明：先读取申请信息，供后续通知使用。
            Application application = applicationService.getApplicationById(applicationId.trim());
            
            // 中文说明：执行撤回操作。
            applicationService.withdrawApplication(applicationId.trim());
            
            // 中文说明：通知对应 MO 有申请被撤回。
            if (application != null) {
                try {
                    Position position = positionService.getPositionById(application.getPositionId());
                    if (position != null) {
                        notificationService.sendApplicationWithdrawnNotification(
                            position.getMoId(),
                            currentUser.getUserId(),
                            application.getPositionId()
                        );
                    }
                } catch (Exception e) {
                    // 中文说明：通知失败不影响主流程。
                    e.printStackTrace();
                }
            }
            
            // 中文说明：返回我的申请列表。
            response.sendRedirect(request.getContextPath() + "/ta/applications/my");
            
        } catch (IllegalArgumentException e) {
            // 中文说明：业务校验失败时回到我的申请列表。
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.setAttribute("errorMessage", e.getMessage());
            }
            response.sendRedirect(request.getContextPath() + "/ta/applications/my");
            
        } catch (IOException e) {
            // 中文说明：IO 失败时回到我的申请列表。
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.setAttribute("errorMessage", "Failed to withdraw application: " + e.getMessage());
            }
            response.sendRedirect(request.getContextPath() + "/ta/applications/my");
        }
    }
    
    /**
     * Displays the current TA's application list.
     */
    private void handleViewMyApplications(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            System.out.println("=== handleViewMyApplications START ===");
            
            // 中文说明：检查登录状态。
            HttpSession session = request.getSession(false);
            System.out.println("Session: " + (session != null ? "exists" : "null"));
            
            if (session == null) {
                System.out.println("No session, redirecting to login");
                response.sendRedirect(request.getContextPath() + "/auth/login");
                return;
            }
            
            User currentUser = (User) session.getAttribute("user");
            System.out.println("Current user: " + (currentUser != null ? currentUser.getUserId() : "null"));
            
            if (currentUser == null) {
                System.out.println("No user in session, redirecting to login");
                response.sendRedirect(request.getContextPath() + "/auth/login");
                return;
            }
            
            // 中文说明：仅 TA 可以查看自己的申请列表。
            System.out.println("User role: " + currentUser.getRole());
            if (currentUser.getRole() != UserRole.TA) {
                System.out.println("User is not TA, sending 403");
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Only TAs can view their own applications");
                return;
            }
            
            // 中文说明：先清理已过期岗位对应的待处理申请。
            try {
                applicationService.cleanupExpiredPositionApplications();
            } catch (IOException e) {
                System.out.println("Warning: Failed to cleanup expired applications: " + e.getMessage());
            }
            
            // 中文说明：加载当前 TA 的全部申请。
            System.out.println("Calling applicationService.getApplicationsByTA()");
            List<Application> applications = applicationService.getApplicationsByTA(currentUser.getUserId());
            System.out.println("Retrieved " + (applications != null ? applications.size() : "null") + " applications");
            
            // 中文说明：写入页面渲染所需数据。
            request.setAttribute("applications", applications);
            System.out.println("Set applications attribute");
            
            // 中文说明：转发到 TA 申请列表页。
            String jspPath = "/WEB-INF/jsp/ta/applications.jsp";
            System.out.println("Forwarding to: " + jspPath);
            request.getRequestDispatcher(jspPath).forward(request, response);
            System.out.println("Forward completed successfully");
            
        } catch (Exception e) {
            System.out.println("=== EXCEPTION in handleViewMyApplications ===");
            System.out.println("Exception type: " + e.getClass().getName());
            System.out.println("Exception message: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("errorMessage", "Failed to load applications: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(request, response);
        }
    }
    
    /**
     * Displays all applications for one MO-owned position.
     */
    private void handleViewPositionApplications(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            // 中文说明：只有已登录用户才能查看岗位申请列表。
            HttpSession session = request.getSession(false);
            if (session == null) {
                response.sendRedirect(request.getContextPath() + "/auth/login");
                return;
            }
            
            User currentUser = (User) session.getAttribute("user");
            if (currentUser == null) {
                response.sendRedirect(request.getContextPath() + "/auth/login");
                return;
            }
            
            // 中文说明：仅 MO 可以查看岗位申请列表。
            if (currentUser.getRole() != UserRole.MO) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Only MOs can view position applications");
                return;
            }
            
            // 中文说明：先清理已过期岗位对应的待处理申请。
            try {
                applicationService.cleanupExpiredPositionApplications();
            } catch (IOException e) {
                System.out.println("Warning: Failed to cleanup expired applications: " + e.getMessage());
            }
            
            // 中文说明：读取目标岗位 ID。
            String positionId = request.getParameter("positionId");
            
            if (positionId == null || positionId.trim().isEmpty()) {
                session.setAttribute("errorMessage", "Position ID cannot be empty");
                response.sendRedirect(request.getContextPath() + "/mo/positions/my");
                return;
            }
            
            // 中文说明：确认岗位存在。
            Position position = positionService.getPositionById(positionId.trim());
            if (position == null) {
                session.setAttribute("errorMessage", "Position not found");
                response.sendRedirect(request.getContextPath() + "/mo/positions/my");
                return;
            }
            
            // 中文说明：加载该岗位的全部申请。
            List<Application> applications = applicationService.getApplicationsByPosition(positionId.trim());
            
            // 中文说明：读取可选状态筛选条件。
            String statusFilter = request.getParameter("status");
            
            // 中文说明：按状态过滤结果列表。
            if (statusFilter != null && !statusFilter.equals("all") && !statusFilter.isEmpty()) {
                try {
                    com.bupt.tarecruitment.model.ApplicationStatus filterStatus = 
                        com.bupt.tarecruitment.model.ApplicationStatus.valueOf(statusFilter.toUpperCase());
                    applications = applications.stream()
                        .filter(app -> app.getStatus() == filterStatus)
                        .collect(java.util.stream.Collectors.toList());
                } catch (IllegalArgumentException e) {
                    // 中文说明：非法筛选值时忽略过滤条件。
                }
            }
            
            // 中文说明：写入页面渲染所需数据。
            request.setAttribute("position", position);
            request.setAttribute("applications", applications);
            request.setAttribute("statusFilter", statusFilter != null ? statusFilter : "all");
            
            // 中文说明：转发到 MO 申请列表页。
            request.getRequestDispatcher("/WEB-INF/jsp/mo/applications.jsp").forward(request, response);
            
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Failed to load applications: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(request, response);
        }
    }
    
    /**
     * Selects an applicant for an MO-owned position.
     */
    private void handleSelectApplicant(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            // 中文说明：只有已登录用户才能执行录用操作。
            HttpSession session = request.getSession(false);
            if (session == null) {
                response.sendRedirect(request.getContextPath() + "/auth/login");
                return;
            }
            
            User currentUser = (User) session.getAttribute("user");
            if (currentUser == null) {
                response.sendRedirect(request.getContextPath() + "/auth/login");
                return;
            }
            
            // 中文说明：录用操作仅对 MO 角色开放。
            if (currentUser.getRole() != UserRole.MO) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Only MOs can select applicants");
                return;
            }
            
            // 中文说明：读取申请 ID 和回跳岗位 ID。
            String applicationId = request.getParameter("applicationId");
            String positionId = request.getParameter("positionId");
            
            if (applicationId == null || applicationId.trim().isEmpty()) {
                session.setAttribute("errorMessage", "Application ID cannot be empty");
                if (positionId != null && !positionId.trim().isEmpty()) {
                    response.sendRedirect(request.getContextPath() + "/mo/applications/position?positionId=" + positionId);
                } else {
                    response.sendRedirect(request.getContextPath() + "/mo/positions/my");
                }
                return;
            }
            
            // 中文说明：执行录用逻辑。
            applicationService.selectApplicant(applicationId.trim());
            
            // 中文说明：向该岗位所有相关申请人发送状态通知。
            try {
                Application selectedApp = applicationService.getApplicationById(applicationId.trim());
                if (selectedApp != null) {
                    List<Application> allApps = applicationService.getApplicationsByPositionId(selectedApp.getPositionId());
                    for (Application app : allApps) {
                        notificationService.sendApplicationStatusNotification(
                            app.getTaId(), 
                            app.getPositionId(), 
                            app.getStatus()
                        );
                    }
                }
            } catch (Exception e) {
                // 中文说明：通知失败不影响主流程。
                e.printStackTrace();
            }
            
            // 中文说明：完成后回到岗位申请页或岗位列表。
            if (positionId != null && !positionId.trim().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/mo/applications/position?positionId=" + positionId);
            } else {
                response.sendRedirect(request.getContextPath() + "/mo/positions/my");
            }
            
        } catch (IllegalArgumentException e) {
            // 中文说明：业务校验失败时回到来源页面。
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.setAttribute("errorMessage", e.getMessage());
            }
            String positionId = request.getParameter("positionId");
            if (positionId != null && !positionId.trim().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/mo/applications/position?positionId=" + positionId);
            } else {
                response.sendRedirect(request.getContextPath() + "/mo/positions/my");
            }
            
        } catch (IOException e) {
            // 中文说明：IO 失败时回到来源页面。
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.setAttribute("errorMessage", "Failed to select applicant: " + e.getMessage());
            }
            String positionId = request.getParameter("positionId");
            if (positionId != null && !positionId.trim().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/mo/applications/position?positionId=" + positionId);
            } else {
                response.sendRedirect(request.getContextPath() + "/mo/positions/my");
            }
        }
    }
}

