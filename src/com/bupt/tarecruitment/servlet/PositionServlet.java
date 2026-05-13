package com.bupt.tarecruitment.servlet;

import com.bupt.tarecruitment.model.Position;
import com.bupt.tarecruitment.model.User;
import com.bupt.tarecruitment.model.UserRole;
import com.bupt.tarecruitment.service.PositionService;
import com.bupt.tarecruitment.service.SearchService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

/**
 */
public class PositionServlet extends HttpServlet {
    
    private PositionService positionService;
    private com.bupt.tarecruitment.service.ApplicationService applicationService;
    private SearchService searchService;
    private com.bupt.tarecruitment.dao.ApplicationDAO applicationDAO;
    
    @Override
    public void init() throws ServletException {
        super.init();
        this.positionService = new PositionService();
        this.applicationService = new com.bupt.tarecruitment.service.ApplicationService();
        this.searchService = new SearchService();
        this.applicationDAO = new com.bupt.tarecruitment.dao.ApplicationDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        
        System.out.println("=== PositionServlet.doGet() ===");
        System.out.println("Request URI: " + request.getRequestURI());
        System.out.println("Context Path: " + request.getContextPath());
        System.out.println("Servlet Path: " + request.getServletPath());
        System.out.println("Path Info: " + pathInfo);
        
        if (pathInfo == null || pathInfo.equals("/")) {
            System.out.println("Handling: View all positions");
            handleViewAllPositions(request, response);
        } else if (pathInfo.equals("/my")) {
            System.out.println("Handling: View my positions");
            handleViewMyPositions(request, response);
        } else if (pathInfo.equals("/create")) {
            System.out.println("Handling: Create position form");
            request.getRequestDispatcher("/WEB-INF/jsp/mo/create-position.jsp").forward(request, response);
        } else if (pathInfo.equals("/edit")) {
            System.out.println("Handling: Edit position form");
            handleEditPositionForm(request, response);
        } else {
            System.out.println("No matching path, returning 404");
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource not found");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request path");
            return;
        }
        
        switch (pathInfo) {
            case "/create":
                handleCreatePosition(request, response);
                break;
            case "/delete":
                handleDeletePosition(request, response);
                break;
            case "/edit":
                handleEditPosition(request, response);
                break;
            case "/update":
                handleUpdatePosition(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource not found");
                break;
        }
    }
    
    /**
     */
    private void handleViewAllPositions(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
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
            
            String keyword = request.getParameter("keyword");
            String minHoursStr = request.getParameter("minHours");
            String maxHoursStr = request.getParameter("maxHours");
            String sortBy = request.getParameter("sortBy");
            if (sortBy == null || sortBy.trim().isEmpty()) {
                sortBy = "newest";
            }
            
            System.out.println("=== Search Parameters ===");
            System.out.println("keyword: " + keyword);
            System.out.println("minHoursStr: " + minHoursStr);
            System.out.println("maxHoursStr: " + maxHoursStr);
            System.out.println("sortBy: " + sortBy);
            
            Integer minHours = null;
            Integer maxHours = null;
            
            try {
                if (minHoursStr != null && !minHoursStr.trim().isEmpty()) {
                    minHours = Integer.parseInt(minHoursStr.trim());
                }
                if (maxHoursStr != null && !maxHoursStr.trim().isEmpty()) {
                    maxHours = Integer.parseInt(maxHoursStr.trim());
                }
            } catch (NumberFormatException e) {
            }
            
            List<Position> positions;
            System.out.println("Using SearchService...");
            positions = searchService.searchPositions(keyword, minHours, maxHours, sortBy);
            System.out.println("SearchService returned " + positions.size() + " positions");
            
            request.setAttribute("keyword", keyword);
            request.setAttribute("minHours", minHours);
            request.setAttribute("maxHours", maxHours);
            request.setAttribute("sortBy", sortBy);
            
            if (currentUser.getRole() == UserRole.TA) {
                List<com.bupt.tarecruitment.model.Application> myApplications = 
                    applicationService.getApplicationsByTA(currentUser.getUserId());
                java.util.Set<String> appliedPositionIds = new java.util.HashSet<>();
                for (com.bupt.tarecruitment.model.Application app : myApplications) {
                    appliedPositionIds.add(app.getPositionId());
                }
                request.setAttribute("appliedPositionIds", appliedPositionIds);
                
                com.bupt.tarecruitment.service.FavoriteService favoriteService = 
                    new com.bupt.tarecruitment.service.FavoriteService();
                List<Position> favoritePositions = favoriteService.getFavoritePositions(currentUser.getUserId());
                java.util.Set<String> favoritedPositionIds = new java.util.HashSet<>();
                for (Position pos : favoritePositions) {
                    favoritedPositionIds.add(pos.getPositionId());
                }
                request.setAttribute("favoritedPositionIds", favoritedPositionIds);
            }
            
            java.util.Map<String, Integer> selectedCountMap = new java.util.HashMap<>();
            java.util.List<Position> availablePositions = new java.util.ArrayList<>();
            
            for (Position pos : positions) {
                int selectedCount = applicationDAO.countSelectedByPositionId(pos.getPositionId());
                selectedCountMap.put(pos.getPositionId(), selectedCount);
                
                if (selectedCount < pos.getMaxPositions() && !pos.isExpired()) {
                    availablePositions.add(pos);
                }
            }
            request.setAttribute("selectedCountMap", selectedCountMap);
            
            request.setAttribute("positions", availablePositions);
            
            if (currentUser.getRole() == UserRole.TA) {
                request.getRequestDispatcher("/WEB-INF/jsp/ta/positions.jsp").forward(request, response);
            } else {
            }
            
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Failed to load positions: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(request, response);
        }
    }
    
    /**
     */
    private void handleViewMyPositions(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
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
            
            if (currentUser.getRole() != UserRole.MO) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Only MOs can view their own positions");
                return;
            }
            
            List<Position> positions = positionService.getPositionsByMO(currentUser.getUserId());
            
            java.util.Map<String, java.util.List<com.bupt.tarecruitment.model.Application>> selectedApplicationsMap = 
                new java.util.HashMap<>();
            
            System.out.println("=== Checking selected TAs for positions ===");
            for (Position position : positions) {
                System.out.println("Position: " + position.getPositionId() + " - " + position.getTitle());
                List<com.bupt.tarecruitment.model.Application> applications = 
                    applicationService.getApplicationsByPosition(position.getPositionId());
                
                System.out.println("  Found " + applications.size() + " applications");
                
                java.util.List<com.bupt.tarecruitment.model.Application> selectedList = new java.util.ArrayList<>();
                for (com.bupt.tarecruitment.model.Application app : applications) {
                    System.out.println("  Application: " + app.getApplicationId() + " - Status: " + app.getStatus());
                    if (app.getStatus() == com.bupt.tarecruitment.model.ApplicationStatus.SELECTED) {
                        System.out.println("  -> SELECTED application found!");
                        selectedList.add(app);
                    }
                }

                if (!selectedList.isEmpty()) {
                    selectedApplicationsMap.put(position.getPositionId(), selectedList);
                }

                if (!selectedApplicationsMap.containsKey(position.getPositionId())) {
                    System.out.println("  -> No SELECTED application for this position");
                }
            }
            
            System.out.println("Positions with selected applications: " + selectedApplicationsMap.size());
            
            request.setAttribute("positions", positions);
            request.setAttribute("selectedApplicationsMap", selectedApplicationsMap);
            
            request.getRequestDispatcher("/WEB-INF/jsp/mo/positions.jsp").forward(request, response);
            
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Failed to load positions: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(request, response);
        }
    }
    
    /**
     */
    private void handleCreatePosition(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
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
            
            if (currentUser.getRole() != UserRole.MO) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Only MOs can create positions");
                return;
            }
            
            String title = request.getParameter("title");
            String description = request.getParameter("description");
            String requirements = request.getParameter("requirements");
            String hoursStr = request.getParameter("hours");
            String maxPositionsStr = request.getParameter("maxPositions");
            
            if (title == null || title.trim().isEmpty()) {
                request.setAttribute("errorMessage", "Position title cannot be empty");
                request.getRequestDispatcher("/WEB-INF/jsp/mo/create-position.jsp").forward(request, response);
                return;
            }
            
            if (description == null || description.trim().isEmpty()) {
                request.setAttribute("errorMessage", "Position description cannot be empty");
                request.getRequestDispatcher("/WEB-INF/jsp/mo/create-position.jsp").forward(request, response);
                return;
            }
            
            if (hoursStr == null || hoursStr.trim().isEmpty()) {
                request.setAttribute("errorMessage", "Work hours cannot be empty");
                request.getRequestDispatcher("/WEB-INF/jsp/mo/create-position.jsp").forward(request, response);
                return;
            }
            
            if (maxPositionsStr == null || maxPositionsStr.trim().isEmpty()) {
                request.setAttribute("errorMessage", "Max positions cannot be empty");
                request.getRequestDispatcher("/WEB-INF/jsp/mo/create-position.jsp").forward(request, response);
                return;
            }
            
            int hours;
            try {
                hours = Integer.parseInt(hoursStr.trim());
                if (hours <= 0) {
                    request.setAttribute("errorMessage", "Work hours must be greater than 0");
                    request.getRequestDispatcher("/WEB-INF/jsp/mo/create-position.jsp").forward(request, response);
                    return;
                }
            } catch (NumberFormatException e) {
                request.setAttribute("errorMessage", "Work hours must be a valid number");
                request.getRequestDispatcher("/WEB-INF/jsp/mo/create-position.jsp").forward(request, response);
                return;
            }
            
            int maxPositions;
            try {
                maxPositions = Integer.parseInt(maxPositionsStr.trim());
                if (maxPositions <= 0) {
                    request.setAttribute("errorMessage", "Max positions must be greater than 0");
                    request.getRequestDispatcher("/WEB-INF/jsp/mo/create-position.jsp").forward(request, response);
                    return;
                }
            } catch (NumberFormatException e) {
                request.setAttribute("errorMessage", "Max positions must be a valid number");
                request.getRequestDispatcher("/WEB-INF/jsp/mo/create-position.jsp").forward(request, response);
                return;
            }
            
            String deadlineStr = request.getParameter("deadline");
            java.util.Date deadline = null;
            if (deadlineStr != null && !deadlineStr.trim().isEmpty()) {
                try {
                    java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd");
                    deadline = dateFormat.parse(deadlineStr.trim());
                    
                    if (deadline.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate().isBefore(java.time.LocalDate.now())) {
                        request.setAttribute("errorMessage", "Deadline cannot be earlier than today");
                        request.getRequestDispatcher("/WEB-INF/jsp/mo/create-position.jsp").forward(request, response);
                        return;
                    }
                } catch (java.text.ParseException e) {
                    request.setAttribute("errorMessage", "Invalid deadline format");
                    request.getRequestDispatcher("/WEB-INF/jsp/mo/create-position.jsp").forward(request, response);
                    return;
                }
            }
            
            Position position;
            if (deadline != null) {
                position = positionService.createPositionWithDeadline(
                    currentUser.getUserId(),
                    title,
                    description,
                    requirements,
                    hours,
                    maxPositions,
                    deadline
                );
            } else {
                position = positionService.createPosition(
                    currentUser.getUserId(),
                    title,
                    description,
                    requirements,
                    hours,
                    maxPositions
                );
            }
            
            response.sendRedirect(request.getContextPath() + "/mo/positions/my");
            
        } catch (IllegalArgumentException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/mo/create-position.jsp").forward(request, response);
        } catch (IOException e) {
            request.setAttribute("errorMessage", "Failed to create position: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/mo/create-position.jsp").forward(request, response);
        }
    }

    private void handleDeletePosition(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
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

            if (currentUser.getRole() != UserRole.MO) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Only MOs can delete positions");
                return;
            }

            String positionId = request.getParameter("positionId");
            if (positionId == null || positionId.trim().isEmpty()) {
                session.setAttribute("errorMessage", "Position ID cannot be empty");
                response.sendRedirect(request.getContextPath() + "/mo/positions/my");
                return;
            }

            Position position = positionService.getPositionById(positionId.trim());
            if (position != null) {
                try {
                    com.bupt.tarecruitment.service.NotificationService notificationService =
                            new com.bupt.tarecruitment.service.NotificationService();
                    notificationService.sendPositionDeletedNotification(positionId.trim(), position.getTitle());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            positionService.deletePosition(positionId.trim());
            response.sendRedirect(request.getContextPath() + "/mo/positions/my");

        } catch (IllegalArgumentException e) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.setAttribute("errorMessage", e.getMessage());
            }
            response.sendRedirect(request.getContextPath() + "/mo/positions/my");
        } catch (IOException e) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.setAttribute("errorMessage", "Failed to delete position: " + e.getMessage());
            }
            response.sendRedirect(request.getContextPath() + "/mo/positions/my");
        }
    }

    private void handleEditPositionForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
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

            if (currentUser.getRole() != UserRole.MO) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Only MOs can edit positions");
                return;
            }

            String positionId = request.getParameter("positionId");
            if (positionId == null || positionId.trim().isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Position ID cannot be empty");
                return;
            }

            Position position = positionService.getPositionById(positionId.trim());
            if (position == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Position not found");
                return;
            }

            if (!position.getMoId().equals(currentUser.getUserId())) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "You can only edit positions created by yourself");
                return;
            }

            request.setAttribute("position", position);
            request.getRequestDispatcher("/WEB-INF/jsp/mo/edit-position.jsp").forward(request, response);

        } catch (Exception e) {
            request.setAttribute("errorMessage", "Failed to load position editing page: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(request, response);
        }
    }

    private void handleEditPosition(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
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

            if (currentUser.getRole() != UserRole.MO) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Only MOs can edit positions");
                return;
            }

            String positionId = request.getParameter("positionId");
            if (positionId == null || positionId.trim().isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Position ID cannot be empty");
                return;
            }

            Position existingPosition = positionService.getPositionById(positionId.trim());
            if (existingPosition == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Position not found");
                return;
            }

            if (!existingPosition.getMoId().equals(currentUser.getUserId())) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "You can only edit positions created by yourself");
                return;
            }

            String title = request.getParameter("title");
            String description = request.getParameter("description");
            String requirements = request.getParameter("requirements");
            String hoursStr = request.getParameter("hours");
            String maxPositionsStr = request.getParameter("maxPositions");
            String deadlineStr = request.getParameter("deadline");

            if (title == null || title.trim().isEmpty()) {
                request.setAttribute("errorMessage", "Position title cannot be empty");
                request.setAttribute("position", existingPosition);
                request.getRequestDispatcher("/WEB-INF/jsp/mo/edit-position.jsp").forward(request, response);
                return;
            }

            if (description == null || description.trim().isEmpty()) {
                request.setAttribute("errorMessage", "Position description cannot be empty");
                request.setAttribute("position", existingPosition);
                request.getRequestDispatcher("/WEB-INF/jsp/mo/edit-position.jsp").forward(request, response);
                return;
            }

            if (hoursStr == null || hoursStr.trim().isEmpty()) {
                request.setAttribute("errorMessage", "Work hours cannot be empty");
                request.setAttribute("position", existingPosition);
                request.getRequestDispatcher("/WEB-INF/jsp/mo/edit-position.jsp").forward(request, response);
                return;
            }

            if (maxPositionsStr == null || maxPositionsStr.trim().isEmpty()) {
                request.setAttribute("errorMessage", "Max positions cannot be empty");
                request.setAttribute("position", existingPosition);
                request.getRequestDispatcher("/WEB-INF/jsp/mo/edit-position.jsp").forward(request, response);
                return;
            }

            int hours;
            try {
                hours = Integer.parseInt(hoursStr.trim());
                if (hours <= 0) {
                    request.setAttribute("errorMessage", "Work hours must be greater than 0");
                    request.setAttribute("position", existingPosition);
                    request.getRequestDispatcher("/WEB-INF/jsp/mo/edit-position.jsp").forward(request, response);
                    return;
                }
            } catch (NumberFormatException e) {
                request.setAttribute("errorMessage", "Work hours must be a valid number");
                request.setAttribute("position", existingPosition);
                request.getRequestDispatcher("/WEB-INF/jsp/mo/edit-position.jsp").forward(request, response);
                return;
            }

            int maxPositions;
            try {
                maxPositions = Integer.parseInt(maxPositionsStr.trim());
                if (maxPositions <= 0) {
                    request.setAttribute("errorMessage", "Max positions must be greater than 0");
                    request.setAttribute("position", existingPosition);
                    request.getRequestDispatcher("/WEB-INF/jsp/mo/edit-position.jsp").forward(request, response);
                    return;
                }
            } catch (NumberFormatException e) {
                request.setAttribute("errorMessage", "Max positions must be a valid number");
                request.setAttribute("position", existingPosition);
                request.getRequestDispatcher("/WEB-INF/jsp/mo/edit-position.jsp").forward(request, response);
                return;
            }

            java.util.Date deadline = null;
            if (deadlineStr != null && !deadlineStr.trim().isEmpty()) {
                try {
                    java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd");
                    deadline = dateFormat.parse(deadlineStr.trim());

                    if (deadline.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate().isBefore(java.time.LocalDate.now())) {
                        request.setAttribute("errorMessage", "Deadline cannot be earlier than today");
                        request.setAttribute("position", existingPosition);
                        request.getRequestDispatcher("/WEB-INF/jsp/mo/edit-position.jsp").forward(request, response);
                        return;
                    }
                } catch (java.text.ParseException e) {
                    request.setAttribute("errorMessage", "Invalid deadline format");
                    request.setAttribute("position", existingPosition);
                    request.getRequestDispatcher("/WEB-INF/jsp/mo/edit-position.jsp").forward(request, response);
                    return;
                }
            }

            positionService.updatePosition(
                    positionId.trim(),
                    title,
                    description,
                    requirements,
                    hours,
                    maxPositions,
                    deadline
            );

            response.sendRedirect(request.getContextPath() + "/mo/positions/my");

        } catch (IllegalArgumentException e) {
            String positionId = request.getParameter("positionId");
            if (positionId != null && !positionId.trim().isEmpty()) {
                Position position = positionService.getPositionById(positionId.trim());
                if (position != null) {
                    request.setAttribute("position", position);
                }
            }
            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/mo/edit-position.jsp").forward(request, response);

        } catch (IOException e) {
            String positionId = request.getParameter("positionId");
            if (positionId != null && !positionId.trim().isEmpty()) {
                Position position = positionService.getPositionById(positionId.trim());
                if (position != null) {
                    request.setAttribute("position", position);
                }
            }
            request.setAttribute("errorMessage", "Failed to update position: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/mo/edit-position.jsp").forward(request, response);
        }
    }

    private void handleUpdatePosition(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        handleEditPosition(request, response);
    }
}


