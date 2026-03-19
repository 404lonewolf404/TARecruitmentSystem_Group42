package com.bupt.tarecruitment.servlet;

import com.bupt.tarecruitment.model.User;
import com.bupt.tarecruitment.model.UserRole;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Dashboard Servlet
 * 根据用户角色显示相应的dashboard页面
 */
public class DashboardServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 获取当前用户
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }
        
        User user = (User) session.getAttribute("user");
        UserRole role = user.getRole();
        
        // 根据角色转发到相应的dashboard页面
        String jspPath = getDashboardJspPath(role);
        request.getRequestDispatcher(jspPath).forward(request, response);
    }
    
    /**
     * 根据用户角色获取对应的JSP路径
     */
    private String getDashboardJspPath(UserRole role) {
        switch (role) {
            case TA:
                return "/WEB-INF/jsp/ta/dashboard.jsp";
            case MO:
                return "/WEB-INF/jsp/mo/dashboard.jsp";
            case ADMIN:
                return "/WEB-INF/jsp/admin/dashboard.jsp";
            default:
                return "/WEB-INF/jsp/login.jsp";
        }
    }
}
