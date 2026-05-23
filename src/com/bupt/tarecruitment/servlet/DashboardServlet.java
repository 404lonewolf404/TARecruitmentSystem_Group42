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
 * Role-aware dashboard servlet.
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
        
        // 中文说明：校验当前请求是否已登录。
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }
        
        User user = (User) session.getAttribute("user");
        UserRole role = user.getRole();
        
        // 中文说明：先加载未读通知数，避免页面角标缺失。
        try {
            int unreadCount = notificationService.getUnreadCount(user.getUserId());
            request.setAttribute("unreadNotificationCount", unreadCount);
        } catch (Exception e) {
            // 中文说明：通知统计失败时降级为 0，不阻断主页加载。
            e.printStackTrace();
            request.setAttribute("unreadNotificationCount", 0);
        }
        
        // 中文说明：按角色加载对应统计数据和图表。
        if (role == UserRole.TA) {
            Map<String, Integer> stats = statisticsService.getTAStats(user.getUserId());
            request.setAttribute("stats", stats);
            // 中文说明：TA 首页展示申请状态分布图。
            String chartData = chartService.getTAApplicationStatusData(user.getUserId());
            request.setAttribute("chartData", chartData);
        } else if (role == UserRole.MO) {
            Map<String, Integer> stats = statisticsService.getMOStats(user.getUserId());
            request.setAttribute("stats", stats);
            // 中文说明：MO 首页展示岗位申请情况图。
            String chartData = chartService.getPositionApplicationsData(user.getUserId());
            request.setAttribute("chartData", chartData);
        } else if (role == UserRole.ADMIN) {
            Map<String, Object> stats = statisticsService.getAdminStats();
            request.setAttribute("stats", stats);
            // 中文说明：管理员首页展示工时和申请状态两类图表。
            String workloadChartData = chartService.getWorkloadChartData();
            String statusChartData = chartService.getApplicationStatusData();
            request.setAttribute("workloadChartData", workloadChartData);
            request.setAttribute("statusChartData", statusChartData);
        }
        
        // 中文说明：根据角色转发到对应主页。
        String jspPath = getDashboardJspPath(role);
        request.getRequestDispatcher(jspPath).forward(request, response);
    }
    
    /**
     * Returns the dashboard JSP path for the given role.
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
