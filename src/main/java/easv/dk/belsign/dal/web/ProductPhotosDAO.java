package easv.dk.belsign.dal.web;

import easv.dk.belsign.be.ProductPhotos;
import easv.dk.belsign.dal.db.DBConnection;

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

    public Map<String, ProductPhotos> getDetailedPhotosByOrderId(int orderId) throws SQLException {
        Map<String, ProductPhotos> photos = new HashMap<>();

        String sql = "SELECT PhotoAngle, PhotoData, Status, Comment FROM ProductPhotos WHERE OrderID = ?";

        try (Connection conn = con.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                ProductPhotos photo = new ProductPhotos();
                String angle = rs.getString("PhotoAngle");
                photo.setPhotoAngle(angle);
                photo.setPhotoData(rs.getBytes("PhotoData"));
                photo.setStatus(rs.getString("Status"));
                photo.setComment(rs.getString("Comment"));
                photos.put(angle.toUpperCase(), photo);
            }
        }
        return photos;
    }

    public void upsertCapturedPhoto(int orderId, String photoAngle, byte[] photoData, String operatorName) throws SQLException {
        String sql = """
        MERGE INTO ProductPhotos AS target
        USING (SELECT ? AS OrderID, ? AS PhotoAngle) AS source
        ON target.OrderID = source.OrderID AND target.PhotoAngle = source.PhotoAngle
        WHEN MATCHED THEN
            UPDATE SET
                PhotoData = ?,
                Status = 'Pending Review',
                CreatedAt = GETDATE(),
                ReviewedAt = NULL,
                ReviewerUserID = NULL,
                Comment = NULL,
                Operator = ?
        WHEN NOT MATCHED THEN
            INSERT (OrderID, PhotoAngle, PhotoData, Status, Operator)
            VALUES (?, ?, ?, 'Pending Review', ?);
    """;

        try (Connection conn = con.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, orderId);           // source.OrderID
            ps.setString(2, photoAngle);     // source.PhotoAngle
            ps.setBytes(3, photoData);       // UPDATE SET PhotoData
            ps.setString(4, operatorName);   // UPDATE SET Operator

            ps.setInt(5, orderId);           // INSERT OrderID
            ps.setString(6, photoAngle);     // INSERT PhotoAngle
            ps.setBytes(7, photoData);       // INSERT PhotoData
            ps.setString(8, operatorName);   // INSERT Operator

            ps.executeUpdate();
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

    public void rejectPhoto(int orderId, String angle, String comment) throws SQLException {
        String sql = "UPDATE ProductPhotos SET Status = 'Rejected', Comment = ? WHERE OrderID = ? AND PhotoAngle = ?";

        try (Connection conn = con.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, comment);
            stmt.setInt(2, orderId);
            stmt.setString(3, angle);

            int rowsAffected = stmt.executeUpdate();
            System.out.println("❌ Rejected photo '" + angle + "' for OrderID: " + orderId + " (" + rowsAffected + " row(s) affected)");
        }
    }

    public Map<String, String> getPhotoStatusByOrderId(int orderId) throws SQLException {
        Map<String, String> statusMap = new HashMap<>();
        String sql = "SELECT PhotoAngle, Status FROM ProductPhotos WHERE OrderID = ?";
        try (Connection conn = con.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String angle = rs.getString("PhotoAngle").toUpperCase();
                String status = rs.getString("Status");
                statusMap.put(angle, status);  // e.g., "Approved", "Rejected", "Pending Review"
            }
        }
        return statusMap;
    }
}
