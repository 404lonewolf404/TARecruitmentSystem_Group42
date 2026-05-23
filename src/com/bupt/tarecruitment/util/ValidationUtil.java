package com.bupt.tarecruitment.util;

/**
 * Validation helpers used by forms and services.
 */
public class ValidationUtil {
    
    /**
     * Validates whether the input matches the email format.
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        String regex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(regex);
    }
    
    /**
     * Validates whether the password meets the minimum strength rule.
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
     * Returns a readable password strength label.
     */
    public static String getPasswordStrength(String password) {
        if (password == null || password.length() < 6) {
            return "Very Weak";
        }
        if (password.length() < 8) {
            return "Weak";
        }
        
        int score = 0;
        if (password.matches(".*[a-z].*")) score++;
        if (password.matches(".*[A-Z].*")) score++;
        if (password.matches(".*\\d.*")) score++;
        if (password.matches(".*[!@#$%^&*].*")) score++;
        
        if (score < 2) return "Weak";
        if (score < 3) return "Medium";
        return "Strong";
    }
    
    /**
     * Escapes basic HTML characters to reduce XSS risk.
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
     * Validates whether a string length is within range after trimming.
     */
    public static boolean isValidLength(String str, int min, int max) {
        if (str == null) {
            return false;
        }
        int len = str.trim().length();
        return len >= min && len <= max;
    }
    
    /**
     * Validates whether the username format is allowed.
     */
    public static boolean isValidUsername(String username) {
        if (username == null) {
            return false;
        }
        return username.matches("^[a-zA-Z0-9_]{3,20}$");
    }
    
    /**
     * Validates whether the phone number matches the mobile pattern.
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null) {
            return false;
        }
        return phone.matches("^1[3-9]\\d{9}$");
    }
}
