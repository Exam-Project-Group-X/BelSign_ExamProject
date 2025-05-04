package easv.dk.belsign.gui.controllers.QAEmployee;


import easv.dk.belsign.dal.web.ProductPhotosDAO;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.ByteArrayInputStream;
import java.sql.SQLException;
import java.util.Map;

public class PhotoGridController {

    @FXML private ImageView frontImage;
    @FXML private ImageView backImage;
    @FXML private ImageView leftImage;
    @FXML private ImageView rightImage;
    @FXML private ImageView topImage;
    @FXML private ImageView bottomImage;

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
        clearAll(); // optional reset

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
/// to insert directly in the database the photo query:
    //INSERT INTO ProductPhotos (OrderID, PhotoAngle, Status, PhotoData)
    //VALUES (1, 'LEFT', 'Pending', 0x52494646dc020000574542505650384ccf0200002f2bc1481005a448b2c5368220188612088290a3a11886a0e428188120026fbd3bef3bb9bd32d236d2fca3c197f3f0e164bc0c236d230d4ef24b3959075fcac1f9872d496edc66f6030c6073100c04bbf2b881ff4467592522797ebfd7069a3521729655d7dd2a1149454424ccda61d5ecc8591a62303421729689eebd845457107863350e862619c4e1d55064045277237ce7742c10914c7d20c7f9a4a8aa81e891c2d0b087fa1b3e21c446e612bb361951628e6b3319af5c75e4aa0f68131c1b8f1cc831cb89c4088da1f60d3318d59adff98622b3849f8884f995c81191f3651789af9cc82c788ec2d094c6651b85baec66198e44a91aecb0eaba4b88c519f29d2b60f5ca81bafc0cbc5906c7ace98c1e2d6c4d026b4d0412dac4452d0fec8471d7bc8c350867d9a3a64bec410ba3dccb10e5638c9cb21cb94a9835d2e1141715644dd50f7c2ff55815aa1d1a6a228968f4e286452234517796c81a91d00ddfcb318b85bdd6b67649bdd72cc6c3adca1a87e0ceda8948a42a12aaafe220eace9284f4fcee9133f63c33e01850f976ab22d891d31b1a09559639032373042c627c62d0c2d9295a1313448fe2c304e63bbc32aac8cf2307120624de88966af282690a846bc9dafceec7f6d78150f2f9eb262ed4fed1bf458e0188352cce1a80490b42c638c20d8de15e67ca3729eec34d241262de077e35317e0e5aa06689ba3fdc0f529335706f81ce08cf5940afd263d6978ba8c99aa607cdd8098bf902767cc0718db5861d39456e7e1103a2c155e4e03c82e194c709bf690895ad251af49193ed5251b048fb318b917d9acf662127fe246506e42a7c4ac3290272f45fef01267601d5484732d4109525690a60929c9ba8b937e53c8bd1744467da8e1da242062a22d82f18a32c62dc79ac00aa46f36200e42d8692e32c5b6ee72d96a24846304486c65677b1e77dbdff71cc0e0919313a4be7a348929e054975c00235cb716f03eb8b9d9e8522e9d1e0c5060000);
}
