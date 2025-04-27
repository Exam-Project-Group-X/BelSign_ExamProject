package easv.dk.belsign.be;

import java.sql.Timestamp;

public class Order {
    private int OrderID;
    private String OrderNumber; // Unique identifier for the order (e.g. "45-00000-000000")
    private String ProductDescription;
    private String OrderStatus; // (e.g. "New", "Under Review", "Approved", "Rejected")
    private Timestamp CreatedAt;
    private Timestamp UpdatedAt;
    private String AssignedToUserID; // This will hold the name of the QA Employee who created the order

    // Default constructor
    public Order() {

    }

    public int getOrderID() {
        return OrderID;
    }

    public void setOrderID(int orderID) {
        OrderID = orderID;
    }

    public String getOrderNumber() {
        return OrderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        OrderNumber = orderNumber;
    }

    public String getProductDescription() {
        return ProductDescription;
    }

    public void setProductDescription(String productDescription) {
        ProductDescription = productDescription;
    }

    public String getOrderStatus() {
        return OrderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        OrderStatus = orderStatus;
    }

    public Timestamp getCreatedAt() {
        return CreatedAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        CreatedAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return UpdatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        UpdatedAt = updatedAt;
    }

    public String getAssignedToUserID() {
        return AssignedToUserID;
    }

    public void setAssignedToUserID(String assignedToUserID) {
        AssignedToUserID = assignedToUserID;
    }
}
