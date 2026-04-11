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
 * 收藏Servlet
 * 处理职位收藏相关的请求
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
            // /favorites 或 /favorites/ - 查看我的收藏
            handleViewFavorites(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "请求的资源不存在");
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
            case "/add":
                handleAddFavorite(request, response);
                break;
            case "/remove":
                handleRemoveFavorite(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "请求的资源不存在");
                break;
        }
    }
    
    /**
     * 处理查看我的收藏请求
     */
    private void handleViewFavorites(HttpServletRequest request, HttpServletResponse response) 
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
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "只有TA可以查看收藏");
                return;
            }
            
            // 获取收藏的职位列表
            List<Position> favoritePositions = favoriteService.getFavoritePositions(currentUser.getUserId());
            
            // 设置到request中
            request.setAttribute("favoritePositions", favoritePositions);
            
            // 转发到收藏页面
            request.getRequestDispatcher("/WEB-INF/jsp/ta/favorites.jsp").forward(request, response);
            
        } catch (Exception e) {
            request.setAttribute("errorMessage", "获取收藏列表失败：" + e.getMessage());
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }
    
    /**
     * 处理添加收藏请求
     */
    private void handleAddFavorite(HttpServletRequest request, HttpServletResponse response) 
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
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "只有TA可以收藏职位");
                return;
            }
            
            // 获取职位ID参数
            String positionId = request.getParameter("positionId");
            
            if (positionId == null || positionId.trim().isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "职位ID不能为空");
                return;
            }
            
            // 添加收藏
            favoriteService.addFavorite(currentUser.getUserId(), positionId.trim());
            
            // 获取返回URL
            String returnUrl = request.getParameter("returnUrl");
            if (returnUrl == null || returnUrl.trim().isEmpty()) {
                // 如果没有 returnUrl，说明是 AJAX 请求，直接返回成功状态
                response.setStatus(HttpServletResponse.SC_OK);
                return;
            }
            
            // 重定向回原页面
            response.sendRedirect(returnUrl);
            
        } catch (IllegalArgumentException e) {
            // 业务逻辑错误
            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher("/error.jsp").forward(request, response);
            
        } catch (IOException e) {
            // 数据访问错误
            request.setAttribute("errorMessage", "添加收藏失败：" + e.getMessage());
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }
    
    /**
     * 处理取消收藏请求
     */
    private void handleRemoveFavorite(HttpServletRequest request, HttpServletResponse response) 
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
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "只有TA可以取消收藏");
                return;
            }
            
            // 获取职位ID参数
            String positionId = request.getParameter("positionId");
            
            if (positionId == null || positionId.trim().isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "职位ID不能为空");
                return;
            }
            
            // 取消收藏
            favoriteService.removeFavorite(currentUser.getUserId(), positionId.trim());
            
            // 获取返回URL
            String returnUrl = request.getParameter("returnUrl");
            if (returnUrl == null || returnUrl.trim().isEmpty()) {
                // 如果没有 returnUrl，说明是 AJAX 请求，直接返回成功状态
                response.setStatus(HttpServletResponse.SC_OK);
                return;
            }
            
            // 重定向回原页面
            response.sendRedirect(returnUrl);
            
        } catch (IllegalArgumentException e) {
            // 业务逻辑错误
            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher("/error.jsp").forward(request, response);
            
        } catch (IOException e) {
            // 数据访问错误
            request.setAttribute("errorMessage", "取消收藏失败：" + e.getMessage());
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }
}
