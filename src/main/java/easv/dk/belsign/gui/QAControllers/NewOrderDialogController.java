package easv.dk.belsign.gui.QAControllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class NewOrderDialogController {

    @FXML
    private Button cancelBtn;

    @FXML
    private Button continueBtn;

    private QAEmployeeController parentController;

    @FXML
    private void initialize() {
        cancelBtn.setOnAction(event -> onCancel());
        continueBtn.setOnAction(event -> onContinue());
    }

    public void setParentController(QAEmployeeController controller) {
        this.parentController = controller;
    }

    private void onContinue() {
        // Optionally read fields, validate, etc.

        parentController.addNewOrderCard(); // adds to screen
        continueBtn.getScene().getWindow().hide(); // closes popup
    }
    private void onCancel() {
        cancelBtn.getScene().getWindow().hide(); // Just closes the window
    }



}
