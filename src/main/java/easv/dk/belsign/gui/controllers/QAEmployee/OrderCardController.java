package easv.dk.belsign.gui.controllers.QAEmployee;

import easv.dk.belsign.be.Order;
import easv.dk.belsign.be.User;
import easv.dk.belsign.gui.ViewManagement.FXMLPath;
import easv.dk.belsign.gui.ViewManagement.Navigation;
import easv.dk.belsign.gui.models.PhotosModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.io.IOException;

public class OrderCardController {
    public Label lblImgQty;
    @FXML private Button btnGenReport;
    @FXML private Label statusLabel;
    @FXML private Label orderNumberLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label createdAtLabel;

    private GridPane loadedPhotoGrid;
    private Order order;
    private User loggedInUser;

    private PhotosModel photosModel;

    public void setPhotosModel(PhotosModel model) {
        this.photosModel = model;
    }


    @FXML
    public void initialize() {
/*
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(FXMLPath.QA_PHOTOGRID));
            loadedPhotoGrid = loader.load(); // Store the loaded grid for reuse
            photoGridController = loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }
*/

    }


    public void setOrderData(Order order) {
        this.order = order;
        orderNumberLabel.setText(order.getOrderNumber());
        descriptionLabel.setText(order.getProductDescription() == null ? "No description" : order.getProductDescription());
        createdAtLabel.setText(order.getCreatedAt() != null ? order.getCreatedAt().toString() : "Now");
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
        // Load photos for the order
//        if (photoGridController != null) {
//            photoGridController.loadPhotosForOrder(order.getOrderID());
//        }
//TODO add the icon plus or minus if approved or not
        // Anchor and inject photo grid
        /*AnchorPane.setTopAnchor(loadedPhotoGrid, 0.0);
        AnchorPane.setBottomAnchor(loadedPhotoGrid, 0.0);
        AnchorPane.setLeftAnchor(loadedPhotoGrid, 0.0);
        AnchorPane.setRightAnchor(loadedPhotoGrid, 0.0);

        photoGridPlaceholder.getChildren().clear();
        photoGridPlaceholder.getChildren().add(loadedPhotoGrid);*/
    }


    public void setLoggedInUser(User user) {
        this.loggedInUser = user;
    }

    public void onClickGenReportBtn(ActionEvent actionEvent) throws IOException {

        Navigation.openQCReportPreview(order);

    }

  /*  public void onPhotoGridClick(MouseEvent mouseEvent) {
        Navigation.goToPhotoReviewView(order, loggedInUser);
}*/

    public void onClickShowImg(MouseEvent mouseEvent) {
        Navigation.goToPhotoReviewView(order, loggedInUser);
    }
}
