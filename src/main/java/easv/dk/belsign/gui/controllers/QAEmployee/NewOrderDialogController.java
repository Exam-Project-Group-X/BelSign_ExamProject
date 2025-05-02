package easv.dk.belsign.gui.controllers.QAEmployee;

import easv.dk.belsign.be.Order;
import easv.dk.belsign.gui.controllers.QAEmployee.model.QAmodel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class NewOrderDialogController {

    @FXML
    private TextField orderIdField;

    @FXML
    private TextField descriptionField;
    @FXML
    private Button cancelBtn;

    @FXML
    private Button continueBtn;

    private QAEmployeeController parentController;
    private final QAmodel model = new QAmodel();
    @FXML
    private void initialize()

    {
        cancelBtn.setOnAction(event -> onCancel());
        continueBtn.setOnAction(event -> onContinue());
    }

    public void setParentController(QAEmployeeController controller) {
        this.parentController = controller;
    }

    private void onContinue() {
        String orderNumber = orderIdField.getText();
        String description = descriptionField.getText();

        if (orderNumber == null || orderNumber.isBlank()) {
            // You can show an alert if needed
            return;
        }
        Order order = new Order();
        order.setOrderNumber(orderNumber);
        order.setProductDescription(description);
        order.setOrderStatus("Awaiting Photos"); // Optional default

        try {
            int newId = model.createOrder(order);
            if (newId != -1) {
                order.setOrderID(newId);
                parentController.addNewOrderCard(order); // ✅ Now passes real data
            }
        } catch (Exception e) {
            e.printStackTrace(); // You can show a better error alert here
        }

        continueBtn.getScene().getWindow().hide();
    }
    private void onCancel() {
        cancelBtn.getScene().getWindow().hide(); // Just closes the window
    }



}
