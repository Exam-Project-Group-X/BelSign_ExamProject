package easv.dk.belsign.gui.controllers.QAEmployee;

import easv.dk.belsign.be.Order;
import easv.dk.belsign.gui.ViewManagement.FXMLPath;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.io.IOException;


public class OrderCardController {
    @FXML private Label statusLabel;
    @FXML private Label orderNumberLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label createdAtLabel;
    @FXML private AnchorPane photoGridPlaceholder;

    private GridPane loadedPhotoGrid;
    private PhotoGridController photoGridController;

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
}
