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

@WebServlet("/admin/*")
public class AdminServlet extends HttpServlet {
    
   
    
    @Override
    public void init() throws ServletException {
        super.init();
        
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
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "请求的资源不存在");
                break;
        }
    }

}
