package easv.dk.belsign.gui.controllers.Operator.components;

import easv.dk.belsign.be.Order;
import easv.dk.belsign.gui.controllers.Operator.OperatorOrdersController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class OperatorOrderCardController {
    @FXML private Label orderNumberLabel, retakePhotosLabel;
    @FXML private GridPane cardRoot;

    private Order order;
    private OperatorOrdersController parentController;;

    public void setOrderData(Order order, OperatorOrdersController parentController) {
        this.order = order;
        this.parentController = parentController;
        orderNumberLabel.setText(order.getOrderNumber());
        // Handle retake photos logic
        if (order.isHasRejectedPhotos()) {
            retakePhotosLabel.setText("Retake Photos");
            retakePhotosLabel.setVisible(true);
        } else {
            retakePhotosLabel.setVisible(false);
        }
        // Handle card click event to open camera view
        cardRoot.setOnMouseClicked(event -> parentController.openTakePictureView(order));
    }
}
