package easv.dk.belsign.dal.web;

import easv.dk.belsign.exceptions.OrderException;
import easv.dk.belsign.be.Order;
import easv.dk.belsign.dal.db.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {
    private DBConnection con = new DBConnection();

    public List<Order> getAllOrders() throws SQLException {
        List <Order> orders = new ArrayList<>();
        String sql= "SELECT * FROM Orders";
        try (Connection conn = con.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Order order = new Order.Builder()
                        .withOrderID(rs.getInt("OrderID"))
                        .withOrderNumber(rs.getString("OrderNumber"))
                        .withProductDescription(rs.getString("ProductDescription"))
                        .withOrderStatus(rs.getString("OrderStatus"))
                        .withCreatedAt(rs.getTimestamp("CreatedAt"))
                        .withUpdatedAt(rs.getTimestamp("UpdatedAt"))
                        .withAssignedToUserID(rs.getString("AssignedToUserID"))
                        .build();
                orders.add(order);
            }
        }
        return orders;
    }

    public ObservableList<Order> getAllPendingOrders() {
        ObservableList<Order> orderList = FXCollections.observableArrayList();
        String sql = """
        SELECT o.OrderID, o.OrderNumber,
               CASE 
                   WHEN EXISTS (
                       SELECT 1 FROM ProductPhotos pp 
                       WHERE pp.OrderID = o.OrderID AND pp.Status = 'Rejected'
                   ) THEN 1 ELSE 0 
               END AS HasRejectedPhotos
        FROM Orders o
        WHERE o.OrderStatus = 'Pending'
        ORDER BY HasRejectedPhotos DESC, o.OrderID ASC
        """;
        try (Connection c = con.getConnection();
             PreparedStatement stmt = c.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Order order = new Order();
                order.setOrderID(rs.getInt("OrderID"));
                order.setOrderNumber(rs.getString("OrderNumber"));
                order.setHasRejectedPhotos(rs.getInt("HasRejectedPhotos") == 1); // maps to boolean
                orderList.add(order);
            }
            System.out.println("Orders retrieved (with rejected photo priority): " + orderList.size());
        } catch (SQLException e) {
            e.printStackTrace();
            throw new OrderException("Error retrieving pending orders: " + e.getMessage());
        }
        return orderList;
    }

    public int updateOrderStatusToPending(int orderId) {
        String sql = "UPDATE Orders SET OrderStatus = 'Pending' WHERE OrderID = ?";
        try (Connection conn = con.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new OrderException("Failed to update order status: " + e.getMessage());
        }
    }

    public int updateOrderStatusToComplete(int orderId) {
        String sql = "UPDATE Orders SET OrderStatus = 'Complete' WHERE OrderID = ?";
        try (Connection conn = con.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new OrderException("Failed to update order status: " + e.getMessage());
        }
    }
}
