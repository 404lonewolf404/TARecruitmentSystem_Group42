package com.bupt.tarecruitment.servlet;

import com.bupt.tarecruitment.model.User;
import com.bupt.tarecruitment.model.Position;
import com.bupt.tarecruitment.model.Application;
import com.bupt.tarecruitment.service.WorkloadService;
import com.bupt.tarecruitment.dao.UserDAO;
import com.bupt.tarecruitment.dao.PositionDAO;
import com.bupt.tarecruitment.dao.ApplicationDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 管理员Servlet
 * 处理管理员相关的请求，包括工作量报告
 */
public class AdminServlet extends HttpServlet {
    
    private WorkloadService workloadService;
    private UserDAO userDAO;
    private PositionDAO positionDAO;
    private ApplicationDAO applicationDAO;
    
    @Override
    public void init() throws ServletException {
        super.init();
        this.workloadService = new WorkloadService();
        this.userDAO = new UserDAO();
        this.positionDAO = new PositionDAO();
        this.applicationDAO = new ApplicationDAO();
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
            case "/workload":
                handleWorkloadReport(request, response);
                break;
            case "/users":
                handleUsersList(request, response);
                break;
            case "/positions":
                handlePositionsList(request, response);
                break;
            case "/applications":
                handleApplicationsList(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "请求的资源不存在");
                break;
        }
    }
    
    /**
     * 处理工作量报告请求
     * 获取所有TA的工作量统计并转发到JSP页面显示
     */
    private void handleWorkloadReport(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            // 调用WorkloadService获取工作量数据
            Map<User, Integer> workloads = workloadService.calculateAllWorkloads();
            
            // 将数据设置为请求属性
            request.setAttribute("workloads", workloads);
            
            // 转发到工作量报告JSP页面
            request.getRequestDispatcher("/WEB-INF/jsp/admin/workload.jsp").forward(request, response);
            
        } catch (Exception e) {
            // 处理错误
            request.setAttribute("errorMessage", "获取工作量报告失败：" + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(request, response);
        }
    }
    
    /**
     * 处理用户列表请求
     * 获取所有用户信息并转发到JSP页面显示
     */
    private void handleUsersList(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            // 获取所有用户
            List<User> allUsers = userDAO.loadAll();
            
            // 将数据设置为请求属性
            request.setAttribute("users", allUsers);
            
            // 转发到用户列表JSP页面
            request.getRequestDispatcher("/WEB-INF/jsp/admin/users.jsp").forward(request, response);
            
        } catch (Exception e) {
            // 处理错误
            request.setAttribute("errorMessage", "获取用户列表失败：" + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(request, response);
        }
    }
    
    /**
     * 处理职位列表请求
     */
    private void handlePositionsList(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            List<Position> allPositions = positionDAO.loadAll();
            List<User> allUsers = userDAO.loadAll();
            
            request.setAttribute("positions", allPositions);
            request.setAttribute("users", allUsers);
            request.getRequestDispatcher("/WEB-INF/jsp/admin/positions.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "获取职位列表失败：" + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(request, response);
        }
    }
    
    /**
     * 处理申请列表请求
     */
    private void handleApplicationsList(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            List<Application> allApplications = applicationDAO.loadAll();
            List<User> allUsers = userDAO.loadAll();
            List<Position> allPositions = positionDAO.loadAll();
            
            request.setAttribute("applications", allApplications);
            request.setAttribute("users", allUsers);
            request.setAttribute("positions", allPositions);
            request.getRequestDispatcher("/WEB-INF/jsp/admin/applications.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "获取申请列表失败：" + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(request, response);
        }
    }
}
