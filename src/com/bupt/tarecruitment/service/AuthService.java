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
 * Authentication and account service.
 */
public class AuthService {

    private UserDAO userDAO;

    /**
     * Creates the authentication service.
     */
    public AuthService() {
        this.userDAO = new UserDAO();
    }

    /**
     * Registers a new user account.
     *
     * @param name name value
     * @param email email value
     * @param password password value
     * @param role role value
     * @param skills skills value
     * @return operation result
     * @throws IllegalArgumentException if operation fails
     * @throws IOException if operation fails
     */
    public User register(String name, String email, String password, UserRole role, String skills)
            throws IllegalArgumentException, IOException {

        // 中文说明：校验注册必要字段。
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }

        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }

        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }

        if (role == null) {
            throw new IllegalArgumentException("Role cannot be empty");
        }

        // 中文说明：邮箱不能重复注册。
        if (userDAO.emailExists(email)) {
            throw new IllegalArgumentException("This email is already registered");
        }

        // 中文说明：构造新用户对象并写入默认值。
        User user = new User();
        user.setUserId(UUID.randomUUID().toString());
        user.setName(name.trim());
        user.setEmail(email.trim());
        user.setPassword(hashPassword(password));
        user.setRole(role);
        user.setSkills(skills != null ? skills.trim() : "");
        user.setCreatedAt(new Date());

        // 中文说明：持久化新用户。
        userDAO.add(user);

        return user;
    }

    /**
     * Authenticates a user by email and password.
     *
     * @param email email value
     * @param password password value
     * @return operation result
     * @throws IllegalArgumentException if operation fails
     */
    public User login(String email, String password) throws IllegalArgumentException {

        // 中文说明：校验登录必要字段。
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }

        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }

        // 中文说明：根据邮箱查询用户。
        User user = userDAO.findByEmail(email.trim());

        if (user == null) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        // 中文说明：比较密码哈希值。
        String hashedPassword = hashPassword(password);
        if (!user.getPassword().equals(hashedPassword)) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        return user;
    }

    /**
     * Resets a user's password by email.
     *
     * @param email email value
     * @param newPassword newPassword value
     * @throws IllegalArgumentException if operation fails
     * @throws IOException if operation fails
     */
    public void resetPasswordByEmail(String email, String newPassword)
            throws IllegalArgumentException, IOException {

        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("New password cannot be empty");
        }

        User user = userDAO.findByEmail(email.trim());
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        user.setPassword(hashPassword(newPassword));
        userDAO.update(user);
    }

    /**
     * Logs the current user out by invalidating the session.
     *
     * @param session session value
     */
    public void logout(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
    }

    /**
     * Returns whether the current session is authenticated.
     *
     * @param session session value
     * @return operation result
     */
    public boolean isAuthenticated(HttpSession session) {
        if (session == null) {
            return false;
        }

        User user = (User) session.getAttribute("user");
        return user != null;
    }

    /**
     * Returns the current logged-in user from the session.
     *
     * @param session session value
     * @return operation result
     */
    public User getCurrentUser(HttpSession session) {
        if (session == null) {
            return null;
        }

        return (User) session.getAttribute("user");
    }

    /**
     * Hashes a plain-text password using SHA-256.
     *
     * @param password password value
     * @return operation result
     */
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());

            // 中文说明：把字节数组转换为十六进制字符串。
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
            throw new RuntimeException("SHA-256 algorithm is not available", e);
        }
    }
}
