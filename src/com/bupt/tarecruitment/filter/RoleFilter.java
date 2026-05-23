package com.bupt.tarecruitment.filter;

import com.bupt.tarecruitment.model.User;
import com.bupt.tarecruitment.model.UserRole;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Authorization filter for role-restricted routes.
 */
public class RoleFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 中文说明：角色过滤器无需额外初始化资源。
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // 中文说明：未登录或会话失效时直接拒绝访问。
        HttpSession session = httpRequest.getSession(false);
        if (session == null) {
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
            return;
        }

        User user = (User) session.getAttribute("user");
        if (user == null) {
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
            return;
        }

        // 中文说明：记录请求路径用于权限判断。
        String requestURI = httpRequest.getRequestURI();

        System.out.println("=== RoleFilter ===");
        System.out.println("Request URI: " + requestURI);
        System.out.println("User Role: " + user.getRole());

        // 中文说明：根据角色和路径执行授权校验。
        if (hasPermission(user.getRole(), requestURI)) {
            // 中文说明：有权限则放行到后续过滤器或目标资源。
            chain.doFilter(request, response);
        } else {
            // 中文说明：无权限直接返回 403。
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
        }
    }

    /**
     * Returns whether the current role can access the target path.
     *
     * @param role role value
     * @param requestURI requestURI value
     * @return operation result
     */
    private boolean hasPermission(UserRole role, String requestURI) {
        // 中文说明：路径前缀映射到角色权限。
        if (requestURI.contains("/ta/")) {
            return role == UserRole.TA;
        }
        if (requestURI.contains("/mo/")) {
            return role == UserRole.MO;
        }
        if (requestURI.contains("/admin/")) {
            return role == UserRole.ADMIN;
        }
        return false;
    }

    @Override
    public void destroy() {
        // 中文说明：过滤器销毁时无额外清理逻辑。
    }
}
