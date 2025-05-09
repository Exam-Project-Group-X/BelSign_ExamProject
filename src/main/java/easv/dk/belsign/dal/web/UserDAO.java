package easv.dk.belsign.dal.web;
import easv.dk.belsign.be.User;
import easv.dk.belsign.dal.db.DBConnection;
import easv.dk.belsign.utils.PasswordUtils;
import easv.dk.belsign.dal.IUserDAO;

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
                    user.setUsername(rs.getString("Username"));
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
    public User getUserByAccessCode(String accessCode) {
        String sql = "SELECT * FROM Users WHERE AccessCode = ?";
        try (Connection conn = new DBConnection().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, accessCode);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setUserID(rs.getInt("UserID"));
                    user.setUsername(rs.getString("Username"));
                    user.setAccessCode(rs.getString("AccessCode"));
                    user.setFullName(rs.getString("FullName"));
                    // Populate other fields as needed
                    return user;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
