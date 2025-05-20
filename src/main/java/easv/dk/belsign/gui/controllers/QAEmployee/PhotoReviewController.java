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

    private final List<StackPane> thumbnailViews = new ArrayList<>();
    private int currentThumbPage = 0;
    private static final int THUMBS_PER_PAGE = 5;
    private StackPane currentSelectedThumb = null;


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
            Map<String, byte[]> photoMap = photoDAO.getPhotosByOrderId(orderId);

            thumbStrip.getChildren().clear();
            thumbnailViews.clear();
            loadedImages.clear();
            currentThumbPage = 0;

            List<String> angles = new ArrayList<>(photoMap.keySet());
            for (String angle : angles) {
                byte[] photoBytes = photoMap.get(angle);
                if (photoBytes == null) continue;

                Image img = new Image(new ByteArrayInputStream(photoBytes));
                ImageView thumb = new ImageView(img);
                loadedImages.add(img);

                thumb.setFitWidth(200);
                thumb.setFitHeight(150);
                thumb.setPreserveRatio(true);
                thumb.setPickOnBounds(true);

                StackPane wrapper = new StackPane(thumb);
                wrapper.getStyleClass().add("thumb-wrapper");

                wrapper.setOnMouseClicked(e -> {
                    mainImg.setImage(img);
                    currentAngle = angle;
                    captionField.setText("Angle: " + angle);

                    if (currentSelectedThumb != null) {
                        currentSelectedThumb.getStyleClass().remove("selected-thumb");
                    }

                    wrapper.getStyleClass().add("selected-thumb");
                    currentSelectedThumb = wrapper;
                });

                thumbnailViews.add(wrapper);

            }

            // Show the first image by default
            if (!loadedImages.isEmpty()) {
                mainImg.setImage(loadedImages.get(0));
                currentAngle = angles.get(0);
                captionField.setText("Angle: " + currentAngle);
            }

            StackPane firstThumbWrapper = thumbnailViews.get(0);
            firstThumbWrapper.getStyleClass().add("selected-thumb");
            currentSelectedThumb = firstThumbWrapper;


            showCurrentThumbPage(); // Display only the first 5 images

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void onClickLogoutBtn(ActionEvent actionEvent) {
        Navigation.goToTitleScreen();
    }


    // The orders load again, when we close, but no need to.
    public void onCloseBtnClick(ActionEvent actionEvent) {
        Navigation.goToQAEmployeeView();
    }




    /// TODO add debug to see : print ("Photo approved:")
    public void ApprovePhoto(ActionEvent actionEvent) {
        try{
            photosModel.approvePhoto(currentOrderId,currentAngle);
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
                AlertUtil.success(RejectBtn.getScene(), "Photo Rejected ✓");
                System.out.println("❌ Photo Rejected: " + currentOrderId + ", " + currentAngle + " - " + comment);
            } catch (SQLException e) {
                e.printStackTrace();
                AlertUtil.error(RejectBtn.getScene(), "Rejection Failed: Database error.");
            }
        });
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


    public void OnClickFinishButton(ActionEvent actionEvent) {

        try {
            qamodel.setOrderToCompleted(currentOrderId);
            Navigation.goToQAEmployeeView();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }




}
