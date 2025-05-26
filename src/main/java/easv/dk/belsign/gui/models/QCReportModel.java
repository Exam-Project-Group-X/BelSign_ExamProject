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

    public ObservableList<QCReport> getQcReportsByOrder(String orderId) {
        try {
            List<QCReport> reportList = qcReportManager.getReportByOrder(orderId);
            qcReportsByOrder.setAll(reportList); // Convert to ArrayList to avoid ConcurrentModificationException
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return qcReportsByOrder;

    }
}
