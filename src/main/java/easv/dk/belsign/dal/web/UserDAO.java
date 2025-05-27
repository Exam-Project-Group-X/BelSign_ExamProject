package easv.dk.belsign.dal.web;
import easv.dk.belsign.be.User;
import easv.dk.belsign.dal.db.DBConnection;
import easv.dk.belsign.utils.PasswordUtils;
import easv.dk.belsign.dal.IUserDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

public class UserDAO implements IUserDAO {
    private DBConnection con = new DBConnection();

    public List<User> getAllUsers() throws SQLException {
        List<User> allUsers = new ArrayList<>();
        String sql = """
        SELECT u.UserID, u.AccessCode, u.FullName, u.Email, u.PasswordHash,
               u.RoleID, u.CreatedAt, u.UpdatedAt, u.Active, r.RoleName
        FROM   Users u
        JOIN   UserRoles r ON r.RoleID = u.RoleID
        """;
        try (Connection connection = con.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int userId = rs.getInt("UserID");
                String accessCode = rs.getString("AccessCode");
                String fullName = rs.getString("FullName");
                String email = rs.getString("Email");
                String passwordHash = rs.getString("PasswordHash");
                int roleId = rs.getInt("RoleID");
                Timestamp createdAt = rs.getTimestamp("CreatedAt");
                Timestamp updatedAt = rs.getTimestamp("UpdatedAt");
                boolean active = rs.getBoolean("Active");
                String roleName = rs.getString("RoleName");
                User user = new User(userId, passwordHash, accessCode,
                        fullName, email, roleId,
                        createdAt, updatedAt, active,
                        roleName);
                allUsers.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return allUsers;
    }

    @Override
    public User createNewUser(User user) throws SQLException {
        String sql = "INSERT INTO Users (FullName, Email, PasswordHash, RoleID) VALUES (?, ?, ?, ?)";

        try (Connection connection = con.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPasswordHash());
            ps.setInt(4, user.getRoleId());
            ps.executeUpdate();
            //Get the generated ID from DB
            ResultSet rs = ps.getGeneratedKeys();
            int userID = 0;
                if (rs.next()) {
                    userID = rs.getInt(1); // Return the generated user ID
                }
                User newUser = new User(userID, user.getPasswordHash(), user.getAccessCode(),
                        user.getFullName(), user.getEmail(), user.getRoleId(),
                        user.getCreatedAt(), user.getUpdatedAt(), user.isActive(),
                        user.getRoleName());
                return newUser;
            }
        catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Error creating new user: " + e.getMessage(), e);
        }
    }

    @Override
    public List<String> getAllRoleNames() throws SQLException {
        List<String> roleList = new ArrayList<>();
        String sql = "SELECT DISTINCT RoleName FROM UserRoles";
        try (Connection connection = con.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                roleList.add(rs.getString("RoleName"));
            }
        } return roleList;
    }

    @Override
    public void deleteUser(User user) throws SQLException {
        String sql = "DELETE FROM users where UserID = ?";
        try (Connection connection = con.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, user.getUserID());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting users and its dependencies: " + e.getMessage(), e);
        }
    }


    @Override
    public void updateUser(User user) throws SQLException {
        String sql = "UPDATE Users SET FullName = ?, Email = ?, PasswordHash = ?, RoleID = ? WHERE UserID = ?";
        try (Connection connection = con.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPasswordHash());
            ps.setInt(4, user.getRoleId());
            ps.setInt(5, user.getUserID());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating users: " + e.getMessage(), e);
        }
    }


    public User authenticateAndGetUser(String email, String rawPassword) {
        String sql = """
        SELECT u.*, r.RoleName
        FROM Users u
        JOIN UserRoles r ON u.RoleID = r.RoleID
        WHERE u.Email = ? AND u.Active = 1
        """;
        try (Connection conn = con.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String storedHash = rs.getString("PasswordHash");
                if (PasswordUtils.checkPassword(rawPassword, storedHash)) {
                    User user = new User();
                    user.setUserID(rs.getInt("UserID"));
                    user.setEmail(rs.getString("Email"));
                    user.setPasswordHash(storedHash);
                    user.setRoleName(rs.getString("RoleName"));
                    user.setAccessCode(rs.getString("AccessCode"));
                    user.setFullName(rs.getString("FullName"));
                    user.setActive(rs.getBoolean("Active"));
                    user.setCreatedAt(rs.getTimestamp("CreatedAt"));
                    user.setUpdatedAt(rs.getTimestamp("UpdatedAt"));
                    return user;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}