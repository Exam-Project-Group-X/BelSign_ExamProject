package easv.dk.belsign.bll;

import easv.dk.belsign.be.QCReport;
import easv.dk.belsign.dal.web.QCReportDAO;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

public class QCReportManager {

    private final QCReportDAO reportDAO = new QCReportDAO();

    public int createReport(QCReport report, int orderId, int signedByUserId) throws SQLException {
        return reportDAO.insertReport(report, orderId, signedByUserId);
    }

    // Optional: if you want to track email sent time
//    public void markReportAsSent(int reportId) throws SQLException {
//        reportDAO.updateSentAt(reportId, Timestamp.valueOf(LocalDateTime.now()));
//    }

    public List<QCReport> getReportsForOrder(int orderId) throws SQLException {
        return reportDAO.getReportsByOrderId(orderId);
    }
}
