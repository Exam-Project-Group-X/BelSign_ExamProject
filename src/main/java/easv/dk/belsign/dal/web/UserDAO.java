package easv.dk.belsign.dal.web;

import easv.dk.belsign.be.User;
import easv.dk.belsign.dal.db.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class UserDAO {

    private final String DB_URL = "jdbc:sqlite:belman.db";
    private final String TABLE_NAME = "Users";

    public UserDAO() {
        try {
            Class.forName("org.sqlite.JDBC");
            createTableIfNotExists();
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC Driver not found.");
            e.printStackTrace();
        }
    }

    private void createTableIfNotExists() {
        String sql = """
            CREATE TABLE IF NOT EXISTS Users (
                UserID INTEGER PRIMARY KEY AUTOINCREMENT,
                Username TEXT NOT NULL UNIQUE,
                PasswordHash TEXT,
                AccessCode TEXT,
                FullName TEXT,
                Email TEXT,
                CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                UpdatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                RoleID INTEGER,
                Active BOOLEAN DEFAULT 1
            );
        """;

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Failed to ensure Users table exists.");
            e.printStackTrace();
        }
    }

    public ObservableList<User> getAllUsers() {
        ObservableList<User> users = FXCollections.observableArrayList();
        String sql = "SELECT * FROM Users";
        try (Connection conn = new DBConnection().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                User user = new User();
                user.setUserID(rs.getInt("UserID"));
                user.setUsername(rs.getString("Username"));
                user.setPasswordHash(rs.getString("PasswordHash"));
                user.setAccessCode(rs.getString("AccessCode"));
                user.setFullName(rs.getString("FullName"));
                user.setEmail(rs.getString("Email"));
                user.setCreatedAt(rs.getTimestamp("CreatedAt"));
                user.setUpdatedAt(rs.getTimestamp("UpdatedAt"));
                user.setActive(true); // Assuming Active column missing — update if needed

                int roleId = rs.getInt("RoleID");
                switch (roleId) {
                    case 1 -> user.setUserRole("Admin");
                    case 2 -> user.setUserRole("QA");
                    case 3 -> user.setUserRole("Operator");
                    default -> user.setUserRole("Unknown");
                }
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
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

    public boolean addUser(User user) {
        String sql = "INSERT INTO " + TABLE_NAME + " (Username, PasswordHash, AccessCode, FullName, Email, CreatedAt, UpdatedAt, RoleID, Active) VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPasswordHash());
            stmt.setString(3, user.getAccessCode());
            stmt.setString(4, user.getFullName());
            stmt.setString(5, user.getEmail());
            stmt.setInt(6, getRoleIdFromString(user.getUserRole()));
            stmt.setBoolean(7, user.isActive());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteUser(User user) {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE Username = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUsername());
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateUser(User user) {
        String sql = "UPDATE " + TABLE_NAME + " SET PasswordHash = ?, FullName = ?, Email = ?, RoleID = ?, Active = ?, UpdatedAt = CURRENT_TIMESTAMP WHERE Username = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getPasswordHash());
            stmt.setString(2, user.getFullName());
            stmt.setString(3, user.getEmail());
            stmt.setInt(4, getRoleIdFromString(user.getUserRole()));
            stmt.setBoolean(5, user.isActive());
            stmt.setString(6, user.getUsername());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateUserRole(User user) {
        String sql = "UPDATE " + TABLE_NAME + " SET RoleID = ?, UpdatedAt = CURRENT_TIMESTAMP WHERE Username = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, getRoleIdFromString(user.getUserRole()));
            stmt.setString(2, user.getUsername());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateUserStatus(User user) {
        String sql = "UPDATE " + TABLE_NAME + " SET Active = ?, UpdatedAt = CURRENT_TIMESTAMP WHERE Username = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBoolean(1, user.isActive());
            stmt.setString(2, user.getUsername());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private int getRoleIdFromString(String role) {
        return switch (role.toLowerCase()) {
            case "admin" -> 1;
            case "qa" -> 2;
            case "operator" -> 3;
            default -> 0;
        };
    }
}
