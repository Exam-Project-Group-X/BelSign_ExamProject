package easv.dk.belsign.be;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class QCReport {
    private int reportID;
    private String orderID; // This will hold the Order Number (e.g. "45-00000-0000")
    private String reportFilePath; // Path to the file on the server
    private String signedByUserID; // This will hold the name (or signature) of the QA Employee
    private String customerEmail; // Customer's email for sending the report (OPTIONAL)
    private Timestamp createdAt;
    private Timestamp sentAt;

    public QCReport(int reportID, String orderID, String reportFilePath, String signedByUserID, String customerEmail, Timestamp createdAt, Timestamp sentAt) {
        this.reportID = reportID;
        this.orderID = orderID;
        this.reportFilePath = reportFilePath;
        this.signedByUserID = signedByUserID;
        this.customerEmail = customerEmail;
        this.createdAt = createdAt;
        this.sentAt = sentAt;
    }

    public QCReport(int reportID, String orderID, String productDescription, String reportFilePath, int signedByUserID, String customerEmail, LocalDateTime createdAt, LocalDateTime sentAt) {

    }

    public int getReportID() {
        return reportID;
    }

    public void setReportID(int reportID) {
        this.reportID = reportID;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getReportFilePath() {
        return reportFilePath;
    }

    public void setReportFilePath(String reportFilePath) {
        this.reportFilePath = reportFilePath;
    }

    public String getSignedByUserID() {
        return signedByUserID;
    }

    public void setSignedByUserID(String signedByUserID) {
        this.signedByUserID = signedByUserID;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getSentAt() {
        return sentAt;
    }

    public void setSentAt(Timestamp sentAt) {
        this.sentAt = sentAt;
    }
}
