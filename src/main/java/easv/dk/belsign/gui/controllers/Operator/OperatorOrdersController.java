package easv.dk.belsign.gui.controllers.Operator;

import easv.dk.belsign.be.Order;
import easv.dk.belsign.bll.OrderManager;
import easv.dk.belsign.gui.ViewManagement.FXMLPath;
import easv.dk.belsign.gui.ViewManagement.ViewManager;
import easv.dk.belsign.gui.controllers.Operator.components.OperatorOrderCardController;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(FXMLPath.CAMERA_VIEW));
            Parent root = loader.load();

            CameraController controller = loader.getController();
            controller.setSelectedOrder(selectedOrder); // ✅ Inject the order here

            // Replace the scene manually
            ViewManager.INSTANCE.getSceneManager().getCurrentStage().setScene(new Scene(root));
            ViewManager.INSTANCE.getSceneManager().getCurrentStage().centerOnScreen();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void OnClickLogoutButton(ActionEvent actionEvent) {
        ViewManager.INSTANCE.showScene(FXMLPath.TITLE_SCREEN);
    }


}
