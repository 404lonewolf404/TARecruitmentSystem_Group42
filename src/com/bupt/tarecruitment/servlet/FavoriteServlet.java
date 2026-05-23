package com.bupt.tarecruitment.servlet;

import com.bupt.tarecruitment.model.Position;
import com.bupt.tarecruitment.model.User;
import com.bupt.tarecruitment.model.UserRole;
import com.bupt.tarecruitment.service.FavoriteService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

/**
 * Handles TA favorite position pages and actions.
 */
public class FavoriteServlet extends HttpServlet {
    
    private FavoriteService favoriteService;
    
    @Override
    public void init() throws ServletException {
        super.init();
        this.favoriteService = new FavoriteService();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            // 中文说明：根路径展示收藏列表。
            handleViewFavorites(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource not found");
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
            case "/add":
                handleAddFavorite(request, response);
                break;
            case "/remove":
                handleRemoveFavorite(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource not found");
                break;
        }
    }
    
    /**
     * Renders the TA favorite positions page.
     */
    private void handleViewFavorites(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            // 中文说明：只有已登录用户才能访问收藏页。
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
            
            // 中文说明：收藏功能仅对 TA 角色开放。
            if (currentUser.getRole() != UserRole.TA) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Only TAs can view favorites");
                return;
            }
            
            // 中文说明：读取 TA 已收藏的岗位。
            List<Position> favoritePositions = favoriteService.getFavoritePositions(currentUser.getUserId());
            
            // 中文说明：同时加载已申请岗位，供页面标记状态。
            com.bupt.tarecruitment.service.ApplicationService applicationService = 
                new com.bupt.tarecruitment.service.ApplicationService();
            List<com.bupt.tarecruitment.model.Application> myApplications = 
                applicationService.getApplicationsByTA(currentUser.getUserId());
            java.util.Set<String> appliedPositionIds = new java.util.HashSet<>();
            for (com.bupt.tarecruitment.model.Application app : myApplications) {
                appliedPositionIds.add(app.getPositionId());
            }
            
            // 中文说明：写入页面渲染所需数据。
            request.setAttribute("favoritePositions", favoritePositions);
            request.setAttribute("appliedPositionIds", appliedPositionIds);
            
            // 中文说明：转发到 TA 收藏页 JSP。
            request.getRequestDispatcher("/WEB-INF/jsp/ta/favorites.jsp").forward(request, response);
            
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Failed to load favorites: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(request, response);
        }
    }
    
    /**
     * Adds a position to the current TA's favorites.
     */
    private void handleAddFavorite(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String returnUrl = request.getParameter("returnUrl");
        
        try {
            // 中文说明：只有已登录用户才能添加收藏。
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
            
            // 中文说明：收藏操作仅对 TA 角色开放。
            if (currentUser.getRole() != UserRole.TA) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Only TAs can add favorites");
                return;
            }
            
            // 中文说明：读取目标岗位 ID。
            String positionId = request.getParameter("positionId");
            
            if (positionId == null || positionId.trim().isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Position ID cannot be empty");
                return;
            }
            
            // 中文说明：执行收藏操作。
            favoriteService.addFavorite(currentUser.getUserId(), positionId.trim());
            
            // 中文说明：异步调用场景下直接返回 200。
            if (returnUrl == null || returnUrl.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_OK);
                return;
            }
            
            // 中文说明：同步页面请求则跳回来源页面。
            response.sendRedirect(getSafeReturnUrl(request, returnUrl));
            
        } catch (IllegalArgumentException e) {
            // 中文说明：无返回地址时直接返回错误状态码。
            if (returnUrl == null || returnUrl.trim().isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
                return;
            }
            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(request, response);
            
        } catch (IOException e) {
            // 中文说明：无返回地址时直接返回错误状态码。
            if (returnUrl == null || returnUrl.trim().isEmpty()) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to add favorite");
                return;
            }
            request.setAttribute("errorMessage", "Failed to add favorite: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(request, response);
        }
    }
    
    /**
     * Removes a position from the current TA's favorites.
     */
    private void handleRemoveFavorite(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String returnUrl = request.getParameter("returnUrl");
        
        try {
            // 中文说明：只有已登录用户才能移除收藏。
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
            
            // 中文说明：移除收藏仅对 TA 角色开放。
            if (currentUser.getRole() != UserRole.TA) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Only TAs can remove favorites");
                return;
            }
            
            // 中文说明：读取目标岗位 ID。
            String positionId = request.getParameter("positionId");
            
            if (positionId == null || positionId.trim().isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Position ID cannot be empty");
                return;
            }
            
            // 中文说明：执行取消收藏操作。
            favoriteService.removeFavorite(currentUser.getUserId(), positionId.trim());
            
            // 中文说明：异步调用场景下直接返回 200。
            if (returnUrl == null || returnUrl.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_OK);
                return;
            }
            
            // 中文说明：同步页面请求则跳回来源页面。
            response.sendRedirect(getSafeReturnUrl(request, returnUrl));
            
        } catch (IllegalArgumentException e) {
            // 中文说明：无返回地址时直接返回错误状态码。
            if (returnUrl == null || returnUrl.trim().isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
                return;
            }
            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(request, response);
            
        } catch (IOException e) {
            if (returnUrl == null || returnUrl.trim().isEmpty()) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to remove favorite");
                return;
            }
            request.setAttribute("errorMessage", "Failed to remove favorite: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(request, response);
        }
    }

    private String getSafeReturnUrl(HttpServletRequest request, String returnUrl) {
        if (returnUrl == null) {
            return request.getContextPath() + "/ta/positions";
        }

        String trimmed = returnUrl.trim();
        if (trimmed.isEmpty()) {
            return request.getContextPath() + "/ta/positions";
        }

        if (trimmed.startsWith("http://") || trimmed.startsWith("https://") || trimmed.startsWith("//")) {
            return request.getContextPath() + "/ta/positions";
        }

        String contextPath = request.getContextPath();
        if (trimmed.startsWith(contextPath + "/")) {
            return trimmed;
        }
        if (trimmed.startsWith("/")) {
            return contextPath + trimmed;
        }

        return contextPath + "/ta/positions";
    }
}


