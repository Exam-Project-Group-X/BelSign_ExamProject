package easv.dk.belsign.gui.controllers.Operator.components;



import easv.dk.belsign.be.Order;
import easv.dk.belsign.gui.controllers.Operator.OperatorOrdersController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;


public class OperatorOrderCardController {

    @FXML
    private Label orderNumberLabel;
    @FXML
    private GridPane cardRoot;

    private Order order;
    private OperatorOrdersController parentController;;

    public void setOrderData(Order order, OperatorOrdersController parentController) {
        this.order = order;
        this.parentController = parentController;
        orderNumberLabel.setText(order.getOrderNumber());

        cardRoot.setOnMouseClicked(event -> parentController.openTakePictureView(order));
    }

}
