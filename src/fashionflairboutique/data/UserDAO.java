/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fashionflairboutique.data;

import fashionflairboutique.models.User;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Chethiya
 */
public class UserDAO {
    public User authenticateUser(String identifier, String plainPassword) throws SQLException {
        // Hash the incoming plain text password directly using internal helper
        String hashedPassword = hashPassword(plainPassword);
        
        // DEBUG PRINTING - Check your IDE console when clicking Login
        // System.out.println("Input Identifier: '" + identifier + "'");
        // System.out.println("Input Password: '" + plainPassword + "'");
        // System.out.println("Generated SHA-256 Hash: " + hashedPassword);

        String query = "SELECT user_id, username, email, full_name, role, is_active " +
                       "FROM users WHERE (email = ? OR username = ?) AND password_hash = ?";

        try (Connection conn = DatabaseConnector.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, identifier);
            stmt.setString(2, identifier);
            stmt.setString(3, hashedPassword);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    if (!rs.getBoolean("is_active")) {
                        return null; // Account disabled
                    }

                    return new User(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("full_name"),
                        rs.getString("role"),
                        rs.getBoolean("is_active")
                            
                    );
                }
            }
        }
        return null; // Authentication failed
    }

    /**
     * Resets/updates a user's password based on their email or username.
     */
    public boolean updatePassword(String identifier, String newPlainPassword) throws SQLException {
        String hashedPassword = hashPassword(newPlainPassword);

        String query = "UPDATE users SET password_hash = ? WHERE (email = ? OR username = ?) AND is_active = 1";

        try (Connection conn = DatabaseConnector.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, hashedPassword);
            stmt.setString(2, identifier);
            stmt.setString(3, identifier);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * method to compute SHA-256 hash of a plain text password.
     */
    private static String hashPassword(String basePassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(basePassword.getBytes(StandardCharsets.UTF_8));
            
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
            
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
    
    public List<User> getAllUsers() throws SQLException {
        List<User> userList = new ArrayList<>();
        String query = "SELECT user_id, username, email, full_name, role, is_active FROM users";

        try (Connection conn = DatabaseConnector.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                User user = new User(
                    rs.getInt("user_id"),
                    rs.getString("username"),
                    rs.getString("email"),
                    rs.getString("full_name"),
                    rs.getString("role"),
                    rs.getBoolean("is_active")
                );
                userList.add(user);
            }
        }
        return userList;
    }
    
    
}
