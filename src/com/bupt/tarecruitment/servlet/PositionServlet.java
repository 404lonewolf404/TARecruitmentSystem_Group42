package com.bupt.tarecruitment.servlet;

import com.bupt.tarecruitment.model.Position;
import com.bupt.tarecruitment.model.User;
import com.bupt.tarecruitment.model.UserRole;
import com.bupt.tarecruitment.service.PositionService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

/**
 * 职位Servlet
 * 处理职位相关的请求：查看、创建、删除职�?
 */
public class PositionServlet extends HttpServlet {
    
    private PositionService positionService;
    private com.bupt.tarecruitment.service.ApplicationService applicationService;
    
    @Override
    public void init() throws ServletException {
        super.init();
        this.positionService = new PositionService();
        this.applicationService = new com.bupt.tarecruitment.service.ApplicationService();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        
        // 调试日志
        System.out.println("=== PositionServlet.doGet() ===");
        System.out.println("Request URI: " + request.getRequestURI());
        System.out.println("Context Path: " + request.getContextPath());
        System.out.println("Servlet Path: " + request.getServletPath());
        System.out.println("Path Info: " + pathInfo);
        
        // 如果路径�?/positions �?/positions/，显示所有开放职位（TA视图�?
        if (pathInfo == null || pathInfo.equals("/")) {
            System.out.println("Handling: View all positions");
            handleViewAllPositions(request, response);
        } else if (pathInfo.equals("/my")) {
            // /positions/my - 查看我的职位（MO视图�?
            System.out.println("Handling: View my positions");
            handleViewMyPositions(request, response);
        } else if (pathInfo.equals("/create")) {
            // /positions/create - 显示创建职位表单（MO视图�?
            System.out.println("Handling: Create position form");
            request.getRequestDispatcher("/WEB-INF/jsp/mo/create-position.jsp").forward(request, response);
        } else {
            System.out.println("No matching path, returning 404");
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
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "无效的请求路�?);
            return;
        }
        
        switch (pathInfo) {
            case "/create":
                handleCreatePosition(request, response);
                break;
            case "/delete":
                handleDeletePosition(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "请求的资源不存在");
                break;
        }
    }
    
    /**
     * 处理查看所有开放职位请求（TA视图�?
     * 需求：4.1 - 当TA查看职位列表时，系统应显示所有可用职位及其详细信�?
     */
    private void handleViewAllPositions(HttpServletRequest request, HttpServletResponse response) 
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
            
            // 获取所有开放职�?
            List<Position> positions = positionService.getAllOpenPositions();
            
            // 如果�?TA 用户，获取已申请的职�?ID 列表
            if (currentUser.getRole() == UserRole.TA) {
                List<com.bupt.tarecruitment.model.Application> myApplications = 
                    applicationService.getApplicationsByTA(currentUser.getUserId());
                java.util.Set<String> appliedPositionIds = new java.util.HashSet<>();
                for (com.bupt.tarecruitment.model.Application app : myApplications) {
                    appliedPositionIds.add(app.getPositionId());
                }
                request.setAttribute("appliedPositionIds", appliedPositionIds);
            }
            
            // 将职位列表设置到request�?
            request.setAttribute("positions", positions);
            
            // 根据用户角色转发到相应的页面
            if (currentUser.getRole() == UserRole.TA) {
                request.getRequestDispatcher("/WEB-INF/jsp/ta/positions.jsp").forward(request, response);
            } else {
                // 如果不是TA角色，也可以查看所有职�?
                request.getRequestDispatcher("/WEB-INF/jsp/ta/positions.jsp").forward(request, response);
            }
            
        } catch (Exception e) {
            request.setAttribute("errorMessage", "获取职位列表失败�? + e.getMessage());
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }
    
    /**
     * 处理查看我的职位请求（MO视图�?
     * 需求：3.2 - 当MO查看其职位时，系统应显示该MO创建的所有职�?
     */
    private void handleViewMyPositions(HttpServletRequest request, HttpServletResponse response) 
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
            
            // 验证用户角色为MO
            if (currentUser.getRole() != UserRole.MO) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "只有MO可以查看自己的职�?);
                return;
            }
            
            // 获取该MO创建的所有职�?
            List<Position> positions = positionService.getPositionsByMO(currentUser.getUserId());
            
            // 为每个职位查找被选中的TA
            java.util.Map<String, com.bupt.tarecruitment.model.Application> selectedApplications = 
                new java.util.HashMap<>();
            
            System.out.println("=== Checking selected TAs for positions ===");
            for (Position position : positions) {
                System.out.println("Position: " + position.getPositionId() + " - " + position.getTitle());
                List<com.bupt.tarecruitment.model.Application> applications = 
                    applicationService.getApplicationsByPosition(position.getPositionId());
                
                System.out.println("  Found " + applications.size() + " applications");
                
                // 查找状态为SELECTED的申�?
                for (com.bupt.tarecruitment.model.Application app : applications) {
                    System.out.println("  Application: " + app.getApplicationId() + " - Status: " + app.getStatus());
                    if (app.getStatus() == com.bupt.tarecruitment.model.ApplicationStatus.SELECTED) {
                        System.out.println("  -> SELECTED application found!");
                        selectedApplications.put(position.getPositionId(), app);
                        break;
                    }
                }
                
                if (!selectedApplications.containsKey(position.getPositionId())) {
                    System.out.println("  -> No SELECTED application for this position");
                }
            }
            
            System.out.println("Total selected applications: " + selectedApplications.size());
            
            // 将职位列表和选中的申请映射设置到request�?
            request.setAttribute("positions", positions);
            request.setAttribute("selectedApplications", selectedApplications);
            
            // 转发到MO职位页面
            request.getRequestDispatcher("/WEB-INF/jsp/mo/positions.jsp").forward(request, response);
            
        } catch (Exception e) {
            request.setAttribute("errorMessage", "获取职位列表失败�? + e.getMessage());
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }
    
    /**
     * 处理创建职位请求（MO操作�?
     * 需求：3.1 - 当MO使用有效信息创建职位时，系统应存储该职位
     * 需求：3.4 - 系统应验证必填的职位字段不为�?
     */
    private void handleCreatePosition(HttpServletRequest request, HttpServletResponse response) 
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
            
            // 验证用户角色为MO
            if (currentUser.getRole() != UserRole.MO) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "只有MO可以创建职位");
                return;
            }
            
            // 获取表单参数
            String title = request.getParameter("title");
            String description = request.getParameter("description");
            String requirements = request.getParameter("requirements");
            String hoursStr = request.getParameter("hours");
            String maxPositionsStr = request.getParameter("maxPositions");
            
            // 验证必填字段不为�?
            if (title == null || title.trim().isEmpty()) {
                request.setAttribute("errorMessage", "职位标题不能为空");
                request.getRequestDispatcher("/WEB-INF/jsp/mo/create-position.jsp").forward(request, response);
                return;
            }
            
            if (description == null || description.trim().isEmpty()) {
                request.setAttribute("errorMessage", "职位描述不能为空");
                request.getRequestDispatcher("/WEB-INF/jsp/mo/create-position.jsp").forward(request, response);
                return;
            }
            
            if (hoursStr == null || hoursStr.trim().isEmpty()) {
                request.setAttribute("errorMessage", "工作时长不能为空");
                request.getRequestDispatcher("/WEB-INF/jsp/mo/create-position.jsp").forward(request, response);
                return;
            }
            
            if (maxPositionsStr == null || maxPositionsStr.trim().isEmpty()) {
                request.setAttribute("errorMessage", "招聘名额不能为空");
                request.getRequestDispatcher("/WEB-INF/jsp/mo/create-position.jsp").forward(request, response);
                return;
            }
            
            // 解析工作时长
            int hours;
            try {
                hours = Integer.parseInt(hoursStr.trim());
                if (hours <= 0) {
                    request.setAttribute("errorMessage", "工作时长必须大于0");
                    request.getRequestDispatcher("/WEB-INF/jsp/mo/create-position.jsp").forward(request, response);
                    return;
                }
            } catch (NumberFormatException e) {
                request.setAttribute("errorMessage", "工作时长必须是有效的数字");
                request.getRequestDispatcher("/WEB-INF/jsp/mo/create-position.jsp").forward(request, response);
                return;
            }
            
            // 解析招聘名额
            int maxPositions;
            try {
                maxPositions = Integer.parseInt(maxPositionsStr.trim());
                if (maxPositions <= 0) {
                    request.setAttribute("errorMessage", "招聘名额必须大于0");
                    request.getRequestDispatcher("/WEB-INF/jsp/mo/create-position.jsp").forward(request, response);
                    return;
                }
            } catch (NumberFormatException e) {
                request.setAttribute("errorMessage", "招聘名额必须是有效的数字");
                request.getRequestDispatcher("/WEB-INF/jsp/mo/create-position.jsp").forward(request, response);
                return;
            }
            
            // 调用服务层创建职�?
            Position position = positionService.createPosition(
                currentUser.getUserId(),
                title,
                description,
                requirements,
                hours,
                maxPositions
            );
            
            // 创建成功，重定向到我的职位页�?
            response.sendRedirect(request.getContextPath() + "/mo/positions/my");
            
        } catch (IllegalArgumentException e) {
            // 业务逻辑错误
            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/mo/create-position.jsp").forward(request, response);
            
        } catch (IOException e) {
            // 数据访问错误
            request.setAttribute("errorMessage", "创建职位失败�? + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/mo/create-position.jsp").forward(request, response);
        }
    }
    
    /**
     * 处理删除职位请求（MO操作�?
     * 需求：3.3 - 当MO删除职位时，系统应移除该职位及所有相关申�?
     */
    private void handleDeletePosition(HttpServletRequest request, HttpServletResponse response) 
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
            
            // 验证用户角色为MO
            if (currentUser.getRole() != UserRole.MO) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "只有MO可以删除职位");
                return;
            }
            
            // 获取职位ID参数
            String positionId = request.getParameter("positionId");
            
            if (positionId == null || positionId.trim().isEmpty()) {
                request.setAttribute("errorMessage", "职位ID不能为空");
                response.sendRedirect(request.getContextPath() + "/mo/positions/my");
                return;
            }
            
            // 调用服务层删除职位（级联删除相关申请�?
            positionService.deletePosition(positionId.trim());
            
            // 删除成功，重定向到我的职位页�?
            response.sendRedirect(request.getContextPath() + "/mo/positions/my");
            
        } catch (IllegalArgumentException e) {
            // 业务逻辑错误（如职位不存在）
            request.setAttribute("errorMessage", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/mo/positions/my");
            
        } catch (IOException e) {
            // 数据访问错误
            request.setAttribute("errorMessage", "删除职位失败�? + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/mo/positions/my");
        }
    }
}
