package easv.dk.belsign.dal.web;


import easv.dk.belsign.dal.db.DBConnection;

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


    public void insertTestPhoto(String filePath, int orderId, String angle) throws Exception {
        byte[] bytes;
        try {
            bytes = Files.readAllBytes(Path.of(filePath));
        } catch (IOException e) {
            System.err.println("❌ Failed to load: " + filePath);
            e.printStackTrace();
            return; // Exit early if reading failed
        }

        String sql = "INSERT INTO ProductPhotos (OrderID, PhotoAngle, Status, PhotoData) VALUES (?, ?, ?, ?)";

        try (Connection conn = con.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, orderId);
            ps.setString(2, angle);
            ps.setString(3, "Pending");
            ps.setBytes(4, bytes);
            ps.executeUpdate();
            System.out.println("✅ Inserted photo for " + angle + orderId);
        }
    }

    public void insertAllTestPhotos() throws Exception {
//        insertTestPhoto("src/main/resources/easv/dk/belsign/testphotos/bulbasaur.jpg", 1, "FRONT");
//        insertTestPhoto("src/main/resources/easv/dk/belsign/testphotos/charmander.jpg", 1, "BACK");
//        insertTestPhoto("src/main/resources/easv/dk/belsign/testphotos/squirtle.png", 1, "LEFT");
//        insertTestPhoto("src/main/resources/easv/dk/belsign/testphotos/pikachu.jpg", 1, "RIGHT");
//        insertTestPhoto("src/main/resources/easv/dk/belsign/testphotos/eevee.jpg", 1, "TOP");
//        insertTestPhoto("src/main/resources/easv/dk/belsign/testphotos/snorlax.png", 1, "BOTTOM");

        insertTestPhoto("src/main/resources/easv/dk/belsign/testphotos/test1.jpeg", 2, "FRONT");
        insertTestPhoto("src/main/resources/easv/dk/belsign/testphotos/test2.jpeg", 2, "BACK");
        insertTestPhoto("src/main/resources/easv/dk/belsign/testphotos/test3.jpeg", 2, "LEFT");
        insertTestPhoto("src/main/resources/easv/dk/belsign/testphotos/test4.jpeg", 2, "RIGHT");
        insertTestPhoto("src/main/resources/easv/dk/belsign/testphotos/test5.jpeg", 2, "TOP");
        insertTestPhoto("src/main/resources/easv/dk/belsign/testphotos/test6.jpeg", 2, "BOTTOM");

    }

}
