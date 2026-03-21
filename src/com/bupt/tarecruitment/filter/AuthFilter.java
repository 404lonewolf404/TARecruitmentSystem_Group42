package com.bupt.tarecruitment.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;

/**
 * 认证过滤�?
 * 拦截所有受保护的请求，验证用户是否已登�?
 * 需求：1.6, 8.1, 8.3
 */
public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 初始化过滤器（如果需要）
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // 获取会话（不创建新会话）
        HttpSession session = httpRequest.getSession(false);
        
        // 检查会话是否存在以及会话中是否有用户信�?
        if (session == null || session.getAttribute("user") == null) {
            // 未认证用户重定向到登录页�?
            String contextPath = httpRequest.getContextPath();
            httpResponse.sendRedirect(contextPath + "/auth/login");
        } else {
            // 用户已认证，继续处理请求
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
        // 清理资源（如果需要）
    }
}
