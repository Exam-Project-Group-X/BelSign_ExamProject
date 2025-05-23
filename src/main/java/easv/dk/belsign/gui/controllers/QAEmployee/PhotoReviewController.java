package easv.dk.belsign.gui.controllers.QAEmployee;

import easv.dk.belsign.be.User;
import easv.dk.belsign.gui.ViewManagement.FXMLManager;
import easv.dk.belsign.gui.ViewManagement.FXMLPath;
import easv.dk.belsign.gui.ViewManagement.Navigation;
import easv.dk.belsign.gui.controllers.TopBarController;
import easv.dk.belsign.gui.models.PhotosModel;
import easv.dk.belsign.gui.models.QAEmployeeModel;
import easv.dk.belsign.utils.AlertUtil;
import javafx.event.ActionEvent;

import easv.dk.belsign.dal.web.ProductPhotosDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.util.Pair;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;




public class PhotoReviewController {

    @FXML private ImageView mainImg;
    @FXML private HBox thumbStrip;
    @FXML private TextField captionField;
    @FXML private Button finishbtn;
    @FXML private Button ApproveBtn;
    @FXML private Button RejectBtn;
    @FXML private Button closeBtn;
    @FXML private StackPane imageHolder;
    @FXML private AnchorPane topBarHolder;

    private TopBarController topBarController;
    private User loggedInUser;

    private PhotosModel photosModel;
    private QAEmployeeModel qamodel;

    private int currentOrderId;
    private String currentAngle;

    private final List<Image> loadedImages = new ArrayList<>();
    private final List<StackPane> thumbnailViews = new ArrayList<>();
    private StackPane currentSelectedThumb = null;
    private int currentThumbPage = 0;
    private static final int THUMBS_PER_PAGE = 5;

    // Setters
    public void setModel(PhotosModel photosModel) { this.photosModel = photosModel; }
    public void setQAEmployeeModel(QAEmployeeModel model) { this.qamodel = model; }
    public void setOrderId(int orderId) { this.currentOrderId = orderId; }
    public void setCurrentAngle(String angle) { this.currentAngle = angle; }
    public void setCaption(String caption) { captionField.setText(caption); }



    @FXML
    public void initialize() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(FXMLPath.TOP_BAR));
            Node topBar = loader.load();
            topBarController = loader.getController();
            topBarHolder.getChildren().setAll(topBar);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




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
                if ("Approved".equalsIgnoreCase(status)) wrapper.getStyleClass().add("approved-thumb");
                else if ("Rejected".equalsIgnoreCase(status)) wrapper.getStyleClass().add("rejected-thumb");

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
            return statusMap.values().stream().allMatch(s -> "Approved".equalsIgnoreCase(s));
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
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

    public void ApprovePhoto(ActionEvent actionEvent) {
        try {
            photosModel.approvePhoto(currentOrderId, currentAngle);
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

    public void onCloseBtnClick(ActionEvent actionEvent) {
        Pair<Parent, QAEmployeeController> pair = FXMLManager.INSTANCE.getFXML(FXMLPath.QA_EMPLOYEE_VIEW);
        QAEmployeeController controller = pair.getValue();
        controller.setLoggedInUser(loggedInUser);
        Navigation.goToQAEmployeeView(pair.getKey());
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

    public void setup(User user, int orderId) {
        this.loggedInUser = user;


        if (topBarController != null && user != null) {
            topBarController.setLoggedInUser(user);
        }

        setOrderId(orderId);
        setCaption("Order #" + orderId);
        setModel(new PhotosModel());
        setQAEmployeeModel(new QAEmployeeModel());

        loadPhotosForOrder(orderId);
    }
}