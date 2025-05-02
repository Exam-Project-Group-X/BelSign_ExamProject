package easv.dk.belsign.gui.controllers.Operator;

import easv.dk.belsign.be.Order;
import easv.dk.belsign.utils.WebcamCaptureDialog;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.UUID;

public class CameraController {

    @FXML
    private ImageView frontImage, backImage, topImage, leftImage, rightImage;

    private File frontFile, backFile, topFile, leftFile, rightFile;
    private Image placeholderImage;
    private Order selectedOrder;

    private final File imageSaveDirectory = new File("C:/Users/havoc/Downloads/Photo-dumper");

    @FXML
    public void initialize() {
        if (!imageSaveDirectory.exists()) {
            imageSaveDirectory.mkdirs();
        }

        URL placeholderUrl = getClass().getResource("/plus.png");
        if (placeholderUrl != null) {
            placeholderImage = new Image(placeholderUrl.toString());
            initImageView(frontImage, f -> frontFile = f);
            initImageView(backImage, f -> backFile = f);
            initImageView(topImage, f -> topFile = f);
            initImageView(leftImage, f -> leftFile = f);
            initImageView(rightImage, f -> rightFile = f);
        }
    }

    private void initImageView(ImageView imageView, java.util.function.Consumer<File> fileConsumer) {
        imageView.setImage(placeholderImage);
        imageView.setOnMouseClicked(e -> captureAndSetImage(imageView, fileConsumer));

        ContextMenu menu = new ContextMenu();

        MenuItem retry = new MenuItem("Retry Capture");
        retry.setOnAction(e -> captureAndSetImage(imageView, fileConsumer));

        MenuItem delete = new MenuItem("Delete Image");
        delete.setOnAction(e -> {
            imageView.setImage(placeholderImage);
            fileConsumer.accept(null);
        });

        menu.getItems().addAll(retry, delete);
        imageView.setOnContextMenuRequested(e -> menu.show(imageView, e.getScreenX(), e.getScreenY()));
    }

    private void captureAndSetImage(ImageView imageView, java.util.function.Consumer<File> fileConsumer) {
        Image captured = new WebcamCaptureDialog().showAndCapture();
        if (captured != null) {
            imageView.setImage(captured);
            File savedFile = saveImageToFile(captured);
            if (savedFile != null) {
                fileConsumer.accept(savedFile);
            }
        }
    }

    private File saveImageToFile(Image image) {
        BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);
        File file = new File(imageSaveDirectory, "captured-" + UUID.randomUUID() + ".png");
        try {
            ImageIO.write(bImage, "PNG", file);
            return file;
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Failed to save image.");
            return null;
        }
    }

    @FXML
    public void uploadImages() {
        if (!isCaptured(frontImage) || !isCaptured(backImage) || !isCaptured(topImage) ||
                !isCaptured(leftImage) || !isCaptured(rightImage)) {
            showAlert("Please capture all 5 images before uploading.");
            return;
        }

        // ImageUploadService.upload(frontFile, backFile, topFile, leftFile, rightFile);

        showAlert("Images uploaded successfully!");
    }

    private boolean isCaptured(ImageView view) {
        return view.getImage() != null && view.getImage() != placeholderImage;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void setSelectedOrder(Order order) {
        this.selectedOrder = order;
        // Perform any additional setup with the selected order if needed
        System.out.println("Selected Order: " + order.getOrderNumber());
    }
}
