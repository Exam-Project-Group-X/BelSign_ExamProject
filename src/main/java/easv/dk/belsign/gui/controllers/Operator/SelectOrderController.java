package easv.dk.belsign.gui.controllers.Operator;

import easv.dk.belsign.be.Order;
import easv.dk.belsign.bll.OrderManager;
import easv.dk.belsign.gui.ViewManagement.FXMLPath;
import easv.dk.belsign.gui.ViewManagement.ViewManager;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.IOException;

public class SelectOrderController {

    @FXML
    private ListView<Order> orderListView;

    private OrderManager orderManager = new OrderManager();

    @FXML
    public void initialize() {
        // Populate the ListView with orders
        ObservableList<Order> orders = orderManager.getAllNewOrders();
        orderListView.setItems(orders);

        // Set a listener for selection
        orderListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                openTakePictureView(newValue);
            }
        });
    }


    private void openTakePictureView(Order selectedOrder) {



            ViewManager.INSTANCE.showScene(FXMLPath.OPERATOR_TAKE_PICTURE_VIEW);


    }
}
