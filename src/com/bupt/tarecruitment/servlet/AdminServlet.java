package com.bupt.tarecruitment.servlet;

import com.bupt.tarecruitment.model.User;
import com.bupt.tarecruitment.service.WorkloadService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

/**
 * 管理员Servlet
 * 处理管理员相关的请求，包括工作量报告
 */
public class AdminServlet extends HttpServlet {
    
    private WorkloadService workloadService;
    
    @Override
    public void init() throws ServletException {
        super.init();
        this.workloadService = new WorkloadService();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "无效的请求路�?);
            return;
        }
        
        switch (pathInfo) {
            case "/workload":
                handleWorkloadReport(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "请求的资源不存在");
                break;
        }
    }
    
    /**
     * 处理工作量报告请�?
     * 获取所有TA的工作量统计并转发到JSP页面显示
     */
    private void handleWorkloadReport(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            // 调用WorkloadService获取工作量数�?
            Map<User, Integer> workloads = workloadService.calculateAllWorkloads();
            
            // 将数据设置为请求属�?
            request.setAttribute("workloads", workloads);
            
            // 转发到工作量报告JSP页面
            request.getRequestDispatcher("/WEB-INF/jsp/admin/workload.jsp").forward(request, response);
            
        } catch (Exception e) {
            // 处理错误
            request.setAttribute("errorMessage", "获取工作量报告失败：" + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(request, response);
        }
    }
}
