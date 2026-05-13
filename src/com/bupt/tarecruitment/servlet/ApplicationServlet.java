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
 * 闁槒绶拠瀛樻閵?
 * 闁槒绶拠瀛樻閵?
 */
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2,  // 2MB
    maxFileSize = 1024 * 1024 * 10,       // 10MB
    maxRequestSize = 1024 * 1024 * 50     // 50MB
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
        
        // 闁槒绶拠瀛樻閵?
        System.out.println("ApplicationServlet.doGet() called");
        System.out.println("Request URI: " + request.getRequestURI());
        System.out.println("Context Path: " + request.getContextPath());
        System.out.println("Servlet Path: " + request.getServletPath());
        System.out.println("Path Info: " + pathInfo);
        
        if (pathInfo == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request path");
            return;
        }
        
        // 闁槒绶拠瀛樻閵?
        if (pathInfo.endsWith("/") && pathInfo.length() > 1) {
            pathInfo = pathInfo.substring(0, pathInfo.length() - 1);
        }
        
        System.out.println("Processed Path Info: " + pathInfo);
        
        switch (pathInfo) {
            case "/apply":
                handleShowApplyForm(request, response);
                break;
            case "/my":
                // 闁槒绶拠瀛樻閵?
                System.out.println("Handling /my request");
                handleViewMyApplications(request, response);
                break;
            case "/position":
                // 闁槒绶拠瀛樻閵?
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
        
        // 闁槒绶拠瀛樻閵?
        request.setCharacterEncoding("UTF-8");
        
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request path");
            return;
        }
        
        switch (pathInfo) {
            case "/apply":
                // 闁槒绶拠瀛樻閵?
                handleApplyForPosition(request, response);
                break;
            case "/withdraw":
                // 闁槒绶拠瀛樻閵?
                handleWithdrawApplication(request, response);
                break;
            case "/select":
                // 闁槒绶拠瀛樻閵?
                handleSelectApplicant(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource not found");
                break;
        }
    }
    
    /**
     * 闁槒绶拠瀛樻閵?
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
        
        // 闁槒绶拠瀛樻閵?
        if (currentUser.getRole() != UserRole.TA) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Only TAs can apply for positions");
            return;
        }
        
        // 闁槒绶拠瀛樻閵?
        String positionId = request.getParameter("positionId");
        if (positionId == null || positionId.trim().isEmpty()) {
            session.setAttribute("errorMessage", "Position ID cannot be empty");
            response.sendRedirect(request.getContextPath() + "/ta/positions");
            return;
        }
        
        // 闁槒绶拠瀛樻閵?
        Position position = positionService.getPositionById(positionId.trim());
        if (position == null) {
            session.setAttribute("errorMessage", "Position not found");
            response.sendRedirect(request.getContextPath() + "/ta/positions");
            return;
        }
        
        // 闁槒绶拠瀛樻閵?
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
        
        // 闁槒绶拠瀛樻閵?
        request.setAttribute("position", position);
        request.getRequestDispatcher("/WEB-INF/jsp/ta/apply-position.jsp").forward(request, response);
    }
    
    /**
     * 闁槒绶拠瀛樻閵?
     * 闁槒绶拠瀛樻閵?
     * 闁槒绶拠瀛樻閵?
     */
    private void handleApplyForPosition(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 闁槒绶拠瀛樻閵?
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
            
            // 闁槒绶拠瀛樻閵?
            if (currentUser.getRole() != UserRole.TA) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Only TAs can apply for positions");
                return;
            }
            
            // 闁槒绶拠瀛樻閵?
            // 闁槒绶拠瀛樻閵?
            String positionId = null;
            String resumeChoice = null;
            Part filePart = null;
            
            // 闁槒绶拠瀛樻閵?
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
                // 闁槒绶拠瀛樻閵?
                if (filePart != null && filePart.getSize() > 0) {
                    String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                    
                    // 闁槒绶拠瀛樻閵?
                    if (!fileName.toLowerCase().endsWith(".pdf")) {
                        session.setAttribute("errorMessage", "Only PDF resume files are supported");
                        response.sendRedirect(request.getContextPath() + "/ta/positions");
                        return;
                    }
                    
                    // 闁槒绶拠瀛樻閵?
                    String uniqueFileName = System.currentTimeMillis() + "_" + fileName;
                    String uploadPath = getServletContext().getRealPath("") + File.separator + "uploads";
                    File uploadDir = new File(uploadPath);
                    if (!uploadDir.exists()) {
                        uploadDir.mkdirs();
                    }
                    
                    String filePath = uploadPath + File.separator + uniqueFileName;
                    filePart.write(filePath);
                    
                    // 闁槒绶拠瀛樻閵?
                    resumePath = "uploads/" + uniqueFileName;
                } else {
                    session.setAttribute("errorMessage", "Please select a resume file to upload");
                    response.sendRedirect(request.getContextPath() + "/ta/positions");
                    return;
                }
            } else {
                // 闁槒绶拠瀛樻閵?
                resumePath = currentUser.getCvPath();
                
                if (resumePath == null || resumePath.trim().isEmpty()) {
                    session.setAttribute("errorMessage", "No resume found. Upload one in your profile or upload a new file now.");
                    response.sendRedirect(request.getContextPath() + "/ta/positions");
                    return;
                }
            }
            
            // 闁槒绶拠瀛樻閵?
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
            
            // 闁槒绶拠瀛樻閵?
            applicationService.applyForPosition(currentUser.getUserId(), positionId.trim(), resumePath);
            
            // 闁槒绶拠瀛樻閵?
            try {
                if (position != null) {
                    notificationService.sendNewApplicationNotification(
                        position.getMoId(),
                        currentUser.getUserId(),
                        positionId.trim()
                    );
                }
            } catch (Exception e) {
                // 闁槒绶拠瀛樻閵?
                e.printStackTrace();
            }
            
            // 闁槒绶拠瀛樻閵?
            session.setAttribute("successMessage", "Application submitted successfully");
            
            // 闁槒绶拠瀛樻閵?
            response.sendRedirect(request.getContextPath() + "/ta/applications/my");
            
        } catch (IllegalArgumentException e) {
            // 闁槒绶拠瀛樻閵?
            if (session != null) {
                session.setAttribute("errorMessage", e.getMessage());
            }
            response.sendRedirect(request.getContextPath() + "/ta/positions");
            
        } catch (IOException e) {
            // 闁槒绶拠瀛樻閵?
            if (session != null) {
                session.setAttribute("errorMessage", "Failed to apply for position: " + e.getMessage());
            }
            response.sendRedirect(request.getContextPath() + "/ta/positions");
        }
    }
    
    /**
     * 闁槒绶拠瀛樻閵?
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
     * 闁槒绶拠瀛樻閵?
     * 闁槒绶拠瀛樻閵?
     */
    private void handleWithdrawApplication(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            // 闁槒绶拠瀛樻閵?
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
            
            // 闁槒绶拠瀛樻閵?
            if (currentUser.getRole() != UserRole.TA) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Only TAs can withdraw applications");
                return;
            }
            
            // 闁槒绶拠瀛樻閵?
            String applicationId = request.getParameter("applicationId");
            
            if (applicationId == null || applicationId.trim().isEmpty()) {
                session.setAttribute("errorMessage", "Application ID cannot be empty");
                response.sendRedirect(request.getContextPath() + "/ta/applications/my");
                return;
            }
            
            // 闁槒绶拠瀛樻閵?
            Application application = applicationService.getApplicationById(applicationId.trim());
            
            // 闁槒绶拠瀛樻閵?
            applicationService.withdrawApplication(applicationId.trim());
            
            // 闁槒绶拠瀛樻閵?
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
                    // 闁槒绶拠瀛樻閵?
                    e.printStackTrace();
                }
            }
            
            // 闁槒绶拠瀛樻閵?
            response.sendRedirect(request.getContextPath() + "/ta/applications/my");
            
        } catch (IllegalArgumentException e) {
            // 闁槒绶拠瀛樻閵?
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.setAttribute("errorMessage", e.getMessage());
            }
            response.sendRedirect(request.getContextPath() + "/ta/applications/my");
            
        } catch (IOException e) {
            // 闁槒绶拠瀛樻閵?
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.setAttribute("errorMessage", "Failed to withdraw application: " + e.getMessage());
            }
            response.sendRedirect(request.getContextPath() + "/ta/applications/my");
        }
    }
    
    /**
     * 闁槒绶拠瀛樻閵?
     * 闁槒绶拠瀛樻閵?
     */
    private void handleViewMyApplications(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            System.out.println("=== handleViewMyApplications START ===");
            
            // 闁槒绶拠瀛樻閵?
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
            
            // 闁槒绶拠瀛樻閵?
            System.out.println("User role: " + currentUser.getRole());
            if (currentUser.getRole() != UserRole.TA) {
                System.out.println("User is not TA, sending 403");
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Only TAs can view their own applications");
                return;
            }
            
            // 闁槒绶拠瀛樻閵?
            try {
                applicationService.cleanupExpiredPositionApplications();
            } catch (IOException e) {
                System.out.println("Warning: Failed to cleanup expired applications: " + e.getMessage());
            }
            
            // 闁槒绶拠瀛樻閵?
            System.out.println("Calling applicationService.getApplicationsByTA()");
            List<Application> applications = applicationService.getApplicationsByTA(currentUser.getUserId());
            System.out.println("Retrieved " + (applications != null ? applications.size() : "null") + " applications");
            
            // 闁槒绶拠瀛樻閵?
            request.setAttribute("applications", applications);
            System.out.println("Set applications attribute");
            
            // 闁槒绶拠瀛樻閵?
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
     * 闁槒绶拠瀛樻閵?
     * 闁槒绶拠瀛樻閵?
     */
    private void handleViewPositionApplications(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            // 闁槒绶拠瀛樻閵?
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
            
            // 闁槒绶拠瀛樻閵?
            if (currentUser.getRole() != UserRole.MO) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Only MOs can view position applications");
                return;
            }
            
            // 闁槒绶拠瀛樻閵?
            try {
                applicationService.cleanupExpiredPositionApplications();
            } catch (IOException e) {
                System.out.println("Warning: Failed to cleanup expired applications: " + e.getMessage());
            }
            
            // 闁槒绶拠瀛樻閵?
            String positionId = request.getParameter("positionId");
            
            if (positionId == null || positionId.trim().isEmpty()) {
                session.setAttribute("errorMessage", "Position ID cannot be empty");
                response.sendRedirect(request.getContextPath() + "/mo/positions/my");
                return;
            }
            
            // 闁槒绶拠瀛樻閵?
            Position position = positionService.getPositionById(positionId.trim());
            if (position == null) {
                session.setAttribute("errorMessage", "Position not found");
                response.sendRedirect(request.getContextPath() + "/mo/positions/my");
                return;
            }
            
            // 闁槒绶拠瀛樻閵?
            List<Application> applications = applicationService.getApplicationsByPosition(positionId.trim());
            
            // 闁槒绶拠瀛樻閵?
            String statusFilter = request.getParameter("status");
            
            // 闁槒绶拠瀛樻閵?
            if (statusFilter != null && !statusFilter.equals("all") && !statusFilter.isEmpty()) {
                try {
                    com.bupt.tarecruitment.model.ApplicationStatus filterStatus = 
                        com.bupt.tarecruitment.model.ApplicationStatus.valueOf(statusFilter.toUpperCase());
                    applications = applications.stream()
                        .filter(app -> app.getStatus() == filterStatus)
                        .collect(java.util.stream.Collectors.toList());
                } catch (IllegalArgumentException e) {
                    // 闁槒绶拠瀛樻閵?
                }
            }
            
            // 闁槒绶拠瀛樻閵?
            request.setAttribute("position", position);
            request.setAttribute("applications", applications);
            request.setAttribute("statusFilter", statusFilter != null ? statusFilter : "all");
            
            // 闁槒绶拠瀛樻閵?
            request.getRequestDispatcher("/WEB-INF/jsp/mo/applications.jsp").forward(request, response);
            
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Failed to load applications: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(request, response);
        }
    }
    
    /**
     * 闁槒绶拠瀛樻閵?
     * 闁槒绶拠瀛樻閵?
     * 闁槒绶拠瀛樻閵?
     */
    private void handleSelectApplicant(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            // 闁槒绶拠瀛樻閵?
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
            
            // 闁槒绶拠瀛樻閵?
            if (currentUser.getRole() != UserRole.MO) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Only MOs can select applicants");
                return;
            }
            
            // 闁槒绶拠瀛樻閵?
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
            
            // 闁槒绶拠瀛樻閵?
            applicationService.selectApplicant(applicationId.trim());
            
            // 闁槒绶拠瀛樻閵?
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
                // 闁槒绶拠瀛樻閵?
                e.printStackTrace();
            }
            
            // 闁槒绶拠瀛樻閵?
            if (positionId != null && !positionId.trim().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/mo/applications/position?positionId=" + positionId);
            } else {
                response.sendRedirect(request.getContextPath() + "/mo/positions/my");
            }
            
        } catch (IllegalArgumentException e) {
            // 闁槒绶拠瀛樻閵?
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
            // 闁槒绶拠瀛樻閵?
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

