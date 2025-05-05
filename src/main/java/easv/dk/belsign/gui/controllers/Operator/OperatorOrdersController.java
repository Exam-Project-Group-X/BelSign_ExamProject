package easv.dk.belsign.gui.controllers.Operator;

import easv.dk.belsign.be.Order;
import easv.dk.belsign.bll.OrderManager;
import easv.dk.belsign.gui.ViewManagement.FXMLPath;
import easv.dk.belsign.gui.ViewManagement.ViewManager;
import easv.dk.belsign.gui.controllers.Operator.components.OperatorOrderCardController;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class OperatorOrdersController {

    @FXML
    private VBox cardContainer;

    private OrderManager orderManager = new OrderManager();

    @FXML
    public void initialize() {
        try {
            for (Order order : orderManager.getAllNewOrders()) {
                addOrderCard(order);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addOrderCard(Order order) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(FXMLPath.OPERATOR_ORDER_CARD));
            Parent card = loader.load();

            OperatorOrderCardController controller = loader.getController();
            controller.setOrderData(order, this);

            cardContainer.getChildren().add(card);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openTakePictureView(Order selectedOrder) {

        ViewManager.INSTANCE.showScene(FXMLPath.OPERATOR_TAKE_PICTURE_VIEW);
    }
}
