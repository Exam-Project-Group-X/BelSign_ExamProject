package easv.dk.belsign.dal;

import easv.dk.belsign.be.QCReport;

import java.sql.SQLException;
import java.util.List;

public interface IQCReportDAO {


    List<QCReport> getReportsForOrder(String orderId) throws SQLException;

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

}
