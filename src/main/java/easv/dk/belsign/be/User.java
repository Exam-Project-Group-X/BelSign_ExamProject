package easv.dk.belsign.be;

import java.sql.Timestamp;

public class User {
    private int userID;
    private String passwordHash;
    private String accessCode;
    private String fullName;
    private String email;
    private int roleId;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private boolean active;
    private String roleName;

    // Default constructor
    public User() {
        this.active = true;
    }

    public User(int userID, String passwordHash, String accessCode, String fullName, String email, int roleId, Timestamp createdAt, Timestamp updatedAt, boolean active, String roleName) {
        this.userID = userID;
        this.passwordHash = passwordHash;
        this.accessCode = accessCode;
        this.fullName = fullName;
        this.email = email;
        this.roleId = roleId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.active = active;
        this.roleName = roleName;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    @Override
    public String toString() {
        return roleName + ',' + email + ',' + fullName;
    }
}