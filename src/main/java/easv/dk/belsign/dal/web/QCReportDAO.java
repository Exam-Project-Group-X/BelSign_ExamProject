package easv.dk.belsign.dal.web;

import easv.dk.belsign.be.QCReport;
import easv.dk.belsign.dal.IQCReportDAO;
import easv.dk.belsign.dal.db.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class QCReportDAO implements IQCReportDAO {
    private DBConnection con = new DBConnection();

    public int insertReport(QCReport report, int orderId, int signedByUserId) throws SQLException {
        String sql = "INSERT INTO QCReports (OrderID, ReportFilePath, SignedByUserID, CustomerEmail) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = con.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, orderId);
            ps.setString(2, report.getReportFilePath());
            ps.setInt(3, signedByUserId);
            ps.setString(4, report.getCustomerEmail());

            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1); // Return generated ReportID
            }
        }

        return -1; //Chekcs if failed
    }

    /*public List<QCReport> getReportsByOrderId(int orderId) throws SQLException {
        List<QCReport> reports = new ArrayList<>();
        //String sql = "SELECT * FROM QCReports WHERE OrderID = ?";

        try (Connection conn = con.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                QCReport report = new QCReport();
                report.setReportID(rs.getInt("ReportID"));
                report.setOrderID(String.valueOf(rs.getInt("OrderID")));
                report.setReportFilePath(rs.getString("ReportFilePath"));
                report.setSignedByUserID(String.valueOf(rs.getInt("SignedByUserID")));
                report.setCustomerEmail(rs.getString("CustomerEmail"));
                report.setCreatedAt(rs.getTimestamp("CreatedAt"));
                report.setSentAt(rs.getTimestamp("SentAt"));

                reports.add(report);
            }
        }
        return reports;
    }*/
    // Java
    @Override
    public List<QCReport> getReportsForOrder(String orderId) throws SQLException {
        List<QCReport> reportsByOrder = new ArrayList<>();
        String sql = """
            SELECT r.ReportID,
                   r.OrderID,
                   o.ProductDescription,
                   r.ReportFilePath,
                   r.SignedByUserID,
                   r.CustomerEmail,
                   r.CreatedAt,
                   r.SentAt
            FROM   QCReports r
            JOIN   Orders o ON o.OrderID = r.OrderID
            WHERE  r.OrderID = ?
            ORDER  BY r.CreatedAt DESC
            LIMIT  1
    """;

        try (Connection c = con.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, orderId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int reportID = rs.getInt("ReportID");
                String orderID = rs.getString("OrderID");
                String productDescription = rs.getString("ProductDescription");
                String reportFilePath = rs.getString("ReportFilePath");
                int signedByUserID = rs.getInt("SignedByUserID");
                String customerEmail = rs.getString("CustomerEmail");
                LocalDateTime createdAt = rs.getObject("CreatedAt", LocalDateTime.class);
                LocalDateTime sentAt = rs.getObject("SentAt", LocalDateTime.class);
                QCReport report = new QCReport(reportID, orderID, productDescription, reportFilePath, signedByUserID, customerEmail, createdAt, sentAt);
                reportsByOrder.add(report);
            }
        } catch (SQLException e) {
            throw new SQLException("Error fetching reports for order: " + orderId, e);
        }
        return reportsByOrder;
    }
}