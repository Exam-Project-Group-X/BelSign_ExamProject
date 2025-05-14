package easv.dk.belsign.dal.web;

import easv.dk.belsign.dal.db.DBConnection;

import java.sql.*;

public class NotificationsDAO {
    private final DBConnection con = new DBConnection();

    public void notifyOperatorOrderReviewed(int orderId) throws SQLException {
        String operatorQuery = "SELECT AssignedToUserID FROM Orders WHERE OrderID = ?";
        int operatorId = -1;

        try (Connection conn = con.getConnection();
             PreparedStatement ps = conn.prepareStatement(operatorQuery)) {
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                operatorId = rs.getInt("AssignedToUserID");
            }
        }

        if (operatorId == -1) return;

        String insert = "INSERT INTO Notifications (UserID, Message, Type, IsRead, CreatedAt) VALUES (?, ?, ?, FALSE, CURRENT_TIMESTAMP)";
        try (Connection conn = con.getConnection();
             PreparedStatement ps = conn.prepareStatement(insert)) {
            ps.setInt(1, operatorId);
            ps.setString(2, "Photos for Order #" + orderId + " have been reviewed.");
            ps.setString(3, "PHOTO_REVIEW_COMPLETE");
            ps.executeUpdate();
        }

        System.out.println("📩 Notification sent to Operator (ID: " + operatorId + ") for reviewed OrderID: " + orderId);
    }
}