package easv.dk.belsign.dal;

import easv.dk.belsign.be.QCReport;

import java.sql.SQLException;
import java.util.List;

public interface IQCReportDAO {
    List<QCReport> getReportsByOrderId(int orderID) throws SQLException;
    QCReport generateQCReport(QCReport rpt) throws SQLException;
}
