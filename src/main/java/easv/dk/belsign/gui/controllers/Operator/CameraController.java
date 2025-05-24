package easv.dk.belsign.gui.controllers.Operator;

import easv.dk.belsign.be.Order;
import easv.dk.belsign.be.ProductPhotos;
import easv.dk.belsign.bll.ProductPhotosManager;
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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.sql.SQLException;
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
        String status;
        String comment;
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

        try {
            ProductPhotosManager manager = new ProductPhotosManager();

            // Only upload rejected or new photos from required
            for (Map.Entry<String, PhotoEntry> entrySet : requiredPhotos.entrySet()) {
                PhotoEntry entry = entrySet.getValue();

                if (entry.image != null && (entry.status == null || "Rejected".equalsIgnoreCase(entry.status))) {
                    manager.upsertCapturedPhoto(
                            selectedOrder.getOrderID(),
                            entry.descriptionField.getText(),
                            convertToBytes(entry.image),
                            operatorName
                    );
                }
            }

            // Upload all extra photos
            for (PhotoEntry entry : extraPhotos) {
                if (entry.image != null) {
                    manager.upsertCapturedPhoto(
                            selectedOrder.getOrderID(),
                            entry.descriptionField.getText(),
                            convertToBytes(entry.image),
                            operatorName
                    );
                }
            }

            showAlert("Photos uploaded successfully!\nSigned by: " + operatorName);
            Navigation.goToOperatorDashboard();

        } catch (Exception e) {
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
                swipeLabel.setTextFill(Color.WHITE);
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

        // Clear existing entries
        requiredPhotos.clear();
        extraPhotos.clear();
        photoGridPane.getChildren().clear();
        photoColIndex = 0;
        photoRowIndex = 0;

        try {
            Map<String, ProductPhotos> photoMap = new ProductPhotosManager().getDetailedPhotosByOrderId(order.getOrderID());

            String[] requiredLabels = {"Front", "Back", "Top", "Left", "Right"};

            for (String angle : requiredLabels) {
                PhotoEntry entry = new PhotoEntry();
                entry.descriptionField = new TextField(angle);
                entry.descriptionField.setPrefWidth(150);

                ProductPhotos photo = photoMap.get(angle.toUpperCase());

                if (photo != null && photo.getPhotoData() != null) {
                    Image image = new Image(new ByteArrayInputStream(photo.getPhotoData()));
                    entry.image = image;
                    entry.status = photo.getStatus();
                    entry.comment = photo.getComment();
                    entry.imageView = new ImageView(image);
                    entry.imageView.setFitHeight(150);
                    entry.imageView.setFitWidth(150);
                    entry.imageView.setPreserveRatio(true);

                    if ("Approved".equalsIgnoreCase(photo.getStatus())) {
                        entry.imageView.setOpacity(0.4); // make transparent
                        entry.imageView.setDisable(true); // lock interaction
                    } else if ("Rejected".equalsIgnoreCase(photo.getStatus())) {
                        // allow recapture
                        entry.imageView.setStyle("-fx-cursor: hand;");
                        entry.imageView.setOnMouseClicked(e -> {
                            Image captured = new WebcamCaptureDialog().showAndCapture();
                            if (captured != null) {
                                entry.image = captured;
                                entry.imageView.setImage(captured);
                                checkSwipeReadiness();
                            }
                        });
                    }

                    // Insert comment label if not null
                    VBox container = new VBox(5);
                    container.getChildren().addAll(entry.imageView, entry.descriptionField);
                    if (entry.comment != null && !entry.comment.isEmpty()) {
                        Label commentLabel = new Label("Comment: " + entry.comment);
                        commentLabel.setWrapText(true);
                        commentLabel.setStyle("-fx-text-fill: red; -fx-font-size: 11px;");
                        container.getChildren().add(commentLabel);
                    }
                    photoGridPane.add(container, photoColIndex, photoRowIndex);
                    photoColIndex++;
                    if (photoColIndex >= photoColumnCount) {
                        photoColIndex = 0;
                        photoRowIndex++;
                    }
                    requiredPhotos.put(angle, entry);
                } else {
                    // Photo is missing, allow capture
                    PhotoEntry newEntry = createPhotoEntry(angle);
                    requiredPhotos.put(angle, newEntry);
                    addPhotoEntryToGrid(newEntry);
                }
            }
        } catch (SQLException e) {
            showAlert("Error loading photos: " + e.getMessage());
        }
    }
}
