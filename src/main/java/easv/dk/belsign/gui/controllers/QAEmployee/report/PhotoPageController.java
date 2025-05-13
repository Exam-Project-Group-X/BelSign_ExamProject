package easv.dk.belsign.gui.controllers.QAEmployee.report;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;

public class PhotoPageController {

    public Label lblPageNumber;
    @FXML
    private GridPane photoGrid;

    public void setPageNumber(int page) {
        lblPageNumber.setText("Page " + page);
    }

    public void setPhotosFromBytes(List<Map.Entry<String, byte[]>> photos) {
        photoGrid.getChildren().clear(); // fx:id in QCReportPhotoPage.fxml

        for (int i = 0; i < photos.size(); i++) {
            Map.Entry<String, byte[]> entry = photos.get(i);

            String angle = entry.getKey();
            byte[] data = entry.getValue();
            if (data == null) continue;

            Image img = new Image(new ByteArrayInputStream(data));

            ImageView imageView = new ImageView(img);
            imageView.setFitHeight(123);
            imageView.setFitWidth(150);
            imageView.setPreserveRatio(true);

            Label label = new Label(angle);
            label.setStyle("-fx-font-weight: bold;");
            label.setPrefWidth(150);
            label.setAlignment(Pos.CENTER);

            VBox box = new VBox(5, imageView, label);
            box.setAlignment(Pos.CENTER);

            int col = i % 3;
            int row = i / 3;
            photoGrid.add(box, col, row); // GridPane with fx:id="photoGrid"
        }
    }
}
