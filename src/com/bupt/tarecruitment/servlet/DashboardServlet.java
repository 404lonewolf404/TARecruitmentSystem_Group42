package com.bupt.tarecruitment.servlet;

import com.bupt.tarecruitment.model.User;
import com.bupt.tarecruitment.model.UserRole;
import com.bupt.tarecruitment.service.StatisticsService;
import com.bupt.tarecruitment.service.NotificationService;
import com.bupt.tarecruitment.service.ChartService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Map;

/**
 * Dashboard Servlet
 * 根据用户角色显示相应的dashboard页面
 * V3.3 - 添加图表可视化支持
 */
public class DashboardServlet extends HttpServlet {
    
    private StatisticsService statisticsService;
    private NotificationService notificationService;
    private ChartService chartService;
    
    @Override
    public void init() {
        this.statisticsService = new StatisticsService();
        this.notificationService = new NotificationService();
        this.chartService = new ChartService();
    }
    
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
        
        // 获取未读通知数量
        try {
            int unreadCount = notificationService.getUnreadCount(user.getUserId());
            request.setAttribute("unreadNotificationCount", unreadCount);
        } catch (Exception e) {
            // 获取通知数量失败不影响主流程
            e.printStackTrace();
            request.setAttribute("unreadNotificationCount", 0);
        }
        
        // 根据角色获取统计数据和图表数据
        if (role == UserRole.TA) {
            Map<String, Integer> stats = statisticsService.getTAStats(user.getUserId());
            request.setAttribute("stats", stats);
            // V3.3 - TA申请状态分布环形图数据
            String chartData = chartService.getTAApplicationStatusData(user.getUserId());
            request.setAttribute("chartData", chartData);
        } else if (role == UserRole.MO) {
            Map<String, Integer> stats = statisticsService.getMOStats(user.getUserId());
            request.setAttribute("stats", stats);
            // V3.3 - MO职位申请数对比图数据
            String chartData = chartService.getPositionApplicationsData(user.getUserId());
            request.setAttribute("chartData", chartData);
        } else if (role == UserRole.ADMIN) {
            Map<String, Object> stats = statisticsService.getAdminStats();
            request.setAttribute("stats", stats);
            // V3.3 - Admin工作量分布柱状图和申请状态饼图数据
            String workloadChartData = chartService.getWorkloadChartData();
            String statusChartData = chartService.getApplicationStatusData();
            request.setAttribute("workloadChartData", workloadChartData);
            request.setAttribute("statusChartData", statusChartData);
        }
        
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
