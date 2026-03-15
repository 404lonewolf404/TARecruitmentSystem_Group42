package com.bupt.tarecruitment.filter;

import com.bupt.tarecruitment.model.User;
import com.bupt.tarecruitment.model.UserRole;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;

/**
 * 角色过滤器
 * 验证用户是否有权限访问特定资源
 * 需求：8.2, 8.4
 */
public class RoleFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 初始化过滤器（如果需要）
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // 获取会话中的用户信息
        HttpSession session = httpRequest.getSession(false);
        User user = (User) session.getAttribute("user");
        
        // 获取请求的URI
        String requestURI = httpRequest.getRequestURI();
        
        // 调试日志
        System.out.println("=== RoleFilter ===");
        System.out.println("Request URI: " + requestURI);
        System.out.println("User Role: " + (user != null ? user.getRole() : "null"));
        
        // 检查用户角色是否有权限访问请求的URI
        if (hasPermission(user.getRole(), requestURI)) {
            // 有权限，继续处理请求
            System.out.println("Permission granted, continuing chain");
            chain.doFilter(request, response);
        } else {
            // 无权限，返回403错误
            System.out.println("Permission denied, sending 403");
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "您没有权限访问此资源");
        }
    }

    /**
     * 检查用户角色是否有权限访问指定的URI
     * 
     * @param role 用户角色
     * @param requestURI 请求的URI
     * @return 如果有权限返回true，否则返回false
     */
    private boolean hasPermission(UserRole role, String requestURI) {
        // 定义角色到URL的映射规则
        
        // TA角色只能访问/ta/*路径
        if (requestURI.contains("/ta/")) {
            return role == UserRole.TA;
        }
        
        // MO角色只能访问/mo/*路径
        if (requestURI.contains("/mo/")) {
            return role == UserRole.MO;
        }
        
        // Admin角色只能访问/admin/*路径
        if (requestURI.contains("/admin/")) {
            return role == UserRole.ADMIN;
        }
        
        // 默认拒绝访问
        return false;
    }

    @Override
    public void destroy() {
        // 清理资源（如果需要）
    }
}
