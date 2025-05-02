package easv.dk.belsign.gui.controllers.Operator;

import easv.dk.belsign.be.Order;
import easv.dk.belsign.bll.OrderManager;
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
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/easv/dk/belsign/views/OperatorViews/take-picture-view.fxml"));
            Parent root = loader.load();

            // Pass the selected order to the next controller if needed
            CameraController controller = loader.getController();
            controller.setSelectedOrder(selectedOrder);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Take Picture");
            stage.show();

            // Close the current window
            Stage currentStage = (Stage) orderListView.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
