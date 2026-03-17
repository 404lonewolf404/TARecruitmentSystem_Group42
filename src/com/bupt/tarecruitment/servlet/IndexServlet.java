package com.bupt.tarecruitment.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Index Servlet
 * 处理根路径访问，重定向到登录页面
 */
public class IndexServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // 重定向到登录页面
        response.sendRedirect(request.getContextPath() + "/auth/login");
    }
}
