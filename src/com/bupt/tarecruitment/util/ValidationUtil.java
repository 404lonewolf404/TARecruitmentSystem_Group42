package com.bupt.tarecruitment.util;

/**
 * 数据验证工具类
 * 提供各种数据验证和安全防护功能
 */
public class ValidationUtil {
    
    /**
     * 严格邮箱验证
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        String regex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(regex);
    }
    
    /**
     * 密码强度检查
     */
    public static boolean isStrongPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        
        boolean hasLetter = password.matches(".*[a-zA-Z].*");
        boolean hasDigit = password.matches(".*\\d.*");
        
        return hasLetter && hasDigit;
    }
    
    /**
     * 获取密码强度描述
     */
    public static String getPasswordStrength(String password) {
        if (password == null || password.length() < 6) {
            return "太弱";
        }
        if (password.length() < 8) {
            return "弱";
        }
        
        int score = 0;
        if (password.matches(".*[a-z].*")) score++;
        if (password.matches(".*[A-Z].*")) score++;
        if (password.matches(".*\\d.*")) score++;
        if (password.matches(".*[!@#$%^&*].*")) score++;
        
        if (score < 2) return "弱";
        if (score < 3) return "中";
        return "强";
    }
    
    /**
     * XSS防护 - HTML转义
     */
    public static String escapeHtml(String input) {
        if (input == null) {
            return null;
        }
        return input.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#x27;")
                   .replace("/", "&#x2F;");
    }
    
    /**
     * 验证字符串长度
     */
    public static boolean isValidLength(String str, int min, int max) {
        if (str == null) {
            return false;
        }
        int len = str.trim().length();
        return len >= min && len <= max;
    }
    
    /**
     * 验证用户名格式（字母、数字、下划线，3-20字符）
     */
    public static boolean isValidUsername(String username) {
        if (username == null) {
            return false;
        }
        return username.matches("^[a-zA-Z0-9_]{3,20}$");
    }
    
    /**
     * 验证手机号格式
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null) {
            return false;
        }
        return phone.matches("^1[3-9]\\d{9}$");
    }
}