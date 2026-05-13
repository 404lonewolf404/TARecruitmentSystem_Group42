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
 * 逻辑说明。
 * 逻辑说明。
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
            // 逻辑说明。
            handleViewFavorites(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource not found");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 逻辑说明。
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
     * 逻辑说明。
     */
    private void handleViewFavorites(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            // 逻辑说明。
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
            
            // 逻辑说明。
            if (currentUser.getRole() != UserRole.TA) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Only TAs can view favorites");
                return;
            }
            
            // 逻辑说明。
            List<Position> favoritePositions = favoriteService.getFavoritePositions(currentUser.getUserId());
            
            // 逻辑说明。
            com.bupt.tarecruitment.service.ApplicationService applicationService = 
                new com.bupt.tarecruitment.service.ApplicationService();
            List<com.bupt.tarecruitment.model.Application> myApplications = 
                applicationService.getApplicationsByTA(currentUser.getUserId());
            java.util.Set<String> appliedPositionIds = new java.util.HashSet<>();
            for (com.bupt.tarecruitment.model.Application app : myApplications) {
                appliedPositionIds.add(app.getPositionId());
            }
            
            // 逻辑说明。
            request.setAttribute("favoritePositions", favoritePositions);
            request.setAttribute("appliedPositionIds", appliedPositionIds);
            
            // 逻辑说明。
            request.getRequestDispatcher("/WEB-INF/jsp/ta/favorites.jsp").forward(request, response);
            
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Failed to load favorites: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(request, response);
        }
    }
    
    /**
     * 逻辑说明。
     */
    private void handleAddFavorite(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            // 逻辑说明。
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
            
            // 逻辑说明。
            if (currentUser.getRole() != UserRole.TA) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Only TAs can add favorites");
                return;
            }
            
            // 逻辑说明。
            String positionId = request.getParameter("positionId");
            
            if (positionId == null || positionId.trim().isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Position ID cannot be empty");
                return;
            }
            
            // 逻辑说明。
            favoriteService.addFavorite(currentUser.getUserId(), positionId.trim());
            
            // 逻辑说明。
            String returnUrl = request.getParameter("returnUrl");
            if (returnUrl == null || returnUrl.trim().isEmpty()) {
                // 逻辑说明。
                response.setStatus(HttpServletResponse.SC_OK);
                return;
            }
            
            // 逻辑说明。
            response.sendRedirect(getSafeReturnUrl(request, returnUrl));
            
        } catch (IllegalArgumentException e) {
            // 逻辑说明。
            String returnUrl = request.getParameter("returnUrl");
            if (returnUrl == null || returnUrl.trim().isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
                return;
            }
            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(request, response);
            
        } catch (IOException e) {
            // 逻辑说明。
            String returnUrl = request.getParameter("returnUrl");
            if (returnUrl == null || returnUrl.trim().isEmpty()) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to add favorite");
                return;
            }
            request.setAttribute("errorMessage", "Failed to add favorite: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(request, response);
        }
    }
    
    /**
     * 逻辑说明。
     */
    private void handleRemoveFavorite(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            // 逻辑说明。
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
            
            // 逻辑说明。
            if (currentUser.getRole() != UserRole.TA) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Only TAs can remove favorites");
                return;
            }
            
            // 逻辑说明。
            String positionId = request.getParameter("positionId");
            
            if (positionId == null || positionId.trim().isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Position ID cannot be empty");
                return;
            }
            
            // 逻辑说明。
            favoriteService.removeFavorite(currentUser.getUserId(), positionId.trim());
            
            // 逻辑说明。
            String returnUrl = request.getParameter("returnUrl");
            if (returnUrl == null || returnUrl.trim().isEmpty()) {
                // 逻辑说明。
                response.setStatus(HttpServletResponse.SC_OK);
                return;
            }
            
            // 逻辑说明。
            response.sendRedirect(getSafeReturnUrl(request, returnUrl));
            
        } catch (IllegalArgumentException e) {
            // 逻辑说明。
            String returnUrl = request.getParameter("returnUrl");
            if (returnUrl == null || returnUrl.trim().isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
                return;
            }
            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(request, response);
            
        } catch (IOException e) {
            String returnUrl = request.getParameter("returnUrl");
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


