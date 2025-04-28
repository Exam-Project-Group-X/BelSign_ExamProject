package easv.dk.belsign.gui.QAControllers;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.control.ComboBox;

public class QAOrdersController {
    @FXML
    private VBox cardContainer;

    @FXML
    private void initialize() {
        // Simulate loading 2 orders
        for (int i = 0; i < 2; i++) {
            cardContainer.getChildren().add(createOrderCard("4504202500" + i, "Order Description " + i, "by Operator " + i));
        }
    }

    private GridPane createOrderCard(String orderId, String description, String operator) {
        GridPane card = new GridPane();
        card.setHgap(10);
        card.setVgap(5);
        card.setStyle("-fx-background-color: #f9f9f9; -fx-padding: 10; -fx-border-color: lightgrey; -fx-border-radius: 5;");

        Label orderIdLabel = new Label(orderId);
        Label descriptionLabel = new Label(description);
        Label createDateLabel = new Label("20 April 2025");
        Label operatorLabel = new Label(operator);
        ImageView productPhoto = new ImageView();
        productPhoto.setFitWidth(162);
        productPhoto.setFitHeight(150);
        ComboBox<String> statusCombo = new ComboBox<>();
        Label updateDateLabel = new Label("27 April 2025");
        Button actionButton = new Button("Details");

        card.add(orderIdLabel, 0, 0);
        card.add(descriptionLabel, 1, 0);
        card.add(createDateLabel, 2, 0);
        card.add(operatorLabel, 3, 0);
        card.add(productPhoto, 4, 0);
        card.add(statusCombo, 5, 0);
        card.add(updateDateLabel, 6, 0);
        card.add(actionButton, 7, 0);

        return card;
    }

}
