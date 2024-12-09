package com.example.quickchat.utils;

import java.util.regex.Pattern;

public class InputValidator {

    // Email validation
    public static boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // Password validation: at least 8 chars, 1 number, 1 special char
    public static boolean isValidPassword(String password) {
        String passwordPattern = "^(?=.*[0-9])(?=.*[@#$%^&+=!]).{8,}$";
        return Pattern.compile(passwordPattern).matcher(password).matches();
    }

    // Username validation: at least 3 chars
    public static boolean isValidUsername(String username) {
        return username != null && username.length() >= 3;
    }
}
