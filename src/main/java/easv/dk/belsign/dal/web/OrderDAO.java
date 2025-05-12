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

    public int createOrder(Order order) throws SQLException {
        String sql = "INSERT INTO Orders (OrderNumber, ProductDescription, OrderStatus) VALUES (?, ?, ?)";
        try (Connection conn = con.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Required field
            ps.setString(1, order.getOrderNumber());

            // Optional: Product Description
            if (order.getProductDescription() == null || order.getProductDescription().isBlank()) {
                ps.setNull(2, Types.VARCHAR);
            } else {
                ps.setString(2, order.getProductDescription());
            }
            ps.setString(3, "New");
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                return keys.getInt(1); // Return the generated OrderID
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // In case of error during insertion
        }

        // Method to get all orders
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
/// TODO change OrderStatus to "pending" or "under review"
    // Get all 'New' Orders from the database
    public ObservableList<Order> getAllNewOrders() {
        ObservableList<Order> orderList = FXCollections.observableArrayList();
        String sql = "SELECT OrderId, OrderNumber FROM Orders WHERE OrderStatus = 'New'";
        try (Connection c = con.getConnection();
             PreparedStatement stmt = c.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Order order = new Order();
                order.setOrderID(rs.getInt("OrderID"));
                order.setOrderNumber(rs.getString("OrderNumber"));
                orderList.add(order);
            }
            System.out.println("Orders retrieved: " + orderList.size());
        } catch (SQLException e) {
            e.printStackTrace(); // Log the exception
            throw new OrderException(e.getMessage());
        }
        return orderList;
    }

    public int updateOrderStatusToPending(int orderId) {

        String sql = "UPDATE Orders SET OrderStatus = 'Pending' WHERE OrderID = ?";
        try (Connection conn = con.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            int rowsAffected = stmt.executeUpdate();

            System.out.println("✅ Order status updated to Pending for OrderID: " + orderId + " (" + rowsAffected + " row(s) affected)");
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

            System.out.println("✅ Order status updated to Complete for OrderID: " + orderId + " (" + rowsAffected + " row(s) affected)");
            return rowsAffected;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new OrderException("Failed to update order status: " + e.getMessage());
        }
    }



}
