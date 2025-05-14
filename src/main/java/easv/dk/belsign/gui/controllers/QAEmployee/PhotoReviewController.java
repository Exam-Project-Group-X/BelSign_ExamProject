package easv.dk.belsign.gui.controllers.QAEmployee;

import easv.dk.belsign.gui.ViewManagement.FXMLPath;
import easv.dk.belsign.gui.ViewManagement.ViewManager;
import easv.dk.belsign.gui.models.PhotosModel;
import easv.dk.belsign.gui.models.QAEmployeeModel;
import easv.dk.belsign.dal.web.ProductPhotosDAO;
import easv.dk.belsign.dal.web.NotificationsDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.sql.SQLException;
import java.util.*;

public class PhotoReviewController {

    public Button closeBtn;
    public Button ApproveBtn;
    public Button RejectBtn;
    @FXML
    private ImageView mainImg;
    @FXML
    private HBox thumbStrip;
    @FXML
    private TextField captionField;

    private PhotosModel photosModel;
    private QAEmployeeModel qamodel;
    private int currentOrderId;
    private String currentAngle;

    private final ProductPhotosDAO photoDAO = new ProductPhotosDAO();
    private final List<Image> loadedImages = new ArrayList<>();

    public void setModel(PhotosModel photosModel) {
        this.photosModel = photosModel;
    }

    public void setQAEmployeeModel(QAEmployeeModel model) {
        this.qamodel = model;
    }

    public void setOrderId(int orderId) {
        this.currentOrderId = orderId;
    }

    public void setCurrentAngle(String angle) {
        this.currentAngle = angle;
    }

    public void setCaption(String caption) {
        captionField.setText(caption);
    }

    public void loadPhotosForOrder(int orderId) {
        try {
            Map<String, ProductPhotosDAO.PhotoData> photoMap = photoDAO.getPhotosByOrderId(orderId);
            thumbStrip.getChildren().clear();
            loadedImages.clear();

            for (Map.Entry<String, ProductPhotosDAO.PhotoData> entry : photoMap.entrySet()) {
                String angle = entry.getKey();
                ProductPhotosDAO.PhotoData photoData = entry.getValue();
                if (photoData.photo == null) continue;

                Image img = new Image(new ByteArrayInputStream(photoData.photo));
                loadedImages.add(img);

                ImageView thumb = new ImageView(img);
                thumb.setFitWidth(200);
                thumb.setFitHeight(150);
                thumb.setPreserveRatio(true);
                thumb.setPickOnBounds(true);

                if ("REJECTED".equalsIgnoreCase(photoData.status)) {
                    thumb.setStyle("-fx-border-color: red; -fx-border-width: 3px;");
                    Tooltip tooltip = new Tooltip("Rejected: " + photoData.rejectionNote);
                    Tooltip.install(thumb, tooltip);
                }

                thumb.setOnMouseClicked(e -> {
                    mainImg.setImage(img);
                    currentAngle = angle;
                    captionField.setText("Angle: " + angle + ("REJECTED".equalsIgnoreCase(photoData.status) ? " [REJECTED]" : ""));
                });

                thumbStrip.getChildren().add(thumb);
            }

            if (!loadedImages.isEmpty()) {
                mainImg.setImage(loadedImages.get(0));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void onClickLogoutBtn(ActionEvent actionEvent) {
        ViewManager.INSTANCE.showScene(FXMLPath.LOGIN);
    }

    public void onCloseBtnClick(ActionEvent actionEvent) {
        ViewManager.INSTANCE.showScene(FXMLPath.QA_EMPLOYEE_VIEW);
    }

    public void ApprovePhoto(ActionEvent actionEvent) {
        try {
            photosModel.approvePhoto(currentOrderId, currentAngle);
            System.out.println("Photo Approved for: " + currentOrderId + " " + currentAngle);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void RejectPhoto(ActionEvent actionEvent) {
        String reason = showRejectionReasonDialog();
        if (reason == null || reason.trim().isEmpty()) {
            System.out.println("Rejection cancelled or no reason provided.");
            return;
        }

        try {
            photosModel.rejectPhoto(currentOrderId, currentAngle, reason);
            System.out.println("Photo Rejected for: " + currentOrderId + " " + currentAngle + " | Reason: " + reason);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String showRejectionReasonDialog() {
        TextField reasonField = new TextField();
        reasonField.setPromptText("Enter rejection reason");

        Button confirmBtn = new Button("Confirm");
        Button cancelBtn = new Button("Cancel");

        HBox buttons = new HBox(10, confirmBtn, cancelBtn);
        buttons.setStyle("-fx-alignment: center;");

        VBox layout = new VBox(10, reasonField, buttons);
        layout.setStyle("-fx-padding: 20;");

        Stage dialogStage = new Stage();
        dialogStage.initOwner(RejectBtn.getScene().getWindow());
        dialogStage.setTitle("Rejection Reason");

        Scene scene = new Scene(layout);
        dialogStage.setScene(scene);

        final String[] result = {null};

        confirmBtn.setOnAction(e -> {
            result[0] = reasonField.getText();
            dialogStage.close();
        });

        cancelBtn.setOnAction(e -> {
            result[0] = null;
            dialogStage.close();
        });

        dialogStage.showAndWait();
        return result[0];
    }

    public void OnClickFinishButton(ActionEvent actionEvent) {
        try {
            qamodel.setOrderToCompleted(currentOrderId);
            //todo refactor
            photosModel.notifyOperatorOrderReviewed(currentOrderId);

            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

