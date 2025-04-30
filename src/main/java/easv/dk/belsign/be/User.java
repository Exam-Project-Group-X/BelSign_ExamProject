package easv.dk.belsign.be;

import java.sql.Timestamp;

public class User {
    private int userID;
    private String username;
    private String passwordHash;
    private String email;
    private String userRole;
    private boolean active;

    private String accessCode; // (optional for Operators)
    private String fullName;   // (optional for additional info)
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Default constructor
    public User() {
        this.active = true;
    }

    // Constructor with main fields (for Admin, QA, Operator)
    public User(String username, String email, String passwordHash, String userRole) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.userRole = userRole;
        this.active = true;
    }

    // Getters and Setters
    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getAccessCode() {
        return accessCode;
    }

    public void setAccessCode(String accessCode) {
        this.accessCode = accessCode;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
}
