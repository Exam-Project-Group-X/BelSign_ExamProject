package easv.dk.belsign.gui.controllers.QAEmployee;

import easv.dk.belsign.gui.ViewManagement.Navigation;
import easv.dk.belsign.gui.models.PhotosModel;
import easv.dk.belsign.gui.models.QAEmployeeModel;
import easv.dk.belsign.utils.AlertUtil;
import javafx.event.ActionEvent;

import easv.dk.belsign.dal.web.ProductPhotosDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import java.io.ByteArrayInputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;




public class PhotoReviewController {

    // === FXML UI components ===
    @FXML private ImageView mainImg;
    @FXML private HBox thumbStrip;
    @FXML private TextField captionField;
    @FXML private Button finishbtn;
    @FXML private Button ApproveBtn;
    @FXML private Button RejectBtn;
    @FXML private Button closeBtn;
    @FXML private StackPane imageHolder;

    // === Models ===
    private PhotosModel photosModel;
    private QAEmployeeModel qamodel;

    // === State ===
    private int currentOrderId;
    private String currentAngle;

    private final List<Image> loadedImages = new ArrayList<>();
    private final List<StackPane> thumbnailViews = new ArrayList<>();
    private StackPane currentSelectedThumb = null;
    private int currentThumbPage = 0;

    private static final int THUMBS_PER_PAGE = 5;

    // === Setters ===
    public void setModel(PhotosModel photosModel) { this.photosModel = photosModel; }
    public void setQAEmployeeModel(QAEmployeeModel model) { this.qamodel = model; }
    public void setOrderId(int orderId) { this.currentOrderId = orderId; }
    public void setCurrentAngle(String angle) { this.currentAngle = angle; }
    public void setCaption(String caption) { captionField.setText(caption); }

    public void loadPhotosForOrder(int orderId) {
        String previousAngle = currentAngle;
        try {
            Map<String, byte[]> photoMap = photosModel.getPhotosByOrderId(orderId);
            Map<String, String> statusMap = photosModel.getPhotoStatusByOrderId(orderId);
            thumbStrip.getChildren().clear();
            thumbnailViews.clear();
            loadedImages.clear();

            List<String> angles = new ArrayList<>(photoMap.keySet());

            for (String angle : angles) {
                byte[] photoBytes = photoMap.get(angle);
                if (photoBytes == null) continue;

                Image img = new Image(new ByteArrayInputStream(photoBytes));
                loadedImages.add(img);

                ImageView thumb = new ImageView(img);
                thumb.setFitWidth(200);
                thumb.setFitHeight(150);
                thumb.setPreserveRatio(true);
                thumb.setPickOnBounds(true);

                StackPane wrapper = new StackPane(thumb);
                wrapper.getStyleClass().add("thumb-wrapper");

                String status = statusMap.getOrDefault(angle.toUpperCase(), "Pending Review");
                if (status.equalsIgnoreCase("Approved")) wrapper.getStyleClass().add("approved-thumb");
                else if (status.equalsIgnoreCase("Rejected")) wrapper.getStyleClass().add("rejected-thumb");

                wrapper.setOnMouseClicked(e -> selectImageByAngle(angle, statusMap, angles));
                thumbnailViews.add(wrapper);
            }

            if (!angles.isEmpty()) {
                if (angles.contains(previousAngle)) {
                    selectImageByAngle(previousAngle, statusMap, angles);
                } else {
                    selectImageByAngle(angles.get(0), statusMap, angles);
                }
            }

            showCurrentThumbPage();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private boolean allPhotosApproved() {
        try {
            Map<String, String> statusMap = photosModel.getPhotoStatusByOrderId(currentOrderId);
            for (String status : statusMap.values()) {
                if (!status.equalsIgnoreCase("Approved")) {
                    return false;
                }
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // safer fallback
        }
    }

    private void applyMainImageHighlight(String angle, Map<String, String> statusMap) {
        mainImg.getStyleClass().removeAll("main-approved", "main-rejected", "main-neutral");

        String status = statusMap.getOrDefault(angle.toUpperCase(), "Pending Review");

        switch (status) {
            case "Approved" -> mainImg.getStyleClass().add("main-approved");
            case "Rejected" -> mainImg.getStyleClass().add("main-rejected");
            default -> mainImg.getStyleClass().add("main-neutral");
        }
    }


    public void onClickLogoutBtn(ActionEvent actionEvent) {
        Navigation.goToTitleScreen();
    }


    // The orders load again, when we close, but no need to

    public void onCloseBtnClick(ActionEvent actionEvent) {
        Navigation.goToQAEmployeeView();
    }




    /// TODO add debug to see : print ("Photo approved:")
    public void ApprovePhoto(ActionEvent actionEvent) {
        try{
            photosModel.approvePhoto(currentOrderId,currentAngle);
            loadPhotosForOrder(currentOrderId);
            System.out.println("Photo Approved for: " + currentOrderId + " " + currentAngle);
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    public void RejectPhoto(ActionEvent actionEvent) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Reject Photo");
        dialog.setHeaderText("Please provide a reason for rejection:");
        dialog.setContentText("Comment:");

        dialog.getEditor().setPromptText("e.g. blurry, wrong photo...");

        dialog.showAndWait().ifPresent(comment -> {
            if (comment.trim().isEmpty()) {
                AlertUtil.error(RejectBtn.getScene(), "You must write a reason to reject.");
                return;
            }

            try {
                photosModel.rejectPhoto(currentOrderId, currentAngle, comment);
                loadPhotosForOrder(currentOrderId);
                AlertUtil.success(RejectBtn.getScene(), "Photo Rejected ✓");

                System.out.println("❌ Photo Rejected: " + currentOrderId + ", " + currentAngle + " - " + comment);
            } catch (SQLException e) {
                e.printStackTrace();
                AlertUtil.error(RejectBtn.getScene(), "Rejection Failed: Database error.");
            }
        });
    }

    public void OnClickFinishButton(ActionEvent actionEvent) {
        if (allPhotosApproved()) {
            try {
                qamodel.setOrderToCompleted(currentOrderId);
                AlertUtil.success(finishbtn.getScene(), "✅ All photos approved. Report ready.");


            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            AlertUtil.info(finishbtn.getScene(), "📸 Operator notified to retake rejected photos.");
            System.out.println("📸 Operator notified to retake rejected photos.");
        }
    }


    private void showCurrentThumbPage() {
        thumbStrip.getChildren().clear();

        int start = currentThumbPage * THUMBS_PER_PAGE;
        int end = Math.min(start + THUMBS_PER_PAGE, thumbnailViews.size());

        for (int i = start; i < end; i++) {
            thumbStrip.getChildren().add(thumbnailViews.get(i));
        }
    }

    @FXML
    private void onLeftArrowClick(ActionEvent event) {
        if (currentThumbPage > 0) {
            currentThumbPage--;
            showCurrentThumbPage();
        }
    }

    @FXML
    private void onRightArrowClick(ActionEvent event) {
        int maxPage = (int) Math.ceil((double) thumbnailViews.size() / THUMBS_PER_PAGE) - 1;
        if (currentThumbPage < maxPage) {
            currentThumbPage++;
            showCurrentThumbPage();
        }
    }




    private void selectImageByAngle(String angle, Map<String, String> statusMap, List<String> angles) {
        int index = angles.indexOf(angle);
        if (index != -1) {
            mainImg.setImage(loadedImages.get(index));
            currentAngle = angle;
            captionField.setText("Angle: " + angle);
            applyMainImageHighlight(angle, statusMap);

            if (currentSelectedThumb != null) {
                currentSelectedThumb.getStyleClass().remove("selected-thumb");
            }

            StackPane wrapper = thumbnailViews.get(index);
            wrapper.getStyleClass().add("selected-thumb");
            currentSelectedThumb = wrapper;

            currentThumbPage = index / THUMBS_PER_PAGE;
        }
    }





}
