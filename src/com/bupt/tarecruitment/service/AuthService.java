package com.bupt.tarecruitment.service;

import com.bupt.tarecruitment.dao.UserDAO;
import com.bupt.tarecruitment.model.User;
import com.bupt.tarecruitment.model.UserRole;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.UUID;

/**
 * 认证服务�?
 * 处理用户注册、登录、登出等认证相关的业务逻辑
 */
public class AuthService {
    
    private UserDAO userDAO;
    
    /**
     * 构造函�?
     */
    public AuthService() {
        this.userDAO = new UserDAO();
    }
    
    /**
     * 用户注册
     * 
     * @param name 用户姓名
     * @param email 用户邮箱
     * @param password 用户密码（明文）
     * @param role 用户角色
     * @param skills 用户技能（可选，仅TA使用�?
     * @return 注册成功的用户对�?
     * @throws IllegalArgumentException 如果邮箱已存在或参数无效
     * @throws IOException 如果数据保存失败
     */
    public User register(String name, String email, String password, UserRole role, String skills) 
            throws IllegalArgumentException, IOException {
        
        // 验证必填字段
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("姓名不能为空");
        }
        
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("邮箱不能为空");
        }
        
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("密码不能为空");
        }
        
        if (role == null) {
            throw new IllegalArgumentException("角色不能为空");
        }
        
        // 检查邮箱唯一�?
        if (userDAO.emailExists(email)) {
            throw new IllegalArgumentException("该邮箱已被注�?);
        }
        
        // 创建新用�?
        User user = new User();
        user.setUserId(UUID.randomUUID().toString());
        user.setName(name.trim());
        user.setEmail(email.trim());
        user.setPassword(hashPassword(password));
        user.setRole(role);
        user.setSkills(skills != null ? skills.trim() : "");
        user.setCreatedAt(new Date());
        
        // 保存用户
        userDAO.add(user);
        
        return user;
    }
    
    /**
     * 用户登录
     * 
     * @param email 用户邮箱
     * @param password 用户密码（明文）
     * @return 登录成功的用户对�?
     * @throws IllegalArgumentException 如果凭证无效
     */
    public User login(String email, String password) throws IllegalArgumentException {
        
        // 验证参数
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("邮箱不能为空");
        }
        
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("密码不能为空");
        }
        
        // 查找用户
        User user = userDAO.findByEmail(email.trim());
        
        if (user == null) {
            throw new IllegalArgumentException("邮箱或密码错�?);
        }
        
        // 验证密码
        String hashedPassword = hashPassword(password);
        if (!user.getPassword().equals(hashedPassword)) {
            throw new IllegalArgumentException("邮箱或密码错�?);
        }
        
        return user;
    }
    
    /**
     * 用户登出
     * 
     * @param session HTTP会话对象
     */
    public void logout(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
    }
    
    /**
     * 检查用户是否已认证
     * 
     * @param session HTTP会话对象
     * @return 如果用户已认证返回true，否则返回false
     */
    public boolean isAuthenticated(HttpSession session) {
        if (session == null) {
            return false;
        }
        
        User user = (User) session.getAttribute("user");
        return user != null;
    }
    
    /**
     * 获取当前登录用户
     * 
     * @param session HTTP会话对象
     * @return 当前登录的用户对象，如果未登录返回null
     */
    public User getCurrentUser(HttpSession session) {
        if (session == null) {
            return null;
        }
        
        return (User) session.getAttribute("user");
    }
    
    /**
     * 使用SHA-256算法对密码进行哈�?
     * 
     * @param password 明文密码
     * @return 哈希后的密码（十六进制字符串�?
     */
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            
            // 转换为十六进制字符串
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256算法不可�?, e);
        }
    }
}
