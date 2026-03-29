package com.bupt.tarecruitment.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * 测试Servlet - 用于诊断路由问题
 */
public class TestServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head><title>测试页面</title></head>");
        out.println("<body>");
        out.println("<h1>Servlet测试成功！</h1>");
        out.println("<p>Context Path: " + request.getContextPath() + "</p>");
        out.println("<p>Servlet Path: " + request.getServletPath() + "</p>");
        out.println("<p>Path Info: " + request.getPathInfo() + "</p>");
        out.println("<p>Request URI: " + request.getRequestURI() + "</p>");
        out.println("<h2>测试链接：</h2>");
        out.println("<ul>");
        out.println("<li><a href='" + request.getContextPath() + "/profile'>Profile</a></li>");
        out.println("<li><a href='" + request.getContextPath() + "/positions'>Positions</a></li>");
        out.println("<li><a href='" + request.getContextPath() + "/applications/my'>My Applications</a></li>");
        out.println("</ul>");
        out.println("</body>");
        out.println("</html>");
    }
}
