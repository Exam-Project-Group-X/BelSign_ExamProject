package easv.dk.belsign.dal.web;

import easv.dk.belsign.be.QCReport;
import easv.dk.belsign.dal.db.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QCReportDAO {

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

    public List<QCReport> getReportsByOrderId(int orderId) throws SQLException {
        List<QCReport> reports = new ArrayList<>();
        String sql = "SELECT * FROM QCReports WHERE OrderID = ?";

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
    }


}
