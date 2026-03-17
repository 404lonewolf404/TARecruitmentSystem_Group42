package com.bupt.tarecruitment.servlet;

import com.bupt.tarecruitment.model.User;
import com.bupt.tarecruitment.model.UserRole;
import com.bupt.tarecruitment.service.AuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * 认证Servlet
 * 处理用户注册、登录和登出请求
 */
public class AuthServlet extends HttpServlet {
    
    private AuthService authService;
    
    @Override
    public void init() throws ServletException {
        super.init();
        this.authService = new AuthService();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "无效的请求路径");
            return;
        }
        
        switch (pathInfo) {
            case "/login":
                // 显示登录页面
                request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response);
                break;
            case "/register":
                // 显示注册页面
                request.getRequestDispatcher("/WEB-INF/jsp/register.jsp").forward(request, response);
                break;
            case "/logout":
                handleLogout(request, response);
                break;
            default:
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
            case "/register":
                handleRegister(request, response);
                break;
            case "/login":
                handleLogin(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "请求的资源不存在");
                break;
        }
    }
    
    /**
     * 处理用户注册请求
     */
    private void handleRegister(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            // 获取表单参数
            String name = request.getParameter("name");
            String email = request.getParameter("email");
            String password = request.getParameter("password");
            String roleStr = request.getParameter("role");
            
            // 验证必填参数
            if (name == null || email == null || password == null || roleStr == null) {
                request.setAttribute("errorMessage", "请填写所有必填字段");
                request.getRequestDispatcher("/WEB-INF/jsp/register.jsp").forward(request, response);
                return;
            }
            
            // 解析角色
            UserRole role;
            try {
                role = UserRole.valueOf(roleStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                request.setAttribute("errorMessage", "无效的用户角色");
                request.getRequestDispatcher("/WEB-INF/jsp/register.jsp").forward(request, response);
                return;
            }
            
            // 调用服务层进行注册
            User user = authService.register(name, email, password, role);
            
            // 注册成功，重定向到登录页面
            request.setAttribute("successMessage", "注册成功！请登录");
            request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response);
            
        } catch (IllegalArgumentException e) {
            // 业务逻辑错误（如邮箱已存在）
            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/register.jsp").forward(request, response);
            
        } catch (IOException e) {
            // 数据访问错误
            request.setAttribute("errorMessage", "注册失败：" + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/register.jsp").forward(request, response);
        }
    }
    
    /**
     * 处理用户登录请求
     */
    private void handleLogin(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            // 获取表单参数
            String email = request.getParameter("email");
            String password = request.getParameter("password");
            
            // 验证必填参数
            if (email == null || password == null) {
                request.setAttribute("errorMessage", "请输入邮箱和密码");
                request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response);
                return;
            }
            
            // 调用服务层进行登录验证
            User user = authService.login(email, password);
            
            // 登录成功，创建会话
            HttpSession session = request.getSession(true);
            session.setAttribute("user", user);
            
            
            
        } catch (IllegalArgumentException e) {
            // 登录失败（凭证错误）
            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response);
        }
    }
    
    /**
     * 处理用户登出请求
     */
    private void handleLogout(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 获取当前会话
        HttpSession session = request.getSession(false);
        
        // 调用服务层进行登出
        authService.logout(session);
        
        // 重定向到登录页面
        response.sendRedirect(request.getContextPath() + "/auth/login");
    }
    
    /**
     * 根据用户角色获取对应的dashboard URL
     * 
     * @param role 用户角色
     * @return dashboard URL
     */
    private String getDashboardUrl(UserRole role) {
        switch (role) {
            case TA:
                return "/ta/dashboard";
            case MO:
                return "/mo/dashboard";
            case ADMIN:
                return "/admin/dashboard";
            default:
                return "/auth/login";
        }
    }
}
