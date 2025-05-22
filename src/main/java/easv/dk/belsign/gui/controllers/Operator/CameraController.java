package easv.dk.belsign.gui.controllers.Operator;

import easv.dk.belsign.be.Order;
import easv.dk.belsign.dal.web.ProductPhotosDAO;
import easv.dk.belsign.gui.ViewManagement.Navigation;
import easv.dk.belsign.utils.WebcamCaptureDialog;

import javafx.animation.TranslateTransition;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import javafx.util.Duration;
import java.util.List;
import java.util.Map;

public class CameraController {

    @FXML private GridPane photoGridPane;
    @FXML private TextField operatorNameField;
    @FXML private StackPane swipePane;
    @FXML private Circle swipeKnob;
    @FXML private Label orderNumber, swipeLabel;

    private double dragStartX;
    private boolean uploadTriggered = false;

    private final Map<String, PhotoEntry> requiredPhotos = new LinkedHashMap<>();
    private final List<PhotoEntry> extraPhotos = new ArrayList<>();

    private Image placeholderImage;
    private Order selectedOrder;

    private int photoColumnCount = 3;
    private int photoRowIndex = 0;
    private int photoColIndex = 0;

    public void onBackClick(ActionEvent actionEvent) {Navigation.goToOperatorDashboard();
    }

    private static class PhotoEntry {
        ImageView imageView;
        TextField descriptionField;
        Image image;
    }

    @FXML
    public void initialize() {
        placeholderImage = new Image(getClass().getResource("/easv/dk/belsign/images/icons/plus2.png").toString());
        String[] requiredLabels = {"Front", "Back", "Top", "Left", "Right"};
        for (String label : requiredLabels) {
            PhotoEntry entry = createPhotoEntry(label);
            requiredPhotos.put(label, entry);
            addPhotoEntryToGrid(entry);
        }
        operatorNameField.textProperty().addListener((obs, oldVal, newVal) -> checkSwipeReadiness());
        initializeSwipeControl();
        checkSwipeReadiness(); // disable swipe by default
    }

    private PhotoEntry createPhotoEntry(String defaultDescription) {
        PhotoEntry entry = new PhotoEntry();
        entry.imageView = new ImageView(placeholderImage);
        entry.imageView.setFitHeight(150);
        entry.imageView.setFitWidth(150);
        entry.imageView.setPreserveRatio(true);
        entry.imageView.setStyle("-fx-cursor: hand;");

        entry.descriptionField = new TextField(defaultDescription);
        entry.descriptionField.setPrefWidth(150);

        entry.imageView.setOnMouseClicked(e -> {
            Image captured = new WebcamCaptureDialog().showAndCapture();
            if (captured != null) {
                entry.image = captured;
                entry.imageView.setImage(captured);
                checkSwipeReadiness(); // update swipe availability
            }
        });
        return entry;
    }

    private void addPhotoEntryToGrid(PhotoEntry entry) {
        VBox container = new VBox(5);
        container.getChildren().addAll(entry.imageView, entry.descriptionField);
        photoGridPane.add(container, photoColIndex, photoRowIndex);

        photoColIndex++;

        if (photoColIndex >= photoColumnCount) {
            photoColIndex = 0;
            photoRowIndex++;
        }
    }

    @FXML
    public void handleAddExtraPhoto() {
        PhotoEntry extra = createPhotoEntry("New Angle");
        extraPhotos.add(extra);
        addPhotoEntryToGrid(extra);
    }

    @FXML
    public void uploadImages() {
        if (selectedOrder == null || selectedOrder.getOrderID() <= 0) {
            showAlert("No valid order selected");
            return;
        }
        String operatorName = operatorNameField.getText().trim();
        if (operatorName.isEmpty()) {
            showAlert("Please enter operator name to sign the upload");
            return;
        }
        for (Map.Entry<String, PhotoEntry> entry : requiredPhotos.entrySet()) {
            if (entry.getValue().image == null) {
                showAlert("Missing required photo: " + entry.getKey());
                return;
            }
        }

        try {
            ProductPhotosDAO dao = new ProductPhotosDAO();

            for (PhotoEntry entry : requiredPhotos.values()) {
                dao.insertCapturedPhoto(
                        selectedOrder.getOrderID(),
                        entry.descriptionField.getText(),
                        convertToBytes(entry.image)
                );
            }

            for (PhotoEntry entry : extraPhotos) {
                if (entry.image != null) {
                    dao.insertCapturedPhoto(
                            selectedOrder.getOrderID(),
                            entry.descriptionField.getText(),
                            convertToBytes(entry.image)
                    );
                }
            }

            showAlert("All photos uploaded successfully!\nSigned by: " + operatorName);
            Navigation.goToOperatorDashboard();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error uploading photos: " + e.getMessage());
        }
    }

    private void checkSwipeReadiness() {
        boolean hasAllRequiredPhotos = requiredPhotos.values().stream()
                .allMatch(entry -> entry.image != null);

        boolean hasOperatorName = !operatorNameField.getText().trim().isEmpty();

        boolean isReady = hasAllRequiredPhotos && hasOperatorName;

        swipePane.setDisable(!isReady);
        swipePane.setOpacity(isReady ? 1.0 : 0.4);
    }

    @FXML
    private void initializeSwipeControl() {
        swipeKnob.setOnMousePressed(event -> dragStartX = event.getSceneX());

        swipeKnob.setOnMouseDragged(event -> {
            double offsetX = event.getSceneX() - dragStartX;
            double maxX = swipePane.getWidth() - swipeKnob.getRadius() * 2;
            double newX = Math.max(0, Math.min(offsetX, maxX));
            swipeKnob.setLayoutX(newX + swipeKnob.getRadius());

            if (newX >= maxX - 5 && !uploadTriggered) {
                uploadTriggered = true;
                swipeLabel.setText("Uploading...");
                swipeLabel.setTextFill(Color.GREEN);
                uploadImages();
            }
        });

        swipeKnob.setOnMouseReleased(event -> {
            if (!uploadTriggered) {
                TranslateTransition tt = new TranslateTransition(Duration.millis(300), swipeKnob);
                tt.setToX(0);
                tt.setOnFinished(e -> swipeKnob.setLayoutX(swipeKnob.getRadius()));
                tt.play();
            }
        });

        swipeKnob.setLayoutX(swipeKnob.getRadius()); // Start at left
    }

    private byte[] convertToBytes(Image image) throws IOException {
        BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(bImage, "png", outputStream);
        return outputStream.toByteArray();
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public void setSelectedOrder(Order order) {
        this.selectedOrder = order;
        if (orderNumber != null && order != null) {
            orderNumber.setText("Order Nr.: " + order.getOrderNumber());
        }
    }
}
