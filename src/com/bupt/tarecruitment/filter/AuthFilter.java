package com.bupt.tarecruitment.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;

/**
 * Authentication filter for protected routes.
 */
public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 中文说明：认证过滤器无需额外初始化资源。
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // 中文说明：尝试从现有会话中读取登录用户。
        HttpSession session = httpRequest.getSession(false);
        
        // 中文说明：未登录时统一跳转到登录页。
        if (session == null || session.getAttribute("user") == null) {
            // 中文说明：拼接上下文路径，兼容不同部署目录。
            String contextPath = httpRequest.getContextPath();
            httpResponse.sendRedirect(contextPath + "/auth/login");
        } else {
            // 中文说明：登录状态有效时继续处理请求。
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
        // 中文说明：过滤器销毁时无额外清理逻辑。
    }
}
