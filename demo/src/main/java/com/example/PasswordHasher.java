package com.example;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordHasher {

    public static String hashPassword(String password) {
        try {
            // Create a SHA-256 MessageDigest
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            // Hash the password and convert it to bytes
            byte[] hashedBytes = md.digest(password.getBytes());
            // Convert the hashed bytes to a hexadecimal string
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b)); // Converts byte to hex
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
    // Verify the password by comparing hashes
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        // Hash the plain-text password
        String hashedPlainPassword = hashPassword(plainPassword);
        // Compare it with the stored hashed password
        return hashedPlainPassword.equals(hashedPassword);
    }
}

