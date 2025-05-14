package easv.dk.belsign.bll;

import easv.dk.belsign.be.QCReport;
import easv.dk.belsign.dal.web.QCReportDAO;

import java.sql.SQLException;
import java.util.List;

public class QCReportManager {

    private final QCReportDAO reportDAO = new QCReportDAO();

    public int createReport(QCReport report, int orderId, int signedByUserId) throws SQLException {
        return reportDAO.insertReport(report, orderId, signedByUserId);
    }

    public List<QCReport> getReportsForOrder(int orderId) throws SQLException {
        return reportDAO.getReportsByOrderId(orderId);
    }
}
