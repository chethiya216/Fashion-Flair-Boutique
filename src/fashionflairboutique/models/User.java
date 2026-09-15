/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fashionflairboutique.models;

/**
 *
 * @author Chethiya
 */
public class User {
    private int userId;
    private String username;
    private String email;
    private String fullName;
    private String role;
    private boolean isActive;

    public User(int userId, String username, String email, String fullName, String role, boolean isActive) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
        this.isActive = isActive;
    }
    
    public String getStatusDisplay() {
        return isActive ? "Active" : "Inactive";
    }

    public int getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getFullName() { return fullName; }
    public String getRole() { return role; }
    public boolean isActive() { return isActive; }
    
}    
