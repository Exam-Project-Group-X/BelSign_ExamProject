package easv.dk.belsign.gui.controllers.Operator;

import easv.dk.belsign.be.Order;
import easv.dk.belsign.bll.OrderManager;
import easv.dk.belsign.dal.web.ProductPhotosDAO;
import easv.dk.belsign.gui.ViewManagement.FXMLPath;
import easv.dk.belsign.gui.ViewManagement.ViewManager;
import easv.dk.belsign.utils.WebcamCaptureDialog;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;


public class CameraController {

    public Button UploadPhotosBtn;
    @FXML
    private ImageView frontImage, backImage, topImage, leftImage, rightImage;

    private File frontFile, backFile, topFile, leftFile, rightFile;
    private Image placeholderImage;
    private Order selectedOrder;

    private final File imageSaveDirectory = new File("media");
    private OrderManager orderManager = new OrderManager();

    @FXML
    public void initialize() {
        if (!imageSaveDirectory.exists()) {
            imageSaveDirectory.mkdirs();
        }

        URL placeholderUrl = getClass().getResource("/easv/dk/belsign/images/icons/plus2.png");
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

//    private void captureAndSetImage(ImageView imageView, java.util.function.Consumer<File> fileConsumer) {
//        Image captured = new WebcamCaptureDialog().showAndCapture();
//        if (captured != null) {
//            imageView.setImage(captured);
//            File savedFile = saveImageToFile(captured);
//            if (savedFile != null) {
//                fileConsumer.accept(savedFile);
//            }
//        }
//    }

    private void captureAndSetImage(ImageView imageView, java.util.function.Consumer<File> fileConsumer) {
        Image captured = new WebcamCaptureDialog().showAndCapture();
        if (captured != null) {
            imageView.setImage(captured);
            fileConsumer.accept(null); // We’re not saving to file anymore
        }
    }



//    private File saveImageToFile(Image image) {
//        BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);
//        File file = new File(imageSaveDirectory, "captured-" + UUID.randomUUID() + ".png");
//        try {
//            ImageIO.write(bImage, "PNG", file);
//            return file;
//        } catch (IOException e) {
//            e.printStackTrace();
//            showAlert("Failed to save image.");
//            return null;
//        }
//    }

    private byte[] convertToBytes(Image image) {
        try {
            BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(bImage, "png", outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Failed to convert image.");
            return null;
        }

    }


    @FXML
    public void uploadImages() throws Exception {


        if (selectedOrder == null) {
            showAlert("❌ No order selected.");
            return;
        }

        System.out.println("📦 Selected Order ID: " + selectedOrder.getOrderID());

        if (selectedOrder.getOrderID() <= 0) {
            showAlert("❌ Invalid Order ID: " + selectedOrder.getOrderID());
            return;
        }

        if (!isCaptured(frontImage) || !isCaptured(backImage) || !isCaptured(topImage) ||
                !isCaptured(leftImage) || !isCaptured(rightImage)) {
            showAlert("Please capture all 5 images before uploading.");
            return;
        }

        try {
            ProductPhotosDAO dao = new ProductPhotosDAO();

            dao.insertCapturedPhoto(selectedOrder.getOrderID(), "FRONT", convertToBytes(frontImage.getImage()));
            dao.insertCapturedPhoto(selectedOrder.getOrderID(), "BACK", convertToBytes(backImage.getImage()));
            dao.insertCapturedPhoto(selectedOrder.getOrderID(), "TOP", convertToBytes(topImage.getImage()));
            dao.insertCapturedPhoto(selectedOrder.getOrderID(), "LEFT", convertToBytes(leftImage.getImage()));
            dao.insertCapturedPhoto(selectedOrder.getOrderID(), "RIGHT", convertToBytes(rightImage.getImage()));

            showAlert("✅ Images uploaded successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("❌ Failed to upload images.");
        }

        orderManager.updateOrderToPending(selectedOrder);
        ViewManager.INSTANCE.showScene(FXMLPath.TITLE_SCREEN);


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
        System.out.println("Selected Order Number: " + order.getOrderNumber());
        System.out.println("Selected OrderID: " + order.getOrderID());
    }
}
