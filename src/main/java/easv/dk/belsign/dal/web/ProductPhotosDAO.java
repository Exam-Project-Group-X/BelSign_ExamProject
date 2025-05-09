package easv.dk.belsign.dal.web;


import easv.dk.belsign.dal.db.DBConnection;
import easv.dk.belsign.exceptions.OrderException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;


public class ProductPhotosDAO {

    private DBConnection con = new DBConnection();

    public Map<String, byte[]> getPhotosByOrderId(int orderId) throws SQLException {
        Map<String, byte[]> photos = new HashMap<>();

        String sql = "SELECT PhotoAngle, PhotoData FROM ProductPhotos WHERE OrderID = ?";
        try (Connection conn = con.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String angle = rs.getString("PhotoAngle");
                byte[] data = rs.getBytes("PhotoData");
                photos.put(angle.toUpperCase(), data);
            }
        }

        return photos;
    }

    public void insertCapturedPhoto(int orderId, String angle, byte[] imageData) throws SQLException {
        if (imageData == null) return;

        String sql = "INSERT INTO ProductPhotos (OrderID, PhotoAngle, Status, PhotoData) VALUES (?, ?, ?, ?)";

        try (Connection conn = con.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, orderId);
            ps.setString(2, angle);
            ps.setString(3, "Pending");
            ps.setBytes(4, imageData);
            ps.executeUpdate();

            System.out.println("✅ Uploaded photo for " + angle + " [Order ID: " + orderId + "]");
        }
    }

    public void approvePhoto(int orderId, String angle) throws SQLException {

        String sql = "UPDATE ProductPhotos SET Status = 'Approved' WHERE OrderID = ? AND PhotoAngle = ?";

        try (Connection conn = con.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, orderId);
            stmt.setString(2, angle);

            int rowsAffected = stmt.executeUpdate();

            System.out.println("✅ Approved photo '" + angle + "' for OrderID: " + orderId + " (" + rowsAffected + " row(s) affected)");
        }

    }

    public void rejectPhoto(int orderId, String angle) throws SQLException {

        String sql = "UPDATE ProductPhotos SET Status = 'Rejected' WHERE OrderID = ? AND PhotoAngle = ?";

        try (Connection conn = con.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, orderId);
            stmt.setString(2, angle);

            int rowsAffected = stmt.executeUpdate();

            System.out.println("✅ Approved photo '" + angle + "' for OrderID: " + orderId + " (" + rowsAffected + " row(s) affected)");
        }

    }
}


