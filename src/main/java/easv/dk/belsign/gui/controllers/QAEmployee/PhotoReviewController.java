package easv.dk.belsign.gui.controllers.QAEmployee;

import easv.dk.belsign.gui.ViewManagement.FXMLPath;
import easv.dk.belsign.gui.ViewManagement.ViewManager;
import javafx.event.ActionEvent;

import easv.dk.belsign.dal.web.ProductPhotosDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.io.ByteArrayInputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;




public class PhotoReviewController {

    public Button closeBtn;
    @FXML
    private ImageView mainImg;
    @FXML
    private HBox thumbStrip;
    @FXML
    private TextField captionField;

    private final ProductPhotosDAO photoDAO = new ProductPhotosDAO();
    private final List<Image> loadedImages = new ArrayList<>();


    public void setCaption(String caption) {
        captionField.setText(caption);
    }

    public void loadPhotosForOrder(int orderId) {
        try {
            Map<String, byte[]> photoMap = photoDAO.getPhotosByOrderId(orderId);
            thumbStrip.getChildren().clear();
            loadedImages.clear();

            for (Map.Entry<String, byte[]> entry : photoMap.entrySet()) {
                byte[] photoBytes = entry.getValue();
                if (photoBytes == null) continue;

                Image img = new Image(new ByteArrayInputStream(photoBytes));
                loadedImages.add(img);

                ImageView thumb = new ImageView(img);
                thumb.setFitWidth(200);
                thumb.setFitHeight(150);
                thumb.setPreserveRatio(true);
                thumb.setPickOnBounds(true);

                thumb.setOnMouseClicked(e -> mainImg.setImage(img));
                thumbStrip.getChildren().add(thumb);
            }

            // Default image
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
}
