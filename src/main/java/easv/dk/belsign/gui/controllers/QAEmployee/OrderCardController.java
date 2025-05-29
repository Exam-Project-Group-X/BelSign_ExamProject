package easv.dk.belsign.gui.controllers.QAEmployee;

import easv.dk.belsign.be.Order;
import easv.dk.belsign.be.QCReport;
import easv.dk.belsign.be.User;
import easv.dk.belsign.gui.ViewManagement.Navigation;
import easv.dk.belsign.gui.models.PhotosModel;
import easv.dk.belsign.gui.models.QCReportModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class OrderCardController {

    @FXML private Label   lblImgQty;
    @FXML private Button  btnGenReport;
    @FXML private Label   statusLabel;
    @FXML private Label   orderNumberLabel;
    @FXML private Label   descriptionLabel;
    @FXML private Label   createdAtLabel;

    private final QCReportModel reportModel = new QCReportModel();
    private PhotosModel photosModel;
    private Order order;
    private User loggedInUser;
    private File reportPdf;            // null until we have one

    public void setPhotosModel(PhotosModel m) { photosModel = m; updatePhotoCount(); }
    public void setLoggedInUser(User  u)      { loggedInUser = u; }

    public void setOrderData(Order o, int cachedPhotoCnt) {
        order = o;

        orderNumberLabel.setText(o.getOrderNumber());
        descriptionLabel.setText(o.getProductDescription() == null ? "No description" : o.getProductDescription());
        createdAtLabel.setText(o.getCreatedAt() == null ? "–" : o.getCreatedAt().toString());

        statusLabel.getStyleClass().removeAll("status-pending", "status-complete");

        String status = o.getOrderStatus();
        boolean hasPhotos = false;

        if (photosModel != null) {
            try {
                hasPhotos = photosModel.countPhotosForOrder(o.getOrderID()) > 0;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        switch (status) {
            case "Pending" -> {
                if (hasPhotos) {
                    statusLabel.setText("Ready For Review");
                } else {
                    statusLabel.setText("Pending");
                }
                statusLabel.getStyleClass().add("status-pending");
            }
            case "Complete" -> {
                statusLabel.setText("Complete");
                statusLabel.getStyleClass().add("status-complete");
            }
        }

        updatePhotoCount();
        updateReportState();
    }

    // add this for converting file paths to run on different platforms
    private static File toPlatformFile(String raw) {
        if (raw != null && raw.matches("^/[A-Za-z]:/.*")) {   // "/C:/…" -> "C:/…"
            raw = raw.substring(1);
        }
        return new File(raw);
    }


    /** Enable/disable + label according to DB + status. */
    public void updateReportState() {

        boolean isComplete = "Complete".equalsIgnoreCase(order.getOrderStatus());

        /* newest report */
        List<QCReport> list = reportModel.getReportsByOrder(order.getOrderID());
        QCReport latest = list.isEmpty() ? null : list.get(0);

        if (latest != null && latest.getReportFilePath() != null) {           // VIEW mode
            //File f = new File(latest.getReportFilePath());
            File f = toPlatformFile(latest.getReportFilePath());
            if (f.exists()) {
                reportPdf = f;
                btnGenReport.setText("View report");
                btnGenReport.setDisable(false);
                return;
            }
        }
        /* GENERATE mode (no file yet) */
        reportPdf = null;
        btnGenReport.setText("Generate report");
        btnGenReport.setDisable(!isComplete);                 // ☑︎ active only if status = Complete
    }

    /** Called by QCReportMainController after **saving** the PDF. */
    public void onReportSaved(File pdf) {
        reportPdf = pdf;
        btnGenReport.setText("View report");
        btnGenReport.setDisable(false);                       // ☑︎ keep it enabled

        /* persist in DB – minimal information */
        reportModel.saveReport(new QCReport(
                0,                                           // auto
                order.getOrderID(),                         // FK
                pdf.getAbsolutePath(),
                loggedInUser.getUserID(),                   // who signed
                null, null, null));                         // timestamps via SQL default
    }

    public void onClickShowImg(MouseEvent e) {
        Navigation.goToPhotoReviewView(order, loggedInUser);
    }

    /* =========================  PHOTO COUNTER starts ========================= */

    public void updatePhotoCount() {
        if (order == null || photosModel == null) return;
        try {
            int count = photosModel.countPhotosForOrder(order.getOrderID());
            lblImgQty.setText(count + " photos");

            // 🟡 Update label if status is Pending AND there are photos
            if ("Pending".equals(order.getOrderStatus())) {
                if (count > 0) {
                    statusLabel.setText("Ready For Review");
                } else {
                    statusLabel.setText("Pending");
                }
            }

        } catch (SQLException ex) {
            lblImgQty.setText("0 photos");
            ex.printStackTrace();
        }
    }

    public void onClickGenReportBtn(ActionEvent actionEvent) throws IOException {
        if (reportPdf == null) {                     // generate
            Navigation.openQCReportPreview(order, this);
        } else {                                     // view
            Desktop.getDesktop().open(reportPdf);
        }
    }
}
