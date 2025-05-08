package easv.dk.belsign.dal.web;

import easv.dk.belsign.be.User;
import easv.dk.belsign.dal.IUserDAO;
import easv.dk.belsign.dal.db.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO implements IUserDAO {
    private DBConnection con = new DBConnection();
    public List<User> getAllUsers() throws SQLException {
        List<User> allUsers = new ArrayList<>();
        String sql = """
        SELECT u.UserID, u.UserName, u.AccessCode, u.FullName, u.Email, u.PasswordHash,
               u.RoleID, u.CreatedAt, u.UpdatedAt, u.Active, r.RoleName
        FROM   Users u
        JOIN   UserRoles r ON r.RoleID = u.RoleID
        """;
        try (Connection connection = con.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
             ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int userId = rs.getInt("UserID");
                String userName = rs.getString("Username");
                String accessCode = rs.getString("AccessCode");
                String fullName = rs.getString("FullName");
                String email = rs.getString("Email");
                String passwordHash = rs.getString("PasswordHash");
                int roleId = rs.getInt("RoleID");
                Timestamp createdAt = rs.getTimestamp("CreatedAt");
                Timestamp updatedAt = rs.getTimestamp("UpdatedAt");
                boolean active = rs.getBoolean("Active");
                String roleName = rs.getString("RoleName");
                User user = new User(userId, userName, passwordHash, accessCode,
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
    public void createNewUser(User user) throws SQLException {
        String sqlUser = "INSERT INTO Users (FullName, Email, Username, PasswordHash, RoleID) VALUES (?, ?, ?, ?, ?)";
        String sqlRole = "INSERT INTO UserRoles (RoleID, RoleName) VALUES (?, ?)";
        Connection connection = con.getConnection();
        try {
            connection.setAutoCommit(false);
            try (PreparedStatement psUser = connection.prepareStatement(sqlUser)) {
                psUser.setString(1, user.getFullName());
                psUser.setString(2, user.getEmail());
                psUser.setString(3, user.getUsername());
                psUser.setString(4, user.getPasswordHash());
                psUser.setInt(5, user.getRoleId());
                psUser.executeUpdate();
            }
            try (PreparedStatement psRole = connection.prepareStatement(sqlRole)) {
                psRole.setInt(1, user.getRoleId());
                psRole.setString(2, user.getRoleName());
                psRole.executeUpdate();
            }
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            e.printStackTrace();
            throw e;
        } finally {
            connection.setAutoCommit(true);
            connection.close();
        }
    }

    @Override
    public void deleteUser(User user) throws SQLException {

    }

    @Override
    public void updateUser(User user) throws SQLException {

    }



}
