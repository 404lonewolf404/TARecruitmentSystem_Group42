package com.bupt.tarecruitment.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Default entry servlet that redirects to the login page.
 */
public class IndexServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // 中文说明：根路径统一重定向到登录页。
        response.sendRedirect(request.getContextPath() + "/auth/login");
    }
}
