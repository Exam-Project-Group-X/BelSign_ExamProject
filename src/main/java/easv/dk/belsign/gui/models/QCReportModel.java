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
    private final ObservableList<QCReport> reports = FXCollections.observableArrayList();

    public ObservableList<QCReport> getReportsByOrder(int orderID) {
        try {
            List<QCReport> reportList = qcReportManager.getReportByOrder(orderID);
            reports.setAll(reportList);           // replace contents
        } catch (SQLException ex) {
            ex.printStackTrace();                    // log + keep old list
        }
        return reports;
    }

    public void saveReport(QCReport rpt) {
        try {
            QCReport saved = qcReportManager.generateQCReport(rpt);
            if (saved != null) reports.add(0, saved);   // newest on top
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
}
