package easv.dk.belsign.be;

import java.sql.Timestamp;

public class User {
    private int UserID;
    private String Username; // For Admins and QA Employees (nullable for Operators)
    private String PasswordHash; // Encrypted password (nullable for Operators)
    private String AccessCode; // For Operators (nullable for Admins and QA Employees)
    private String FullName;
    private String Email;
    private Timestamp CreatedAt; // Timestamp for account creation
    private Timestamp UpdatedAt; // Timestamp for last update
    private String UserRole; // This will hold the Role's name ("Admin", "QA Employee" or "Operator")

    // Default constructor
    public User() {

    }

    public int getUserID() {
        return UserID;
    }

    public void setUserID(int userID) {
        UserID = userID;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getPasswordHash() {
        return PasswordHash;
    }

    public void setPasswordHash(String passwordHash) {
        PasswordHash = passwordHash;
    }

    public String getAccessCode() {
        return AccessCode;
    }

    public void setAccessCode(String accessCode) {
        AccessCode = accessCode;
    }

    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public Timestamp getCreatedAt() {
        return CreatedAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        CreatedAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return UpdatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        UpdatedAt = updatedAt;
    }

    public String getUserRole() {
        return UserRole;
    }

    public void setUserRole(String userRole) {
        UserRole = userRole;
    }
}
