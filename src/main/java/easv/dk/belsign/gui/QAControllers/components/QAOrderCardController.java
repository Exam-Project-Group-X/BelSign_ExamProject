package easv.dk.belsign.gui.QAControllers.components;

import easv.dk.belsign.be.Order;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class QAOrderCardController {

    @FXML private Label orderNumberLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label createdAtLabel;

    public void setOrderData(Order order) {
        orderNumberLabel.setText(order.getOrderNumber());
        descriptionLabel.setText(order.getProductDescription() == null ? "No description" : order.getProductDescription());
        createdAtLabel.setText(order.getCreatedAt() != null ? order.getCreatedAt().toString() : "Now");

}

}
