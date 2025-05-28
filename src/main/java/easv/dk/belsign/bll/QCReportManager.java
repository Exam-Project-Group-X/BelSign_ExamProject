package easv.dk.belsign.bll;

import easv.dk.belsign.be.QCReport;
import easv.dk.belsign.dal.IQCReportDAO;
import easv.dk.belsign.dal.web.QCReportDAO;

import java.sql.SQLException;
import java.util.List;

public class QCReportManager {
    private final IQCReportDAO reportDAO = new QCReportDAO();

    public List<QCReport> getReportByOrder(int orderID) throws SQLException {
        return reportDAO.getReportsByOrderId(orderID);
    }

    public QCReport generateQCReport(QCReport rpt) throws SQLException {
        return reportDAO.generateQCReport(rpt);
    }
}
