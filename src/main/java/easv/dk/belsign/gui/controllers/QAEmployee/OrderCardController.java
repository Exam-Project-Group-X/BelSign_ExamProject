package easv.dk.belsign.gui.controllers.QAEmployee;

import easv.dk.belsign.be.Order;
import easv.dk.belsign.be.User;
import easv.dk.belsign.gui.ViewManagement.FXMLPath;
import easv.dk.belsign.gui.ViewManagement.Navigation;
import easv.dk.belsign.gui.models.PhotosModel;
import easv.dk.belsign.gui.models.QAEmployeeModel;
import easv.dk.belsign.utils.AlertUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class OrderCardController {
    @FXML private Label  lblImgQty;
    @FXML private Button btnGenReport;
    @FXML private Label  statusLabel;
    @FXML private Label  orderNumberLabel;
    @FXML private Label  descriptionLabel;
    @FXML private Label  createdAtLabel;

    private Order order;
    private User loggedInUser;
    private PhotosModel photosModel;
    private File reportPdf;

    public void setPhotosModel(PhotosModel model) {
        this.photosModel = model;
        updatePhotoCount();// may run now if order set
    }

    public void setLoggedInUser(User user) {
        this.loggedInUser = user;
    }

    public void setOrderData(Order order) {
        this.order = order;
        orderNumberLabel.setText(order.getOrderNumber());
        descriptionLabel.setText(order.getProductDescription() == null ? "No description" : order.getProductDescription());
        createdAtLabel.setText(order.getCreatedAt() == null ? order.getCreatedAt().toString() : "Now");
        updatePhotoCount();   // reset, will be updated later
        /* ----- status handling ----- */
        String status = order.getOrderStatus();
        statusLabel.setText(status);

        statusLabel.getStyleClass().removeAll("status-new", "status-pending", "status-complete");
        switch (status) {
            case "New"      -> statusLabel.getStyleClass().add("status-new");
            case "Pending"  -> statusLabel.getStyleClass().add("status-pending");
            case "Complete" -> statusLabel.getStyleClass().add("status-complete");
        }
        btnGenReport.setDisable(!"Complete".equals(status));
    }

    public void onClickGenReportBtn(ActionEvent actionEvent) throws IOException {

        //Navigation.openQCReportPreview(order);
        //Navigation.openQCReportPreview(order, this);

        if (reportPdf == null) {               // no PDF yet → generate
            Navigation.openQCReportPreview(order, this);
        } else {                               // PDF already exists → view
            try { Desktop.getDesktop().open(reportPdf); }
            catch (IOException ex) { ex.printStackTrace(); }
        }
    }

    public void onClickShowImg(MouseEvent mouseEvent) {
        Navigation.goToPhotoReviewView(order, loggedInUser);
    }

    /* after the user
     * successfully saves the PDF.  Switches the button to “View report”. */
    public void onReportSaved(File pdf) {
        this.reportPdf = pdf;
        btnGenReport.setText("View report");
        btnGenReport.setDisable(false);
    }

    private void updatePhotoCount() {
        if (order == null || photosModel == null) return;

        try {
            int count = photosModel.countPhotosForOrder(order.getOrderID());
            lblImgQty.setText(count + " photos");
        } catch (SQLException ex) {
            lblImgQty.setText("0 photos");
            ex.printStackTrace();
        }
    }
}
