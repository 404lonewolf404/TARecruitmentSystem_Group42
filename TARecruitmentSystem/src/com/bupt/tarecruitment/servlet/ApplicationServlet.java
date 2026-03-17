package com.bupt.tarecruitment.servlet;

import com.bupt.tarecruitment.model.Application;
import com.bupt.tarecruitment.model.Position;
import com.bupt.tarecruitment.model.User;
import com.bupt.tarecruitment.model.UserRole;
import com.bupt.tarecruitment.service.ApplicationService;
import com.bupt.tarecruitment.service.PositionService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

/**
 * 申请Servlet
 * 处理申请相关的请求：申请职位、撤回申请、查看申请列表、选择申请者
 */
public class ApplicationServlet extends HttpServlet {
    
    private ApplicationService applicationService;
    private PositionService positionService;
    
    @Override
    public void init() throws ServletException {
        super.init();
        this.applicationService = new ApplicationService();
        this.positionService = new PositionService();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        
        // 调试日志
        System.out.println("ApplicationServlet.doGet() called");
        System.out.println("Request URI: " + request.getRequestURI());
        System.out.println("Context Path: " + request.getContextPath());
        System.out.println("Servlet Path: " + request.getServletPath());
        System.out.println("Path Info: " + pathInfo);
        
        if (pathInfo == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "无效的请求路径");
            return;
        }
        
        // 移除尾部斜杠（如果有）
        if (pathInfo.endsWith("/") && pathInfo.length() > 1) {
            pathInfo = pathInfo.substring(0, pathInfo.length() - 1);
        }
        
        System.out.println("Processed Path Info: " + pathInfo);
        
        switch (pathInfo) {
            case "/my":
                // 查看我的申请（TA视图）
                System.out.println("Handling /my request");
                handleViewMyApplications(request, response);
                break;
            case "/position":
                // 查看职位的申请列表（MO视图）
                System.out.println("Handling /position request");
                handleViewPositionApplications(request, response);
                break;
            default:
                System.out.println("No matching case, returning 404");
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "请求的资源不存在");
                break;
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 设置请求编码为UTF-8
        request.setCharacterEncoding("UTF-8");
        
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "无效的请求路径");
            return;
        }
        
        switch (pathInfo) {
            case "/apply":
                // 申请职位（TA操作）
                handleApplyForPosition(request, response);
                break;
            case "/withdraw":
                // 撤回申请（TA操作）
                handleWithdrawApplication(request, response);
                break;
            case "/select":
                // 选择申请者（MO操作）
                handleSelectApplicant(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "请求的资源不存在");
                break;
        }
    }
    
    /**
     * 处理申请职位请求（TA操作）
     * 需求：4.2 - 当TA申请职位时，系统应创建申请记录并关联TA和职位
     * 需求：4.3 - 当TA尝试重复申请同一职位时，系统应拒绝该申请
     */
    private void handleApplyForPosition(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 获取当前登录用户
        HttpSession session = request.getSession(false);
        
        try {
            if (session == null) {
                response.sendRedirect(request.getContextPath() + "/login.jsp");
                return;
            }
            
            User currentUser = (User) session.getAttribute("user");
            if (currentUser == null) {
                response.sendRedirect(request.getContextPath() + "/login.jsp");
                return;
            }
            
            // 验证用户角色为TA
            if (currentUser.getRole() != UserRole.TA) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "只有TA可以申请职位");
                return;
            }
            
            // 获取职位ID参数
            String positionId = request.getParameter("positionId");
            
            if (positionId == null || positionId.trim().isEmpty()) {
                session.setAttribute("errorMessage", "职位ID不能为空");
                response.sendRedirect(request.getContextPath() + "/ta/positions");
                return;
            }
            
            // 调用服务层申请职位
            applicationService.applyForPosition(currentUser.getUserId(), positionId.trim());
            
            // 申请成功，设置成功消息到session
            session.setAttribute("successMessage", "申请提交成功！");
            
            // 重定向到我的申请页面
            response.sendRedirect(request.getContextPath() + "/ta/applications/my");
            
        } catch (IllegalArgumentException e) {
            // 业务逻辑错误（如重复申请、职位不存在）
            if (session != null) {
                session.setAttribute("errorMessage", e.getMessage());
            }
            response.sendRedirect(request.getContextPath() + "/ta/positions");
            
        } catch (IOException e) {
            // 数据访问错误
            if (session != null) {
                session.setAttribute("errorMessage", "申请职位失败：" + e.getMessage());
            }
            response.sendRedirect(request.getContextPath() + "/ta/positions");
        }
    }
    
    /**
     * 处理撤回申请请求（TA操作）
     * 需求：4.5 - 当TA撤回申请时，系统应移除该申请记录
     */
    private void handleWithdrawApplication(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            // 获取当前登录用户
            HttpSession session = request.getSession(false);
            if (session == null) {
                response.sendRedirect(request.getContextPath() + "/login.jsp");
                return;
            }
            
            User currentUser = (User) session.getAttribute("user");
            if (currentUser == null) {
                response.sendRedirect(request.getContextPath() + "/login.jsp");
                return;
            }
            
            // 验证用户角色为TA
            if (currentUser.getRole() != UserRole.TA) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "只有TA可以撤回申请");
                return;
            }
            
            // 获取申请ID参数
            String applicationId = request.getParameter("applicationId");
            
            if (applicationId == null || applicationId.trim().isEmpty()) {
                request.setAttribute("errorMessage", "申请ID不能为空");
                response.sendRedirect(request.getContextPath() + "/ta/applications/my");
                return;
            }
            
            // 调用服务层撤回申请
            applicationService.withdrawApplication(applicationId.trim());
            
            // 撤回成功，重定向到我的申请页面
            response.sendRedirect(request.getContextPath() + "/ta/applications/my");
            
        } catch (IllegalArgumentException e) {
            // 业务逻辑错误（如申请不存在）
            request.setAttribute("errorMessage", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/ta/applications/my");
            
        } catch (IOException e) {
            // 数据访问错误
            request.setAttribute("errorMessage", "撤回申请失败：" + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/ta/applications/my");
        }
    }
    
    /**
     * 处理查看我的申请请求（TA视图）
     * 需求：4.4 - 当TA查看其申请时，系统应显示该TA提交的所有申请及其状态
     */
    private void handleViewMyApplications(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            System.out.println("=== handleViewMyApplications START ===");
            
            // 获取当前登录用户
            HttpSession session = request.getSession(false);
            System.out.println("Session: " + (session != null ? "exists" : "null"));
            
            if (session == null) {
                System.out.println("No session, redirecting to login");
                response.sendRedirect(request.getContextPath() + "/login.jsp");
                return;
            }
            
            User currentUser = (User) session.getAttribute("user");
            System.out.println("Current user: " + (currentUser != null ? currentUser.getUserId() : "null"));
            
            if (currentUser == null) {
                System.out.println("No user in session, redirecting to login");
                response.sendRedirect(request.getContextPath() + "/login.jsp");
                return;
            }
            
            // 验证用户角色为TA
            System.out.println("User role: " + currentUser.getRole());
            if (currentUser.getRole() != UserRole.TA) {
                System.out.println("User is not TA, sending 403");
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "只有TA可以查看自己的申请");
                return;
            }
            
            // 获取该TA的所有申请
            System.out.println("Calling applicationService.getApplicationsByTA()");
            List<Application> applications = applicationService.getApplicationsByTA(currentUser.getUserId());
            System.out.println("Retrieved " + (applications != null ? applications.size() : "null") + " applications");
            
            // 将申请列表设置到request中
            request.setAttribute("applications", applications);
            System.out.println("Set applications attribute");
            
            // 转发到TA申请页面
            String jspPath = "/WEB-INF/jsp/ta/applications.jsp";
            System.out.println("Forwarding to: " + jspPath);
            request.getRequestDispatcher(jspPath).forward(request, response);
            System.out.println("Forward completed successfully");
            
        } catch (Exception e) {
            System.out.println("=== EXCEPTION in handleViewMyApplications ===");
            System.out.println("Exception type: " + e.getClass().getName());
            System.out.println("Exception message: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("errorMessage", "获取申请列表失败：" + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(request, response);
        }
    }
    
    /**
     * 处理查看职位申请列表请求（MO视图）
     * 需求：5.1 - 当MO查看职位申请时，系统应显示该职位的所有申请者及其信息
     */
    private void handleViewPositionApplications(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            // 获取当前登录用户
            HttpSession session = request.getSession(false);
            if (session == null) {
                response.sendRedirect(request.getContextPath() + "/login.jsp");
                return;
            }
            
            User currentUser = (User) session.getAttribute("user");
            if (currentUser == null) {
                response.sendRedirect(request.getContextPath() + "/login.jsp");
                return;
            }
            
            // 验证用户角色为MO
            if (currentUser.getRole() != UserRole.MO) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "只有MO可以查看职位申请");
                return;
            }
            
            // 获取职位ID参数
            String positionId = request.getParameter("positionId");
            
            if (positionId == null || positionId.trim().isEmpty()) {
                request.setAttribute("errorMessage", "职位ID不能为空");
                response.sendRedirect(request.getContextPath() + "/mo/positions/my");
                return;
            }
            
            // 获取职位信息
            Position position = positionService.getPositionById(positionId.trim());
            if (position == null) {
                request.setAttribute("errorMessage", "职位不存在");
                response.sendRedirect(request.getContextPath() + "/mo/positions/my");
                return;
            }
            
            // 获取该职位的所有申请
            List<Application> applications = applicationService.getApplicationsByPosition(positionId.trim());
            
            // 将职位和申请列表设置到request中
            request.setAttribute("position", position);
            request.setAttribute("applications", applications);
            
            // 转发到MO申请页面
            request.getRequestDispatcher("/WEB-INF/jsp/mo/applications.jsp").forward(request, response);
            
        } catch (Exception e) {
            request.setAttribute("errorMessage", "获取申请列表失败：" + e.getMessage());
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }
    
    /**
     * 处理选择申请者请求（MO操作）
     * 需求：5.2 - 当MO选择申请者时，系统应将该申请状态更新为SELECTED
     * 需求：5.3 - 当MO选择申请者时，系统应将同一职位的其他申请状态更新为REJECTED
     */
    private void handleSelectApplicant(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            // 获取当前登录用户
            HttpSession session = request.getSession(false);
            if (session == null) {
                response.sendRedirect(request.getContextPath() + "/login.jsp");
                return;
            }
            
            User currentUser = (User) session.getAttribute("user");
            if (currentUser == null) {
                response.sendRedirect(request.getContextPath() + "/login.jsp");
                return;
            }
            
            // 验证用户角色为MO
            if (currentUser.getRole() != UserRole.MO) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "只有MO可以选择申请者");
                return;
            }
            
            // 获取申请ID和职位ID参数
            String applicationId = request.getParameter("applicationId");
            String positionId = request.getParameter("positionId");
            
            if (applicationId == null || applicationId.trim().isEmpty()) {
                request.setAttribute("errorMessage", "申请ID不能为空");
                if (positionId != null && !positionId.trim().isEmpty()) {
                    response.sendRedirect(request.getContextPath() + "/mo/applications/position?positionId=" + positionId);
                } else {
                    response.sendRedirect(request.getContextPath() + "/mo/positions/my");
                }
                return;
            }
            
            // 调用服务层选择申请者
            applicationService.selectApplicant(applicationId.trim());
            
            // 选择成功，重定向回职位申请列表页面
            if (positionId != null && !positionId.trim().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/mo/applications/position?positionId=" + positionId);
            } else {
                response.sendRedirect(request.getContextPath() + "/mo/positions/my");
            }
            
        } catch (IllegalArgumentException e) {
            // 业务逻辑错误（如申请不存在）
            request.setAttribute("errorMessage", e.getMessage());
            String positionId = request.getParameter("positionId");
            if (positionId != null && !positionId.trim().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/mo/applications/position?positionId=" + positionId);
            } else {
                response.sendRedirect(request.getContextPath() + "/mo/positions/my");
            }
            
        } catch (IOException e) {
            // 数据访问错误
            request.setAttribute("errorMessage", "选择申请者失败：" + e.getMessage());
            String positionId = request.getParameter("positionId");
            if (positionId != null && !positionId.trim().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/mo/applications/position?positionId=" + positionId);
            } else {
                response.sendRedirect(request.getContextPath() + "/mo/positions/my");
            }
        }
    }
}
