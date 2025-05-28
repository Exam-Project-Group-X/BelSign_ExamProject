package easv.dk.belsign.gui.models;

import easv.dk.belsign.be.QCReport;
import easv.dk.belsign.bll.QCReportManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class QCReportModel {

    private final QCReportManager qcReportManager = new QCReportManager();
    private final ObservableList<QCReport> qcReportsByOrder = FXCollections.observableArrayList();

    public ObservableList<QCReport> getQcReportsByOrder(String orderID) {
        try {
            List<QCReport> rpt = qcReportManager.getReportByOrder(orderID);
            qcReportsByOrder.setAll(rpt);           // replace contents
        } catch (SQLException ex) {
            ex.printStackTrace();                    // log + keep old list
        }
        return qcReportsByOrder;
    }

    public QCReport generateQCReport(QCReport report) {
        try {
            QCReport newReport = qcReportManager.generateQCReport(report);
            if (newReport != null) {
                // If the report was successfully generated, add it to the list
                qcReportsByOrder.add(newReport);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return report;
    }
}
