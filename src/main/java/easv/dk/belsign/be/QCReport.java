package easv.dk.belsign.be;

import java.sql.Timestamp;

public class QCReport {
    private int ReportID;
    private String OrderID; // This will hold the Order Number (e.g. "45-00000-0000")
    private String ReportFilePath; // Path to the file on the server
    private String SignedByUserID; // This will hold the name (or signature) of the QA Employee
    private String CustomerEmail; // Customer's email for sending the report (OPTIONAL)
    private Timestamp CreatedAt;
    private Timestamp SentAt;

    // Default constructor
    public QCReport() {

    }

    public int getReportID() {
        return ReportID;
    }

    public void setReportID(int reportID) {
        ReportID = reportID;
    }

    public String getOrderID() {
        return OrderID;
    }

    public void setOrderID(String orderID) {
        OrderID = orderID;
    }

    public String getReportFilePath() {
        return ReportFilePath;
    }

    public void setReportFilePath(String reportFilePath) {
        ReportFilePath = reportFilePath;
    }

    public String getSignedByUserID() {
        return SignedByUserID;
    }

    public void setSignedByUserID(String signedByUserID) {
        SignedByUserID = signedByUserID;
    }

    public String getCustomerEmail() {
        return CustomerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        CustomerEmail = customerEmail;
    }

    public Timestamp getCreatedAt() {
        return CreatedAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        CreatedAt = createdAt;
    }

    public Timestamp getSentAt() {
        return SentAt;
    }

    public void setSentAt(Timestamp sentAt) {
        SentAt = sentAt;
    }
}
