package easv.dk.belsign.dal.db;

import easv.dk.belsign.be.Order;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAODB {
    private DBConnection con = new DBConnection();

    public int createOrder(Order order ) throws SQLException {

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
        /// for the QA Method to load all orders
        public List<Order> getAllOrders() throws SQLException{
            List <Order> orders = new ArrayList<>();
            String sql= "SELECT * FROM Orders";

            try (Connection conn = con.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)){

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

    }

