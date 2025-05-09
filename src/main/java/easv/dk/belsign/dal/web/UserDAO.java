package easv.dk.belsign.dal.web;

import easv.dk.belsign.be.User;
import easv.dk.belsign.dal.db.DBConnection;
import easv.dk.belsign.utils.PasswordUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {
    private DBConnection con = new DBConnection();


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
                    user.setUserRole(rs.getString("RoleName"));
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
