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

        String query = "SELECT user_id, username, email, full_name, role, status " +
                       "FROM users WHERE (email = ? OR username = ?) AND password_hash = ?";

        try (Connection conn = DatabaseConnector.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, identifier);
            stmt.setString(2, identifier);
            stmt.setString(3, hashedPassword);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    
                    String status = rs.getString("status");
                
                    // Block authentication if user is not Active
                    if (!"Active".equalsIgnoreCase(status)) {
                        return null; // Account disabled or inactive
                    }

                    return new User(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("full_name"),
                        rs.getString("role"),
                        rs.getString("status")
                            
                            
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

        String query = "UPDATE users SET full_name=?, role=?, hashedPassword=?, status=? WHERE user_id=?";

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
        String query = "SELECT user_id, username, email, full_name, role, status FROM users";

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
                    rs.getString("status")
                );
                userList.add(user);
            }
        }
        return userList;
    }
    
    public String validateUserForAdd(User user, String plainPassword) {
        // 1. Basic Null and Empty Checks
        if (user == null) {
            return "User data cannot be null.";
        }
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            return "Username is required.";
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            return "Email address is required.";
        }
        if (user.getFullName() == null || user.getFullName().trim().isEmpty()) {
            return "Full Name is required.";
        }
        if (plainPassword == null || plainPassword.isEmpty()) {
            return "Password is required.";
        }

        // 2. Format & Pattern Checks
        if (!user.getUsername().matches("^[a-zA-Z0-9_]{3,20}$")) {
            return "Username must be 3-20 alphanumeric characters or underscores.";
        }

        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        if (!user.getEmail().matches(emailRegex)) {
            return "Invalid email format.";
        }

        if (plainPassword.length() < 6) {
            return "Password must be at least 6 characters long.";
        }

        // 3. Database Integrity Checks (Uniqueness)
        String checkSql = "SELECT username, email FROM users WHERE username = ? OR email = ?";
        
        try (Connection conn = DatabaseConnector.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(checkSql)) {

            stmt.setString(1, user.getUsername().trim());
            stmt.setString(2, user.getEmail().trim());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String existingUsername = rs.getString("username");
                    String existingEmail = rs.getString("email");

                    if (user.getUsername().equalsIgnoreCase(existingUsername)) {
                        return "Username '" + user.getUsername() + "' is already taken.";
                    }
                    if (user.getEmail().equalsIgnoreCase(existingEmail)) {
                        return "Email address is already registered.";
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return "Database validation error: " + e.getMessage();
        }

        // Return null if all validations pass
        return null; 
    }

    /**
     * Inserts a new user after running DAO validation.
     */
    public boolean addUser(User user, String plainPassword) {
        // 1. Validate inputs before executing SQL
        String validationError = validateUserForAdd(user, plainPassword);
        if (validationError != null) {
            System.err.println("Validation failed: " + validationError);
            return false;
        }

        String sql = "INSERT INTO users (username, email, full_name, role, status, password_hash) VALUES (?, ?, ?, ?, ?, ?)";
        String hashedPassword = hashPassword(plainPassword);

        try (Connection conn = DatabaseConnector.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUsername().trim());
            stmt.setString(2, user.getEmail().trim());
            stmt.setString(3, user.getFullName().trim());
            stmt.setString(4, user.getRole());
            stmt.setString(5, user.getStatus());
            stmt.setString(6, hashedPassword);

            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;

        } catch (SQLException e) {
            System.err.println("Database Exception in addUser: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // 2. UPDATE USER
    public boolean updateUser(User user) throws SQLException {
        boolean updatePassword = user.getPassword() != null && !user.getPassword().trim().isEmpty();

        String query;
        if (updatePassword) {
            query = "UPDATE users SET username = ?, full_name = ?, email = ?, role = ?, status = ?, password_hash = ? WHERE user_id = ?";
        } else {
            query = "UPDATE users SET username = ?, full_name = ?, email = ?, role = ?, status = ? WHERE user_id = ?";
        }

        try (Connection conn = DatabaseConnector.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getFullName());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getRole());
            stmt.setString(5, user.getStatus());

            if (updatePassword) {
                stmt.setString(6, hashPassword(user.getPassword()));
                stmt.setInt(7, user.getUserId());
            } else {
                stmt.setInt(6, user.getUserId());
            }

            return stmt.executeUpdate() > 0;
        }
    }

    // 3. DELETE USER
    public boolean deleteUser(int userId) throws SQLException {
        String query = "DELETE FROM users WHERE user_id = ?";
        try (Connection conn = DatabaseConnector.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            return stmt.executeUpdate() > 0;
        }
    }
    
    
}
