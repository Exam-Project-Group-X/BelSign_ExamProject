package easv.dk.belsign.gui.controllers.QAEmployee;

import easv.dk.belsign.dal.web.ProductPhotosDAO;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.ByteArrayInputStream;
import java.sql.SQLException;
import java.util.Map;

public class PhotoGridController {
    @FXML
    private ImageView frontImage;
    @FXML
    private ImageView backImage;
    @FXML
    private ImageView leftImage;
    @FXML
    private ImageView rightImage;
    @FXML
    private ImageView topImage;
    @FXML
    private ImageView bottomImage;

    private final ProductPhotosDAO photoDAO = new ProductPhotosDAO();

    public void setPhoto(String angle, byte[] photoData) {
        if (photoData == null) {
            System.out.println("No photo data for angle: " + angle);
            return;
        }
        Image image = new Image(new ByteArrayInputStream(photoData));
        System.out.println("Setting image for " + angle + ": " + photoData.length + " bytes");
        switch (angle.toUpperCase()) {
            case "FRONT" -> frontImage.setImage(image);
            case "BACK" -> backImage.setImage(image);
            case "LEFT" -> leftImage.setImage(image);
            case "RIGHT" -> rightImage.setImage(image);
            case "TOP" -> topImage.setImage(image);
            case "BOTTOM" -> bottomImage.setImage(image);
            default -> System.out.println("Unknown angle: " + angle);
        }
    }

    public void loadPhotosForOrder(int orderId) {
        System.out.println("Loading photos for order ID: " + orderId);
        clearAll();
        try {
            Map<String, byte[]> photoMap = photoDAO.getPhotosByOrderId(orderId);
            for (Map.Entry<String, byte[]> entry : photoMap.entrySet()) {
                setPhoto(entry.getKey(), entry.getValue());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void clearAll() {
        frontImage.setImage(null);
        backImage.setImage(null);
        leftImage.setImage(null);
        rightImage.setImage(null);
        topImage.setImage(null);
        bottomImage.setImage(null);
    }
}
