package easv.dk.belsign.dal.web;

import easv.dk.belsign.be.QCReport;
import easv.dk.belsign.dal.IQCReportDAO;
import easv.dk.belsign.dal.db.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QCReportDAO implements IQCReportDAO {
    private DBConnection con = new DBConnection();

    @Override
    public QCReport generateQCReport(QCReport rpt) throws SQLException {
        String sql = """
        INSERT INTO QCReports (OrderID, ReportFilePath, SignedByUserID, CustomerEmail)
        VALUES ( ?, ?, ?, ? )
        """;
        try (Connection conn = con.getConnection();
             PreparedStatement ps =
                     conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt   (1, rpt.getOrderID());          // FK to Orders
            ps.setString(2, rpt.getReportFilePath());   // absolute path
            ps.setInt   (3, rpt.getSignedByUserID()); // QA user
            ps.setString(4, rpt.getCustomerEmail());    // may be null
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int reportID = keys.getInt(1);
                    /* build a full object to return */
                    return new QCReport(
                            reportID,
                            rpt.getOrderID(),
                            rpt.getReportFilePath(),
                            rpt.getSignedByUserID(),
                            rpt.getCustomerEmail(),
                            /* CreatedAt  */ null,   // let SQL Server keep the real timestamp
                            /* SentAt     */ null);
                }
            }
            return null;  // should not happen unless INSERT failed silently
        }
        catch (SQLException ex) {
            ex.printStackTrace();
            throw new SQLException("Error creating QC-report: " + ex.getMessage(), ex);
        }
    }

    @Override
    public List<QCReport> getReportsByOrderId(int orderID) throws SQLException {
        String sql = "SELECT * FROM QCReports WHERE OrderID = ? ORDER  BY CreatedAt DESC";
        List<QCReport> reports = new ArrayList<>();
        try (Connection conn = con.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    QCReport rpt = new QCReport(
                            rs.getInt       ("ReportID"),
                            rs.getInt    ("OrderID"),
                            rs.getString    ("ReportFilePath"),
                            rs.getInt("SignedByUserID") ,
                            rs.getString    ("CustomerEmail"),
                            rs.getTimestamp ("CreatedAt"),
                            rs.getTimestamp ("SentAt")
                    );
                    reports.add(rpt);
                }
            }
        } catch (SQLException ex) {
            throw new SQLException("Error fetching QC reports for order "
                    + orderID, ex);
        }
        return reports;
    }
}