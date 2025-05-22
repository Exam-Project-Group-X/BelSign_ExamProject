package easv.dk.belsign.be;

import java.sql.Timestamp;

public class ProductPhotos {
    private int PhotoID;
    private String OrderID; // This will hold the Order Number (e.g. "45-00000-0000")
    private String FilePath; // Path or URL where the photo is stored
    private String PhotoAngle; // ("Front", "Back", "Left", "Right", etc.)
    private String Status; // "Pending Review", "Approved" or "Rejected"
    private Timestamp CreatedAt; // Timestamp of photo upload
    private Timestamp ReviewedAt; // Timestamp of photo review
    private String ReviewerUserID; // This will hold the name of the reviewer (QA Employee)
    private String Comment;
    private String Operator;

    // Default constructor
    public ProductPhotos() {

    }

    public int getPhotoID() {
        return PhotoID;
    }

    public void setPhotoID(int photoID) {
        PhotoID = photoID;
    }

    public String getOrderID() {
        return OrderID;
    }

    public void setOrderID(String orderID) {
        OrderID = orderID;
    }

    public String getFilePath() {
        return FilePath;
    }

    public void setFilePath(String filePath) {
        FilePath = filePath;
    }

    public String getPhotoAngle() {
        return PhotoAngle;
    }

    public void setPhotoAngle(String photoAngle) {
        PhotoAngle = photoAngle;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public Timestamp getCreatedAt() {
        return CreatedAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        CreatedAt = createdAt;
    }

    public Timestamp getReviewedAt() {
        return ReviewedAt;
    }

    public void setReviewedAt(Timestamp reviewedAt) {
        ReviewedAt = reviewedAt;
    }

    public String getReviewerUserID() {
        return ReviewerUserID;
    }

    public void setReviewerUserID(String reviewerUserID) {
        ReviewerUserID = reviewerUserID;
    }

    public String getComment() {
        return Comment;
    }

    public void setComment(String comment) {
        Comment = comment;
    }

    public String getOperator() {
        return Operator;
    }

    public void setOperator(String operator) {
        Operator = operator;
    }
}
