package easv.dk.belsign.gui.controllers.QAEmployee;

import easv.dk.belsign.be.Order;
import easv.dk.belsign.be.User;
import easv.dk.belsign.bll.OrderManager;
import easv.dk.belsign.bll.PhotoReviewService;
import easv.dk.belsign.gui.ViewManagement.FXMLManager;
import easv.dk.belsign.gui.ViewManagement.FXMLPath;
import easv.dk.belsign.gui.ViewManagement.Navigation;
import easv.dk.belsign.gui.ViewManagement.ViewManager;
import easv.dk.belsign.gui.controllers.TopBarController;
import easv.dk.belsign.gui.models.PhotosModel;
import easv.dk.belsign.gui.models.QAEmployeeModel;
import easv.dk.belsign.gui.util.AlertUtil;
import javafx.application.Platform;
import javafx.event.ActionEvent;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
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
    @FXML private Label lblOrderNo;
    @FXML private Button deleteBtn;
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

    private final List<String> angles = new ArrayList<>();
    private final List<Image> loadedImages = new ArrayList<>();
    private final List<StackPane> thumbnailViews = new ArrayList<>();
    private StackPane currentSelectedThumb = null;
    private int currentThumbPage = 0;
    private static final int THUMBS_PER_PAGE = 5;
    private Order order;

    private PhotoReviewService photoReviewService;

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
            Map<String, byte[]> photoMap = photoReviewService.getPhotos(orderId);
            Map<String, String> statusMap = photoReviewService.getStatuses(orderId);

            clearThumbnails();

            angles.clear();
            angles.addAll(photoMap.keySet());
            for (String angle : angles) {
                byte[] photoBytes = photoMap.get(angle);
                if (photoBytes == null) continue;

                Image img = createImageFromBytes(photoBytes);
                loadedImages.add(img);

                StackPane thumbWrapper = createThumbnailWrapper(img, angle, statusMap);
                thumbnailViews.add(thumbWrapper);
            }

            if (!angles.isEmpty()) {
                String angleToSelect = angles.contains(previousAngle) ? previousAngle : angles.get(0);
                selectImageByAngle(angleToSelect, statusMap, angles);
            }

            showCurrentThumbPage();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void clearThumbnails() {
        thumbStrip.getChildren().clear();
        thumbnailViews.clear();
        loadedImages.clear();
    }

    private Image createImageFromBytes(byte[] bytes) {
        return new Image(new ByteArrayInputStream(bytes));
    }

    private StackPane createThumbnailWrapper(Image img, String angle, Map<String, String> statusMap) {
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

        wrapper.setOnMouseClicked(e -> selectImageByAngle(angle, statusMap, new ArrayList<>(statusMap.keySet())));

        return wrapper;
    }

    private boolean allPhotosApproved() {
        try {
            return photoReviewService.allPhotosApproved(currentOrderId);
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
            photoReviewService.approvePhoto(currentOrderId, currentAngle);
            loadPhotosForOrder(currentOrderId);
            System.out.println("Photo Approved for: " + currentOrderId + " " + currentAngle);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void DeletePhoto(ActionEvent actionEvent){
        if (currentAngle == null || currentAngle.isEmpty()) {
            AlertUtil.error(RejectBtn.getScene(), "No photo selected to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Deletion");
        confirm.setHeaderText("Are you sure you want to delete this photo?");
        confirm.setContentText("Angle: " + currentAngle);

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    // Deletes from DB
                    photoReviewService.deletePhoto(currentOrderId, currentAngle);

                    //  Remove from UI lists
                    int index = angles.indexOf(currentAngle);
                    if (index >= 0) {
                        angles.remove(index);
                        loadedImages.remove(index);
                        thumbnailViews.remove(index);
                    }

                    // Refresh UI
                    currentAngle = angles.isEmpty() ? null : angles.get(0);
                    currentSelectedThumb = null;

                    if (currentAngle != null) {
                        mainImg.setImage(loadedImages.get(0));
                        captionField.setText("Angle: " + currentAngle);
                    } else {
                        mainImg.setImage(null);
                        captionField.setText("");
                    }

                    showCurrentThumbPage();

                    AlertUtil.success(RejectBtn.getScene(), "Photo deleted ✓");
                    System.out.println("🗑️ Deleted photo: Order " + currentOrderId + " – " + currentAngle);

                } catch (SQLException e) {
                    e.printStackTrace();
                    AlertUtil.error(RejectBtn.getScene(), "Failed to delete photo from database.");
                }
            }
        });
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
                photoReviewService.rejectPhoto(currentOrderId, currentAngle, comment);
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
                photoReviewService.completeOrder(currentOrderId);
                goBackToQaPanel();

                // Show success in the next scene
                Platform.runLater(() ->
                        AlertUtil.success(
                                ViewManager.INSTANCE.getSceneManager()
                                        .getCurrentStage()
                                        .getScene(),
                                "✅ All photos approved. Report ready.")
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            AlertUtil.info(finishbtn.getScene(), "📸 Operator notified to retake rejected photos.");
            System.out.println("📸 Operator notified to retake rejected photos.");
        }
    }

    public void onCloseBtnClick(ActionEvent actionEvent) {
        goBackToQaPanel();
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
        this.currentOrderId = orderId;

        //set the label to "Order ID: " + orderId
        try {
            // Use OrderManager to load every Order
            List<Order> all = new OrderManager().getAllOrders();

            // Find the one whose ID matches
            Order matching = all.stream()
                    .filter(o -> o.getOrderID() == orderId)
                    .findFirst()
                    .orElse(null);

            if (matching != null) {
                // 3) Now set the label using order.getOrderNumber()
                lblOrderNo.setText("Order No: " + matching.getOrderNumber());
            } else {
                // fallback if something went wrong
                lblOrderNo.setText("Order ID: " + orderId);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            lblOrderNo.setText("Order ID: " + orderId);
        }


        if (topBarController != null && user != null) {
            topBarController.setLoggedInUser(user);
        }

        setOrderId(orderId);

        setCaption("Order #" + orderId);
        PhotosModel photosModel = new PhotosModel();
        QAEmployeeModel qaModel = new QAEmployeeModel();

        photoReviewService = new PhotoReviewService(photosModel, qaModel);

        loadPhotosForOrder(orderId);
    }
    public void goBackToQaPanel(){
        Pair<Parent, QAEmployeeController> pair = FXMLManager.INSTANCE.getFXML(FXMLPath.QA_EMPLOYEE_VIEW);
        QAEmployeeController controller = pair.getValue();
        controller.setLoggedInUser(loggedInUser);
        Navigation.goToQAEmployeeView(pair.getKey());
    }
}