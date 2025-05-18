package easv.dk.belsign.gui.controllers.QAEmployee;

import easv.dk.belsign.be.Order;
import easv.dk.belsign.gui.ViewManagement.FXMLManager;
import easv.dk.belsign.gui.ViewManagement.FXMLPath;
import easv.dk.belsign.gui.ViewManagement.Navigation;
import easv.dk.belsign.gui.controllers.QAEmployee.report.QCReportController;
import easv.dk.belsign.gui.models.PhotosModel;
import easv.dk.belsign.gui.models.QAEmployeeModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.IOException;


public class OrderCardController {
    @FXML private Button btnGenReport;
    @FXML private Label statusLabel;
    @FXML private Label orderNumberLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label createdAtLabel;
    @FXML private AnchorPane photoGridPlaceholder;

    private GridPane loadedPhotoGrid;
    private PhotoGridController photoGridController;
    private Order order;


    private PhotosModel photosModel;

    public void setPhotosModel(PhotosModel model) {
        this.photosModel = model;
    }


    @FXML
    public void initialize() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(FXMLPath.QA_PHOTOGRID));
            loadedPhotoGrid = loader.load(); // Store the loaded grid for reuse
            photoGridController = loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void setOrderData(Order order) {
        this.order = order;
        orderNumberLabel.setText(order.getOrderNumber());
        descriptionLabel.setText(order.getProductDescription() == null ? "No description" : order.getProductDescription());
        createdAtLabel.setText(order.getCreatedAt() != null ? order.getCreatedAt().toString() : "Now");
        String status = order.getOrderStatus();
        statusLabel.setText(status);
        if ("New".equals(status)) {
            statusLabel.setStyle("-fx-background-color: #F7EAEA; -fx-text-fill: #E57373;");
        } else if ("Pending".equals(status)) {
            statusLabel.setStyle("-fx-background-color: #F7F3EA; -fx-text-fill: #F4B400;");
        } else if ("Complete".equals(status)) {
            statusLabel.setStyle("-fx-background-color: #EAF7F3; -fx-text-fill: #35B587;");
        } else {
            statusLabel.setStyle("");
        }
        if ("Complete".equals(status)) {
            btnGenReport.setDisable(false);
        } else {
            btnGenReport.setDisable(true);
        }
        // Load photos for the order
        if (photoGridController != null) {
            photoGridController.loadPhotosForOrder(order.getOrderID());
        }
//TODO add the icon plus or minus if approved or not
        // Anchor and inject photo grid
        AnchorPane.setTopAnchor(loadedPhotoGrid, 0.0);
        AnchorPane.setBottomAnchor(loadedPhotoGrid, 0.0);
        AnchorPane.setLeftAnchor(loadedPhotoGrid, 0.0);
        AnchorPane.setRightAnchor(loadedPhotoGrid, 0.0);

        photoGridPlaceholder.getChildren().clear();
        photoGridPlaceholder.getChildren().add(loadedPhotoGrid);
    }

    public void onClickGenReportBtn(ActionEvent actionEvent) throws IOException {

        Navigation.openQCReportPreview(order);

    }

    public void onPhotoGridClick(MouseEvent mouseEvent) {
        Navigation.goToPhotoReviewView(order);
}
}
